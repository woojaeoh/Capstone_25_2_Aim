package capstone25_2.aim.domain.dto.stock;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyAverageTargetPriceDTO {
    private LocalDate date;                // 날짜
    private Double averageTargetPrice;     // 해당 날짜의 평균 목표주가
}
