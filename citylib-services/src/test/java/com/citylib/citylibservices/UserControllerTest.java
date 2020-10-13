package com.citylib.citylibservices;


import com.citylib.citylibservices.controller.UserController;
import com.citylib.citylibservices.dto.UserDto;
import com.citylib.citylibservices.exception.UserAlreadyExistsException;
import com.citylib.citylibservices.model.User;
import com.citylib.citylibservices.repository.RoleRepository;
import com.citylib.citylibservices.repository.UserRepository;
import com.citylib.citylibservices.sources.ObjectBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = UserController.class)
public class UserControllerTest extends AbstractControllerTest {

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private RoleRepository roleRepository;

    @Autowired
    MockMvc mockMvc;

    private ObjectBuilder objectBuilder;



    @BeforeEach
    void initObjectBuilder() {
        objectBuilder = new ObjectBuilder();
    }

    @Test
    @DisplayName("GET / User by Id / Returns User")
    void givenId_shouldReturnUserObject() throws Exception {
        long vUserId = 1;
        Optional<User> vUser = Optional.ofNullable(objectBuilder.user());
        when(userRepository.findById(vUserId)).thenReturn(vUser);

        assumeThat(vUser).isPresent();

        String body = mockMvc.perform(get("/users/id/{id}", vUserId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        User result = new ObjectMapper().readerFor(User.class).readValue(body);

        assertThat(result).usingRecursiveComparison().isEqualTo(objectBuilder.user());
    }

    @Test
    @DisplayName("GET / User by Mail / Returns User")
    void givenEmail_shouldReturnUserObject() throws Exception {

        String vUserEmail = StringEscapeUtils.unescapeHtml("email@mail.com");
        when(userRepository.findByEmail(vUserEmail)).thenReturn(Optional.ofNullable(objectBuilder.user()));
        when(roleRepository.findByDefTrue()).thenReturn(objectBuilder.role());

        String body = mockMvc.perform(get("/users/email/{email}/", vUserEmail))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        User result = new ObjectMapper().readerFor(User.class).readValue(body);

        assertThat(result.getRoles()).contains(objectBuilder.role());
    }

    @Test
    @DisplayName("GET / User by Mail / Do not get Roles if user does not exist")
    void givenEmail_whenUserNotExists_shouldNotGetRoles() throws Exception {
        String vUserEmail = StringEscapeUtils.unescapeHtml("email@mail.com");
        when(userRepository.findByEmail(vUserEmail)).thenReturn(Optional.empty());

        String body = mockMvc.perform(get("/users/email/{email}/", vUserEmail))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        User result = new ObjectMapper().readerFor(User.class).readValue(body);

        assertThat(result).isNull();
        verify(roleRepository, times(0)).findByDefTrue();
    }


    @Test
    void givenUserDtoWithExistingEmail_shouldReturnHttpConflictAndThrowException() throws Exception {
        UserDto vUserDto = objectBuilder.userDto();
        when(userRepository.findByEmail(vUserDto.getEmail())).thenReturn(Optional.ofNullable(objectBuilder.user()));

        mockMvc.perform(MockMvcRequestBuilders
                .post("/users/register")
                .content(asJsonString(objectBuilder.userDto()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserAlreadyExistsException))
                .andExpect(result -> assertEquals("Un compte existe déjà pour l'adresse email : " + vUserDto.getEmail(), Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }

    @Test
    void givenUserDtoWithUnknownEmail_ShouldReturnHttpCreated() throws Exception {
        UserDto vUserDto = objectBuilder.userDto();
        when(userRepository.findByEmail(vUserDto.getEmail())).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                .post("/users/register")
                .content(asJsonString(objectBuilder.userDto()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

    }
    
}

