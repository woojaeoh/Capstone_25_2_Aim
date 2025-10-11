package capstone25_2.aim.domain.dto.analysis;

import capstone25_2.aim.domain.entity.Analysis;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisResponseDTO {

    private Long id;
    private String hiddenOpinion;
    private String keywords;
    private Long reportId;

    public static AnalysisResponseDTO fromEntity(Analysis analysis) {
        return AnalysisResponseDTO.builder()
                .id(analysis.getId())
                .hiddenOpinion(analysis.getHiddenOpinion())
                .keywords(analysis.getKeywords())
                .reportId(analysis.getReport().getId())
                .build();
    }
}
