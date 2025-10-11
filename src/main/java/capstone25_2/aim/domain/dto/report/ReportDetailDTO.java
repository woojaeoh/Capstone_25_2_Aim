package capstone25_2.aim.domain.dto.report;

import capstone25_2.aim.domain.entity.Analysis;
import capstone25_2.aim.domain.entity.Report;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor @Builder
public class ReportDetailDTO {

    private Long reportId;
    private String reportTitle;
    private LocalDate reportDate;
    private String surfaceOpinion;
    private Integer targetPrice;
    private Integer prevTargetDiff;

    private String analystName;
    private String firmName;

  //  private AnalysisSummaryDTO analysis; // 포함

    public static ReportDetailDTO of(Report report, Analysis analysis) {
        return ReportDetailDTO.builder()
                .reportId(report.getId())
                .reportTitle(report.getReportTitle())
                .reportDate(LocalDate.from(report.getReportDate()))
                .surfaceOpinion(report.getSurfaceOpinion().name())
                .targetPrice(report.getTargetPrice())
                .prevTargetDiff(report.getPrevTargetDiff())
                .analystName(report.getAnalyst().getAnalystName())
                .firmName(report.getAnalyst().getFirmName())
      //          .analysis(AnalysisSummaryDTO.fromEntity(analysis))
                .build();
    }




}
