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

    public static StockResponseDTO fromEntity(Stock stock) {
        return StockResponseDTO.builder()
                .id(stock.getId())
                .stockCode(stock.getStockCode())
                .stockName(stock.getStockName())
                .sector(stock.getSector())
                .build();
    }
}
