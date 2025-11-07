package capstone25_2.aim.service;

import capstone25_2.aim.domain.dto.analyst.AnalystMetricsDTO;
import capstone25_2.aim.domain.dto.analyst.AnalystRankingResponseDTO;
import capstone25_2.aim.domain.entity.AnalystMetrics;
import capstone25_2.aim.domain.entity.ClosePrice;
import capstone25_2.aim.domain.entity.Report;
import capstone25_2.aim.repository.AnalystMetricsRepository;
import capstone25_2.aim.repository.AnalystRepository;
import capstone25_2.aim.repository.ClosePriceRepository;
import capstone25_2.aim.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnalystMetricsService {

    private final AnalystMetricsRepository metricsRepository;
    private final ReportRepository reportRepository;
    private final AnalystRepository analystRepository;
    private final ClosePriceRepository closePriceRepository;

    // 랭킹 리스트 조회 (기본: accuracyRate 순)
    public AnalystRankingResponseDTO getRankedAnalysts(String sortBy) {
        List<AnalystMetrics> metricsList = metricsRepository.findAll();
        return createRankedResponse(metricsList,sortBy);
    }

    // 🔹 특정 종목 기준 랭킹
    public AnalystRankingResponseDTO getRankedAnalystsByStock(Long stockId, String sortBy) {
        // 1. 해당 종목의 리포트를 전부 가져옴
        List<Long> analystIds = reportRepository.findByStockId(stockId).stream()
                .map(r -> r.getAnalyst().getId())
                .distinct()
                .toList();

        // 2. 애널리스트 ID 기반으로 메트릭 필터링
        List<AnalystMetrics> metricsList = metricsRepository.findAll().stream()
                .filter(m -> analystIds.contains(m.getAnalyst().getId()))
                .toList();

        // 3. 정렬 결과 반환
        return createRankedResponse(metricsList, sortBy);
    }

    // 내부 정렬 로직 (중복 제거)
    private static AnalystRankingResponseDTO createRankedResponse(List<AnalystMetrics> metricsList, String sortBy) {
        Comparator<AnalystMetrics> comparator = switch (sortBy) {
            case "returnRate" -> Comparator.comparing(AnalystMetrics::getReturnRate).reversed();
            case "targetDiffRate" -> Comparator.comparing(AnalystMetrics::getTargetDiffRate);
            default -> Comparator.comparing(AnalystMetrics::getAccuracyRate).reversed();
        };

        List<AnalystMetricsDTO> ranking = metricsList.stream()
                .sorted(comparator)
                .map(AnalystMetricsDTO::fromEntity)
                .toList();

        return AnalystRankingResponseDTO.builder()
                .criteria(sortBy)
                .rankingList(ranking)
                .build();
    }

    /**
     * 애널리스트 정확도, 수익률, 목표가 오차율 계산 후 저장
     * 최근 5년 리포트 기준으로 계산
     */
    @Transactional
    public void calculateAndSaveAccuracyRate(Long analystId) {
        // 1. 최근 5년 리포트 조회
        LocalDateTime fiveYearsAgo = LocalDateTime.now().minusYears(5);
        List<Report> recentReports = reportRepository
                .findByAnalystIdAndReportDateAfterOrderByReportDateDesc(analystId, fiveYearsAgo);

        if (recentReports.isEmpty()) {
            return; // 리포트가 없으면 계산 불가
        }

        // 2. 각 리포트 평가 (정확도, 수익률, 목표가 오차율, 상대적 성과)
        int totalEvaluated = 0;
        int correctCount = 0;
        double totalReturn = 0.0;
        double totalTargetDiff = 0.0;
        int targetDiffCount = 0; // 목표가 오차율 계산 가능한 리포트 수

        double totalReturnDiff = 0.0; // 종목별 평균 대비 수익률 차이 누적
        int returnDiffCount = 0;
        double totalTargetDiffDiff = 0.0; // 종목별 평균 대비 목표가 오차율 차이 누적
        int targetDiffDiffCount = 0;

        for (Report report : recentReports) {
            EvaluationResult result = evaluateReportWithReturn(report);
            if (result != null) { // null이면 평가 불가 (데이터 부족)
                totalEvaluated++;
                if (result.isCorrect) {
                    correctCount++;
                }
                totalReturn += result.returnRate;

                // 목표가 오차율: 의견 불일치가 아닌 경우만 집계
                if (result.targetDiffRate != null) {
                    totalTargetDiff += result.targetDiffRate;
                    targetDiffCount++;
                }

                // 해당 종목에 대한 모든 애널리스트들의 평균 계산 (자기 포함)
                StockAverageMetrics stockAvg = calculateStockAverageMetrics(
                    report.getStock().getId(),
                    fiveYearsAgo
                );

                // 수익률 차이 계산
                if (stockAvg.averageReturn != null) {
                    totalReturnDiff += (result.returnRate - stockAvg.averageReturn);
                    returnDiffCount++;
                }

                // 목표가 오차율 차이 계산 (의견 일치 케이스만)
                if (result.targetDiffRate != null && stockAvg.averageTargetDiff != null) {
                    totalTargetDiffDiff += (result.targetDiffRate - stockAvg.averageTargetDiff);
                    targetDiffDiffCount++;
                }
            }
        }

        // 3. 정확도, 평균 수익률, 평균 목표가 오차율, 상대적 성과 계산
        if (totalEvaluated == 0) {
            return; // 평가 가능한 리포트가 없으면 저장하지 않음
        }

        double accuracyRate = (double) correctCount / totalEvaluated * 100.0;
        double averageReturn = totalReturn / totalEvaluated;

        // 목표가 오차율: 의견 일치 리포트만 평균 계산
        double averageTargetDiff = (targetDiffCount > 0)
            ? totalTargetDiff / targetDiffCount
            : 0.0; // 의견 일치 리포트가 없으면 0

        // 애널리스트 평균 대비 수익률 차이
        Double avgReturnDiff = (returnDiffCount > 0)
            ? totalReturnDiff / returnDiffCount
            : null;

        // 애널리스트 평균 대비 목표가 오차율 차이
        Double avgTargetDiff = (targetDiffDiffCount > 0)
            ? totalTargetDiffDiff / targetDiffDiffCount
            : null;

        // 4. AnalystMetrics 조회 또는 생성 후 저장
        AnalystMetrics metrics = analystRepository.findById(analystId)
                .map(analyst -> analyst.getAnalystMetrics())
                .orElseGet(AnalystMetrics::new);

        metrics.setAccuracyRate(accuracyRate);
        metrics.setReturnRate(averageReturn);
        metrics.setTargetDiffRate(averageTargetDiff);
        metrics.setAvgReturnDiff(avgReturnDiff);
        metrics.setAvgTargetDiff(avgTargetDiff);
        metrics.setAnalyst(analystRepository.findById(analystId).orElseThrow());

        metricsRepository.save(metrics);
    }

    /**
     * 평가 결과를 담는 내부 클래스
     */
    private static class EvaluationResult {
        boolean isCorrect;
        double returnRate;        // 수익률
        Double targetDiffRate;    // 목표가 오차율 (의견 불일치시 null)

        EvaluationResult(boolean isCorrect, double returnRate, Double targetDiffRate) {
            this.isCorrect = isCorrect;
            this.returnRate = returnRate;
            this.targetDiffRate = targetDiffRate;
        }
    }

    /**
     * 개별 리포트 평가 (정확도 + 수익률 + 목표가 오차율)
     * @return EvaluationResult (정확도, 수익률, 목표가 오차율 포함) 또는 null (평가 불가)
     */
    private EvaluationResult evaluateReportWithReturn(Report report) {
        // 1. 중간에 의견 변화가 있는지 확인
        LocalDateTime oneYearLater = report.getReportDate().plusYears(1);
        Optional<Report> opinionChange = findOpinionChangeBeforeTarget(report, oneYearLater);

        // 의견이 변경되었으면 이 리포트는 평가 제외 (의견 변화 이후의 새 리포트부터 다시 평가)
        if (opinionChange.isPresent()) {
            return null;
        }

        // 2. 리포트 발행 시점의 실제 주가 조회
        Optional<ClosePrice> reportDatePriceOpt = getActualPriceAtDate(
                report.getStock().getId(), report.getReportDate());

        if (reportDatePriceOpt.isEmpty()) {
            return null; // 리포트 발행 시점 주가 데이터 없으면 평가 불가
        }

        Integer reportDatePrice = reportDatePriceOpt.get().getClosePrice();

        // 3. 1년 후의 실제 주가 조회
        Optional<ClosePrice> actualPriceOpt = getActualPriceAtDate(report.getStock().getId(), oneYearLater);

        if (actualPriceOpt.isEmpty()) {
            return null; // 1년 후 주가 데이터 없으면 평가 불가
        }

        Integer oneYearLaterPrice = actualPriceOpt.get().getClosePrice();
        Integer targetPrice = report.getTargetPrice();
        Double hiddenOpinion = report.getHiddenOpinion();

        if (targetPrice == null || targetPrice == 0 || reportDatePrice == 0) {
            return null; // 목표가나 발행시점 주가가 없으면 평가 불가
        }

        // 4. 정확도 판단
        boolean isCorrect = isOpinionCorrect(hiddenOpinion, targetPrice, oneYearLaterPrice);

        // 5. 수익률 계산: (1년 후 종가 - 리포트 발행시점 종가) / 리포트 발행시점 종가 * 100
        double returnRate = ((double) (oneYearLaterPrice - reportDatePrice) / reportDatePrice) * 100.0;

        // 6. 목표가 오차율 계산: 의견 불일치시 null 반환 (BUY인데 하락 예측 등)
        Double targetDiffRate = null;
        if (!isOpinionMismatch(report.getSurfaceOpinion(), hiddenOpinion)) {
            targetDiffRate = ((double) (targetPrice - oneYearLaterPrice) / targetPrice) * 100.0;
        }

        return new EvaluationResult(isCorrect, returnRate, targetDiffRate);
    }

    /**
     * 특정 날짜 이후 가장 가까운 거래일의 실제 주가 조회
     */
    private Optional<ClosePrice> getActualPriceAtDate(Long stockId, LocalDateTime targetDateTime) {
        return closePriceRepository.findFirstByStockIdAndTradeDateGreaterThanEqualOrderByTradeDateAsc(
                stockId, targetDateTime.toLocalDate());
    }

    /**
     * 리포트 이후 ~ 목표일 이전에 같은 종목에 대한 의견 변화가 있었는지 확인
     */
    private Optional<Report> findOpinionChangeBeforeTarget(Report originalReport, LocalDateTime targetDate) {
        return reportRepository.findFirstByAnalystIdAndStockIdAndReportDateAfterOrderByReportDateAsc(
                originalReport.getAnalyst().getId(),
                originalReport.getStock().getId(),
                originalReport.getReportDate()
        ).filter(nextReport -> nextReport.getReportDate().isBefore(targetDate));
    }

    /**
     * hiddenOpinion과 실제 주가 변동이 일치하는지 판단
     * hiddenOpinion: 0.0 ~ 1.0 (0에 가까울수록 하락, 1에 가까울수록 상승)
     *
     * @param hiddenOpinion 숨겨진 의견 (0.0 ~ 1.0)
     * @param targetPrice 목표가
     * @param actualPrice 실제 주가
     * @return 의견이 맞았는지 여부
     */
    private boolean isOpinionCorrect(Double hiddenOpinion, Integer targetPrice, Integer actualPrice) {
        if (hiddenOpinion == null || targetPrice == null || actualPrice == null) {
            return false;
        }

        // 목표가 대비 실제 주가 달성률
        boolean priceIncreased = actualPrice >= targetPrice; // 실제로 목표가 이상 달성했는지
        boolean priceDecreased = actualPrice < targetPrice; // 실제로 목표가 미달성했는지

        // hiddenOpinion 해석: 0.5 기준으로 상승/하락 판단
        boolean predictedIncrease = hiddenOpinion >= 0.5;

        // 예측과 실제가 일치하면 정답
        return (predictedIncrease && priceIncreased) || (!predictedIncrease && priceDecreased);
    }

    /**
     * surfaceOpinion과 hiddenOpinion이 불일치하는지 판단
     * BUY인데 hiddenOpinion이 하락(< 0.5)이거나
     * SELL인데 hiddenOpinion이 상승(>= 0.5)인 경우 불일치로 판단
     *
     * @param surfaceOpinion 표면 의견 (BUY, HOLD, SELL)
     * @param hiddenOpinion 숨겨진 의견 (0.0 ~ 1.0)
     * @return 의견 불일치 여부
     */
    private boolean isOpinionMismatch(SurfaceOpinion surfaceOpinion, Double hiddenOpinion) {
        if (surfaceOpinion == null || hiddenOpinion == null) {
            return false;
        }

        boolean hiddenBullish = hiddenOpinion >= 0.5; // 숨은 의견이 상승

        // BUY인데 hiddenOpinion이 하락 예측
        if (surfaceOpinion == SurfaceOpinion.BUY && !hiddenBullish) {
            return true;
        }

        // SELL인데 hiddenOpinion이 상승 예측
        if (surfaceOpinion == SurfaceOpinion.SELL && hiddenBullish) {
            return true;
        }

        return false;
    }

    /**
     * 특정 종목에 대한 모든 애널리스트들의 평균 수익률과 목표가 오차율 계산
     *
     * @param stockId 종목 ID
     * @param fiveYearsAgo 5년 전 날짜
     * @return 해당 종목에 대한 모든 애널리스트들의 평균 메트릭
     */
    private StockAverageMetrics calculateStockAverageMetrics(
            Long stockId, LocalDateTime fiveYearsAgo) {

        // 해당 종목에 대한 모든 애널리스트들의 최근 5년 리포트 조회
        List<Report> allAnalystReports = reportRepository
                .findByStockIdAndReportDateAfterOrderByReportDateDesc(stockId, fiveYearsAgo);

        if (allAnalystReports.isEmpty()) {
            return new StockAverageMetrics(null, null);
        }

        // 각 리포트 평가
        double totalReturn = 0.0;
        int returnCount = 0;
        double totalTargetDiff = 0.0;
        int targetDiffCount = 0;

        for (Report report : allAnalystReports) {
            EvaluationResult result = evaluateReportWithReturn(report);
            if (result != null) {
                totalReturn += result.returnRate;
                returnCount++;

                if (result.targetDiffRate != null) {
                    totalTargetDiff += result.targetDiffRate;
                    targetDiffCount++;
                }
            }
        }

        Double averageReturn = (returnCount > 0) ? totalReturn / returnCount : null;
        Double averageTargetDiff = (targetDiffCount > 0) ? totalTargetDiff / targetDiffCount : null;

        return new StockAverageMetrics(averageReturn, averageTargetDiff);
    }

    /**
     * 종목별 평균 메트릭을 담는 내부 클래스
     */
    private static class StockAverageMetrics {
        Double averageReturn;      // 해당 종목 모든 애널리스트들의 평균 수익률
        Double averageTargetDiff;  // 해당 종목 모든 애널리스트들의 평균 목표가 오차율

        StockAverageMetrics(Double averageReturn, Double averageTargetDiff) {
            this.averageReturn = averageReturn;
            this.averageTargetDiff = averageTargetDiff;
        }
    }

}
