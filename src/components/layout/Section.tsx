import React from 'react';
import styled from 'styled-components';

type SectionProps = {
  title: string;
  subtitle?: string;
  children: React.ReactNode;
};

const Container = styled.div`
  padding: 24px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
`;

const Title = styled.h2`
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
`;

const Subtitle = styled.p`
  margin: 0 0 16px 0;
  font-size: 14px;
  color: #666;
`;

const Content = styled.div`
  margin-top: 16px;
`;

export const Section: React.FC<SectionProps> = ({
  title,
  subtitle,
  children,
}) => {
  return (
    <Container>
      <Title>{title}</Title>
      {subtitle && <Subtitle>{subtitle}</Subtitle>}
      <Content>{children}</Content>
    </Container>
  );
};

/*
// Mock 테스트 예시:
<Section title="애널리스트 랭킹" subtitle="정답률 기준으로 정렬된 애널리스트 목록">
  <div>컨텐츠 영역</div>
</Section>
*/

