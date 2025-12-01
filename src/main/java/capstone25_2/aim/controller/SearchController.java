package capstone25_2.aim.controller;

import capstone25_2.aim.domain.dto.search.AnalystSearchResultDTO;
import capstone25_2.aim.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 애널리스트 검색 (이름 또는 회사명)
     * 드롭다운 표시용 - 로그 저장 X
     */
    @GetMapping
    @Operation(summary = "애널리스트 검색 (이름 또는 회사명)",
            description = "애널리스트 검색 (이름 또는 회사명)하면 드롭다운으로 매칭되는 리스트 보여줌." )
    public List<AnalystSearchResultDTO> searchAnalysts(@RequestParam String keyword) {
        return searchService.searchAnalysts(keyword);
    }

    /**
     * 애널리스트 검색 로그 저장
     * 사용자가 드롭다운에서 특정 애널리스트를 클릭했을 때 호출
     */
    @PostMapping("/log/{analystId}")
    @Operation(summary = "애널리스트 검색 로그 저장. ",
            description = "애널리스트 검색 (이름 또는 회사명)하면 해당 애널리스트 검색 수 count 하나씩 증가."+
                    "검색 로그를 저장해 count+=1하면서 , 최근 7일동안 가장 hot한 애널리스트 top3제공"
    )
    public void logAnalystSearch(@PathVariable Long analystId) {
        searchService.logAnalystSearch(analystId);
    }
}
