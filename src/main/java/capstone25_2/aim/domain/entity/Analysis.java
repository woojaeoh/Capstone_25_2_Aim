package capstone25_2.aim.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Analysis {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hiddenOpinion;
    private String keywords;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

}
