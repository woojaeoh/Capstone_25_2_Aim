package capstone25_2.aim.service;

import capstone25_2.aim.domain.dto.analyst.AnalystMetricsDTO;
import capstone25_2.aim.domain.dto.analyst.AnalystRankingResponseDTO;
import capstone25_2.aim.domain.entity.*;
import capstone25_2.aim.repository.AnalystMetricsRepository;
import capstone25_2.aim.repository.AnalystRepository;
import capstone25_2.aim.repository.ClosePriceRepository;
import capstone25_2.aim.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

        // 상대적 성과 계산
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

                // 상대적 성과 계산
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
     * 애널리스트 정확도, 수익률, 목표가 오차율 계산 후 저장 (전체 평균 비교 버전)
     * 전체 애널리스트 평균과 비교하여 성능 최적화
     *
     * @param analystId 애널리스트 ID
     * @param globalAverage 전체 애널리스트 평균 메트릭
     * @param fiveYearsAgo 5년 전 날짜
     */
    @Transactional
    public void calculateAndSaveAccuracyRateWithCache(
            Long analystId,
            GlobalAverageMetrics globalAverage,
            LocalDateTime fiveYearsAgo) {

        // 1. 최근 5년 리포트 조회
        List<Report> recentReports = reportRepository
                .findByAnalystIdAndReportDateAfterOrderByReportDateDesc(analystId, fiveYearsAgo);

        if (recentReports.isEmpty()) {
            return; // 리포트가 없으면 계산 불가
        }

        // 2. 각 리포트 평가 (정확도, 수익률, 목표가 오차율)
        int totalEvaluated = 0;
        int correctCount = 0;
        double totalReturn = 0.0;
        double totalTargetDiff = 0.0;
        int targetDiffCount = 0;

        for (Report report : recentReports) {
            EvaluationResult result = evaluateReportWithReturn(report);
            if (result != null) {
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
            }
        }

        // 3. 정확도, 평균 수익률, 평균 목표가 오차율 계산
        if (totalEvaluated == 0) {
            return;
        }

        double accuracyRate = (double) correctCount / totalEvaluated * 100.0;
        double averageReturn = totalReturn / totalEvaluated;

        double averageTargetDiff = (targetDiffCount > 0)
            ? totalTargetDiff / targetDiffCount
            : 0.0;

        // 4. 전체 애널리스트 평균 대비 차이 계산
        Double avgReturnDiff = null;
        Double avgTargetDiff = null;

        if (globalAverage != null) {
            // 수익률 차이: 이 애널리스트의 평균 수익률 - 전체 평균 수익률
            if (globalAverage.averageReturn != null) {
                avgReturnDiff = averageReturn - globalAverage.averageReturn;
            }

            // 목표가 오차율 차이: 이 애널리스트의 평균 목표가 오차율 - 전체 평균 목표가 오차율
            if (targetDiffCount > 0 && globalAverage.averageTargetDiff != null) {
                avgTargetDiff = averageTargetDiff - globalAverage.averageTargetDiff;
            }
        }

        // 5. AnalystMetrics 조회 또는 생성 후 저장
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
     * hiddenOpinion의 3단계 분류(BUY/HOLD/SELL)가 변경된 경우에만 의견 변화로 판단
     */
    private Optional<Report> findOpinionChangeBeforeTarget(Report originalReport, LocalDateTime targetDate) {
        // 원본 리포트 이후의 모든 리포트를 시간순으로 조회
        List<Report> laterReports = reportRepository.findByAnalystIdAndStockIdOrderByReportDateAsc(
                originalReport.getAnalyst().getId(),
                originalReport.getStock().getId()
        ).stream()
                .filter(r -> r.getReportDate().isAfter(originalReport.getReportDate()))
                .filter(r -> r.getReportDate().isBefore(targetDate))
                .toList();

        if (laterReports.isEmpty()) {
            return Optional.empty();
        }

        // 원본 리포트의 의견 분류
        String originalCategory = HiddenOpinionLabel.toSimpleCategory(originalReport.getHiddenOpinion());
        String previousCategory = originalCategory;

        // 시간순으로 순회하면서 의견 변화가 있는지 확인
        for (Report laterReport : laterReports) {
            String currentCategory = HiddenOpinionLabel.toSimpleCategory(laterReport.getHiddenOpinion());

            // 이전 리포트와 의견이 다르면 의견 변화로 판단
            if (!java.util.Objects.equals(previousCategory, currentCategory)) {
                return Optional.of(laterReport);
            }
            previousCategory = currentCategory;
        }

        return Optional.empty();
    }

    /**
     * hiddenOpinion과 실제 주가 변동이 일치하는지 3단계로 판단
     *
     * 예측 분류 (3단계):
     * - BUY: hiddenOpinion >= 0.75
     * - HOLD: 0.4 <= hiddenOpinion < 0.75
     * - SELL: hiddenOpinion < 0.4
     *
     * 실제 결과 분류 (목표가 기준):
     * - BUY: 1년 후 실제 주가 >= 목표가
     * - HOLD: 목표가 * 0.9 <= 실제 주가 < 목표가 (목표가 ±10% 범위)
     * - SELL: 실제 주가 < 목표가 * 0.9
     *
     * @param hiddenOpinion 숨겨진 의견 (0.0 ~ 1.0)
     * @param targetPrice 목표가
     * @param actualPrice 1년 후 실제 주가
     * @return 예측과 실제가 일치하는지 여부
     */
    private boolean isOpinionCorrect(Double hiddenOpinion, Integer targetPrice, Integer actualPrice) {
        if (hiddenOpinion == null || targetPrice == null || actualPrice == null || targetPrice == 0) {
            return false;
        }

        // 1. 예측을 3단계로 분류 (BUY/HOLD/SELL)
        String predictedCategory = HiddenOpinionLabel.toSimpleCategory(hiddenOpinion);
        if (predictedCategory == null) {
            return false;
        }

        // 2. 실제 주가를 3단계로 분류 (목표가 기준)
        String actualCategory;
        if (actualPrice >= targetPrice) {
            actualCategory = "BUY";  // 목표가 이상 달성
        } else if (actualPrice >= targetPrice * 0.9) {
            actualCategory = "HOLD";  // 목표가 90% ~ 100% 사이 (±10% 범위)
        } else {
            actualCategory = "SELL";  // 목표가 90% 미달
        }

        // 3. 예측과 실제가 일치하면 정답
        return predictedCategory.equals(actualCategory);
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
     * 모든 애널리스트의 지표를 전체 평균과 비교하여 일괄 계산 (성능 최적화 버전)
     *
     * @return 계산된 애널리스트 수
     */
    @Transactional
    public int calculateAllAnalystMetricsWithCache() {
        System.out.println("📊 모든 애널리스트 지표 일괄 계산 시작 (최적화 버전)...");

        LocalDateTime fiveYearsAgo = LocalDateTime.now().minusYears(5);

        // 1. 전체 애널리스트의 평균 수익률과 목표가 오차율 계산
        System.out.println("📈 전체 애널리스트 평균 계산 중...");
        GlobalAverageMetrics globalAverage = calculateGlobalAverageMetrics(fiveYearsAgo);

        if (globalAverage.averageReturn != null) {
            System.out.println("  ✓ 전체 평균 수익률: " + String.format("%.2f", globalAverage.averageReturn) + "%");
        }
        if (globalAverage.averageTargetDiff != null) {
            System.out.println("  ✓ 전체 평균 목표가 오차율: " + String.format("%.2f", globalAverage.averageTargetDiff) + "%");
        }

        // 2. 모든 애널리스트 조회
        List<Analyst> allAnalysts = analystRepository.findAll();
        System.out.println("👥 전체 애널리스트 수: " + allAnalysts.size());

        // 3. 각 애널리스트마다 전체 평균과 비교하여 지표 계산
        int calculatedCount = 0;
        for (Analyst analyst : allAnalysts) {
            try {
                calculateAndSaveAccuracyRateWithCache(analyst.getId(), globalAverage, fiveYearsAgo);
                calculatedCount++;

                // 10명마다 진행 상황 출력
                if (calculatedCount % 10 == 0) {
                    System.out.println("  ⏳ 애널리스트 계산: " + calculatedCount + "/" + allAnalysts.size());
                }
            } catch (Exception e) {
                System.err.println("⚠️ 애널리스트 " + analyst.getId() + " 지표 계산 실패: " + e.getMessage());
            }
        }

        System.out.println("✅ 애널리스트 지표 계산 완료: " + calculatedCount + "명");
        return calculatedCount;
    }

    /**
     * 전체 애널리스트의 평균 수익률과 목표가 오차율 계산
     *
     * @param fiveYearsAgo 5년 전 날짜
     * @return 전체 애널리스트들의 평균 메트릭
     */
    private GlobalAverageMetrics calculateGlobalAverageMetrics(LocalDateTime fiveYearsAgo) {
        // 모든 리포트 조회 (최근 5년)
        List<Report> allReports = reportRepository
                .findAll().stream()
                .filter(r -> r.getReportDate().isAfter(fiveYearsAgo))
                .collect(Collectors.toList());

        if (allReports.isEmpty()) {
            return new GlobalAverageMetrics(null, null);
        }

        // 각 리포트 평가
        double totalReturn = 0.0;
        int returnCount = 0;
        double totalTargetDiff = 0.0;
        int targetDiffCount = 0;

        for (Report report : allReports) {
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

        return new GlobalAverageMetrics(averageReturn, averageTargetDiff);
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

    /**
     * 전체 애널리스트 평균 메트릭을 담는 내부 클래스
     */
    private static class GlobalAverageMetrics {
        Double averageReturn;      // 전체 애널리스트들의 평균 수익률
        Double averageTargetDiff;  // 전체 애널리스트들의 평균 목표가 오차율

        GlobalAverageMetrics(Double averageReturn, Double averageTargetDiff) {
            this.averageReturn = averageReturn;
            this.averageTargetDiff = averageTargetDiff;
        }
    }

}
