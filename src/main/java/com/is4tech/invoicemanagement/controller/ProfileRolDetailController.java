package com.is4tech.invoicemanagement.controller;

import java.util.List;
import java.util.ArrayList;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.is4tech.invoicemanagement.dto.ProfileRoleDetailDto;
import com.is4tech.invoicemanagement.dto.ProfileRoleDetailDtoId;
import com.is4tech.invoicemanagement.exception.ResourceNorFoundException;
import com.is4tech.invoicemanagement.model.ProfileRoleDetailId;
import com.is4tech.invoicemanagement.service.ProfileRoleDetailService;
import com.is4tech.invoicemanagement.utils.Message;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/invoice-management/v0.1/")
public class ProfileRolDetailController {

    private static final String ID_ENTITY = "profile_rol_detail_id";
    
    @Autowired
    private ProfileRoleDetailService profileRoleDetailService;

    private static final String NAME_ENTITY = "ProfileRolDetail";

    @PostMapping("/profile-rol-detail")
    public ResponseEntity<Message> saveProfileRolDetail(@RequestBody @Valid ProfileRoleDetailDtoId profileRoleDetailDtoId) throws BadRequestException{
        ProfileRoleDetailDto profileRoleDetailDtoSave = null;
        try {
            Integer profileId = profileRoleDetailDtoId.getProfileId();
            List<Integer> rolsId = profileRoleDetailService.existByIdProfileRolNotIncluidesDetail(profileRoleDetailDtoId.getProfileId(),profileRoleDetailDtoId);

            if(!(rolsId.isEmpty())){
                for (Integer rolsIdModific : rolsId) {
                    deleteProfileRolDetail(profileId, rolsIdModific);
                }
            }


            for (Integer busqueda : profileRoleDetailDtoId.getRols()) {
                ProfileRoleDetailId detailId = ProfileRoleDetailId.builder()
                                                        .profileId(profileId)
                                                        .roleId(busqueda)
                                                        .build();
                
                if(!(profileRoleDetailService.existByIdProfileRolDetail(detailId))){
                    ProfileRoleDetailDtoId detailSave = ProfileRoleDetailDtoId.builder()
                                                            .profileId(profileId)
                                                            .roleId(busqueda)
                                                            .build();
                    profileRoleDetailDtoSave = profileRoleDetailService.saveProfileRoleDetail(detailSave);
                }
            }
            return new ResponseEntity<>(Message.builder()
                .note("Saved successfully")
                .object(profileRoleDetailDtoSave)
                .build(),
                HttpStatus.CREATED);
        } catch (DataAccessException e) {
            throw new BadRequestException("Error save record: " + e.getMessage());
        }
    }

    @DeleteMapping("/profile-rol-detail/{idProfile}/{idRol}")
    public ResponseEntity<Message> deleteProfileRolDetail(@PathVariable Integer idProfile, @PathVariable Integer idRol) throws BadRequestException{
        try {
            profileRoleDetailService.deleteProfileRolDetailByIds(idProfile, idRol);
            return new ResponseEntity<>(Message.builder()
                                        .object(null)
                                        .build(),
                                        HttpStatus.NO_CONTENT);
        } catch (DataAccessException e) {
            throw new BadRequestException("Error deleting record: " + e.getMessage());
        }
    }

    @GetMapping("/profile-rol-detail/{idProfile}/{idRol}")
    public ResponseEntity<Message> showByIdProfile(@PathVariable Integer idProfile, @PathVariable Integer idRol){
        ProfileRoleDetailId idBusqueda = ProfileRoleDetailId.builder().profileId(idProfile).roleId(idRol).build();
        ProfileRoleDetailDto profileRoleDetailDto = profileRoleDetailService.finByIdProfileRoleDetail(idBusqueda);
        if(profileRoleDetailDto == null)
            throw new ResourceNorFoundException(NAME_ENTITY, ID_ENTITY, idBusqueda.toString());

        return new ResponseEntity<>(Message.builder()
                                    .note("Record found")
                                    .object(ProfileRoleDetailDto.builder()
                                            .profile(profileRoleDetailDto.getProfile())
                                            .roles(profileRoleDetailDto.getRoles())
                                            .build())
                                    .build(),
                                    HttpStatus.OK);
    }

    @GetMapping("/profile-rol-details")
    public ResponseEntity<Message> showAllProfiles(@PageableDefault(size = 10) Pageable pageable){
        List<ProfileRoleDetailDto> profileRolDetailsId = profileRoleDetailService.listAllProfileRolDetail(pageable);
        if(profileRolDetailsId.isEmpty())
            throw new ResourceNorFoundException(NAME_ENTITY);

        return new ResponseEntity<>(Message.builder()
                            .note("Records found")
                            .object(profileRolDetailsId)
                            .build(),
                            HttpStatus.OK);
    }
}
