package com.darmokhval.test_task.repository;

import com.darmokhval.test_task.model.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class UserSpecification {

    public static Specification<User> hasBirthDateBetween(LocalDate from, LocalDate to) {
        return (root, query, criteriaBuilder) -> {
            if (from == null || to == null) {
                throw new IllegalArgumentException("Both 'from' and 'to' dates must be specified!");
            }
            if (from.isBefore(to)) {
                throw new IllegalArgumentException("'From' date must be earlier than 'to' date!");
            }
            return criteriaBuilder.between(root.get("birthdate"), from, to);
        };
    }
}
