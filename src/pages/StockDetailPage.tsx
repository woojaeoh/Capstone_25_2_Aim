import styled from 'styled-components';
import { StockHeader } from '../components/stock/StockHeader';
import { AnalystCard } from '../components/analyst/AnalystCard';

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

const OpinionSummary = styled.div`
  display: flex;
  gap: 16px;
  margin-top: 16px;
`;

const OpinionBox = styled.div`
  flex: 1;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  text-align: center;
`;

const OpinionLabel = styled.div`
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
`;

const OpinionValue = styled.div`
  font-size: 24px;
  font-weight: 700;
  color: #333;
`;

const TargetPriceGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-top: 16px;
`;

const TargetPriceItem = styled.div`
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 8px;
`;

const TargetPriceLabel = styled.div`
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
`;

const TargetPriceValue = styled.div`
  font-size: 20px;
  font-weight: 700;
  color: #333;
`;

const UpsidePotential = styled.div`
  margin-top: 16px;
  padding: 16px;
  background-color: #e8f5e9;
  border-radius: 8px;
  text-align: center;
`;

const UpsideText = styled.div`
  font-size: 16px;
  font-weight: 600;
  color: #2e7d32;
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

const AnalystList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 16px;
`;

export const StockDetailPage = () => {
  // Mock 데이터
  const stock = {
    name: '삼성전자',
    ticker: '005930',
    sector: 'IT/전자',
  };

  const analysts = [
    {
      id: 1,
      name: '김애널리스트',
      affiliation: '삼성증권',
      accuracy: 85.5,
      avgReturn: 12.3,
      targetPriceError: 5.2,
      score: 92,
    },
    {
      id: 2,
      name: '이애널리스트',
      affiliation: 'KB증권',
      accuracy: 82.1,
      avgReturn: 10.8,
      targetPriceError: 6.5,
      score: 88,
    },
  ];

  return (
    <PageContainer>
      <Section>
        <StockHeader
          name={stock.name}
          ticker={stock.ticker}
          sector={stock.sector}
        />
      </Section>

      <Section>
        <SectionTitle>종합 의견 요약</SectionTitle>
        <OpinionSummary>
          <OpinionBox>
            <OpinionLabel>매수</OpinionLabel>
            <OpinionValue>60%</OpinionValue>
          </OpinionBox>
          <OpinionBox>
            <OpinionLabel>보유</OpinionLabel>
            <OpinionValue>30%</OpinionValue>
          </OpinionBox>
          <OpinionBox>
            <OpinionLabel>매도</OpinionLabel>
            <OpinionValue>10%</OpinionValue>
          </OpinionBox>
        </OpinionSummary>
      </Section>

      <Section>
        <SectionTitle>목표가 요약</SectionTitle>
        <TargetPriceGrid>
          <TargetPriceItem>
            <TargetPriceLabel>평균 목표가</TargetPriceLabel>
            <TargetPriceValue>85,000원</TargetPriceValue>
          </TargetPriceItem>
          <TargetPriceItem>
            <TargetPriceLabel>최고 목표가</TargetPriceLabel>
            <TargetPriceValue>95,000원</TargetPriceValue>
          </TargetPriceItem>
          <TargetPriceItem>
            <TargetPriceLabel>최저 목표가</TargetPriceLabel>
            <TargetPriceValue>75,000원</TargetPriceValue>
          </TargetPriceItem>
        </TargetPriceGrid>
        <UpsidePotential>
          <UpsideText>상승 여력: +15.2%</UpsideText>
        </UpsidePotential>
      </Section>

      <Section>
        <SectionTitle>지난 가격 및 12개월 전망</SectionTitle>
        <ChartPlaceholder>차트 영역 (Recharts로 교체 예정)</ChartPlaceholder>
      </Section>

      <Section>
        <SectionTitle>이 종목을 커버하는 애널리스트</SectionTitle>
        <AnalystList>
          {analysts.map((analyst) => (
            <AnalystCard
              key={analyst.id}
              name={analyst.name}
              affiliation={analyst.affiliation}
              accuracy={analyst.accuracy}
              avgReturn={analyst.avgReturn}
              targetPriceError={analyst.targetPriceError}
              score={analyst.score}
            />
          ))}
        </AnalystList>
      </Section>
    </PageContainer>
  );
};

