package capstone25_2.aim.service;

import capstone25_2.aim.domain.dto.analyst.AnalystMetricsDTO;
import capstone25_2.aim.domain.dto.analyst.AnalystReportSummaryDTO;
import capstone25_2.aim.domain.dto.analyst.CoveredStockDTO;
import capstone25_2.aim.domain.entity.Analyst;
import capstone25_2.aim.domain.entity.AnalystMetrics;
import capstone25_2.aim.domain.entity.HiddenOpinionLabel;
import capstone25_2.aim.domain.entity.Report;
import capstone25_2.aim.repository.AnalystMetricsRepository;
import capstone25_2.aim.repository.AnalystRepository;
import capstone25_2.aim.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalystService {
    private final AnalystRepository analystRepository;
    private final ReportRepository reportRepository;
    private final AnalystMetricsRepository analystMetricsRepository;

    public List<Analyst> getAnalystsByFirm(String firmName) {
        return analystRepository.findByFirmName(firmName);
    }

    public Optional<Analyst> getAnalystById(Long id) {
        return analystRepository.findById(id);
    }

    // 애널리스트가 커버하는 종목 리스트 조회
    @Transactional(readOnly = true)
    public List<CoveredStockDTO> getCoveredStocks(Long analystId) {
        List<Report> reports = reportRepository.findByAnalystIdOrderByReportDateDesc(analystId);

        if (reports.isEmpty()) {
            return List.of();
        }

        // 종목별로 그룹핑하고 리포트 수 계산
        Map<Long, List<Report>> reportsByStock = reports.stream()
                .collect(Collectors.groupingBy(r -> r.getStock().getId()));

        return reportsByStock.entrySet().stream()
                .map(entry -> {
                    Report firstReport = entry.getValue().get(0);
                    return CoveredStockDTO.builder()
                            .stockId(firstReport.getStock().getId())
                            .stockName(firstReport.getStock().getStockName())
                            .stockCode(firstReport.getStock().getStockCode())
                            .sector(firstReport.getStock().getSector())
                            .reportCount(entry.getValue().size())
                            .build();
                })
                .sorted(Comparator.comparing(CoveredStockDTO::getReportCount).reversed())
                .collect(Collectors.toList());
    }

    // 애널리스트의 리포트 목록 조회 (최근 순)
    @Transactional(readOnly = true)
    public List<AnalystReportSummaryDTO> getAnalystReports(Long analystId) {
        List<Report> reports = reportRepository.findByAnalystIdOrderByReportDateDesc(analystId);

        return reports.stream()
                .map(report -> AnalystReportSummaryDTO.builder()
                        .reportId(report.getId())
                        .reportTitle(report.getReportTitle())
                        .reportDate(report.getReportDate().toLocalDate())
                        .stockName(report.getStock().getStockName())
                        .stockCode(report.getStock().getStockCode())
                        .targetPrice(report.getTargetPrice())
                        .surfaceOpinion(report.getSurfaceOpinion())
                        .hiddenOpinion(report.getHiddenOpinion())
                        .hiddenOpinionLabel(HiddenOpinionLabel.fromScore(report.getHiddenOpinion()))
                        .build())
                .collect(Collectors.toList());
    }

    // 애널리스트 지표 조회
    @Transactional(readOnly = true)
    public AnalystMetricsDTO getAnalystMetrics(Long analystId) {
        Optional<AnalystMetrics> metrics = analystMetricsRepository.findByAnalystId(analystId);
        return metrics.map(AnalystMetricsDTO::fromEntity).orElse(null);
    }

}
