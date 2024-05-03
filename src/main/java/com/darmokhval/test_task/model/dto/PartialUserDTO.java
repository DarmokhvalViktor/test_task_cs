package com.darmokhval.test_task.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartialUserDTO {
    private String firstName;
    private String lastName;
    @Past(message = "Date of birth must be earlier than today")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;
    @Email
    private String email;
    private String address;
    private String phoneNumber;
}
