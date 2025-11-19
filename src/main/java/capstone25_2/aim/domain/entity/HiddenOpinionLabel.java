package capstone25_2.aim.domain.entity;

public enum HiddenOpinionLabel {
    STRONG_BUY,
    BUY,
    HOLD,
    SELL,
    STRONG_SELL;

    /**
     * hiddenOpinion 숫자 값을 라벨로 변환
     * 0.0 ~ 0.2: STRONG_SELL
     * 0.2 ~ 0.4: SELL
     * 0.4 ~ 0.6: HOLD
     * 0.6 ~ 0.8: BUY
     * 0.8 ~ 1.0: STRONG_BUY
     */
    public static HiddenOpinionLabel fromScore(Double score) {
        if (score == null) {
            return null;
        }

        if (score >= 0.8) {
            return STRONG_BUY;
        } else if (score >= 0.6) {
            return BUY;
        } else if (score >= 0.4) {
            return HOLD;
        } else if (score >= 0.2) {
            return SELL;
        } else {
            return STRONG_SELL;
        }
    }
}
