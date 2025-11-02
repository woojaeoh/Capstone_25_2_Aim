package capstone25_2.aim.controller;

import capstone25_2.aim.domain.dto.stock.StockResponseDTO;
import capstone25_2.aim.domain.entity.Stock;
import capstone25_2.aim.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;
    // 전체 종목 조회
    @GetMapping
    public List<StockResponseDTO> getAllStocks() {
        return stockService.getAllStocks().stream()
                .map(StockResponseDTO::fromEntity)
                .toList();
    }

    // 종목 ID로 조회
    @GetMapping("/{stockId}")
    public StockResponseDTO getStockById(@PathVariable Long stockId) {
        Stock stock = stockService.getStockById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
        return StockResponseDTO.fromEntity(stock);
    }

    // 종목 코드로 조회 (예: /stocks/code/005930)
    @GetMapping("/code/{stockCode}")
    public StockResponseDTO getStockByCode(@PathVariable String stockCode) {
        Stock stock = stockService.getStockByCode(stockCode)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
        return StockResponseDTO.fromEntity(stock);
    }

    // 키워드로 종목 검색 (예: /stocks/search?keyword=삼성 또는 /stocks/search?keyword=005930)
    // 종목명 또는 종목코드에서 검색 (부분 일치)
    @GetMapping("/search")
    public List<StockResponseDTO> searchStocks(@RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) String sector) {
        List<Stock> stocks;

        if (keyword != null && !keyword.trim().isEmpty()) {
            // 키워드로 검색 (종목명 또는 종목코드)
            stocks = stockService.searchStocksByKeyword(keyword);
        } else if (sector != null && !sector.trim().isEmpty()) {
            // 업종으로 필터링
            stocks = stockService.filterStocksBySector(sector);
        } else {
            // 파라미터가 없으면 전체 목록 반환
            stocks = stockService.getAllStocks();
        }

        return stocks.stream()
                .map(StockResponseDTO::fromEntity)
                .toList();
    }
}
