package capstone25_2.aim.domain.dto.report;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetPriceTrendDTO {

    private LocalDate reportDate;
    private Integer targetPrice;
    private String analystName;
    private String firmName;
    private Long reportId;
    private String percentageChange;

}
