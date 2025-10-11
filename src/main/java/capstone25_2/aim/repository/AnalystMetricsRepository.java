package capstone25_2.aim.repository;

import capstone25_2.aim.domain.entity.AnalystMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnalystMetricsRepository extends JpaRepository<AnalystMetrics, Long> {
    Optional<AnalystMetrics> findByAnalystId(Long analystId);

  //  List<AnalystMetrics> findAll();
}
