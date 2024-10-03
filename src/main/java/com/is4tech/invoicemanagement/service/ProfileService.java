package com.is4tech.invoicemanagement.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.is4tech.invoicemanagement.model.Profile;
import com.is4tech.invoicemanagement.dto.ProfileDto;
import com.is4tech.invoicemanagement.repository.ProfileRespository;

@Service
public class ProfileService {
  
  private final ProfileRespository profileRespository;

  public ProfileService(ProfileRespository profileRespository) {
    this.profileRespository = profileRespository;
  }

  public Page<Profile> listAllProfile(Pageable pageable){
    return profileRespository.findAll(pageable);
  }

  @Transactional
  public Profile saveProfile(ProfileDto profileDto){
    Profile profile = Profile.builder()
                        .profileId(profileDto.getProfileId())
                        .name(profileDto.getName())
                        .description(profileDto.getDescription())
                        .status(profileDto.getStatus())
                        .build();
    return profileRespository.save(profile);
  }

  @Transactional(readOnly = true)
  public ProfileDto finByIdProfile(Integer id){
    return toDto(profileRespository.findById(id).orElse(null));
  }

  @Transactional
  public void deleteProfile(ProfileDto profileDto){
    Profile profile = Profile.builder()
      .profileId(profileDto.getProfileId())
      .name(profileDto.getName())
      .description(profileDto.getDescription())
      .status(profileDto.getStatus())
      .roles(null)
      .build();
    profileRespository.delete(profile);
  }

  public boolean existById(Integer id){
    return profileRespository.existsById(id);
  }

  private ProfileDto toDto(Profile profile){
    return ProfileDto.builder()
      .profileId(profile.getProfileId())
      .name(profile.getName())
      .description(profile.getDescription())
      .status(profile.getStatus())
      .build();
  }

}
