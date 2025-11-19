import React from 'react';
import styled from 'styled-components';

type PaginationProps = {
  currentPage: number;      // 1부터 시작
  totalPages: number;
  onPageChange: (page: number) => void;
};

const Container = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  padding: 24px 0;
  margin-top: 24px;
`;

const PageButton = styled.button<{ active?: boolean; disabled?: boolean }>`
  padding: 8px 12px;
  border: 1px solid ${(props) => (props.active ? '#007bff' : '#e0e0e0')};
  border-radius: 4px;
  background-color: ${(props) => {
    if (props.disabled) return '#f5f5f5';
    return props.active ? '#007bff' : '#ffffff';
  }};
  color: ${(props) => {
    if (props.disabled) return '#ccc';
    return props.active ? '#ffffff' : '#333';
  }};
  font-size: 14px;
  font-weight: ${(props) => (props.active ? '600' : '500')};
  cursor: ${(props) => (props.disabled ? 'not-allowed' : 'pointer')};
  transition: all 0.2s;
  min-width: 40px;

  &:hover:not(:disabled) {
    border-color: #007bff;
    background-color: ${(props) => (props.active ? '#0056b3' : '#f0f8ff')};
  }
`;

const Ellipsis = styled.span`
  padding: 8px 4px;
  color: #666;
  font-size: 14px;
`;

export const Pagination: React.FC<PaginationProps> = ({
  currentPage,
  totalPages,
  onPageChange,
}) => {
  const getPageNumbers = () => {
    const pages: (number | string)[] = [];
    const maxVisible = 5;

    if (totalPages <= maxVisible) {
      // 전체 페이지가 5개 이하인 경우 모두 표시
      for (let i = 1; i <= totalPages; i++) {
        pages.push(i);
      }
    } else {
      // 첫 페이지
      pages.push(1);

      if (currentPage <= 3) {
        // 현재 페이지가 앞쪽에 있는 경우
        for (let i = 2; i <= 4; i++) {
          pages.push(i);
        }
        pages.push('ellipsis');
        pages.push(totalPages);
      } else if (currentPage >= totalPages - 2) {
        // 현재 페이지가 뒤쪽에 있는 경우
        pages.push('ellipsis');
        for (let i = totalPages - 3; i <= totalPages; i++) {
          pages.push(i);
        }
      } else {
        // 현재 페이지가 중간에 있는 경우
        pages.push('ellipsis');
        for (let i = currentPage - 1; i <= currentPage + 1; i++) {
          pages.push(i);
        }
        pages.push('ellipsis');
        pages.push(totalPages);
      }
    }

    return pages;
  };

  const handlePrev = () => {
    if (currentPage > 1) {
      onPageChange(currentPage - 1);
    }
  };

  const handleNext = () => {
    if (currentPage < totalPages) {
      onPageChange(currentPage + 1);
    }
  };

  const pageNumbers = getPageNumbers();

  return (
    <Container>
      <PageButton
        disabled={currentPage === 1}
        onClick={handlePrev}
      >
        이전
      </PageButton>

      {pageNumbers.map((page, index) => {
        if (page === 'ellipsis') {
          return <Ellipsis key={`ellipsis-${index}`}>...</Ellipsis>;
        }

        const pageNum = page as number;
        return (
          <PageButton
            key={pageNum}
            active={currentPage === pageNum}
            onClick={() => onPageChange(pageNum)}
          >
            {pageNum}
          </PageButton>
        );
      })}

      <PageButton
        disabled={currentPage === totalPages}
        onClick={handleNext}
      >
        다음
      </PageButton>
    </Container>
  );
};

/*
// Mock 테스트 예시:
const [currentPage, setCurrentPage] = useState(1);
const totalPages = 5;

<Pagination
  currentPage={currentPage}
  totalPages={totalPages}
  onPageChange={(page) => setCurrentPage(page)}
/>
*/

