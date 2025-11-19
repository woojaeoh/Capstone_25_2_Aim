import React from 'react';
import styled from 'styled-components';

type ReportItem = {
  id: string;
  title: string;
  date: string;
  stockName: string;
  link?: string;
};

type ReportsListProps = {
  reports: ReportItem[];
};

const Container = styled.div`
  padding: 20px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
`;

const ReportItemWrapper = styled.div`
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  margin-bottom: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;

  &:last-child {
    margin-bottom: 0;
  }
`;

const ReportInfo = styled.div`
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
`;

const ReportTitle = styled.div`
  font-size: 16px;
  font-weight: 600;
  color: #333;
`;

const ReportMeta = styled.div`
  font-size: 14px;
  color: #666;
`;

const ReportButton = styled.button`
  padding: 8px 16px;
  border: 1px solid #007bff;
  border-radius: 4px;
  background-color: #007bff;
  color: #ffffff;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
  white-space: nowrap;

  &:hover {
    background-color: #0056b3;
  }
`;

export const ReportsList: React.FC<ReportsListProps> = ({ reports }) => {
  return (
    <Container>
      {reports.map((report) => (
        <ReportItemWrapper key={report.id}>
          <ReportInfo>
            <ReportTitle>{report.title}</ReportTitle>
            <ReportMeta>
              {report.date} · {report.stockName}
            </ReportMeta>
          </ReportInfo>
          <ReportButton onClick={() => report.link && window.open(report.link)}>
            리포트 보기
          </ReportButton>
        </ReportItemWrapper>
      ))}
    </Container>
  );
};

/*
// Mock 테스트 예시:
<ReportsList
  reports={[
    {
      id: '1',
      title: '삼성전자, 반도체 회복세 지속 전망',
      date: '2024-01-15',
      stockName: '삼성전자',
      link: 'https://example.com/report/1',
    },
    {
      id: '2',
      title: 'SK하이닉스, HBM 수요 증가로 실적 개선 기대',
      date: '2024-01-10',
      stockName: 'SK하이닉스',
    },
  ]}
/>
*/

