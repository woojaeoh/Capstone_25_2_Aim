package capstone25_2.aim.domain.dto.stock;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClosePriceTrendDTO {

    private LocalDate tradeDate;
    private Integer closePrice;

}
