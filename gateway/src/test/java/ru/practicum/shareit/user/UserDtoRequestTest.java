package ru.practicum.shareit.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidUser_shouldPassValidation() {
        UserDtoRequest user = new UserDtoRequest();
        user.setName("Valid User");
        user.setEmail("validuser@example.com");

        Set<ConstraintViolation<UserDtoRequest>> violations = validator.validate(user, Create.class);
        assertTrue(violations.isEmpty(), "Valid user should not have validation errors");

        violations = validator.validate(user, Update.class);
        assertTrue(violations.isEmpty(), "Valid user should not have validation errors");
    }

    @Test
    void whenNameIsBlank_shouldFailValidation() {
        UserDtoRequest user = new UserDtoRequest();
        user.setName("");
        user.setEmail("validuser@example.com");

        Set<ConstraintViolation<UserDtoRequest>> violations = validator.validate(user, Create.class);
        assertEquals(1, violations.size(), "User with blank name should have a validation error");
        assertEquals("must not be blank", violations.iterator().next().getMessage());
    }

    @Test
    void whenEmailIsEmpty_shouldFailValidation() {
        UserDtoRequest user = new UserDtoRequest();
        user.setName("Valid User");
        user.setEmail("");

        Set<ConstraintViolation<UserDtoRequest>> violations = validator.validate(user, Create.class);
        assertEquals(1, violations.size(), "User with empty email should have a validation error");
        assertEquals("must not be empty", violations.iterator().next().getMessage());
    }

    @Test
    void whenEmailIsInvalid_shouldFailValidation() {
        UserDtoRequest user = new UserDtoRequest();
        user.setName("Valid User");
        user.setEmail("invalid-email");

        Set<ConstraintViolation<UserDtoRequest>> violations = validator.validate(user, Create.class);
        assertEquals(1, violations.size(), "User with invalid email should have a validation error");
        assertEquals("must be a well-formed email address", violations.iterator().next().getMessage());
    }

    @Test
    void whenEmailExceedsMaxLength_shouldFailValidation() {
        UserDtoRequest user = new UserDtoRequest();
        user.setName("Valid User");
        user.setEmail("A".repeat(513) + "@example.com");

        Set<ConstraintViolation<UserDtoRequest>> violations = validator.validate(user, Create.class);
        assertEquals(2, violations.size(), "Email exceeding max length should have a validation error");
        assertEquals("must be a well-formed email address", violations.iterator().next().getMessage());
    }

    @Test
    void whenEmailIsNull_shouldFailValidation() {
        UserDtoRequest user = new UserDtoRequest();
        user.setName("Valid User");
        user.setEmail(null);

        Set<ConstraintViolation<UserDtoRequest>> violations = validator.validate(user, Create.class);
        assertEquals(1, violations.size(), "Null email should have a validation error");
        assertEquals("must not be empty", violations.iterator().next().getMessage());
    }
}
