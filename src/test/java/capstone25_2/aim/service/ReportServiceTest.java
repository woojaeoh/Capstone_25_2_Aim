package capstone25_2.aim.service;

import capstone25_2.aim.domain.dto.stock.StockConsensusDTO;
import capstone25_2.aim.domain.entity.Analyst;
import capstone25_2.aim.domain.entity.Report;
import capstone25_2.aim.domain.entity.Stock;
import capstone25_2.aim.domain.entity.SurfaceOpinion;
import capstone25_2.aim.repository.AnalystRepository;
import capstone25_2.aim.repository.ReportRepository;
import capstone25_2.aim.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportService 단위 테스트")
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private AnalystRepository analystRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private AnalystMetricsService analystMetricsService;

    @InjectMocks
    private ReportService reportService;

    private Stock testStock;
    private Analyst analyst1;
    private Analyst analyst2;
    private Analyst analyst3;

    @BeforeEach
    void setUp() {
        // 테스트용 Stock 데이터
        testStock = new Stock();
        testStock.setId(1L);
        testStock.setStockName("삼성전자");
        testStock.setStockCode("005930");

        // 테스트용 Analyst 데이터
        analyst1 = new Analyst();
        analyst1.setId(1L);
        analyst1.setAnalystName("김철수");
        analyst1.setFirmName("A증권");

        analyst2 = new Analyst();
        analyst2.setId(2L);
        analyst2.setAnalystName("이영희");
        analyst2.setFirmName("B증권");

        analyst3 = new Analyst();
        analyst3.setId(3L);
        analyst3.setAnalystName("박민수");
        analyst3.setFirmName("C증권");
    }

    @Test
    @DisplayName("종목별 surfaceOpinion 종합 의견 조회 - 정상 케이스")
    void getStockConsensus_WithValidReports_ShouldReturnConsensus() {
        // given
        Long stockId = 1L;
        LocalDateTime now = LocalDateTime.now();

        // 애널리스트1: 오래된 리포트(SELL), 최신 리포트(BUY) -> BUY만 집계
        Report oldReport1 = createReport(1L, analyst1, testStock, now.minusYears(2), SurfaceOpinion.SELL);
        Report latestReport1 = createReport(2L, analyst1, testStock, now.minusMonths(1), SurfaceOpinion.BUY);

        // 애널리스트2: 최신 리포트(BUY) -> BUY만 집계
        Report latestReport2 = createReport(3L, analyst2, testStock, now.minusMonths(2), SurfaceOpinion.BUY);

        // 애널리스트3: 최신 리포트(HOLD) -> HOLD만 집계
        Report latestReport3 = createReport(4L, analyst3, testStock, now.minusMonths(3), SurfaceOpinion.HOLD);

        List<Report> allReports = List.of(oldReport1, latestReport1, latestReport2, latestReport3);

        given(stockRepository.findById(stockId)).willReturn(Optional.of(testStock));
        given(reportRepository.findByStockIdAndReportDateAfterOrderByReportDateDesc(
                eq(stockId), any(LocalDateTime.class)))
                .willReturn(allReports);

        // when
        StockConsensusDTO result = reportService.getStockConsensus(stockId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStockId()).isEqualTo(1L);
        assertThat(result.getStockName()).isEqualTo("삼성전자");
        assertThat(result.getStockCode()).isEqualTo("005930");

        // 집계 대상: BUY, BUY, HOLD
        assertThat(result.getTotalAnalysts()).isEqualTo(3);
        assertThat(result.getTotalReports()).isEqualTo(3);

        // BUY: 2개
        assertThat(result.getBuyCount()).isEqualTo(2);

        // HOLD: 1개
        assertThat(result.getHoldCount()).isEqualTo(1);

        // SELL: 0개
        assertThat(result.getSellCount()).isEqualTo(0);

        verify(stockRepository, times(1)).findById(stockId);
        verify(reportRepository, times(1))
                .findByStockIdAndReportDateAfterOrderByReportDateDesc(eq(stockId), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("종목별 surfaceOpinion 종합 의견 조회 - 모두 BUY")
    void getStockConsensus_WithAllBuy_ShouldReturnAllBuy() {
        // given
        Long stockId = 1L;
        LocalDateTime now = LocalDateTime.now();

        Report report1 = createReport(1L, analyst1, testStock, now.minusMonths(1), SurfaceOpinion.BUY);
        Report report2 = createReport(2L, analyst2, testStock, now.minusMonths(2), SurfaceOpinion.BUY);
        Report report3 = createReport(3L, analyst3, testStock, now.minusMonths(3), SurfaceOpinion.BUY);

        List<Report> allReports = List.of(report1, report2, report3);

        given(stockRepository.findById(stockId)).willReturn(Optional.of(testStock));
        given(reportRepository.findByStockIdAndReportDateAfterOrderByReportDateDesc(
                eq(stockId), any(LocalDateTime.class)))
                .willReturn(allReports);

        // when
        StockConsensusDTO result = reportService.getStockConsensus(stockId);

        // then
        assertThat(result.getBuyCount()).isEqualTo(3);
        assertThat(result.getHoldCount()).isEqualTo(0);
        assertThat(result.getSellCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("종목별 surfaceOpinion 종합 의견 조회 - 모두 SELL")
    void getStockConsensus_WithAllSell_ShouldReturnAllSell() {
        // given
        Long stockId = 1L;
        LocalDateTime now = LocalDateTime.now();

        Report report1 = createReport(1L, analyst1, testStock, now.minusMonths(1), SurfaceOpinion.SELL);
        Report report2 = createReport(2L, analyst2, testStock, now.minusMonths(2), SurfaceOpinion.SELL);
        Report report3 = createReport(3L, analyst3, testStock, now.minusMonths(3), SurfaceOpinion.SELL);

        List<Report> allReports = List.of(report1, report2, report3);

        given(stockRepository.findById(stockId)).willReturn(Optional.of(testStock));
        given(reportRepository.findByStockIdAndReportDateAfterOrderByReportDateDesc(
                eq(stockId), any(LocalDateTime.class)))
                .willReturn(allReports);

        // when
        StockConsensusDTO result = reportService.getStockConsensus(stockId);

        // then
        assertThat(result.getBuyCount()).isEqualTo(0);
        assertThat(result.getHoldCount()).isEqualTo(0);
        assertThat(result.getSellCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("종목별 surfaceOpinion 종합 의견 조회 - 리포트가 없는 경우 예외 발생")
    void getStockConsensus_WithNoReports_ShouldThrowException() {
        // given
        Long stockId = 1L;
        given(stockRepository.findById(stockId)).willReturn(Optional.of(testStock));
        given(reportRepository.findByStockIdAndReportDateAfterOrderByReportDateDesc(
                eq(stockId), any(LocalDateTime.class)))
                .willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> reportService.getStockConsensus(stockId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No reports found for stock");

        verify(stockRepository, times(1)).findById(stockId);
    }

    @Test
    @DisplayName("종목별 surfaceOpinion 종합 의견 조회 - 종목이 없는 경우 예외 발생")
    void getStockConsensus_WithInvalidStockId_ShouldThrowException() {
        // given
        Long invalidStockId = 999L;
        given(stockRepository.findById(invalidStockId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reportService.getStockConsensus(invalidStockId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Stock not found");

        verify(stockRepository, times(1)).findById(invalidStockId);
    }

    @Test
    @DisplayName("종목별 surfaceOpinion 종합 의견 조회 - surfaceOpinion이 null인 리포트는 제외")
    void getStockConsensus_WithNullSurfaceOpinion_ShouldFilterOut() {
        // given
        Long stockId = 1L;
        LocalDateTime now = LocalDateTime.now();

        Report report1 = createReport(1L, analyst1, testStock, now.minusMonths(1), SurfaceOpinion.BUY);
        Report report2 = createReport(2L, analyst2, testStock, now.minusMonths(2), null); // null
        Report report3 = createReport(3L, analyst3, testStock, now.minusMonths(3), SurfaceOpinion.HOLD);

        List<Report> allReports = List.of(report1, report2, report3);

        given(stockRepository.findById(stockId)).willReturn(Optional.of(testStock));
        given(reportRepository.findByStockIdAndReportDateAfterOrderByReportDateDesc(
                eq(stockId), any(LocalDateTime.class)))
                .willReturn(allReports);

        // when
        StockConsensusDTO result = reportService.getStockConsensus(stockId);

        // then
        // null 제외하고 BUY, HOLD만 집계
        assertThat(result.getTotalReports()).isEqualTo(2);
        assertThat(result.getTotalAnalysts()).isEqualTo(3); // 애널리스트는 3명
        assertThat(result.getBuyCount()).isEqualTo(1);
        assertThat(result.getHoldCount()).isEqualTo(1);
        assertThat(result.getSellCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("종목별 surfaceOpinion 종합 의견 조회 - 같은 애널리스트의 여러 리포트 중 최신만 선택")
    void getStockConsensus_WithMultipleReportsFromSameAnalyst_ShouldSelectLatest() {
        // given
        Long stockId = 1L;
        LocalDateTime now = LocalDateTime.now();

        // 애널리스트1: 3개의 리포트
        Report old1 = createReport(1L, analyst1, testStock, now.minusYears(2), SurfaceOpinion.SELL);
        Report mid1 = createReport(2L, analyst1, testStock, now.minusYears(1), SurfaceOpinion.HOLD);
        Report latest1 = createReport(3L, analyst1, testStock, now.minusMonths(1), SurfaceOpinion.BUY); // 최신

        // 애널리스트2: 2개의 리포트
        Report old2 = createReport(4L, analyst2, testStock, now.minusMonths(6), SurfaceOpinion.SELL);
        Report latest2 = createReport(5L, analyst2, testStock, now.minusMonths(2), SurfaceOpinion.BUY); // 최신

        List<Report> allReports = List.of(old1, mid1, latest1, old2, latest2);

        given(stockRepository.findById(stockId)).willReturn(Optional.of(testStock));
        given(reportRepository.findByStockIdAndReportDateAfterOrderByReportDateDesc(
                eq(stockId), any(LocalDateTime.class)))
                .willReturn(allReports);

        // when
        StockConsensusDTO result = reportService.getStockConsensus(stockId);

        // then
        // 최신 리포트만 집계: BUY (애널리스트1), BUY (애널리스트2)
        assertThat(result.getTotalAnalysts()).isEqualTo(2);
        assertThat(result.getTotalReports()).isEqualTo(2);
        assertThat(result.getBuyCount()).isEqualTo(2);
        assertThat(result.getHoldCount()).isEqualTo(0);
        assertThat(result.getSellCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("종목별 종합 의견 조회 - 평균 목표가 계산")
    void getStockConsensus_ShouldCalculateAverageTargetPrice() {
        // given
        Long stockId = 1L;
        LocalDateTime now = LocalDateTime.now();

        // 목표가: 90000, 85000, 70000
        Report report1 = createReportWithTargetPrice(1L, analyst1, testStock, now.minusMonths(1),
                SurfaceOpinion.BUY, 90000);
        Report report2 = createReportWithTargetPrice(2L, analyst2, testStock, now.minusMonths(2),
                SurfaceOpinion.BUY, 85000);
        Report report3 = createReportWithTargetPrice(3L, analyst3, testStock, now.minusMonths(3),
                SurfaceOpinion.HOLD, 70000);

        List<Report> allReports = List.of(report1, report2, report3);

        given(stockRepository.findById(stockId)).willReturn(Optional.of(testStock));
        given(reportRepository.findByStockIdAndReportDateAfterOrderByReportDateDesc(
                eq(stockId), any(LocalDateTime.class)))
                .willReturn(allReports);

        // when
        StockConsensusDTO result = reportService.getStockConsensus(stockId);

        // then
        // 평균 목표가: (90000 + 85000 + 70000) / 3 = 81666.67
        assertThat(result.getAverageTargetPrice()).isCloseTo(81666.67, org.assertj.core.data.Offset.offset(0.1));
    }

    // 헬퍼 메서드: 테스트용 Report 생성
    private Report createReport(Long id, Analyst analyst, Stock stock, LocalDateTime reportDate, SurfaceOpinion surfaceOpinion) {
        Report report = new Report();
        report.setId(id);
        report.setAnalyst(analyst);
        report.setStock(stock);
        report.setReportDate(reportDate);
        report.setSurfaceOpinion(surfaceOpinion);
        report.setTargetPrice(80000); // 기본 목표가
        return report;
    }

    // 헬퍼 메서드: 목표가를 지정할 수 있는 Report 생성
    private Report createReportWithTargetPrice(Long id, Analyst analyst, Stock stock, LocalDateTime reportDate,
                                                SurfaceOpinion surfaceOpinion, Integer targetPrice) {
        Report report = new Report();
        report.setId(id);
        report.setAnalyst(analyst);
        report.setStock(stock);
        report.setReportDate(reportDate);
        report.setSurfaceOpinion(surfaceOpinion);
        report.setTargetPrice(targetPrice);
        return report;
    }
}
