import React from 'react';
import styled from 'styled-components';

type AnalystCardProps = {
  name: string;
  firm: string;
  rank?: number;
  accuracy: number;
  avgReturn: number;
  targetError: number;
  compositeScore?: number;
  onClick?: () => void;
};

const Container = styled.div<{ hasOnClick: boolean }>`
  padding: 20px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  margin-bottom: 16px;
  cursor: ${(props) => (props.hasOnClick ? 'pointer' : 'default')};
  transition: box-shadow 0.2s;

  &:hover {
    box-shadow: ${(props) =>
      props.hasOnClick ? '0 4px 12px rgba(0, 0, 0, 0.12)' : '0 2px 6px rgba(0, 0, 0, 0.08)'};
  }
`;

const Header = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
`;

const NameSection = styled.div`
  flex: 1;
`;

const Name = styled.h3`
  margin: 0 0 4px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
`;

const Firm = styled.p`
  margin: 0;
  font-size: 14px;
  color: #666;
`;

const RankBadge = styled.div`
  padding: 4px 12px;
  background-color: #f0f0f0;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 600;
  color: #333;
`;

const MetricsGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 16px;
`;

const MetricItem = styled.div`
  display: flex;
  flex-direction: column;
  gap: 4px;
`;

const MetricLabel = styled.span`
  font-size: 12px;
  color: #888;
`;

const MetricValue = styled.span`
  font-size: 16px;
  font-weight: 600;
  color: #333;
`;

const ScoreSection = styled.div`
  padding-top: 16px;
  border-top: 1px solid #e0e0e0;
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

const ScoreLabel = styled.span`
  font-size: 14px;
  color: #666;
`;

const ScoreValue = styled.span`
  font-size: 20px;
  font-weight: 700;
  color: #333;
`;

export const AnalystCard: React.FC<AnalystCardProps> = ({
  name,
  firm,
  rank,
  accuracy,
  avgReturn,
  targetError,
  compositeScore,
  onClick,
}) => {
  return (
    <Container hasOnClick={!!onClick} onClick={onClick}>
      <Header>
        <NameSection>
          <Name>{name}</Name>
          <Firm>{firm}</Firm>
        </NameSection>
        {rank && <RankBadge>#{rank}</RankBadge>}
      </Header>
      <MetricsGrid>
        <MetricItem>
          <MetricLabel>정답률</MetricLabel>
          <MetricValue>{accuracy}%</MetricValue>
        </MetricItem>
        <MetricItem>
          <MetricLabel>평균 수익률</MetricLabel>
          <MetricValue>{avgReturn}%</MetricValue>
        </MetricItem>
        <MetricItem>
          <MetricLabel>목표가 오차율</MetricLabel>
          <MetricValue>{targetError}%</MetricValue>
        </MetricItem>
      </MetricsGrid>
      {compositeScore !== undefined && (
        <ScoreSection>
          <ScoreLabel>AIM's Score</ScoreLabel>
          <ScoreValue>{compositeScore}</ScoreValue>
        </ScoreSection>
      )}
    </Container>
  );
};

/*
// Mock 테스트 예시:
<AnalystCard
  name="김애널리스트"
  firm="삼성증권"
  rank={1}
  accuracy={85.5}
  avgReturn={12.3}
  targetError={5.2}
  compositeScore={92}
  onClick={() => console.log('클릭됨')}
/>
*/

