package capstone25_2.aim.controller;

import capstone25_2.aim.domain.dto.stock.StockResponseDTO;
import capstone25_2.aim.domain.entity.Stock;
import capstone25_2.aim.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
