package capstone25_2.aim.service;

import capstone25_2.aim.domain.dto.analyst.AnalystMetricsDTO;
import capstone25_2.aim.domain.dto.analyst.AnalystRankingResponseDTO;
import capstone25_2.aim.domain.entity.*;
import capstone25_2.aim.repository.AnalystMetricsRepository;
import capstone25_2.aim.repository.AnalystRepository;
import capstone25_2.aim.repository.ClosePriceRepository;
import capstone25_2.aim.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnalystMetricsService 단위 테스트")
class AnalystMetricsServiceTest {

    @Mock
    private AnalystMetricsRepository metricsRepository;

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private AnalystRepository analystRepository;

    @Mock
    private ClosePriceRepository closePriceRepository;

    @InjectMocks
    private AnalystMetricsService analystMetricsService;

    private Analyst testAnalyst1;
    private Analyst testAnalyst2;
    private Analyst testAnalyst3;
    private AnalystMetrics testMetrics1;
    private AnalystMetrics testMetrics2;
    private AnalystMetrics testMetrics3;
    private Stock testStock;

    @BeforeEach
    void setUp() {
        // 테스트용 Analyst 데이터 생성
        testAnalyst1 = new Analyst();
        testAnalyst1.setId(1L);
        testAnalyst1.setAnalystName("김철수");
        testAnalyst1.setFirmName("A증권");

        testAnalyst2 = new Analyst();
        testAnalyst2.setId(2L);
        testAnalyst2.setAnalystName("이영희");
        testAnalyst2.setFirmName("B증권");

        testAnalyst3 = new Analyst();
        testAnalyst3.setId(3L);
        testAnalyst3.setAnalystName("박민수");
        testAnalyst3.setFirmName("C증권");

        // 테스트용 AnalystMetrics 데이터 생성
        testMetrics1 = new AnalystMetrics();
        testMetrics1.setId(1L);
        testMetrics1.setAnalyst(testAnalyst1);
        testMetrics1.setAccuracyRate(85.0);
        testMetrics1.setReturnRate(15.5);
        testMetrics1.setTargetDiffRate(5.2);
        testMetrics1.setAvgReturnDiff(2.3);
        testMetrics1.setAvgTargetDiff(1.1);

        testMetrics2 = new AnalystMetrics();
        testMetrics2.setId(2L);
        testMetrics2.setAnalyst(testAnalyst2);
        testMetrics2.setAccuracyRate(90.0);
        testMetrics2.setReturnRate(12.3);
        testMetrics2.setTargetDiffRate(3.8);
        testMetrics2.setAvgReturnDiff(1.5);
        testMetrics2.setAvgTargetDiff(0.9);

        testMetrics3 = new AnalystMetrics();
        testMetrics3.setId(3L);
        testMetrics3.setAnalyst(testAnalyst3);
        testMetrics3.setAccuracyRate(80.0);
        testMetrics3.setReturnRate(18.7);
        testMetrics3.setTargetDiffRate(6.1);
        testMetrics3.setAvgReturnDiff(3.2);
        testMetrics3.setAvgTargetDiff(1.8);

        // 테스트용 Stock 데이터 생성
        testStock = new Stock();
        testStock.setId(1L);
        testStock.setStockName("삼성전자");
        testStock.setStockCode("005930");
    }

    @Test
    @DisplayName("정확도 기준 애널리스트 랭킹 조회 - 기본 정렬")
    void getRankedAnalysts_WithAccuracyRate_ShouldReturnSortedList() {
        // given
        List<AnalystMetrics> metricsList = List.of(testMetrics1, testMetrics2, testMetrics3);
        given(metricsRepository.findAll()).willReturn(metricsList);

        // when
        AnalystRankingResponseDTO result = analystMetricsService.getRankedAnalysts("accuracyRate");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCriteria()).isEqualTo("accuracyRate");
        assertThat(result.getRankingList()).hasSize(3);

        // 정확도 내림차순 정렬 확인: 90.0 > 85.0 > 80.0
        assertThat(result.getRankingList().get(0).getAccuracyRate()).isEqualTo(90.0);
        assertThat(result.getRankingList().get(1).getAccuracyRate()).isEqualTo(85.0);
        assertThat(result.getRankingList().get(2).getAccuracyRate()).isEqualTo(80.0);

