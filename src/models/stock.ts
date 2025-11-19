import type { AnalystRankingEntry } from './analyst';

// 종목 기본 정보
export type StockCore = {
  name: string;
  ticker: string;
  sector: string;
};

// 종목 의견 요약
export type StockOpinionSummary = {
  buyCount: number;
  holdCount: number;
  sellCount: number;
};

// 목표가 정보
export type TargetPriceInfo = {
  currentPrice: number;
  avgTargetPrice: number;
  highTargetPrice: number;
  lowTargetPrice: number;
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

