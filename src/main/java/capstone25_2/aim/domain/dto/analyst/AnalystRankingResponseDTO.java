package capstone25_2.aim.domain.dto.analyst;

import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AnalystRankingResponseDTO { //실제로 프론트에 전달할 랭킹 리스트
    private String criteria; //정렬 기준 명 -> ex) accuracyRate, returnRate
    private List<AnalystMetricsDTO> rankingList;
}
