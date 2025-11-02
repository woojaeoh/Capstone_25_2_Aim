package capstone25_2.aim.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Analyst {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="analyst_id")
    private Long id;

    private String analystName; //@Column을 붙이지 않아도 자동으로 컬럼 이름 매핑.

    private String firmName;

    @OneToMany(mappedBy= "analyst",cascade= CascadeType.ALL)
    private List<Report> reports = new ArrayList<>();

    /**
     *
        To One은 기본이 EAGER이기에 꼭 LAZY로 전부 변경해줘야 함
        + 컬렉션은 필드 초기화.
     */
    @OneToOne(mappedBy = "analyst",cascade = CascadeType.ALL, fetch = FetchType.LAZY) //주로 access를 많이 하는 analyst쪽에 fk를 놓는다.
    private AnalystMetrics analystMetrics = new AnalystMetrics();
}
