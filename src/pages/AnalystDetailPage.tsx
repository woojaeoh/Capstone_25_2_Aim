import styled from 'styled-components';
import { MetricCard } from '../components/analyst/MetricCard';

const PageContainer = styled.div`
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  max-width: 1200px;
  margin: 0 auto;
`;

const Section = styled.section`
  padding: 24px;
  background-color: #ffffff;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
`;

const SectionTitle = styled.h2`
  margin: 0 0 16px 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
`;

const HeaderWrapper = styled.div`
  padding: 24px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
`;

const AnalystName = styled.h2`
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 700;
  color: #333;
`;

const Affiliation = styled.p`
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #666;
`;

const SectorList = styled.div`
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 12px;
`;

const SectorTag = styled.span`
  padding: 6px 12px;
  background-color: #e3f2fd;
  border-radius: 4px;
  font-size: 14px;
  color: #1976d2;
  font-weight: 500;
`;

const MetricsGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-top: 16px;
`;

const Table = styled.table`
  width: 100%;
  border-collapse: collapse;
  margin-top: 16px;
`;

const TableHeader = styled.thead`
  background-color: #f8f9fa;
`;

const TableHeaderCell = styled.th`
  padding: 12px;
  text-align: left;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  border-bottom: 2px solid #e0e0e0;
`;

const TableBody = styled.tbody``;

const TableRow = styled.tr`
  border-bottom: 1px solid #e0e0e0;

  &:hover {
    background-color: #f8f9fa;
  }
`;

const TableCell = styled.td`
  padding: 12px;
  font-size: 14px;
  color: #333;
`;

const ChartPlaceholder = styled.div`
  width: 100%;
  height: 300px;
  background-color: #f8f9fa;
  border: 1px dashed #ccc;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 14px;
  margin-top: 16px;
`;

const ReportList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 16px;
`;

const ReportItem = styled.div`
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

const ReportInfo = styled.div`
  display: flex;
  flex-direction: column;
  gap: 4px;
`;

const ReportTitle = styled.div`
  font-size: 16px;
  font-weight: 600;
  color: #333;
`;

const ReportMeta = styled.div`
  font-size: 14px;
  color: #666;
`;

const ReportButton = styled.button`
  padding: 8px 16px;
  border: 1px solid #007bff;
  border-radius: 4px;
  background-color: #007bff;
  color: #ffffff;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;

  &:hover {
    background-color: #0056b3;
  }
`;

export const AnalystDetailPage = () => {
  // Mock 데이터
  const analyst = {
    name: '김애널리스트',
    affiliation: '삼성증권',
    sectors: ['IT/전자', '반도체', '디스플레이'],
    metrics: {
      accuracy: '85.5%',
      avgReturn: '12.3%',
      targetPriceError: '5.2%',
      avgReturnVsMarket: '+3.5%',
      accuracyVsMarket: '+8.2%',
    },
    coveredStocks: [
      { name: '삼성전자', ticker: '005930', sector: 'IT/전자' },
      { name: 'SK하이닉스', ticker: '000660', sector: '반도체' },
      { name: 'LG디스플레이', ticker: '034220', sector: '디스플레이' },
    ],
    reports: [
      {
        id: 1,
        title: '삼성전자, 반도체 회복세 지속 전망',
        date: '2024-01-15',
        stock: '삼성전자',
      },
      {
        id: 2,
        title: 'SK하이닉스, HBM 수요 증가로 실적 개선 기대',
        date: '2024-01-10',
        stock: 'SK하이닉스',
      },
      {
        id: 3,
        title: 'LG디스플레이, OLED 시장 성장세 지속',
        date: '2024-01-05',
        stock: 'LG디스플레이',
      },
    ],
  };

  return (
    <PageContainer>
      <Section>
        <HeaderWrapper>
          <AnalystName>{analyst.name}</AnalystName>
          <Affiliation>{analyst.affiliation}</Affiliation>
          <SectorList>
            {analyst.sectors.map((sector, index) => (
              <SectorTag key={index}>{sector}</SectorTag>
            ))}
          </SectorList>
        </HeaderWrapper>
      </Section>

      <Section>
        <SectionTitle>핵심 지표</SectionTitle>
        <MetricsGrid>
          <MetricCard label="정답률" value={analyst.metrics.accuracy} />
          <MetricCard label="평균 수익률" value={analyst.metrics.avgReturn} />
          <MetricCard
            label="목표가 오차율"
            value={analyst.metrics.targetPriceError}
          />
          <MetricCard
            label="평균 대비 수익률"
            value={analyst.metrics.avgReturnVsMarket}
          />
          <MetricCard
            label="평균 대비 목표가 정확도"
            value={analyst.metrics.accuracyVsMarket}
          />
        </MetricsGrid>
      </Section>

      <Section>
        <SectionTitle>커버 종목 리스트</SectionTitle>
        <Table>
          <TableHeader>
            <tr>
              <TableHeaderCell>종목명</TableHeaderCell>
              <TableHeaderCell>티커</TableHeaderCell>
              <TableHeaderCell>섹터</TableHeaderCell>
            </tr>
          </TableHeader>
          <TableBody>
            {analyst.coveredStocks.map((stock, index) => (
              <TableRow key={index}>
                <TableCell>{stock.name}</TableCell>
                <TableCell>{stock.ticker}</TableCell>
                <TableCell>{stock.sector}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Section>

      <Section>
        <SectionTitle>최근 1년 리포트 발행 추이</SectionTitle>
        <ChartPlaceholder>
          리포트 발행 추이 차트 (Recharts로 교체 예정)
        </ChartPlaceholder>
      </Section>

      <Section>
        <SectionTitle>리포트 목록</SectionTitle>
        <ReportList>
          {analyst.reports.map((report) => (
            <ReportItem key={report.id}>
              <ReportInfo>
                <ReportTitle>{report.title}</ReportTitle>
                <ReportMeta>
                  {report.date} · {report.stock}
                </ReportMeta>
              </ReportInfo>
              <ReportButton>리포트 보기</ReportButton>
            </ReportItem>
          ))}
        </ReportList>
      </Section>
    </PageContainer>
  );
};

