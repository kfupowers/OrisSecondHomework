package ru.kpfu.itis.shakirov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.kpfu.itis.shakirov.model.MethodMetricEntity;

import java.util.Optional;

public interface MethodMetricRepository extends JpaRepository<MethodMetricEntity, Long> {

    Optional<MethodMetricEntity> findByMethodKey(String methodKey);

    @Modifying
    @Transactional
    @Query("UPDATE MethodMetricEntity m SET m.successCount = m.successCount + 1 WHERE m.methodKey = :key")
    int incrementSuccess(@Param("key") String key);

    @Modifying
    @Transactional
    @Query("UPDATE MethodMetricEntity m SET m.failureCount = m.failureCount + 1 WHERE m.methodKey = :key")
    int incrementFailure(@Param("key") String key);
}