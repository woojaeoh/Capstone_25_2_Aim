package capstone25_2.aim.service;

import capstone25_2.aim.domain.dto.report.TargetPriceTrendDTO;
import capstone25_2.aim.domain.dto.report.TargetPriceTrendResponseDTO;
import capstone25_2.aim.domain.entity.Report;
import capstone25_2.aim.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;

    public List<Report> getReportsByStockId(Long stockId){
        return reportRepository.findByStockId(stockId);
    }

    public List<Report> getReportsByAnalystId(Long analystId){
        return reportRepository.findByAnalystId(analystId);
    }

    public Optional<Report> getReportById(Long reportId){
        return reportRepository.findById(reportId);
    }

    // 최신 5년의 리포트 리스트 조회 (종목별)
    public List<Report> getRecentReportsByStockId(Long stockId){
        LocalDateTime fiveYearsAgo = LocalDateTime.now().minusYears(5);
        return reportRepository.findByStockIdAndReportDateAfterOrderByReportDateDesc(stockId, fiveYearsAgo);
    }

    // 최신 5년의 리포트 리스트 조회 (애널리스트별)
    public List<Report> getRecentReportsByAnalystId(Long analystId){
        LocalDateTime fiveYearsAgo = LocalDateTime.now().minusYears(5);
        return reportRepository.findByAnalystIdAndReportDateAfterOrderByReportDateDesc(analystId, fiveYearsAgo);
    }

    // 종목별 목표가 변동 추이 데이터 생성
    public TargetPriceTrendResponseDTO getTargetPriceTrend(Long stockId){
        List<Report> recentReports = getRecentReportsByStockId(stockId);

        if(recentReports.isEmpty()){
            throw new RuntimeException("No reports found for stock");
        }

        // 목표가 변동 추이 리스트 생성
        List<TargetPriceTrendDTO> trendList = recentReports.stream()
                .map(report -> {
                    String percentageChange = calculatePercentageChange(report);
                    return TargetPriceTrendDTO.builder()
                            .reportDate(LocalDate.from(report.getReportDate()))
                            .targetPrice(report.getTargetPrice())
                            .analystName(report.getAnalyst().getAnalystName())
                            .firmName(report.getAnalyst().getFirmName())
                            .reportId(report.getId())
                            .percentageChange(percentageChange)
                            .build();
                })
                .collect(Collectors.toList());

        // 응답 DTO 생성
        Report firstReport = recentReports.get(0);
        return TargetPriceTrendResponseDTO.builder()
                .stockName(firstReport.getStock().getStockName())
                .stockCode(firstReport.getStock().getStockCode())
                .targetPriceTrend(trendList)
                .reportCount(trendList.size())
                .build();
    }

    /**
     * 직전 대비 목표주가 변동률 계산
     * @param report 현재 리포트
     * @return 변동률 문자열 (예: "+8.89%", "-13.15%") 또는 null (이전 리포트가 없는 경우)
     */
    private String calculatePercentageChange(Report report) {
        // 이전 리포트가 없으면 null 반환
        if (report.getPrevReport() == null) {
            return null;
        }

        Integer currentPrice = report.getTargetPrice();
        Integer prevPrice = report.getPrevReport().getTargetPrice();

        // 현재 가격 또는 이전 가격이 없으면 null 반환
        if (currentPrice == null || prevPrice == null || prevPrice == 0) {
            return null;
        }

        // 변동률 계산: ((현재가격 - 이전가격) / 이전가격) * 100
        double changeRate = ((double) (currentPrice - prevPrice) / prevPrice) * 100;

        // 소수점 두자리로 포맷팅하고 부호 추가
        String formattedRate = String.format("%.2f", Math.abs(changeRate));

        if (changeRate > 0) {
            return "+" + formattedRate + "%";
        } else if (changeRate < 0) {
            return "-" + formattedRate + "%";
        } else {
            return "0.00%";
        }
    }
}
