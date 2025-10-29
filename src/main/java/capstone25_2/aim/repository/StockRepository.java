package capstone25_2.aim.repository;

import capstone25_2.aim.domain.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    //Top 100 종목 리스트나 단일 종목 조회용
    //findAll() : 기본 제공
    //findByStockCode() : 종목코드 기준 단일 조회

    Optional<Stock> findByStockCode(String stockCode);

    //검색 기능
    //키워드로 종목명 또는 종목코드 검색 (부분 일치, 대소문자 무시)
    List<Stock> findByStockNameContainingIgnoreCaseOrStockCodeContaining(String stockName, String stockCode);

    //종목명으로만 검색
    List<Stock> findByStockNameContainingIgnoreCase(String stockName);

    //업종으로 필터링
    List<Stock> findBySector(String sector);
}
