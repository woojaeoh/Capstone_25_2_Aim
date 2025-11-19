import React from 'react';
import styled from 'styled-components';

type CoveredStock = {
  name: string;
  ticker: string;
  sector: string;
};

type CoveredStocksTableProps = {
  stocks: CoveredStock[];
};

const Container = styled.div`
  padding: 20px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
`;

const Table = styled.table`
  width: 100%;
  border-collapse: collapse;
`;

const TableHeader = styled.thead`
  background-color: #f8f9fa;
`;

const TableHeaderCell = styled.th`
  padding: 12px;
  text-align: left;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  border-bottom: 2px solid #e0e0e0;
`;

const TableBody = styled.tbody``;

const TableRow = styled.tr`
  border-bottom: 1px solid #e0e0e0;

  &:hover {
    background-color: #f8f9fa;
  }

  &:last-child {
    border-bottom: none;
  }
`;

const TableCell = styled.td`
  padding: 12px;
  font-size: 14px;
  color: #333;
`;

export const CoveredStocksTable: React.FC<CoveredStocksTableProps> = ({
  stocks,
}) => {
  return (
    <Container>
      <Table>
        <TableHeader>
          <tr>
            <TableHeaderCell>종목명</TableHeaderCell>
            <TableHeaderCell>티커</TableHeaderCell>
            <TableHeaderCell>섹터</TableHeaderCell>
          </tr>
        </TableHeader>
        <TableBody>
          {stocks.map((stock, index) => (
            <TableRow key={index}>
              <TableCell>{stock.name}</TableCell>
              <TableCell>{stock.ticker}</TableCell>
              <TableCell>{stock.sector}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </Container>
  );
};

/*
// Mock 테스트 예시:
<CoveredStocksTable
  stocks={[
    { name: '삼성전자', ticker: '005930', sector: 'IT/전자' },
    { name: 'SK하이닉스', ticker: '000660', sector: '반도체' },
    { name: 'LG디스플레이', ticker: '034220', sector: '디스플레이' },
  ]}
/>
*/

