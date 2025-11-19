// 리포트 기본 정보
export type Report = {
  id: string;
  title: string;
  date: string;
  stockName: string;
  stockTicker?: string;
  link?: string;
  analystId?: string;
  analystName?: string;
  firm?: string;
};

