package com.darmokhval.test_task.mapper;

import com.darmokhval.test_task.model.dto.UserDTO;
import com.darmokhval.test_task.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User dtoToEntity(UserDTO userDTO) {
        return User.builder()
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .birthDate(userDTO.getBirthDate())
                .email(userDTO.getEmail())
                .address(userDTO.getAddress() != null ? userDTO.getAddress() : "anywhere")
                .phoneNumber(userDTO.getPhoneNumber() != null ? userDTO.getPhoneNumber() : "000-000-0000")
                .build();
    }
    public UserDTO entityToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .email(user.getEmail())
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
