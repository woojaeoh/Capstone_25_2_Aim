package capstone25_2.aim.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
public class AnalystMetrics {

    @Id @GeneratedValue
    private Long id;

    private Float accuracyRate; //정확도
    private Float returnRate; //수익률
    private Float targetDiffRate; //목표가 오차율
    private Float avgReturnDiff; //애널리스트 평균대비 수익률 오차
    private Float avgTargetDiff; //애널리스트 평균 대비 목표가 오차율

    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analyst_id")
    private Analyst analyst;

    @PrePersist
    public void prePersist() {
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
