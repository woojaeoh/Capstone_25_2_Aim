package capstone25_2.aim.domain.dto.sector;

import capstone25_2.aim.domain.entity.HiddenOpinionLabel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorStockDTO {
    private Long stockId;
    private String stockName;
    private String stockCode;
    private Double upsidePotential;      // 상승 여력 (%)
    private Double buyRatio;             // 매수 비율 (%)
    private HiddenOpinionLabel latestOpinion;  // 최신 리포트 의견 (5단계)
}
