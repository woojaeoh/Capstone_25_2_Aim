package capstone25_2.aim.controller;

import capstone25_2.aim.domain.dto.report.ReportDetailDTO;
import capstone25_2.aim.domain.dto.report.ReportRequestDTO;
import capstone25_2.aim.domain.dto.report.ReportResponseDTO;
import capstone25_2.aim.domain.dto.report.TargetPriceTrendResponseDTO;
import capstone25_2.aim.domain.entity.Report;
import capstone25_2.aim.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "Report", description = "리포트 관련 API")
public class ReportController {

    private final ReportService reportService;

    // 특정 애널리스트의 최신 5년 리포트 리스트 조회
    @GetMapping("/analyst/{analystId}")
    @Operation(summary = "애널리스트별 최신 5년 리포트 조회", description = "특정 애널리스트의 최신 5년간 리포트 리스트를 조회합니다.")
    public List<ReportResponseDTO> getReportsByAnalystId(
            @Parameter(description = "애널리스트 ID") @PathVariable Long analystId) {
        return reportService.getRecentReportsByAnalystId(analystId).stream()
                .map(ReportResponseDTO::fromEntity)
                .toList();
    }

    // 특정 종목의 최신 5년 리포트 리스트 조회
    @GetMapping("/stock/{stockId}")
    @Operation(summary = "종목별 최신 5년 리포트 조회", description = "특정 종목의 최신 5년간 리포트 리스트를 조회합니다.")
    public List<ReportResponseDTO> getReportsByStockId(
            @Parameter(description = "종목 ID") @PathVariable Long stockId) {
        return reportService.getRecentReportsByStockId(stockId).stream()
                .map(ReportResponseDTO::fromEntity)
                .toList();
    }

    // 종목별 목표가 변동 추이 데이터 조회
    @GetMapping("/stock/{stockId}/target-price-trend")
    @Operation(summary = "종목별 목표가 변동 추이 조회", description = "특정 종목의 최신 5년간 목표가 변동 추이를 그래프 데이터로 조회합니다. 직전 대비 목표가 변동률(+8.89%, -13.15% 형식)을 포함합니다.")
    public TargetPriceTrendResponseDTO getTargetPriceTrend(
            @Parameter(description = "종목 ID") @PathVariable Long stockId) {
        return reportService.getTargetPriceTrend(stockId);
    }

    // 리포트 상세 조회
    @GetMapping("/{reportId}")
    @Operation(summary = "리포트 상세 조회", description = "특정 리포트의 상세 정보를 조회합니다.")
    public ReportDetailDTO getReportDetail(
            @Parameter(description = "리포트 ID") @PathVariable Long reportId) {
        Report report = reportService.getReportById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        return ReportDetailDTO.fromEntity(report);
    }

    // AI 모델로부터 리포트 데이터 저장
    @PostMapping("/from-ai")
    @Operation(
            summary = "AI 모델로부터 리포트 저장",
            description = "AI 모델이 분석한 리포트 데이터를 저장합니다. " +
                    "Analyst 정보가 없으면 새로 생성하고, 있으면 기존 데이터를 사용합니다."
    )
    public ResponseEntity<ReportDetailDTO> saveReportFromAI(@RequestBody ReportRequestDTO requestDTO) {
        try {
            Report savedReport = reportService.saveReportFromAI(requestDTO);
            ReportDetailDTO responseDTO = ReportDetailDTO.fromEntity(savedReport);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
