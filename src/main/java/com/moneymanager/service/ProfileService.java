package com.moneymanager.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.moneymanager.dto.ProfileDTO;
import com.moneymanager.entity.ProfileEntity;
import com.moneymanager.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final EmailService emailService;

    public ProfileDTO registerProfile(ProfileDTO profileDTO) {
        ProfileEntity profileEntity = toEntity(profileDTO);
        profileEntity.setActivationToken(UUID.randomUUID().toString());
        profileEntity = profileRepository.save(profileEntity);

        String activationLink = "http://localhost:8080/api/v1.0/activate?token=" + profileEntity.getActivationToken();
        String subject = "Activate Your MoneyManager Account";
        String body = "Click the following link to activate your account: " + activationLink;
        emailService.sendEmail(profileEntity.getEmail(), subject, body);

        profileDTO = toDTO(profileEntity);
        return profileDTO;
    }

    public ProfileEntity toEntity(ProfileDTO profileDTO) {
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(profileDTO.getPassword())
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }

    public ProfileDTO toDTO(ProfileEntity profileEntity) {
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    public boolean activateProfile(String activationToken) {
        return profileRepository.findByActivationToken(activationToken)
                .map(profile -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                }).orElse(false);
    }
}
