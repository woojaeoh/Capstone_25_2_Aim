import type { StockCore } from './stock';
import type { Report } from './report';

// 애널리스트 기본 정보
export type AnalystCore = {
  id: string;
  name: string;
  firm: string;
  sectors: string[];
};

// 애널리스트 성과 지표
export type AnalystMetrics = {
  accuracy: number;            // 정답률 (%)
  avgReturn: number;           // 평균 수익률 (%)
  targetError: number;         // 목표가 오차율 (%)
  relativeReturn: number;      // 애널 평균 대비 수익률
  relativeTargetError: number; // 애널 평균 대비 목표가 오차율
  compositeScore: number;      // 종합 점수
};

// 메인 페이지 랭킹용
export type AnalystRankingEntry = AnalystCore & {
  rank: number;
  metrics: AnalystMetrics;
};

// 애널리스트 상세 페이지 전체 데이터
export type AnalystDetailPageData = {
  analyst: AnalystCore;
  metrics: AnalystMetrics;
  coveredStocks: StockCore[];
  reports: Report[];
  reportFrequency: {
    month: string;
    count: number;
  }[];
};

