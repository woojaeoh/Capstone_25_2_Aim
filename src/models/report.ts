// 리포트 기본 정보
export type Report = {
  id: string;           // 리포트 ID
  title: string;        // 리포트 제목
  date: string;         // 리포트 날짜
  stockName: string;    // 종목 이름
  stockTicker?: string; // 종목 티커
  link?: string;        // 리포트 링크
  analystId?: string;   // 애널리스트 ID
  analystName?: string; // 애널리스트 이름
  firm?: string;        // 증권사 이름
};

