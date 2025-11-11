package capstone25_2.aim.service;

import capstone25_2.aim.domain.dto.stock.StockConsensusDTO;
import capstone25_2.aim.domain.entity.Stock;
import capstone25_2.aim.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final ReportService reportService;

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    //code는 크롤링 데이터용 식별자 -> 크롤링 시 코드 기준으로 리포트 검색.
    public Optional<Stock> getStockByCode(String stockCode){
        return stockRepository.findByStockCode(stockCode);
    }


    //id는 내부 식별자
    public Optional<Stock> getStockById(Long id){
        return stockRepository.findById(id);
    }

    // 종목 ID로 조회 + 종합 의견 포함
    public Optional<StockConsensusDTO> getStockConsensusById(Long id){
        try {
            return Optional.of(reportService.getStockConsensus(id));
        } catch (RuntimeException e) {
            // 리포트가 없거나 에러 발생 시 null 반환
            return Optional.empty();
        }
    }

    //키워드로 종목 검색 (종목명 또는 종목코드에서 검색)
    public List<Stock> searchStocksByKeyword(String keyword){
        if(keyword == null || keyword.trim().isEmpty()){
            return List.of(); //빈 리스트 반환
        }
        return stockRepository.findByStockNameContainingIgnoreCaseOrStockCodeContaining(keyword, keyword);
    }

    //종목명으로만 검색
    public List<Stock> searchStocksByName(String name){
        if(name == null || name.trim().isEmpty()){
            return List.of();
        }
        return stockRepository.findByStockNameContainingIgnoreCase(name);
    }

    //업종으로 필터링
    public List<Stock> filterStocksBySector(String sector){
        if(sector == null || sector.trim().isEmpty()){
            return List.of();
        }
        return stockRepository.findBySector(sector);
    }
}
