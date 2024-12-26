package com.dao.quiz.models.domain.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableDatesEntity {
    @Column(name="created_at")
    @CreationTimestamp
    private Instant createdAt;

    @Column(name="updated_at")
    @UpdateTimestamp
    private Instant updatedAt;
}
