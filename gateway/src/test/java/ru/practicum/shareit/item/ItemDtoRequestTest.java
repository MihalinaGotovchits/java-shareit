package ru.practicum.shareit.item;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.utils.Create;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ItemDtoRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidItem_shouldPassValidation() {
        ItemDtoRequest item = new ItemDtoRequest();
        item.setName("Valid Item");
        item.setDescription("This is a valid item description.");
        item.setAvailable(true);
        item.setRequestId(1L);

        Set<ConstraintViolation<ItemDtoRequest>> violations = validator.validate(item, Create.class);
        assertTrue(violations.isEmpty(), "Valid Item should not have validation errors");
    }

    @Test
    void whenNameIsBlank_shouldFailValidation() {
        ItemDtoRequest item = new ItemDtoRequest();
        item.setName("");
        item.setDescription("Valid description.");
        item.setAvailable(true);

        Set<ConstraintViolation<ItemDtoRequest>> violations = validator.validate(item, Create.class);
        assertEquals(1, violations.size(), "Item with blank name should have a validation error");
        assertEquals("не должно быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void whenDescriptionIsBlank_shouldFailValidation() {
        ItemDtoRequest item = new ItemDtoRequest();
        item.setName("Valid Item");
        item.setDescription("");
        item.setAvailable(true);

        Set<ConstraintViolation<ItemDtoRequest>> violations = validator.validate(item, Create.class);
        assertEquals(1, violations.size(), "Item with blank description should have a validation error");
        assertEquals("не должно быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void whenAvailableIsNull_shouldFailValidation() {
        ItemDtoRequest item = new ItemDtoRequest();
        item.setName("Valid Item");
        item.setDescription("Valid description.");
        item.setAvailable(null);

        Set<ConstraintViolation<ItemDtoRequest>> violations = validator.validate(item, Create.class);
        assertEquals(1, violations.size(), "Item with null availability should have a validation error");
        assertEquals("не должно равняться null", violations.iterator().next().getMessage());
    }

    @Test
    void whenNameExceedsMaxLength_shouldFailValidation() {
        ItemDtoRequest item = new ItemDtoRequest();
        item.setName("A".repeat(256));
        item.setDescription("Valid description.");
        item.setAvailable(true);

        Set<ConstraintViolation<ItemDtoRequest>> violations = validator.validate(item, Create.class);
        assertEquals(1, violations.size(), "Item with name exceeding max length should have a validation error");
        assertEquals("размер должен находиться в диапазоне от 0 до 255", violations.iterator().next().getMessage());
    }

    @Test
    void whenDescriptionExceedsMaxLength_shouldFailValidation() {
        ItemDtoRequest item = new ItemDtoRequest();
        item.setName("Valid Item");
        item.setDescription("A".repeat(1001));
        item.setAvailable(true);

        Set<ConstraintViolation<ItemDtoRequest>> violations = validator.validate(item, Create.class);
        assertEquals(1, violations.size(), "Item with description exceeding max length should have a validation error");
        assertEquals("размер должен находиться в диапазоне от 0 до 1000", violations.iterator().next().getMessage());
    }
}
