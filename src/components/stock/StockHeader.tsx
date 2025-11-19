import React from 'react';
import styled from 'styled-components';

type StockHeaderProps = {
  name: string;
  ticker: string;
  sector: string;
};

const Container = styled.div`
  padding: 24px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
`;

const StockName = styled.h2`
  margin: 0 0 12px 0;
  font-size: 24px;
  font-weight: 700;
  color: #333;
`;

const InfoRow = styled.div`
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
`;

const InfoItem = styled.div`
  display: flex;
  gap: 8px;
  align-items: center;
`;

const InfoLabel = styled.span`
  font-size: 14px;
  color: #666;
`;

const InfoValue = styled.span`
  font-size: 14px;
  font-weight: 600;
  color: #333;
`;

export const StockHeader: React.FC<StockHeaderProps> = ({
  name,
  ticker,
  sector,
}) => {
  return (
    <Container>
      <StockName>{name}</StockName>
      <InfoRow>
        <InfoItem>
          <InfoLabel>티커:</InfoLabel>
          <InfoValue>{ticker}</InfoValue>
        </InfoItem>
        <InfoItem>
          <InfoLabel>섹터:</InfoLabel>
          <InfoValue>{sector}</InfoValue>
        </InfoItem>
      </InfoRow>
    </Container>
  );
};

/*
// Mock 테스트 예시:
<StockHeader
  name="삼성전자"
  ticker="005930"
  sector="IT/전자"
/>
*/

