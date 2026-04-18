package ru.kpfu.itis.shakirov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kpfu.itis.shakirov.model.BenchmarkRecordEntity;

import java.util.List;

public interface BenchmarkRecordRepository extends JpaRepository<BenchmarkRecordEntity, Long> {

    List<BenchmarkRecordEntity> findByMethodKey(String methodKey);

    @Query("SELECT b.methodKey, COUNT(b), MIN(b.durationNanos), MAX(b.durationNanos), AVG(b.durationNanos) " +
            "FROM BenchmarkRecordEntity b GROUP BY b.methodKey")
    List<Object[]> getAggregatedStats();

    @Query(value = "SELECT duration_nanos FROM benchmark_records WHERE method_key = :key ORDER BY duration_nanos",
            nativeQuery = true)
    List<Long> findAllDurationsSorted(@Param("key") String key);
}