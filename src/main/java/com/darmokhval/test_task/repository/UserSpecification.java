package com.darmokhval.test_task.repository;

import com.darmokhval.test_task.model.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class UserSpecification {

    public static Specification<User> hasBirthDateBetween(LocalDate from, LocalDate to) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("birthDate"), from, to);
    }
}
