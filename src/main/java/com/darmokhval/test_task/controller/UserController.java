package com.darmokhval.test_task.controller;

import com.darmokhval.test_task.model.dto.PartialUserDTO;
import com.darmokhval.test_task.model.dto.UserDTO;
import com.darmokhval.test_task.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/birth_date")
    public ResponseEntity<List<UserDTO>> findUsersByBirthDateRange(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findUsersByBirthDateRange(from, to));
    }

    @PostMapping()
    public ResponseEntity<UserDTO> createUser(
            @RequestBody @Valid UserDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDTO));
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @RequestBody @Valid UserDTO userDTO,
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(userDTO, id));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> patchUser(
            @RequestBody @Valid PartialUserDTO userDTO,
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.patchUser(userDTO, id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.deleteUser(id));
    }
}
