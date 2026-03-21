package com.runhub.running.service;

import com.runhub.running.dto.CreateExportTemplateRequest;
import com.runhub.running.dto.ExportTemplateDto;
import com.runhub.running.model.ExportTemplate;
import com.runhub.running.model.TemplateVote;
import com.runhub.running.repository.ExportTemplateRepository;
import com.runhub.running.repository.TemplateVoteRepository;
import com.runhub.users.dto.UserDto;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExportTemplateService {

    private final ExportTemplateRepository templateRepo;
    private final TemplateVoteRepository voteRepo;
    private final UserService userService;

    public ExportTemplateService(ExportTemplateRepository templateRepo,
                                  TemplateVoteRepository voteRepo,
                                  UserService userService) {
        this.templateRepo = templateRepo;
        this.voteRepo = voteRepo;
        this.userService = userService;
    }

    public List<ExportTemplateDto> getAllTemplates(String email) {
        User user = userService.getUserEntityByEmail(email);
        return templateRepo.findAllByOrderByVotesDesc().stream()
                .map(t -> toDto(t, user.getId()))
                .collect(Collectors.toList());
    }

    public ExportTemplateDto createTemplate(String email, CreateExportTemplateRequest req) {
        User user = userService.getUserEntityByEmail(email);
        ExportTemplate template = new ExportTemplate();
        template.setCreatorId(user.getId());
        template.setName(req.getName());
        template.setDescription(req.getDescription());
        template.setCssLayout(req.getCssLayout());
        template.setPreviewUrl(req.getPreviewUrl());
        template.setTags(req.getTags());
        template = templateRepo.save(template);
        return toDto(template, user.getId());
    }

    @Transactional
    public ExportTemplateDto toggleVote(String email, Long templateId) {
        User user = userService.getUserEntityByEmail(email);
        ExportTemplate template = templateRepo.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        var existing = voteRepo.findByUserIdAndTemplateId(user.getId(), templateId);
        if (existing.isPresent()) {
            voteRepo.delete(existing.get());
            template.setVotes(Math.max(0, template.getVotes() - 1));
        } else {
            TemplateVote vote = new TemplateVote();
            vote.setUserId(user.getId());
            vote.setTemplateId(templateId);
            voteRepo.save(vote);
            template.setVotes(template.getVotes() + 1);
        }
        template = templateRepo.save(template);
        return toDto(template, user.getId());
    }

    @Transactional
    public ExportTemplateDto incrementDownloads(Long templateId) {
        ExportTemplate template = templateRepo.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));
        template.setDownloads(template.getDownloads() + 1);
        template = templateRepo.save(template);
        return toDto(template, null);
    }

    private ExportTemplateDto toDto(ExportTemplate t, Long userId) {
        ExportTemplateDto dto = new ExportTemplateDto();
        dto.setId(t.getId());
        dto.setCreatorId(t.getCreatorId());
        dto.setName(t.getName());
        dto.setDescription(t.getDescription());
        dto.setCssLayout(t.getCssLayout());
        dto.setPreviewUrl(t.getPreviewUrl());
        dto.setVotes(t.getVotes());
        dto.setDownloads(t.getDownloads());
        dto.setCreatedAt(t.getCreatedAt());
        dto.setTags(t.getTags());
        dto.setHasVoted(userId != null && voteRepo.existsByUserIdAndTemplateId(userId, t.getId()));

        try {
            UserDto creator = userService.getUserById(t.getCreatorId());
            dto.setCreatorUsername(creator.getUsername());
        } catch (Exception e) {
            dto.setCreatorUsername("unknown");
        }

        return dto;
    }
}
