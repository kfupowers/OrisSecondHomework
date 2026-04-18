package ru.kpfu.itis.shakirov.model;

import jakarta.persistence.*;

@Entity
@Table(name = "method_metrics", schema = "oris",
        uniqueConstraints = @UniqueConstraint(columnNames = "method_key"))
public class MethodMetricEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "method_key", nullable = false, unique = true)
    private String methodKey;

    @Column(name = "success_count")
    private long successCount = 0;

    @Column(name = "failure_count")
    private long failureCount = 0;

    public MethodMetricEntity() {}

    public MethodMetricEntity(String methodKey) {
        this.methodKey = methodKey;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMethodKey() { return methodKey; }
    public void setMethodKey(String methodKey) { this.methodKey = methodKey; }
    public long getSuccessCount() { return successCount; }
    public void setSuccessCount(long successCount) { this.successCount = successCount; }
    public long getFailureCount() { return failureCount; }
    public void setFailureCount(long failureCount) { this.failureCount = failureCount; }

    public long getTotalCount() {
        return successCount + failureCount;
    }
}