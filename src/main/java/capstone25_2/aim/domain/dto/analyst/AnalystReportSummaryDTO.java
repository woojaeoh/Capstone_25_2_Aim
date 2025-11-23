package capstone25_2.aim.domain.dto.analyst;

import capstone25_2.aim.domain.entity.HiddenOpinionLabel;
import capstone25_2.aim.domain.entity.SurfaceOpinion;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalystReportSummaryDTO {
    private Long reportId;          // 리포트 ID
    private String reportTitle;     // 리포트 제목
    private LocalDate reportDate;   // 리포트 날짜
    private String stockName;       // 종목명
    private String stockCode;       // 종목코드
    private Integer targetPrice;    // 목표가
    private SurfaceOpinion surfaceOpinion;  // 표면 의견
    private Double hiddenOpinion;   // 숨겨진 의견 점수
    private HiddenOpinionLabel hiddenOpinionLabel;  // 숨겨진 의견 라벨
}
