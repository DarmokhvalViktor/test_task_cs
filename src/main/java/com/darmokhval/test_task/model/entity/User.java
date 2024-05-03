package com.darmokhval.test_task.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "\"user\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty(message = "Firstname shouldn't be empty!")
    private String firstName;
    @NotEmpty(message = "Lastname shouldn't be empty!")
    private String lastName;
    @NotNull(message = "Date of birth must be in valid format")
    @Past(message = "Date of birth must be earlier than today")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;
    @Email
    @NotEmpty(message = "Email shouldn't be empty!")
    private String email;
    private String address;
    private String phoneNumber;
}
