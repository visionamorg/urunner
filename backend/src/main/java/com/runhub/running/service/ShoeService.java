package com.runhub.running.service;

import com.runhub.running.model.Shoe;
import com.runhub.running.repository.ShoeRepository;
import com.runhub.users.model.User;
import com.runhub.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ShoeService {

    private final ShoeRepository shoeRepository;
    private final UserService userService;

    public List<Map<String, Object>> getShoes(String email) {
        User user = userService.getUserEntityByEmail(email);
        return shoeRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toMap).toList();
    }

    @Transactional
    public Map<String, Object> createShoe(Map<String, Object> req, String email) {
        User user = userService.getUserEntityByEmail(email);
        boolean isDefault = Boolean.TRUE.equals(req.get("isDefault"));

        if (isDefault) {
            shoeRepository.findByUserIdAndIsDefaultTrue(user.getId())
                    .ifPresent(s -> { s.setIsDefault(false); shoeRepository.save(s); });
        }

        Shoe shoe = Shoe.builder()
                .user(user)
                .brand((String) req.get("brand"))
                .model((String) req.get("model"))
                .nickname((String) req.getOrDefault("nickname", ""))
                .maxDistanceKm(Double.parseDouble(req.getOrDefault("maxDistanceKm", "800").toString()))
                .isDefault(isDefault)
                .build();
        return toMap(shoeRepository.save(shoe));
    }

    @Transactional
    public Map<String, Object> retireShoe(Long shoeId, String email) {
        User user = userService.getUserEntityByEmail(email);
        Shoe shoe = shoeRepository.findById(shoeId)
                .orElseThrow(() -> new RuntimeException("Shoe not found"));
        if (!shoe.getUser().getId().equals(user.getId())) throw new RuntimeException("Unauthorized");
        shoe.setRetired(true);
        shoe.setIsDefault(false);
        return toMap(shoeRepository.save(shoe));
    }

    @Transactional
    public void addDistance(Long shoeId, double distanceKm) {
        shoeRepository.findById(shoeId).ifPresent(shoe -> {
            shoe.setTotalDistanceKm(shoe.getTotalDistanceKm() + distanceKm);
            shoeRepository.save(shoe);
        });
    }

    private Map<String, Object> toMap(Shoe s) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", s.getId());
        m.put("brand", s.getBrand());
        m.put("model", s.getModel());
        m.put("nickname", s.getNickname());
        m.put("maxDistanceKm", s.getMaxDistanceKm());
        m.put("totalDistanceKm", s.getTotalDistanceKm());
        m.put("wearPercent", s.getWearPercent());
        m.put("isDefault", s.getIsDefault());
        m.put("retired", s.getRetired());
        String status = s.getWearPercent() >= 100 ? "CRITICAL" : s.getWearPercent() >= 80 ? "WARNING" : "GOOD";
        m.put("status", status);
        return m;
    }
}
