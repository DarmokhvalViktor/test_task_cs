package com.darmokhval.test_task.service;

import com.darmokhval.test_task.mapper.UserMapper;
import com.darmokhval.test_task.model.dto.PartialUserDTO;
import com.darmokhval.test_task.model.dto.UserDTO;
import com.darmokhval.test_task.model.entity.User;
import com.darmokhval.test_task.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    public void findUsersByBirthDateRangeValidRangeTest() {
        LocalDate from = LocalDate.of(2000, 1, 1);
        LocalDate to = LocalDate.of(2010, 12, 31);

        User user1 = User.builder().id(1L).firstName("John").lastName("Doe").birthDate(LocalDate.of(2005, 6, 15)).build();
        User user2 = User.builder().id(2L).firstName("Jane").lastName("Doe").birthDate(LocalDate.of(2007, 7, 20)).build();

        when(userRepository.findAll(any(Specification.class))).thenReturn(List.of(user1, user2));
        when(userMapper.entityToDTO(user1)).thenReturn(new UserDTO(1L, "John", "Doe", LocalDate.of(2005, 6, 15), "john@example.com", "123 Main St", "123-456-7890"));
        when(userMapper.entityToDTO(user2)).thenReturn(new UserDTO(2L, "Jane", "Doe", LocalDate.of(2007, 7, 20), "jane@example.com", "456 Elm St", "987-654-3210"));

        List<UserDTO> result = userService.findUsersByBirthDateRange(from, to);

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Jane", result.get(1).getFirstName());
        verify(userRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    public void findUsersByBirthDateRangeInvalidRangeTest() {
        LocalDate from = LocalDate.of(2010, 12, 31);
        LocalDate to = LocalDate.of(2000, 1, 1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.findUsersByBirthDateRange(from, to)
        );

        assertEquals("'From' date must be earlier than 'To' date!", exception.getMessage());

        verify(userRepository, never()).findAll(any(Specification.class));
    }

    @Test
    public void findUsersByBirthDateRangeNoUsersTest() {
        LocalDate from = LocalDate.of(2000, 1, 1);
        LocalDate to = LocalDate.of(2010, 12, 31);

        when(userRepository.findAll(any(Specification.class))).thenReturn(List.of());

        List<UserDTO> result = userService.findUsersByBirthDateRange(from, to);

        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll(any(Specification.class));
    }
    @Test
    public void createUserTest() {
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

    @Test
    public void updateUserTest() {
        Long userId = 1L;
        User existingUser = User.builder().id(userId).firstName("John").lastName("Doe").email("john@gmail.com").build();
        UserDTO updatedUserDTO = UserDTO.builder().id(userId).firstName("John").lastName("Doe").email("johnny@example.com").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        existingUser.setEmail("johnny@example.com");
        existingUser.setFirstName("John");
        existingUser.setLastName("Doe");

        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(userMapper.entityToDTO(existingUser)).thenReturn(updatedUserDTO);

        UserDTO result = userService.updateUser(updatedUserDTO, userId);

        assertEquals("johnny@example.com", result.getEmail());
        assertEquals("John", result.getFirstName());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    public void updateUserNotFoundTest() {
        Long userId = 99999L;
        UserDTO updatedUserDTO = UserDTO.builder().id(userId).firstName("Jane").lastName("Doe").email("jane@example.com").build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(updatedUserDTO, userId)
        );
        assertEquals(String.format("User with ID %s wasn't found", userId), exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void updateUserEmailAlreadyTakenTest() {
        Long userId = 1L;
        Long otherUserId = 2L;
        User existingUser = User.builder().id(userId).firstName("John").lastName("Doe").email("john@example.com").build();
        User otherUser = User.builder().id(otherUserId).firstName("Jane").lastName("Smith").email("jane@example.com").build();
        UserDTO updatedUserDTO = UserDTO.builder().id(userId).firstName("John").lastName("Doe").email("jane@example.com").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(otherUser));
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUser(updatedUserDTO, userId)
        );

        assertEquals(String.format("Email %s already taken!", "jane@example.com"), exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void patchUserTest() {
        Long userId = 1L;
        User existingUser = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .birthDate(LocalDate.of(2000, 1, 1))
                .build();
        PartialUserDTO patchDTO = PartialUserDTO.builder()
                .email("johnny@example.com")
                .firstName("Johnny")
                .build();
        User updatedUser = User.builder()
                .id(userId)
                .firstName("Johnny")
                .lastName("Doe")
                .email("johnny@example.com")
                .birthDate(existingUser.getBirthDate())
                .build();
        UserDTO resultingDTO = UserDTO.builder()
                .id(userId)
                .firstName("Johnny")
                .lastName("Doe")
                .email("johnny@example.com")
                .birthDate(existingUser.getBirthDate())
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(userMapper.entityToDTO(updatedUser)).thenReturn(resultingDTO);
        UserDTO result = userService.patchUser(patchDTO, userId);
        assertEquals("Johnny", result.getFirstName());
        assertEquals("johnny@example.com", result.getEmail());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    public void patchUserNotFoundTest() {
        Long userId = 99999L;
        PartialUserDTO patchDTO = PartialUserDTO.builder().firstName("Alice").build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.patchUser(patchDTO, userId)
        );

        assertEquals(String.format("User with ID %s wasn't found", userId), exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void deleteUserTest() {
        Long validId = 1L;
        User validUser = User.builder().id(validId).firstName("John").lastName("Doe").build();
        when(userRepository.findById(validId)).thenReturn(Optional.of(validUser));

        String result = userService.deleteUser(1L);
        assertEquals(String.format("User with ID %s was deleted", validId), result);
        verify(userRepository, times(1)).deleteById(validId);
    }

    @Test
    public void deleteUserInvalidIdTest() {
        Long invalidId = 99999L;
        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.deleteUser(invalidId)
        );
        assertEquals(
                String.format("User with ID %s wasn't found", invalidId),
                exception.getMessage()
        );
        verify(userRepository, never()).deleteById(invalidId);
    }
}
