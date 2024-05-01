package com.darmokhval.test_task.service;

import com.darmokhval.test_task.mapper.UserMapper;
import com.darmokhval.test_task.model.dto.UserDTO;
import com.darmokhval.test_task.model.entity.User;
import com.darmokhval.test_task.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Value("${user.required_age}")
    private int requiredAge;

    public UserDTO createUser(UserDTO userDTO) {
        if (!isValidAge(userDTO.getBirthDate())) {
            throw new IllegalArgumentException(String.format("User must be older than %s to register!", requiredAge));
        }
        User user = userRepository.save(userMapper.dtoToEntity(userDTO));
        return userMapper.entityToDTO(user);
    }

    private boolean isValidAge(LocalDate birthDate) {
        int userAge = Period.between(birthDate, LocalDate.now()).getYears();
        return userAge >= requiredAge;
    }


}
