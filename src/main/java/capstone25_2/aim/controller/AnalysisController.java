package capstone25_2.aim.controller;

import capstone25_2.aim.domain.dto.analysis.AnalysisResponseDTO;
import capstone25_2.aim.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
public class AnalysisController {
    private final AnalysisService analysisService;

    // 리포트 ID로 분석 결과 조회
    @GetMapping("/report/{reportId}")
    public AnalysisResponseDTO getAnalysisByReport(@PathVariable Long reportId) {
        return analysisService.getAnalysisByReportId(reportId)
                .map(AnalysisResponseDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Analysis not found"));
    }
}
