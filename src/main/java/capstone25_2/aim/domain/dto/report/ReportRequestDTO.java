package capstone25_2.aim.domain.dto.report;

import capstone25_2.aim.domain.entity.SurfaceOpinion;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportRequestDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnalystInfo {
        private String analystName;
        private String firmName;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReportInfo {
        private String stockCode; // 종목코드 (예: "005930")
        private String reportTitle;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate reportDate;

        private Integer targetPrice;
        private SurfaceOpinion surfaceOpinion; // BUY, HOLD, SELL
        private Double hiddenOpinion; // 0.0 ~ 1.0
    }

    private AnalystInfo analyst;
    private ReportInfo report;
}
