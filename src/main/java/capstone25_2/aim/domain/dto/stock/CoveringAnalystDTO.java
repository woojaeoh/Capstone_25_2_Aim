package capstone25_2.aim.domain.dto.stock;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoveringAnalystDTO {
    private Long analystId;                // 애널리스트 ID
    private String analystName;            // 애널리스트 이름
    private String firmName;               // 증권사 이름
    private Integer latestTargetPrice;     // 최신 목표가
    private LocalDate latestReportDate;    // 최신 리포트 날짜
    private String latestOpinion;          // 최신 의견 (BUY/HOLD/SELL)

    // 애널리스트 지표 (metrics)
    private Double accuracyRate;           // 예측 정답률 (%)
    private Double returnRate;             // 평균 수익률 (%)
    private Double targetDiffRate;         // 목표가 오차율 (%)
    private Double avgReturnDiff;          // 평균 수익 편차
    private Double avgTargetDiff;          // 평균 목표가 편차
}
