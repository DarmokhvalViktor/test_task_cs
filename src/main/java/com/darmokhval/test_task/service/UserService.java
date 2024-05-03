package com.darmokhval.test_task.service;

import com.darmokhval.test_task.mapper.UserMapper;
import com.darmokhval.test_task.model.dto.PartialUserDTO;
import com.darmokhval.test_task.model.dto.UserDTO;
import com.darmokhval.test_task.model.entity.User;
import com.darmokhval.test_task.repository.UserRepository;
import com.darmokhval.test_task.repository.UserSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Value("${user.required_age}")
    private int requiredAge;

    public List<UserDTO> findUsersByBirthDateRange(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Both 'from' and 'to' dates must be specified!");
        }
        if (from.isAfter(to) || from.isEqual(to)) {
            throw new IllegalArgumentException("'From' date must be earlier than 'To' date!");
        }
        Specification<User> specification = UserSpecification.hasBirthDateBetween(from, to);
        List<User> users = userRepository.findAll(specification);
        return users.stream()
                .map(userMapper::entityToDTO)
                .toList();
    }
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        if (!isValidAge(userDTO.getBirthDate())) {
            throw new IllegalArgumentException(String.format("User must be older than %s to register!", requiredAge));
        }
        checkIfEmailIsUsed(userDTO.getEmail(), null);
        User user = userRepository.save(userMapper.dtoToEntity(userDTO));
        return userMapper.entityToDTO(user);
    }

    @Transactional
    public UserDTO updateUser(UserDTO userDTO, Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isEmpty()) {
            throw new IllegalArgumentException(String.format("User with ID %s wasn't found", id));
        }
        checkIfEmailIsUsed(userDTO.getEmail(), id);
        User existingUser = optionalUser.get();
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setBirthDate(userDTO.getBirthDate());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setAddress(userDTO.getAddress());
        existingUser.setPhoneNumber(userDTO.getPhoneNumber());

        User updatedUser = userRepository.save(existingUser);
        return userMapper.entityToDTO(updatedUser);
    }

    @Transactional
    public UserDTO patchUser(PartialUserDTO userDTO, Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()) {
            checkIfEmailIsUsed(userDTO.getEmail(), id);
            User existingUser = optionalUser.get();
            User updatedUser = partiallyUpdateUserFields(existingUser, userDTO);
            updatedUser = userRepository.save(updatedUser);
            return userMapper.entityToDTO(updatedUser);
        }
        throw new IllegalArgumentException(String.format("User with ID %s wasn't found", id));
    }

    @Transactional
    public String deleteUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()) {
            userRepository.deleteById(id);
            return String.format("User with ID %s was deleted", id);
        }
        throw new IllegalArgumentException(String.format("User with ID %s wasn't found", id));
    }

    private User partiallyUpdateUserFields(User existingUser, PartialUserDTO userDTO) {
        if(userDTO.getBirthDate() != null) {
            if(!isValidAge(userDTO.getBirthDate())) {
                throw new IllegalArgumentException(String.format("User must be older than %s to use this site!", requiredAge));
            }
            existingUser.setBirthDate(userDTO.getBirthDate());
        }
        if(userDTO.getFirstName() != null) {
            existingUser.setFirstName(userDTO.getFirstName());
        }
        if(userDTO.getLastName() != null) {
            existingUser.setLastName(userDTO.getLastName());
        }
        if(userDTO.getEmail() != null) {
            existingUser.setEmail(userDTO.getEmail());
        }
        if(userDTO.getAddress() != null) {
            existingUser.setAddress(userDTO.getAddress());
        }
        if(userDTO.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(userDTO.getPhoneNumber());
        }
        return existingUser;

    }

    /**
     * Validate age. If age < required age, return false;
     */
    private boolean isValidAge(LocalDate birthDate) {
        int userAge = Period.between(birthDate, LocalDate.now()).getYears();
        return userAge >= requiredAge;
    }

    /**
     * Find if email is already in database. If yes, check if email used by current user or no.
     */
    private void checkIfEmailIsUsed(String email, Long currentOwnerId) {
        Optional<User> existingOwnerWithEmail = userRepository.findByEmail(email);
        if(existingOwnerWithEmail.isPresent() && (currentOwnerId == null ||
                !existingOwnerWithEmail.get().getId().equals(currentOwnerId))) {
            throw new IllegalArgumentException(String.format("Email %s already taken!",
                    email));
        }
    }
}
