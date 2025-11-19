import React from 'react';
import styled from 'styled-components';

type MetricCardProps = {
  label: string;
  value: string;
  description?: string;
  trend?: 'up' | 'down' | 'neutral';
};

const Container = styled.div`
  padding: 20px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  gap: 8px;
`;

const Label = styled.span`
  font-size: 14px;
  color: #666;
  font-weight: 500;
`;

const ValueRow = styled.div`
  display: flex;
  align-items: baseline;
  gap: 8px;
`;

const Value = styled.span`
  font-size: 24px;
  font-weight: 700;
  color: #333;
`;

const TrendIcon = styled.span<{ trend: 'up' | 'down' | 'neutral' }>`
  font-size: 16px;
  color: ${(props) => {
    if (props.trend === 'up') return '#4caf50';
    if (props.trend === 'down') return '#f44336';
    return '#999';
  }};
`;

const Description = styled.p`
  margin: 0;
  font-size: 12px;
  color: #888;
`;

export const MetricCard: React.FC<MetricCardProps> = ({
  label,
  value,
  description,
  trend,
}) => {
  const getTrendIcon = () => {
    if (trend === 'up') return '↑';
    if (trend === 'down') return '↓';
    return '→';
  };

  return (
    <Container>
      <Label>{label}</Label>
      <ValueRow>
        <Value>{value}</Value>
        {trend && <TrendIcon trend={trend}>{getTrendIcon()}</TrendIcon>}
      </ValueRow>
      {description && <Description>{description}</Description>}
    </Container>
  );
};

/*
// Mock 테스트 예시:
<MetricCard
  label="정답률"
  value="85.5%"
  description="전체 리포트 대비 정확도"
  trend="up"
/>
*/

