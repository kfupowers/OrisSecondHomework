package ru.kpfu.itis.shakirov.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "benchmark_records", schema = "oris")
public class BenchmarkRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "method_key", nullable = false)
    private String methodKey;

    @Column(name = "duration_nanos", nullable = false)
    private long durationNanos;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public BenchmarkRecordEntity() {}

    public BenchmarkRecordEntity(String methodKey, long durationNanos) {
        this.methodKey = methodKey;
        this.durationNanos = durationNanos;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMethodKey() { return methodKey; }
    public void setMethodKey(String methodKey) { this.methodKey = methodKey; }
    public long getDurationNanos() { return durationNanos; }
    public void setDurationNanos(long durationNanos) { this.durationNanos = durationNanos; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}