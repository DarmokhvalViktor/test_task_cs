package com.darmokhval.test_task.service;

import com.darmokhval.test_task.mapper.UserMapper;
import com.darmokhval.test_task.model.dto.UserDTO;
import com.darmokhval.test_task.model.entity.User;
import com.darmokhval.test_task.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserService userService;
    private final static int REQUIRED_AGE = 18;

    @BeforeEach
    public void setUp() throws Exception {
        // Use reflection to set the private field `requiredAge`
        Field field = UserService.class.getDeclaredField("requiredAge");
        field.setAccessible(true); // Allows modifying private fields
        field.setInt(userService, 18); // Set `requiredAge` to 18
    }

    @Test
    public void CreateUserTest() {
        LocalDate validBirthDate = LocalDate.of(1999, 12, 31);
        UserDTO userDTO = UserDTO.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .birthDate(validBirthDate)
                .email("email@test.com")
                .address("test_address")
                .phoneNumber("13-33-33-333")
                .build();
        User user = User.builder()
                .firstName("FirstName")
                .lastName("LastName")
                .birthDate(validBirthDate)
                .email("email@test.com")
                .address("test_address")
                .phoneNumber("13-33-33-333")
                .build();
        when(userMapper.dtoToEntity(userDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.entityToDTO(user)).thenReturn(userDTO);

        UserDTO createdUserDTO  = userService.createUser(userDTO);
        verify(userRepository).save(user);
        verify(userMapper).entityToDTO(user);
        verify(userMapper).dtoToEntity(userDTO);

        assertEquals(userDTO.getFirstName(), createdUserDTO.getFirstName());
        assertEquals(userDTO.getLastName(), createdUserDTO.getLastName());
        assertEquals(userDTO.getEmail(), createdUserDTO.getEmail());
        assertEquals(userDTO.getBirthDate(), createdUserDTO.getBirthDate());
    }

    @Test
    public void createUserThrowExceptionTooYoungTest() {
        LocalDate invalidBirthDate = LocalDate.now().minusYears(REQUIRED_AGE - 2); // Too young
        System.out.println("invalidBirthDate" + invalidBirthDate);
        UserDTO userDTO = UserDTO.builder()
                .birthDate(invalidBirthDate)
                .firstName("FirstName")
                .lastName("LastName")
                .email("email@test.com")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.createUser(userDTO);
        });
        String expectedMessage = String.format("User must be older than %s to register!", REQUIRED_AGE);


        assertEquals(expectedMessage, exception.getMessage(), "Exception message did not match expected!");
    }
}
