package com.runhub.running.repository;

import com.runhub.running.model.ExportTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExportTemplateRepository extends JpaRepository<ExportTemplate, Long> {
    List<ExportTemplate> findAllByOrderByVotesDesc();
    List<ExportTemplate> findByCreatorIdOrderByCreatedAtDesc(Long creatorId);
}
