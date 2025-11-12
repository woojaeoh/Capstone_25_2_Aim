package capstone25_2.aim.domain.dto.stock;

import capstone25_2.aim.domain.dto.report.TargetPriceTrendResponseDTO;
import capstone25_2.aim.domain.entity.Stock;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class StockResponseDTO {
    private Long id;
    private String stockCode;
    private String stockName;
    private String sector;

    // 애널리스트 종합 의견 (종목 상세 조회 시에만 포함)
    private StockConsensusDTO consensus;

    // 목표가 변동 추이 (종목 상세 조회 시에만 포함)
    private TargetPriceTrendResponseDTO targetPriceTrend;

    // 종가 변동 추이 (종목 상세 조회 시에만 포함)
    private List<ClosePriceTrendDTO> closePriceTrend;

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

    // 상세 조회용 (모든 정보 포함: consensus, targetPriceTrend, closePriceTrend)
    public static StockResponseDTO fromEntityWithFullDetails(
            Stock stock,
            StockConsensusDTO consensus,
            TargetPriceTrendResponseDTO targetPriceTrend,
            List<ClosePriceTrendDTO> closePriceTrend) {
        return StockResponseDTO.builder()
                .id(stock.getId())
                .stockCode(stock.getStockCode())
                .stockName(stock.getStockName())
                .sector(stock.getSector())
                .consensus(consensus)
                .targetPriceTrend(targetPriceTrend)
                .closePriceTrend(closePriceTrend)
                .build();
    }
}
