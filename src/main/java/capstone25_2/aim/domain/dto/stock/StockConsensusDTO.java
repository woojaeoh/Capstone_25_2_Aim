package capstone25_2.aim.domain.dto.stock;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockConsensusDTO {
    private Long stockId;
    private String stockName;
    private String stockCode;

    // surfaceOpinion 통계 (BUY, HOLD, SELL 기준)
    private Integer buyCount;              // 매수 의견 수
    private Integer holdCount;             // 보유 의견 수
    private Integer sellCount;             // 매도 의견 수

    // 목표가 통계
    private Double averageTargetPrice;     // 평균 목표가 (의견 변화 이후 리포트 기준)

    // 상승 여력
    private Double upsidePotential;        // 상승 여력 (%) = (평균 목표가 - 현재 종가) / 현재 종가 * 100

    private Integer totalReports;          // 집계된 리포트 개수 (의견 변화 이후 리포트)
    private Integer totalAnalysts;         // 총 애널리스트 수
}
