import React from 'react';
import styled from 'styled-components';

type OpinionSummaryProps = {
  buyCount: number;
  holdCount: number;
  sellCount: number;
};

const Container = styled.div`
  padding: 20px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
`;

const Title = styled.h3`
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
`;

const OpinionGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
`;

const OpinionBox = styled.div`
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  background-color: #f8f9fa;
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

const BuyBox = styled(OpinionBox)`
  background-color: #e8f5e9;
`;

const HoldBox = styled(OpinionBox)`
  background-color: #fff3e0;
`;

const SellBox = styled(OpinionBox)`
  background-color: #ffebee;
`;

const calculatePercentage = (count: number, total: number) => {
  if (total === 0) return 0;
  return Math.round((count / total) * 100);
};

export const OpinionSummaryPlaceholder: React.FC<OpinionSummaryProps> = ({
  buyCount,
  holdCount,
  sellCount,
}) => {
  const total = buyCount + holdCount + sellCount;
  const buyPercent = calculatePercentage(buyCount, total);
  const holdPercent = calculatePercentage(holdCount, total);
  const sellPercent = calculatePercentage(sellCount, total);

  return (
    <Container>
      <Title>종합 의견 요약</Title>
      <OpinionGrid>
        <BuyBox>
          <OpinionLabel>매수</OpinionLabel>
          <OpinionValue>{buyPercent}%</OpinionValue>
        </BuyBox>
        <HoldBox>
          <OpinionLabel>보유</OpinionLabel>
          <OpinionValue>{holdPercent}%</OpinionValue>
        </HoldBox>
        <SellBox>
          <OpinionLabel>매도</OpinionLabel>
          <OpinionValue>{sellPercent}%</OpinionValue>
        </SellBox>
      </OpinionGrid>
    </Container>
  );
};

/*
// Mock 테스트 예시:
<OpinionSummaryPlaceholder
  buyCount={12}
  holdCount={6}
  sellCount={2}
/>
*/

