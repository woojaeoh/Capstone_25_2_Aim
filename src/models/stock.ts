import type { AnalystRankingEntry } from './analyst';

// 종목 기본 정보
export type StockCore = {
  name: string;    // 종목 이름
  ticker: string;  // 종목 티커
  sector: string;  // 종목 섹터
};

// 종목 의견 요약
export type StockOpinionSummary = {
  buyCount: number;  // 매수 의견 수
  holdCount: number; // 보유 의견 수
  sellCount: number; // 매도 의견 수
};

// 목표가 정보
export type TargetPriceInfo = {
  currentPrice: number;    // 주식 현재가
  avgTargetPrice: number;  // 애널 평균 목표가
  highTargetPrice: number; // 애널 최고 목표가
  lowTargetPrice: number;  // 애널 최저 목표가
};

// 가격 차트 데이터 포인트
export type PriceDataPoint = {
  date: string; 
  price: number;
};

// 종목 상세 페이지 전체 데이터
export type StockDetailPageData = {
  stock: StockCore;
  opinionSummary: StockOpinionSummary;
  targetPrice: TargetPriceInfo;
  priceHistory: PriceDataPoint[];
  forecast: PriceDataPoint[];
  coveringAnalysts: AnalystRankingEntry[];
};

