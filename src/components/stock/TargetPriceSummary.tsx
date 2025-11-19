import React from 'react';
import styled from 'styled-components';

type TargetPriceSummaryProps = {
  currentPrice: number;
  avgTargetPrice: number;
  highTargetPrice: number;
  lowTargetPrice: number;
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

const PriceGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 16px;
`;

const PriceItem = styled.div`
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 8px;
`;

const PriceLabel = styled.div`
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
`;

const PriceValue = styled.div`
  font-size: 20px;
  font-weight: 700;
  color: #333;
`;

const UpsideSection = styled.div`
  padding: 16px;
  background-color: #e8f5e9;
  border-radius: 8px;
  text-align: center;
`;

const UpsideLabel = styled.div`
  font-size: 14px;
  color: #2e7d32;
  margin-bottom: 4px;
`;

const UpsideValue = styled.div`
  font-size: 18px;
  font-weight: 700;
  color: #2e7d32;
`;

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('ko-KR').format(price) + '원';
};

const calculateUpside = (
  currentPrice: number,
  avgTargetPrice: number
): number => {
  if (currentPrice === 0) return 0;
  return ((avgTargetPrice - currentPrice) / currentPrice) * 100;
};

export const TargetPriceSummary: React.FC<TargetPriceSummaryProps> = ({
  currentPrice,
  avgTargetPrice,
  highTargetPrice,
  lowTargetPrice,
}) => {
  const upside = calculateUpside(currentPrice, avgTargetPrice);

  return (
    <Container>
      <Title>목표가 요약</Title>
      <PriceGrid>
        <PriceItem>
          <PriceLabel>평균 목표가</PriceLabel>
          <PriceValue>{formatPrice(avgTargetPrice)}</PriceValue>
        </PriceItem>
        <PriceItem>
          <PriceLabel>최고 목표가</PriceLabel>
          <PriceValue>{formatPrice(highTargetPrice)}</PriceValue>
        </PriceItem>
        <PriceItem>
          <PriceLabel>최저 목표가</PriceLabel>
          <PriceValue>{formatPrice(lowTargetPrice)}</PriceValue>
        </PriceItem>
        <PriceItem>
          <PriceLabel>현재가</PriceLabel>
          <PriceValue>{formatPrice(currentPrice)}</PriceValue>
        </PriceItem>
      </PriceGrid>
      <UpsideSection>
        <UpsideLabel>상승 여력</UpsideLabel>
        <UpsideValue>
          {upside > 0 ? '+' : ''}
          {upside.toFixed(1)}%
        </UpsideValue>
      </UpsideSection>
    </Container>
  );
};

/*
// Mock 테스트 예시:
<TargetPriceSummary
  currentPrice={75000}
  avgTargetPrice={85000}
  highTargetPrice={95000}
  lowTargetPrice={75000}
/>
*/

