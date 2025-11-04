package capstone25_2.aim.repository;

import capstone25_2.aim.domain.entity.Analyst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnalystRepository extends JpaRepository<Analyst, Long> {

    //애널리스트 정보 조회 및 소속 증권사별 리스트
    //findAll() : 기본 제공
    //findByFirmName() : ex) 키움증권 소속 애널리스트 검색
    List<Analyst> findByFirmName(String firmName);

    // 애널리스트 이름과 회사명으로 조회 (AI 모델 데이터 저장용)
    Optional<Analyst> findByAnalystNameAndFirmName(String analystName, String firmName);
}
