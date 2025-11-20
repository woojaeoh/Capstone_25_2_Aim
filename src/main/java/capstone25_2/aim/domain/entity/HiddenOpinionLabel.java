package capstone25_2.aim.domain.entity;

public enum HiddenOpinionLabel {
    STRONG_BUY,
    BUY,
    HOLD,
    SELL,
    STRONG_SELL;

    /**
     * hiddenOpinion 숫자 값을 5단계 라벨로 변환
     * 0.96 이상: STRONG_BUY
     * 0.75 ~ 0.96: BUY
     * 0.4 ~ 0.75: HOLD
     * 0.1 ~ 0.4: SELL
     * 0.1 미만: STRONG_SELL
     */
    public static HiddenOpinionLabel fromScore(Double score) {
        if (score == null) {
            return null;
        }

        if (score >= 0.96) {
            return STRONG_BUY;
        } else if (score >= 0.75) {
            return BUY;
        } else if (score >= 0.4) {
            return HOLD;
        } else if (score >= 0.1) {
            return SELL;
        } else {
            return STRONG_SELL;
        }
    }

    /**
     * hiddenOpinion을 3단계 의견(BUY, HOLD, SELL)으로 분류
     * 정확도 평가 및 의견 변화 감지용으로 사용
     *
     * 5단계를 3단계로 통합:
     * - BUY: STRONG_BUY + BUY (0.75 이상)
     * - HOLD: HOLD (0.4 ~ 0.75)
     * - SELL: SELL + STRONG_SELL (0.4 미만)
     *
     * @param score hiddenOpinion 값 (0.0 ~ 1.0)
     * @return "BUY" (0.75 이상), "HOLD" (0.4 ~ 0.75), "SELL" (0.4 미만)
     */
    public static String toSimpleCategory(Double score) {
        if (score == null) {
            return null;
        }

        if (score >= 0.75) {
            return "BUY";
        } else if (score >= 0.4) {
            return "HOLD";
        } else {
            return "SELL";
        }
    }
}
