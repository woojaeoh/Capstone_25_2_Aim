package capstone25_2.aim.service;

import capstone25_2.aim.domain.dto.search.AnalystSearchResultDTO;
import capstone25_2.aim.domain.entity.Analyst;
import capstone25_2.aim.domain.entity.SearchLog;
import capstone25_2.aim.repository.AnalystRepository;
import capstone25_2.aim.repository.SearchLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final AnalystRepository analystRepository;
    private final SearchLogRepository searchLogRepository;

    /**
     * 애널리스트 검색 (이름 또는 회사명으로 검색)
     * 이름순으로 정렬
     */
    public List<AnalystSearchResultDTO> searchAnalysts(String keyword) {
        List<Analyst> analysts = analystRepository.findAll().stream()
                .filter(analyst ->
                    analyst.getAnalystName().contains(keyword) ||
                    analyst.getFirmName().contains(keyword))
                .sorted(Comparator.comparing(Analyst::getAnalystName))  // 이름순 정렬
                .collect(Collectors.toList());

        return analysts.stream()
                .map(analyst -> AnalystSearchResultDTO.builder()
                        .analystId(analyst.getId())
                        .analystName(analyst.getAnalystName())
                        .firmName(analyst.getFirmName())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 애널리스트 검색 로그 저장
     */
    @Transactional
    public void logAnalystSearch(Long analystId) {
        Analyst analyst = analystRepository.findById(analystId)
                .orElseThrow(() -> new RuntimeException("Analyst not found"));

        SearchLog log = new SearchLog();
        log.setAnalyst(analyst);
        searchLogRepository.save(log);
    }
}
