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
public class Stock {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="stock_id")
    private Long id;

    private String stockCode;

    private String stockName;

    private String sector;

    //to many 관계는 기본이 LAZY
    @OneToMany(mappedBy ="stock", cascade= CascadeType.ALL) //mappedBy -> 나는 연관관계의 주인이 아니다.
    private List<Report> reports = new ArrayList<>();

}
