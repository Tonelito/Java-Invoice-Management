package com.is4tech.invoicemanagement.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.is4tech.invoicemanagement.dto.NameSearchDto;
import com.is4tech.invoicemanagement.dto.ProfileDto;
import com.is4tech.invoicemanagement.dto.ProfileRoleDetailDtoId;
import com.is4tech.invoicemanagement.dto.RolDto;
import com.is4tech.invoicemanagement.exception.BadRequestException;
import com.is4tech.invoicemanagement.exception.ResourceNorFoundException;
import com.is4tech.invoicemanagement.model.ProfileRoleDetail;
import com.is4tech.invoicemanagement.model.ProfileRoleDetailId;
import com.is4tech.invoicemanagement.model.Rol;
import com.is4tech.invoicemanagement.service.AuditService;
import com.is4tech.invoicemanagement.service.ProfileRoleDetailService;
import com.is4tech.invoicemanagement.service.ProfileService;
import com.is4tech.invoicemanagement.service.RolService;
import com.is4tech.invoicemanagement.utils.Message;
import com.is4tech.invoicemanagement.utils.MessagePage;

import jakarta.servlet.http.HttpServletRequest;

class ProfileControlerTest {
 
    @Mock
    private AuditService auditService;

    @Mock
    private ProfileService profileService;

    @Mock 
    private HttpServletRequest request;

    @InjectMocks
    private ProfileController profileController;

    @Mock
    private ProfileRoleDetailService profileRoleDetailService;

    @Mock
    private RolService rolService;

    @InjectMocks
    private ProfileController controllerSpy;

    private ProfileDto profileDto;
    private RolDto rolDto;
    private List<RolDto> savedRols;
    private List<Integer> rols = new ArrayList<>();
    private List<ProfileRoleDetail> savedProfileRoleDetails;
    private int id = 1;

    private static final String NAME_ENTITY = "Profile";
    private static final String ID_ENTITY = "profile_id";
    private static final String UNEXPEDTED_ERROR = "Unexpected error occurred: Unexpected error";
    private static final String NOT_FOUND = "No se encontraron registros de Profile en el sistema";
    private static final String DB_ERROR = "Unexpected error occurred: DB error";
    private static final String MESSAJE_UNEXPEDTED = "Unexpected error";
    private static final String MESSAJE_DB_ERROR = "DB error";
    private static final String MESSAJE_NOT_FOUND = "No Encontrado en el sistema en el sistema";

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        savedRols = new ArrayList<>();
        rolDto = RolDto.builder()
            .rolId(id)
            .name("ROLE_TEMP")
            .build();
        savedRols.add(rolDto);

        //Investigacion pendiente
        savedProfileRoleDetails = List.of(new ProfileRoleDetail(new ProfileRoleDetailId(id, id), 
            null, new Rol(id, "ADMIN", "CODE", true, savedProfileRoleDetails)));
        
        rols.add(1);
        rols.add(2);
        rols.add(3);
        