        verify(metricsRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("수익률 기준 애널리스트 랭킹 조회")
    void getRankedAnalysts_WithReturnRate_ShouldReturnSortedByReturn() {
        // given
        List<AnalystMetrics> metricsList = List.of(testMetrics1, testMetrics2, testMetrics3);
        given(metricsRepository.findAll()).willReturn(metricsList);

        // when
        AnalystRankingResponseDTO result = analystMetricsService.getRankedAnalysts("returnRate");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCriteria()).isEqualTo("returnRate");
        assertThat(result.getRankingList()).hasSize(3);

        // 수익률 내림차순 정렬 확인: 18.7 > 15.5 > 12.3
        assertThat(result.getRankingList().get(0).getReturnRate()).isEqualTo(18.7);
        assertThat(result.getRankingList().get(1).getReturnRate()).isEqualTo(15.5);
        assertThat(result.getRankingList().get(2).getReturnRate()).isEqualTo(12.3);

        verify(metricsRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("목표가 오차율 기준 애널리스트 랭킹 조회")
    void getRankedAnalysts_WithTargetDiffRate_ShouldReturnSortedByTargetDiff() {
        // given
        List<AnalystMetrics> metricsList = List.of(testMetrics1, testMetrics2, testMetrics3);
        given(metricsRepository.findAll()).willReturn(metricsList);

        // when
        AnalystRankingResponseDTO result = analystMetricsService.getRankedAnalysts("targetDiffRate");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCriteria()).isEqualTo("targetDiffRate");
        assertThat(result.getRankingList()).hasSize(3);

        // 목표가 오차율 오름차순 정렬 확인: 3.8 < 5.2 < 6.1
        assertThat(result.getRankingList().get(0).getTargetDiffRate()).isEqualTo(3.8);
        assertThat(result.getRankingList().get(1).getTargetDiffRate()).isEqualTo(5.2);
        assertThat(result.getRankingList().get(2).getTargetDiffRate()).isEqualTo(6.1);

        verify(metricsRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("특정 종목 기준 애널리스트 랭킹 조회")
    void getRankedAnalystsByStock_ShouldReturnFilteredAndSortedList() {
        // given
        Long stockId = 1L;

        Report report1 = new Report();
        report1.setId(1L);
        report1.setAnalyst(testAnalyst1);
        report1.setStock(testStock);

        Report report2 = new Report();
        report2.setId(2L);
        report2.setAnalyst(testAnalyst2);
        report2.setStock(testStock);

        List<Report> reports = List.of(report1, report2);
        List<AnalystMetrics> allMetrics = List.of(testMetrics1, testMetrics2, testMetrics3);

        given(reportRepository.findByStockId(stockId)).willReturn(reports);
        given(metricsRepository.findAll()).willReturn(allMetrics);

        // when
        AnalystRankingResponseDTO result = analystMetricsService.getRankedAnalystsByStock(stockId, "accuracyRate");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCriteria()).isEqualTo("accuracyRate");
        assertThat(result.getRankingList()).hasSize(2); // analyst1, analyst2만 포함

        // 해당 종목 관련 애널리스트만 포함되었는지 확인
        List<Long> analystIds = result.getRankingList().stream()
                .map(AnalystMetricsDTO::getAnalystId)
                .toList();
        assertThat(analystIds).containsExactlyInAnyOrder(1L, 2L);
        assertThat(analystIds).doesNotContain(3L);

        verify(reportRepository, times(1)).findByStockId(stockId);
        verify(metricsRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("특정 종목의 리포트가 없는 경우 빈 리스트 반환")
    void getRankedAnalystsByStock_WithNoReports_ShouldReturnEmptyList() {
        // given
        Long stockId = 999L;
        given(reportRepository.findByStockId(stockId)).willReturn(List.of());
        given(metricsRepository.findAll()).willReturn(List.of(testMetrics1, testMetrics2, testMetrics3));

        // when
        AnalystRankingResponseDTO result = analystMetricsService.getRankedAnalystsByStock(stockId, "accuracyRate");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRankingList()).isEmpty();

        verify(reportRepository, times(1)).findByStockId(stockId);
    }

    @Test
    @DisplayName("정확도 계산 - 리포트가 없는 경우 저장하지 않음")
    void calculateAndSaveAccuracyRate_WithNoReports_ShouldNotSave() {
        // given
        Long analystId = 1L;
        LocalDateTime fiveYearsAgo = LocalDateTime.now().minusYears(5);

        given(reportRepository.findByAnalystIdAndReportDateAfterOrderByReportDateDesc(
                eq(analystId), any(LocalDateTime.class)))
                .willReturn(List.of());

        // when
        analystMetricsService.calculateAndSaveAccuracyRate(analystId);

        // then
        verify(reportRepository, times(1))
                .findByAnalystIdAndReportDateAfterOrderByReportDateDesc(eq(analystId), any(LocalDateTime.class));
        verify(metricsRepository, never()).save(any(AnalystMetrics.class));
    }

    @Test
    @DisplayName("정확도 계산 - 평가 가능한 리포트가 있는 경우 메트릭 저장")
    void calculateAndSaveAccuracyRate_WithValidReports_ShouldSaveMetrics() {
        // given
        Long analystId = 1L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reportDate = now.minusYears(2);

        // 리포트 생성
        Report testReport = new Report();
        testReport.setId(1L);
        testReport.setAnalyst(testAnalyst1);
        testReport.setStock(testStock);
        testReport.setReportDate(reportDate);
        testReport.setTargetPrice(100000);
        testReport.setHiddenOpinion(0.7); // 상승 예측
        testReport.setSurfaceOpinion(SurfaceOpinion.BUY);

        List<Report> reports = List.of(testReport);

        // 리포트 발행 시점 종가
        ClosePrice reportDatePrice = new ClosePrice();
        reportDatePrice.setClosePrice(80000);
        reportDatePrice.setTradeDate(reportDate.toLocalDate());

        // 1년 후 종가
        ClosePrice oneYearLaterPrice = new ClosePrice();
        oneYearLaterPrice.setClosePrice(110000); // 목표가 달성
        oneYearLaterPrice.setTradeDate(reportDate.plusYears(1).toLocalDate());

        given(reportRepository.findByAnalystIdAndReportDateAfterOrderByReportDateDesc(
                eq(analystId), any(LocalDateTime.class)))
                .willReturn(reports);

        given(closePriceRepository.findFirstByStockIdAndTradeDateGreaterThanEqualOrderByTradeDateAsc(
                eq(testStock.getId()), eq(reportDate.toLocalDate())))
                .willReturn(Optional.of(reportDatePrice));

        given(closePriceRepository.findFirstByStockIdAndTradeDateGreaterThanEqualOrderByTradeDateAsc(
                eq(testStock.getId()), eq(reportDate.plusYears(1).toLocalDate())))
                .willReturn(Optional.of(oneYearLaterPrice));

        given(reportRepository.findFirstByAnalystIdAndStockIdAndReportDateAfterOrderByReportDateAsc(
                anyLong(), anyLong(), any(LocalDateTime.class)))
                .willReturn(Optional.empty()); // 의견 변화 없음

        given(reportRepository.findByStockIdAndReportDateAfterOrderByReportDateDesc(
                anyLong(), any(LocalDateTime.class)))
                .willReturn(reports);

        given(analystRepository.findById(analystId)).willReturn(Optional.of(testAnalyst1));
        given(metricsRepository.save(any(AnalystMetrics.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        analystMetricsService.calculateAndSaveAccuracyRate(analystId);

        // then
        verify(metricsRepository, times(1)).save(any(AnalystMetrics.class));
        verify(analystRepository, times(2)).findById(analystId); // 조회 + 저장 시
    }

    @Test
    @DisplayName("빈 메트릭 리스트로 랭킹 조회 시 빈 결과 반환")
    void getRankedAnalysts_WithEmptyMetrics_ShouldReturnEmptyList() {
        // given
        given(metricsRepository.findAll()).willReturn(List.of());

        // when
        AnalystRankingResponseDTO result = analystMetricsService.getRankedAnalysts("accuracyRate");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRankingList()).isEmpty();
        assertThat(result.getCriteria()).isEqualTo("accuracyRate");

        verify(metricsRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("알 수 없는 정렬 기준으로 조회 시 기본 정렬(accuracyRate) 적용")
    void getRankedAnalysts_WithUnknownSortBy_ShouldUseDefaultSort() {
        // given
        List<AnalystMetrics> metricsList = List.of(testMetrics1, testMetrics2, testMetrics3);
        given(metricsRepository.findAll()).willReturn(metricsList);

        // when
        AnalystRankingResponseDTO result = analystMetricsService.getRankedAnalysts("unknownCriteria");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCriteria()).isEqualTo("unknownCriteria");

        // 기본 정렬(accuracyRate 내림차순)이 적용되어야 함
        assertThat(result.getRankingList().get(0).getAccuracyRate()).isEqualTo(90.0);
        assertThat(result.getRankingList().get(1).getAccuracyRate()).isEqualTo(85.0);
        assertThat(result.getRankingList().get(2).getAccuracyRate()).isEqualTo(80.0);

        verify(metricsRepository, times(1)).findAll();
    }
}
