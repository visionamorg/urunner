package com.runhub.running.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "template_votes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"userId", "templateId"})
})
public class TemplateVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long templateId;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public TemplateVote() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
