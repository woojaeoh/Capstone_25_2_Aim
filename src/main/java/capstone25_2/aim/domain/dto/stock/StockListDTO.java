package capstone25_2.aim.domain.dto.stock;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockListDTO {
    private Long id;                    // 종목 ID (상세 페이지 이동용)
    private String stockName;           // 종목명
    private String stockCode;           // 종목코드
    private String sector;              // 섹터
    private Double upsidePotential;     // 상승여력 (%)
    private Double buyRatio;            // 매수 비율 (%)
}
