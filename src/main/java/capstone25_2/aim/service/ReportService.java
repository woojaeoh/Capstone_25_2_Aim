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
                .map(report -> TargetPriceTrendDTO.builder()
                        .reportDate(LocalDate.from(report.getReportDate()))
                        .targetPrice(report.getTargetPrice())
                        .analystName(report.getAnalyst().getAnalystName())
                        .firmName(report.getAnalyst().getFirmName())
                        .reportId(report.getId())
                        .build())
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
}
