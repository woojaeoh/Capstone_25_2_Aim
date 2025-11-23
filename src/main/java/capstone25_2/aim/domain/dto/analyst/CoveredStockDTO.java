package capstone25_2.aim.domain.dto.analyst;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoveredStockDTO {
    private Long stockId;           // 종목 ID
    private String stockName;       // 종목명
    private String stockCode;       // 종목코드
    private String sector;          // 섹터
    private Integer reportCount;    // 해당 종목에 대한 리포트 수
}
