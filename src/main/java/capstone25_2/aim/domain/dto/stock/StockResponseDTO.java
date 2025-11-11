package capstone25_2.aim.domain.dto.stock;

import capstone25_2.aim.domain.entity.Stock;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class StockResponseDTO {
    private Long id;
    private String stockCode;
    private String stockName;
    private String sector;

    // 애널리스트 종합 의견 (종목 상세 조회 시에만 포함)
    private StockConsensusDTO consensus;

    // 리스트 조회용 (종합 의견 없음)
    public static StockResponseDTO fromEntity(Stock stock) {
        return StockResponseDTO.builder()
                .id(stock.getId())
                .stockCode(stock.getStockCode())
                .stockName(stock.getStockName())
                .sector(stock.getSector())
                .build();
    }

    // 상세 조회용 (종합 의견 포함)
    public static StockResponseDTO fromEntityWithConsensus(Stock stock, StockConsensusDTO consensus) {
        return StockResponseDTO.builder()
                .id(stock.getId())
                .stockCode(stock.getStockCode())
                .stockName(stock.getStockName())
                .sector(stock.getSector())
                .consensus(consensus)
                .build();
    }
}
