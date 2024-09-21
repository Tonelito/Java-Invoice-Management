package com.is4tech.invoicemanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.is4tech.invoicemanagement.model.Profile;
import com.is4tech.invoicemanagement.dto.ProfileDto;
import com.is4tech.invoicemanagement.repository.ProfileRespository;

@Service
public class ProfileService {
  
  @Autowired
  private ProfileRespository profileRespository;

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
  public Profile finByIdProfile(Integer id){
    return profileRespository.findById(id).orElse(null);
  }

  @Transactional
  public void deleteProfile(Profile profile){
    profileRespository.delete(profile);
  }

  public boolean existById(Integer id){
    return profileRespository.existsById(id);
  }

}
