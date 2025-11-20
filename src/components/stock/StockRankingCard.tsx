import React from 'react';
import styled from 'styled-components';

export type StockRankingCardProps = {
  name: string;
  ticker: string;
  sector: string;
  upside: number;
  buyRatio: number;
  rank: number;
  onClick?: () => void;
};

export const StockRankingCard: React.FC<StockRankingCardProps> = ({
  name,
  ticker,
  sector,
  upside,
  buyRatio,
  rank,
  onClick,
}) => {
  const formattedUpside =
    upside >= 0 ? `+${upside.toFixed(1)}%` : `${upside.toFixed(1)}%`;

  return (
    <CardWrapper onClick={onClick} $clickable={Boolean(onClick)}>
      <HeaderRow>
        <div>
          <Title>{name}</Title>
          <SubInfo>
            {ticker} · {sector}
          </SubInfo>
        </div>
        <RankBadge>{`#${rank}`}</RankBadge>
      </HeaderRow>
      <MetricsRow>
        <MetricItem>
          <MetricLabel>상승 여력</MetricLabel>
          <MetricValue>{formattedUpside}</MetricValue>
        </MetricItem>
        <MetricItem>
          <MetricLabel>매수 비율</MetricLabel>
          <MetricValue>{`${buyRatio.toFixed(1)}%`}</MetricValue>
        </MetricItem>
      </MetricsRow>
    </CardWrapper>
  );
};

type CardWrapperProps = {
  $clickable: boolean;
};

const CardWrapper = styled.div<CardWrapperProps>`
  background: #ffffff;
  border-radius: 12px;
  padding: 16px 20px;
  box-shadow: 0 2px 6px rgba(15, 23, 42, 0.06);
  display: flex;
  flex-direction: column;
  gap: 12px;
  cursor: ${({ $clickable }) => ($clickable ? 'pointer' : 'default')};
  transition: transform 0.15s ease, box-shadow 0.15s ease;

  &:hover {
    transform: ${({ $clickable }) => ($clickable ? 'translateY(-2px)' : 'none')};
    box-shadow: ${({ $clickable }) =>
      $clickable ? '0 4px 12px rgba(15, 23, 42, 0.12)' : '0 2px 6px rgba(15, 23, 42, 0.06)'};
  }
`;

const HeaderRow = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
`;

const Title = styled.h3`
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: #111827;
`;

const SubInfo = styled.p`
  font-size: 13px;
  color: #6b7280;
  margin: 4px 0 0;
`;

const RankBadge = styled.span`
  background: #2563eb;
  color: #ffffff;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 999px;
  font-weight: 600;
`;

const MetricsRow = styled.div`
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
`;

const MetricItem = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2px;
`;

const MetricLabel = styled.span`
  font-size: 12px;
  color: #6b7280;
`;

const MetricValue = styled.span`
  font-size: 14px;
  font-weight: 600;
  color: #111827;
`;

// 예시
// <StockRankingCard
//   name="삼성전자"
//   ticker="005930"
//   sector="IT 하드웨어"
//   upside={18.3}
//   buyRatio={72.5}
//   rank={1}
// />

