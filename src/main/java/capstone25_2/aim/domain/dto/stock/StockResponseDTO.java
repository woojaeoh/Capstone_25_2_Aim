package capstone25_2.aim.domain.dto.stock;

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

    // 종가 변동 추이 (종목 상세 조회 시에만 포함)
    private List<ClosePriceTrendDTO> closePriceTrend;

    // 날짜별 애널리스트 평균 목표주가 리스트 (종목 상세 조회 시에만 포함)
    private List<DailyAverageTargetPriceDTO> dailyAverageTargetPrices;

    // 현재 기준 목표가 통계 (최대/평균/최소, 종목 상세 조회 시에만 포함)
    private TargetPriceStatsDTO targetPriceStats;

    // 해당 종목을 커버하는 애널리스트 목록 (종목 상세 조회 시에만 포함)
    private List<CoveringAnalystDTO> coveringAnalysts;

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

    // 상세 조회용 (모든 정보 포함)
    public static StockResponseDTO fromEntityWithFullDetails(
            Stock stock,
            StockConsensusDTO consensus,
            List<ClosePriceTrendDTO> closePriceTrend,
            List<DailyAverageTargetPriceDTO> dailyAverageTargetPrices,
            TargetPriceStatsDTO targetPriceStats,
            List<CoveringAnalystDTO> coveringAnalysts) {
        return StockResponseDTO.builder()
                .id(stock.getId())
                .stockCode(stock.getStockCode())
                .stockName(stock.getStockName())
                .sector(stock.getSector())
                .consensus(consensus)
                .closePriceTrend(closePriceTrend)
                .dailyAverageTargetPrices(dailyAverageTargetPrices)
                .targetPriceStats(targetPriceStats)
                .coveringAnalysts(coveringAnalysts)
                .build();
    }
}
