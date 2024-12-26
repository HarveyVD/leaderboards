package com.dao.quiz.models.domain;

import com.dao.quiz.models.domain.base.AuditableDatesEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Table(name = "quizzes")
@Entity
public class Quiz extends AuditableDatesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String topic;
}
