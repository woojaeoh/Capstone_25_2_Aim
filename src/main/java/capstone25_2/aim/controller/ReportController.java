package capstone25_2.aim.controller;

import capstone25_2.aim.domain.dto.report.ReportDetailDTO;
import capstone25_2.aim.domain.dto.report.ReportRequestDTO;
import capstone25_2.aim.domain.dto.report.ReportResponseDTO;
import capstone25_2.aim.domain.dto.report.TargetPriceTrendResponseDTO;
import capstone25_2.aim.domain.dto.stock.StockConsensusDTO;
import capstone25_2.aim.domain.entity.Report;
import capstone25_2.aim.domain.entity.SurfaceOpinion;
import capstone25_2.aim.service.ReportService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    // 리포트 상세 조회
    @GetMapping("/{reportId}")
    @Operation(summary = "리포트 상세 조회", description = "특정 리포트의 상세 정보를 조회합니다.")
    public ReportDetailDTO getReportDetail(
            @Parameter(description = "리포트 ID") @PathVariable Long reportId) {
        Report report = reportService.getReportById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        return ReportDetailDTO.fromEntity(report);
    }

    // AI 모델로부터 리포트 데이터 저장 (단일)
    @PostMapping("/from-ai")
    @Operation(
            summary = "AI 모델로부터 리포트 저장 (단일)",
            description = "AI 모델이 분석한 리포트 데이터를 저장합니다. " +
                    "Analyst 정보가 없으면 새로 생성하고, 있으면 기존 데이터를 사용합니다. " +
                    "stockCode로 Stock을 조회하여 연결합니다."
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

    // AI 모델로부터 리포트 데이터 배치 저장
    @PostMapping("/batch")
    @Operation(
            summary = "AI 모델로부터 리포트 배치 저장",
                    description = "Python에서 DataFrame을 JSON 배열로 보내면 여러 개의 리포트를 한번에 저장합니다. " +
                            "각 리포트마다 Analyst는 조회/생성하고, stockCode로 Stock을 찾아 연결합니다."
    )
            public ResponseEntity<List<ReportDetailDTO>> saveReportsFromAIBatch(
                    @RequestBody List<ReportRequestDTO> requestDTOList) {
                try {
                    List<Report> savedReports = reportService.saveReportsFromAIBatch(requestDTOList);
                    List<ReportDetailDTO> responseDTOList = savedReports.stream()
                    .map(ReportDetailDTO::fromEntity)
                    .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTOList);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // CSV 파일 업로드로 리포트 배치 저장
    @PostMapping(value = "/upload-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "CSV 파일로 리포트 배치 저장",
            description = "CSV 파일을 업로드하여 여러 개의 리포트를 한번에 저장합니다. " +
                    "CSV 컬럼: analystName, firmName, stockCode, reportTitle, reportDate, targetPrice, surfaceOpinion, hiddenOpinion"
    )
    public ResponseEntity<?> uploadCsvReports(
            @Parameter(description = "CSV 파일") @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 비어있습니다.");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".csv") && !filename.endsWith(".CSV"))) {
            return ResponseEntity.badRequest().body("CSV 파일만 업로드 가능합니다.");
        }

        try {
            List<ReportRequestDTO> requestDTOList = parseCsvToReportRequestDTOList(file);

            if (requestDTOList.isEmpty()) {
                return ResponseEntity.badRequest().body("CSV 파일에 데이터가 없습니다.");
            }

            List<Report> savedReports = reportService.saveReportsFromAIBatch(requestDTOList);
            List<ReportDetailDTO> responseDTOList = savedReports.stream()
                    .map(ReportDetailDTO::fromEntity)
                    .collect(java.util.stream.Collectors.toList());

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTOList);
        } catch (IOException | CsvException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("CSV 파일 파싱 오류: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("데이터 저장 오류: " + e.getMessage());
        }
    }

    /**
     * CSV 파일을 파싱하여 ReportRequestDTO 리스트로 변환
     * CSV 컬럼 순서: analystName, firmName, stockCode, reportTitle, reportDate, targetPrice, surfaceOpinion, hiddenOpinion
     */
    private List<ReportRequestDTO> parseCsvToReportRequestDTOList(MultipartFile file)
            throws IOException, CsvException {

        List<ReportRequestDTO> result = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> rows = reader.readAll();

            // 첫 번째 행은 헤더로 스킵
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);

                if (row.length < 8) {
                    continue; // 컬럼 수가 부족하면 스킵
                }

                // AnalystInfo 생성
                ReportRequestDTO.AnalystInfo analystInfo = ReportRequestDTO.AnalystInfo.builder()
                        .analystName(row[0].trim())
                        .firmName(row[1].trim())
                        .build();

                // ReportInfo 생성
                ReportRequestDTO.ReportInfo reportInfo = ReportRequestDTO.ReportInfo.builder()
                        .stockCode(row[2].trim())
                        .reportTitle(row[3].trim())
                        .reportDate(LocalDate.parse(row[4].trim(), dateFormatter))
                        .targetPrice(Integer.parseInt(row[5].trim()))
                        .surfaceOpinion(SurfaceOpinion.valueOf(row[6].trim().toUpperCase()))
                        .hiddenOpinion(Double.parseDouble(row[7].trim()))
                        .build();

                // ReportRequestDTO 생성
                ReportRequestDTO requestDTO = ReportRequestDTO.builder()
                        .analyst(analystInfo)
                        .report(reportInfo)
                        .build();

                result.add(requestDTO);
            }
        }

        return result;
    }
}
