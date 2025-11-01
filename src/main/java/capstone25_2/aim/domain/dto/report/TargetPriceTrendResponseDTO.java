package capstone25_2.aim.domain.dto.report;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetPriceTrendResponseDTO {

    private String stockName;
    private String stockCode;
    private List<TargetPriceTrendDTO> targetPriceTrend;
    private Integer reportCount;

}