        profileDto = ProfileDto.builder()
            .profileId(id)
            .name("ADMIN")
            .description("Admin temp")
            .status(true)
            .rolsId(rols)
            .build();
    }

    @Test
    void testSaveProfile() {
        controllerSpy = Mockito.spy(profileController);

        when(profileService.saveProfile(any(ProfileDto.class), any(HttpServletRequest.class)))
                .thenReturn(profileDto);
        
        when(rolService.findByIdRol(anyInt())).thenReturn(rolDto);

        ResponseEntity<Message> response = controllerSpy.saveProfile(profileDto, request);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Saved successfully", response.getBody().getNote());
        
        for (Integer rolId : profileDto.getRolsId()) {
            verify(rolService, atLeastOnce()).findByIdRol(rolId);
        }

        //Se multiplica por 4 hace que se llame 4 veces por cada rol.
        verify(rolService, times(profileDto.getRolsId().size() * 4)).findByIdRol(anyInt());
    }
        

    @Test
    void testSaveProfileDataAccessException() {
        when(profileService.saveProfile(any(ProfileDto.class), any(HttpServletRequest.class)))
                .thenThrow(new DataAccessException(MESSAJE_DB_ERROR) {});

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            profileController.saveProfile(profileDto, request);
        });

        assertEquals("Error saving record: DB error", exception.getMessage());
    }

    @Test
    void saveProfileResourceNorFoundException() {
        when(profileService.saveProfile(any(ProfileDto.class), any(HttpServletRequest.class)))
                .thenThrow(new ResourceNorFoundException(MESSAJE_NOT_FOUND));

        ResourceNorFoundException exception = assertThrows(ResourceNorFoundException.class, () -> {
            profileController.saveProfile(profileDto, request);
        });

        assertEquals(NOT_FOUND, exception.getMessage());
    }

    @Test
    void saveProfileException() {
        when(profileService.saveProfile(any(ProfileDto.class), any(HttpServletRequest.class)))
                .thenThrow(new RuntimeException(MESSAJE_UNEXPEDTED));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            profileController.saveProfile(profileDto, request);
        });

        assertEquals(UNEXPEDTED_ERROR, exception.getMessage());
    }

    @Test
    void updateProfileSuccess() {
        when(profileService.existsById(anyInt())).thenReturn(true);
        when(profileService.updateProfile(anyInt(), any(ProfileDto.class), any(HttpServletRequest.class)))
                .thenReturn(profileDto);
        when(rolService.findByIdRol(anyInt())).thenReturn(rolDto);

        ResponseEntity<Message> response = profileController.updateProfile(profileDto, id, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Update successfully", response.getBody().getNote());

        verify(profileService, atLeastOnce()).updateProfile(anyInt(), any(ProfileDto.class), any(HttpServletRequest.class));
        verify(rolService, times(profileDto.getRolsId().size() * 4)).findByIdRol(anyInt());
    }

    @Test
    void updateProfileResourceNotFoundException() {
        when(profileService.existsById(id)).thenReturn(false);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            profileController.updateProfile(profileDto, id, request);
        });

        assertEquals("Unexpected error occurred: Profile was not found with: profile_id = '1'", exception.getMessage());
    }

    @Test
    void updateProfileDataAccessException() {
        when(profileService.existsById(anyInt())).thenReturn(true);
        when(profileService.updateProfile(anyInt(), any(ProfileDto.class), any(HttpServletRequest.class)))
                .thenThrow(new DataAccessException(MESSAJE_DB_ERROR) {});

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            profileController.updateProfile(profileDto, id, request);
        });

        assertEquals("Error updating record: DB error", exception.getMessage());
    }

    @Test
    void updateProfileException() {
        when(profileService.existsById(id)).thenReturn(true);
        when(profileService.updateProfile(anyInt(), any(ProfileDto.class), any(HttpServletRequest.class)))
                .thenThrow(new RuntimeException(MESSAJE_UNEXPEDTED));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            profileController.updateProfile(profileDto, id, request);
        });

        assertEquals(UNEXPEDTED_ERROR, exception.getMessage());
    }

    @Test
    void statusChangeProfile(){
        when(profileService.existsById(id)).thenReturn(true);
        when(profileService.findByIdProfile(id)).thenReturn(profileDto);

        ResponseEntity<Message> response = profileController.statusChangeProfile(1, request);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Update successfully", response.getBody().getNote());
    
        verify(profileService, times(1))
            .saveProfile(any(ProfileDto.class), any(HttpServletRequest.class));
    }

    @Test
    void statusChangeProfileResourceNorFoundException() {
        when(profileService.existsById(2)).thenReturn(false);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            profileController.statusChangeProfile(2, request);
        });
        
        assertEquals("Unexpected error occurred: Profile was not found with: profile_id = '2'", 
            exception.getMessage());

        verify(profileService).existsById(2);
    }

    @Test
    void statusChangeProfileDataAccessException() {
        when(profileService.existsById(id)).thenReturn(true);
        when(profileService.findByIdProfile(id)).thenReturn(profileDto);
        when(profileService.saveProfile(any(), any(HttpServletRequest.class)))
                .thenThrow(new DataAccessException(MESSAJE_DB_ERROR) {});
        when(profileController.statusChangeProfile(id, any(HttpServletRequest.class)))
                .thenThrow(new DataAccessException(MESSAJE_DB_ERROR) {});

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            profileController.statusChangeProfile(id, request);
        });

        assertEquals("Error updating record: DB error", exception.getMessage());
    }

    @Test
    void statusChangeProfileException() {
        when(profileService.existsById(id)).thenReturn(true);
        when(profileService.findByIdProfile(id)).thenReturn(profileDto);
        when(profileService.saveProfile(any(), any(HttpServletRequest.class)))
                .thenThrow(new BadRequestException(MESSAJE_DB_ERROR) {});
        when(profileController.statusChangeProfile(id, any(HttpServletRequest.class)))
                .thenThrow(new RuntimeException(MESSAJE_UNEXPEDTED) {});

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            profileController.saveProfile(profileDto, request);
        });

        assertEquals(DB_ERROR, exception.getMessage());
    }

    @Test
    void deleteProfile() {
        when(profileService.existsById(id)).thenReturn(true);
        when(profileService.findByIdProfile(id)).thenReturn(profileDto);

        ResponseEntity<Message> response = profileController.deleteProfile(id, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(profileService).deleteProfile(profileDto, request);
    }

    @Test
    void deleteProfileResourceNotFoundException() {
        when(profileService.existsById(id)).thenReturn(false);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            profileController.deleteProfile(id, request);
        });

        assertEquals("Rol not found: Profile was not found with: profile_id = '1'", exception.getMessage());
        
        verify(profileService).existsById(id);
    }

    @Test
    void deleteProfileDataAccessException() {
        when(profileService.existsById(id)).thenReturn(true);
        when(profileService.findByIdProfile(id)).thenReturn(profileDto);
        doThrow(new DataAccessException(MESSAJE_DB_ERROR) {})
            .when(profileService).deleteProfile(any(ProfileDto.class), any(HttpServletRequest.class));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            profileController.deleteProfile(id, request);
        });

        assertEquals("Error deleting record: DB error", exception.getMessage());

        verify(profileService).deleteProfile(any(ProfileDto.class), any(HttpServletRequest.class));
    }

    @Test
    void deleteProfileGeneralException() {
        when(profileService.existsById(id)).thenReturn(true);
        when(profileService.findByIdProfile(id)).thenReturn(profileDto);
        doThrow(new RuntimeException(MESSAJE_UNEXPEDTED))
            .when(profileService).deleteProfile(any(ProfileDto.class), any(HttpServletRequest.class));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            profileController.deleteProfile(id, request);
        });

        assertEquals(UNEXPEDTED_ERROR, exception.getMessage());

        verify(profileService).deleteProfile(profileDto, request);
    }

    @Test
    void showAllProfiles() {
        Pageable pageable = PageRequest.of(0, 10);

        when(profileService.listAllProfile(any(Pageable.class))).thenReturn(null);
    
        ResponseEntity<Message> response = profileController.showAllProfiles(pageable, request);
    
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Records found", response.getBody().getNote());
        verify(profileService, times(1)).listAllProfile(any(Pageable.class));
    }

    @Test
    void showByIdProfile() {
        when(profileService.findByIdProfile(id)).thenReturn(profileDto);
    
        when(profileRoleDetailService.findByIdProfileRol(id)).thenReturn(savedProfileRoleDetails);
    
        when(rolService.findByIdRol(anyInt())).thenReturn(rolDto);
    
        ResponseEntity<Message> response = profileController.showByIdProfile(id, request);
    
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Record found", response.getBody().getNote());
        verify(profileService, times(1)).findByIdProfile(anyInt());
    }

    @Test
    void showByIdProfileNotFound() {
        when(profileService.findByIdProfile(id)).thenReturn(null);

        ResourceNorFoundException exception = assertThrows(ResourceNorFoundException.class, () -> {
            profileController.showByIdProfile(id, request);
        });

        assertEquals(NAME_ENTITY + " was not found with: " + ID_ENTITY + " = '" + id + "'", exception.getMessage());

        verify(profileService).findByIdProfile(id);
    }

    @Test
    void showByIdProfileUnexpectedException() {
        when(profileService.findByIdProfile(id)).thenThrow(new RuntimeException(MESSAJE_UNEXPEDTED));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            profileController.showByIdProfile(id, request);
        });

        assertEquals(UNEXPEDTED_ERROR, exception.getMessage());

        verify(profileService).findByIdProfile(id);
    }

    @Test
    void showByNameProfile() {
        NameSearchDto nameSearchDto = new NameSearchDto();
        nameSearchDto.setName("Test");
        Pageable pageable = mock(Pageable.class);
        MessagePage mockMessagePage = new MessagePage();

        when(profileService.findByNameProfile(nameSearchDto.getName(), pageable)).thenReturn(mockMessagePage);

        ResponseEntity<MessagePage> response = profileController.showByNameProfile(nameSearchDto, pageable, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockMessagePage, response.getBody());

        verify(profileService).findByNameProfile(nameSearchDto.getName(), pageable);
    }

    @Test
    void showByNameProfileNotFound() {
        NameSearchDto nameSearchDto = new NameSearchDto();
        nameSearchDto.setName("NonExistent");
        Pageable pageable = mock(Pageable.class);

        when(profileService.findByNameProfile(nameSearchDto.getName(), pageable))
            .thenThrow(new ResourceNorFoundException(NAME_ENTITY));

        ResourceNorFoundException exception = assertThrows(ResourceNorFoundException.class, () -> {
            profileController.showByNameProfile(nameSearchDto, pageable, request);
        });

        assertEquals("No se encontraron registros de Profile en el sistema", exception.getMessage());

        verify(profileService).findByNameProfile(nameSearchDto.getName(), pageable);
    }

    @Test
    void showByNameProfileUnexpectedException() {
        NameSearchDto nameSearchDto = new NameSearchDto();
        nameSearchDto.setName("Test");
        Pageable pageable = mock(Pageable.class);

        when(profileService.findByNameProfile(nameSearchDto.getName(), pageable))
            .thenThrow(new RuntimeException(MESSAJE_UNEXPEDTED));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            profileController.showByNameProfile(nameSearchDto, pageable, request);
        });

        assertEquals(UNEXPEDTED_ERROR, exception.getMessage());

        verify(profileService).findByNameProfile(nameSearchDto.getName(), pageable);
    }

    @Test
    void shouldDeleteProfileRoleDetailsWhenRolsIdIsNotEmpty() {
        Integer profileId = 1;
        List<Integer> rolsIdModific = List.of(2, 3);

        ProfileRoleDetailDtoId profileRoleDetailDtoId = ProfileRoleDetailDtoId.builder()
            .profileId(profileId)
            .rols(rolsIdModific)
            .build();
        
        when(profileRoleDetailService.existByIdProfileRolNotIncluidesDetail(profileId, profileRoleDetailDtoId))
            .thenReturn(rolsIdModific);
        
        profileController.savedRolId(profileRoleDetailDtoId, request);

        for (Integer rolId : rolsIdModific) {
            verify(profileRoleDetailService, times(1))
                .deleteProfileRolDetailByIds(profileId, rolId, request);
        }
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenNoRolesFound() {
        Integer profileId = 1;
        when(profileRoleDetailService.findByIdProfileRol(profileId)).thenReturn(Collections.emptyList());

        ResourceNorFoundException thrown = assertThrows(
            ResourceNorFoundException.class,
            () -> profileController.getAllRols(profileId)
        );

        assertEquals("Profile was not found with: profile_id = '1'", thrown.getMessage());
    }

    @Test
    void shouldSaveNewProfileRoleDetailsWhenRolsDoNotExist() {
        Integer profileId = 1;
        List<Integer> rolsIds = List.of(2, 3);
        ProfileRoleDetailDtoId profileRoleDetailDtoId = new ProfileRoleDetailDtoId();
        profileRoleDetailDtoId.setProfileId(profileId);
        profileRoleDetailDtoId.setRols(rolsIds);

        when(profileRoleDetailService.existByIdProfileRolDetail(any())).thenReturn(false);
        when(rolService.findByIdRol(anyInt())).thenReturn(rolDto);

        profileController.savedRolId(profileRoleDetailDtoId, request);

        verify(profileRoleDetailService, times(2)).saveProfileRoleDetail(any(), eq(request));
    }

    @Test
    void shouldReturnRolsWhenProfileRoleDetailsExist() {
        Integer profileId = 1;
    
        ProfileRoleDetailId profileRoleDetailId1 = ProfileRoleDetailId.builder()
            .profileId(profileId)
            .roleId(1)
            .build();
    
        ProfileRoleDetailId profileRoleDetailId2 = ProfileRoleDetailId.builder()
            .profileId(profileId)
            .roleId(2)
            .build();
    
        List<ProfileRoleDetail> profileRoleDetails = List.of(
            ProfileRoleDetail.builder()
                .id(profileRoleDetailId1)
                .role(Rol.builder().rolId(1).name("Admin").build())
                .build(),
            ProfileRoleDetail.builder()
                .id(profileRoleDetailId2)
                .role(Rol.builder().rolId(2).name("User").build())
                .build()
        );
    
        when(profileRoleDetailService.findByIdProfileRol(profileId)).thenReturn(profileRoleDetails);
    
        assertEquals(2, profileController.getAllRols(profileId).size());
        assertEquals("Admin", profileController.getAllRols(profileId).get(0).getName());
        assertEquals("User", profileController.getAllRols(profileId).get(1).getName());
    }

    @Test
    void shouldThrowExceptionWhenProfileRoleDetailIsEmptyOrNull() {
        Integer profileId = 1;

        when(profileRoleDetailService.findByIdProfileRol(profileId)).thenReturn(null);

        ResourceNorFoundException exception = assertThrows(
            ResourceNorFoundException.class, 
            () -> profileController.getAllRols(profileId)
        );

        assertEquals("Profile was not found with: profile_id = '1'", exception.getMessage());

        when(profileRoleDetailService.findByIdProfileRol(profileId)).thenReturn(Collections.emptyList());

        exception = assertThrows(
            ResourceNorFoundException.class, 
            () -> profileController.getAllRols(profileId)
        );

        assertEquals("Profile was not found with: profile_id = '1'", exception.getMessage());
    }

    @Test
    void shouldSaveProfileRoleDetailWhenNotExist() {
        Integer profileId = 1;
        Integer rolId = 2;

        ProfileRoleDetailDtoId profileRoleDetailDtoId = ProfileRoleDetailDtoId.builder()
            .profileId(profileId)
            .rols(List.of(rolId))
            .build();

        ProfileRoleDetailId detailId = ProfileRoleDetailId.builder()
            .profileId(profileId)
            .roleId(rolId)
            .build();

        when(profileRoleDetailService.existByIdProfileRolDetail(detailId)).thenReturn(false);

        profileController.savedRolId(profileRoleDetailDtoId, request);

        verify(rolService, times(1)).findByIdRol(rolId);
    }
}
