import React from 'react';
import styled from 'styled-components';

type SortKey = 'accuracy' | 'avgReturn' | 'targetError';

type SortBarProps = {
  activeKey: SortKey;
  onChange: (key: SortKey) => void;
};

const Container = styled.div`
  padding: 16px 20px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
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

const sortLabels: Record<SortKey, string> = {
  accuracy: '정답률',
  avgReturn: '수익률',
  targetError: '오차율',
};

export const SortBar: React.FC<SortBarProps> = ({ activeKey, onChange }) => {
  const sortKeys: SortKey[] = ['accuracy', 'avgReturn', 'targetError'];

  return (
    <Container>
      {sortKeys.map((key) => (
        <SortButton
          key={key}
          active={activeKey === key}
          onClick={() => onChange(key)}
        >
          {sortLabels[key]}
        </SortButton>
      ))}
    </Container>
  );
};

/*
// Mock 테스트 예시:
const [sortKey, setSortKey] = useState<SortKey>('accuracy');

<SortBar
  activeKey={sortKey}
  onChange={(key) => setSortKey(key)}
/>
*/

