package ru.practicum.shareit.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestDtoRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidDescription_shouldPassValidation() {
        ItemRequestDtoRequest request = new ItemRequestDtoRequest();
        request.setDescription("This is a valid item request description.");

        Set<ConstraintViolation<ItemRequestDtoRequest>> violations = validator.validate(request, Create.class);
        assertTrue(violations.isEmpty(), "Valid description should not have validation errors");

        violations = validator.validate(request, Update.class);
        assertTrue(violations.isEmpty(), "Valid description should not have validation errors");
    }

    @Test
    void whenDescriptionIsBlank_shouldFailValidation() {
        ItemRequestDtoRequest request = new ItemRequestDtoRequest();
        request.setDescription("");

        Set<ConstraintViolation<ItemRequestDtoRequest>> violations = validator.validate(request, Create.class);
        assertEquals(1, violations.size(), "Blank description should have a validation error");
        assertEquals("не должно быть пустым", violations.iterator().next().getMessage());

        violations = validator.validate(request, Update.class);
        assertEquals(1, violations.size(), "Blank description should have a validation error");
    }

    @Test
    void whenDescriptionExceedsMaxLength_shouldFailValidation() {
        ItemRequestDtoRequest request = new ItemRequestDtoRequest();
        request.setDescription("A".repeat(1001));

        Set<ConstraintViolation<ItemRequestDtoRequest>> violations = validator.validate(request, Create.class);
        assertEquals(1, violations.size(), "Description exceeding max length should have a validation error");
        assertEquals("размер должен находиться в диапазоне от 0 до 1000", violations.iterator().next().getMessage());

        violations = validator.validate(request, Update.class);
        assertEquals(1, violations.size(), "Description exceeding max length should have a validation error");
    }

    @Test
    void whenDescriptionIsNull_shouldFailValidation() {
        ItemRequestDtoRequest request = new ItemRequestDtoRequest();
        request.setDescription(null);

        Set<ConstraintViolation<ItemRequestDtoRequest>> violations = validator.validate(request, Create.class);
        assertEquals(1, violations.size(), "Null description should have a validation error");
        assertEquals("не должно быть пустым", violations.iterator().next().getMessage());

        violations = validator.validate(request, Update.class);
        assertEquals(1, violations.size(), "Null description should have a validation error");
    }
}

