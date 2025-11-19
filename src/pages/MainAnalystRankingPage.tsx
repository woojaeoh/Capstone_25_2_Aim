import styled from 'styled-components';
import { useState } from 'react';
import { AnalystCard } from '../components/analyst/AnalystCard';
import { Pagination } from '../components/common/Pagination';
import { mockAnalystRankings } from '../mocks/analystRankings';

const PageContainer = styled.div`
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  max-width: 1200px;
  margin: 0 auto;
`;

const HeaderSection = styled.section`
  padding: 32px 24px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
`;

const ServiceName = styled.h1`
  margin: 0 0 8px 0;
  font-size: 32px;
  font-weight: 700;
  color: #333;
`;

const ServiceDescription = styled.p`
  margin: 0;
  font-size: 16px;
  color: #666;
`;

const SortBar = styled.section`
  padding: 16px 24px;
  background-color: #ffffff;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  display: flex;
  gap: 12px;
`;

const SortButton = styled.button<{ active: boolean }>`
  padding: 8px 16px;
  border: 1px solid ${(props) => (props.active ? '#007bff' : '#e0e0e0')};
  border-radius: 4px;
  background-color: ${(props) => (props.active ? '#007bff' : '#ffffff')};
  color: ${(props) => (props.active ? '#ffffff' : '#333')};
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: #007bff;
    background-color: ${(props) => (props.active ? '#0056b3' : '#f0f8ff')};
  }
`;

const RankingSection = styled.section`
  display: flex;
  flex-direction: column;
  gap: 16px;
`;

const SectionTitle = styled.h2`
  margin: 0 0 16px 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
`;

type SortType = 'accuracy' | 'return' | 'error';

const PAGE_SIZE = 10;

export const MainAnalystRankingPage = () => {
  const [sortType, setSortType] = useState<SortType>('accuracy');
  const [currentPage, setCurrentPage] = useState(1);

  // 정렬된 배열 생성
  const sortedAnalysts = [...mockAnalystRankings]
    .sort((a, b) => {
      if (sortType === 'accuracy') {
        // metrics.accuracy 내림차순 (높은 순)
        return b.metrics.accuracy - a.metrics.accuracy;
      } else if (sortType === 'return') {
        // metrics.avgReturn 내림차순 (높은 순)
        return b.metrics.avgReturn - a.metrics.avgReturn;
      } else {
        // metrics.targetError 오름차순 (낮은 순일수록 좋은 순)
        return a.metrics.targetError - b.metrics.targetError;
      }
    })
    .map((analyst, index) => ({
      ...analyst,
      rank: index + 1, // 정렬 후 새로운 순위 할당
    }));

  // 페이지네이션 계산
  const totalPages = Math.ceil(sortedAnalysts.length / PAGE_SIZE);
  const startIndex = (currentPage - 1) * PAGE_SIZE;
  const endIndex = startIndex + PAGE_SIZE;
  const currentPageAnalysts = sortedAnalysts.slice(startIndex, endIndex).map((analyst, index) => ({
    ...analyst,
    rank: startIndex + index + 1, // 전체 순위 기준으로 rank 업데이트
  }));

  return (
    <PageContainer>
      <HeaderSection>
        <ServiceName>애널리스트 리포트</ServiceName>
        <ServiceDescription>
          증권사 애널리스트의 리포트를 기반으로 한 투자 인사이트 플랫폼
        </ServiceDescription>
      </HeaderSection>

      <SortBar>
        <SortButton
          active={sortType === 'accuracy'}
          onClick={() => {
            setSortType('accuracy');
            setCurrentPage(1);
          }}
        >
          정답률
        </SortButton>
        <SortButton
          active={sortType === 'return'}
          onClick={() => {
            setSortType('return');
            setCurrentPage(1);
          }}
        >
          수익률
        </SortButton>
        <SortButton
          active={sortType === 'error'}
          onClick={() => {
            setSortType('error');
            setCurrentPage(1);
          }}
        >
          오차율
        </SortButton>
      </SortBar>

      <RankingSection>
        <SectionTitle>애널리스트 랭킹</SectionTitle>
        {currentPageAnalysts.map((analyst) => (
          <AnalystCard
            key={analyst.id}
            name={analyst.name}
            firm={analyst.firm}
            rank={analyst.rank}
            sectors={analyst.sectors}
            accuracy={analyst.metrics.accuracy}
            avgReturn={analyst.metrics.avgReturn}
            targetError={analyst.metrics.targetError}
            compositeScore={analyst.metrics.compositeScore}
          />
        ))}
        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={setCurrentPage}
        />
      </RankingSection>
    </PageContainer>
  );
};

