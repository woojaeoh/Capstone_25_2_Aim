package capstone25_2.aim.domain.dto.sector;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectorListDTO {
    private String sectorName;
    private Integer stockCount;           // 섹터 내 종목 수
    private Double buyRatio;              // 섹터 전체 매수 비율 (%) - 소수점 첫째자리까지

    // 섹터 종합 의견 분포 (각 종목의 최신 리포트 기준)
    private Integer strongBuyCount;       // STRONG_BUY 종목 수
    private Integer buyCount;             // BUY 종목 수
    private Integer holdCount;            // HOLD 종목 수
    private Integer sellCount;            // SELL 종목 수
    private Integer strongSellCount;      // STRONG_SELL 종목 수
}
