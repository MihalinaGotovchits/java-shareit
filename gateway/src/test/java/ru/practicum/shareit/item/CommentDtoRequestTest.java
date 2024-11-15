package ru.practicum.shareit.item;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.utils.Create;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CommentDtoRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenTextIsValid_shouldPassValidation() {
        CommentDtoRequest comment = new CommentDtoRequest();
        comment.setText("This is a valid comment.");

        Set<ConstraintViolation<CommentDtoRequest>> violations = validator.validate(comment, Create.class);
        assertTrue(violations.isEmpty(), "Valid comment should not have validation errors");
    }

    @Test
    void whenTextIsBlank_shouldFailValidation() {
        CommentDtoRequest comment = new CommentDtoRequest();
        comment.setText("");

        Set<ConstraintViolation<CommentDtoRequest>> violations = validator.validate(comment, Create.class);
        assertEquals(1, violations.size(), "Blank comment should have a validation error");

        ConstraintViolation<CommentDtoRequest> violation = violations.iterator().next();
        assertEquals("не должно быть пустым", violation.getMessage());
    }

    @Test
    void whenTextExceedsMaxLength_shouldFailValidation() {
        CommentDtoRequest comment = new CommentDtoRequest();
        comment.setText("A".repeat(1001));

        Set<ConstraintViolation<CommentDtoRequest>> violations = validator.validate(comment, Create.class);
        assertEquals(1, violations.size(), "Comment exceeding max length should have a validation error");

        ConstraintViolation<CommentDtoRequest> violation = violations.iterator().next();
        assertEquals("размер должен находиться в диапазоне от 0 до 1000", violation.getMessage());
    }

    @Test
    void whenTextIsNull_shouldFailValidation() {
        CommentDtoRequest comment = new CommentDtoRequest();
        comment.setText(null);

        Set<ConstraintViolation<CommentDtoRequest>> violations = validator.validate(comment, Create.class);
        assertEquals(1, violations.size(), "Comment with null text should have a validation error");

        ConstraintViolation<CommentDtoRequest> violation = violations.iterator().next();
        assertEquals("не должно быть пустым", violation.getMessage());
    }
}

