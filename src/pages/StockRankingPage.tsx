import { useMemo, useState } from 'react';
import styled from 'styled-components';
import { StockRankingCard } from '../components/stock/StockRankingCard';
import { Pagination } from '../components/common/Pagination';
import { mockStockRankings } from '../mocks/stockRankings';

type StockSortType = 'upsideHigh' | 'upsideLow' | 'buyHigh' | 'buyLow';

const PAGE_SIZE = 10;

export const StockRankingPage = () => {
  const [sortType, setSortType] = useState<StockSortType>('upsideHigh');
  const [currentPage, setCurrentPage] = useState(1);

  const { currentPageStocks, totalPages, startIndex } = useMemo(() => {
    const sortedStocks = [...mockStockRankings].sort((a, b) => {
      switch (sortType) {
        case 'upsideHigh':
          return b.upside - a.upside;
        case 'upsideLow':
          return a.upside - b.upside;
        case 'buyHigh':
          return b.buyRatio - a.buyRatio;
        case 'buyLow':
          return a.buyRatio - b.buyRatio;
        default:
          return 0;
      }
    });

    const total = Math.ceil(sortedStocks.length / PAGE_SIZE);
    const start = (currentPage - 1) * PAGE_SIZE;
    const end = start + PAGE_SIZE;

    return {
      currentPageStocks: sortedStocks.slice(start, end),
      totalPages: total,
      startIndex: start,
    };
  }, [currentPage, sortType]);

  const handleSortChange = (nextSort: StockSortType) => {
    setSortType(nextSort);
    setCurrentPage(1);
  };

  return (
    <PageContainer>
      <HeaderSection>
        <ServiceName>종목 랭킹</ServiceName>
        <ServiceDescription>
          애널리스트 리포트를 기반으로 종목별 상승 여력과 매수 의견 비율을 비교합니다.
        </ServiceDescription>
      </HeaderSection>

      <SortBar>
        <SortButton
          type="button"
          $active={sortType === 'upsideHigh'}
          onClick={() => handleSortChange('upsideHigh')}
        >
          상승 여력 높은 순
        </SortButton>
        <SortButton
          type="button"
          $active={sortType === 'upsideLow'}
          onClick={() => handleSortChange('upsideLow')}
        >
          상승 여력 낮은 순
        </SortButton>
        <SortButton
          type="button"
          $active={sortType === 'buyHigh'}
          onClick={() => handleSortChange('buyHigh')}
        >
          매수 비율 높은 순
        </SortButton>
        <SortButton
          type="button"
          $active={sortType === 'buyLow'}
          onClick={() => handleSortChange('buyLow')}
        >
          매수 비율 낮은 순
        </SortButton>
      </SortBar>

      <RankingSection>
        <SectionTitle>종목 랭킹</SectionTitle>
        <CardList>
          {currentPageStocks.map((stock, index) => (
            <StockRankingCard
              key={stock.ticker}
              name={stock.name}
              ticker={stock.ticker}
              sector={stock.sector}
              upside={stock.upside}
              buyRatio={stock.buyRatio}
              rank={startIndex + index + 1}
              onClick={() => console.log('go to stock detail:', stock.ticker)}
            />
          ))}
        </CardList>
        <Pagination currentPage={currentPage} totalPages={totalPages} onPageChange={setCurrentPage} />
      </RankingSection>
    </PageContainer>
  );
};

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
  flex-wrap: wrap;
`;

const SortButton = styled.button<{ $active: boolean }>`
  padding: 8px 16px;
  border: 1px solid ${({ $active }) => ($active ? '#2563eb' : '#e0e0e0')};
  border-radius: 4px;
  background-color: ${({ $active }) => ($active ? '#2563eb' : '#ffffff')};
  color: ${({ $active }) => ($active ? '#ffffff' : '#333')};
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: #2563eb;
    background-color: ${({ $active }) => ($active ? '#1d4ed8' : '#f0f8ff')};
  }
`;

const RankingSection = styled.section`
  display: flex;
  flex-direction: column;
  gap: 16px;
`;

const SectionTitle = styled.h2`
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
`;

const CardList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 12px;
`;

