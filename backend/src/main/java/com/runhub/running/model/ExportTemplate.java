package com.runhub.running.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "export_templates")
public class ExportTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long creatorId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String cssLayout;

    @Column(length = 500)
    private String previewUrl;

    @Column(nullable = false)
    private Integer votes = 0;

    @Column(nullable = false)
    private Integer downloads = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ExportTemplate() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCreatorId() { return creatorId; }
    public void setCreatorId(Long creatorId) { this.creatorId = creatorId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCssLayout() { return cssLayout; }
    public void setCssLayout(String cssLayout) { this.cssLayout = cssLayout; }

    public String getPreviewUrl() { return previewUrl; }
    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }

    public Integer getVotes() { return votes; }
    public void setVotes(Integer votes) { this.votes = votes; }

    public Integer getDownloads() { return downloads; }
    public void setDownloads(Integer downloads) { this.downloads = downloads; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
