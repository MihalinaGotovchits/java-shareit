package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private final User user = User.builder()
            .id(null)
            .name("user")
            .email("user@mail.ru")
            .build();
    private final Item item = Item.builder()
            .id(null)
            .name("item")
            .description("nice item")
            .available(true)
            .owner(user)
            .request(null)
            .build();
    private final Comment comment = Comment.builder()
            .id(null)
            .text("comment")
            .item(item)
            .author(user)
            .created(LocalDateTime.of(2024, 11, 8, 12, 30))
            .build();

    @BeforeEach
    void setUp() {
        userRepository.save(user);
        itemRepository.save(item);
        commentRepository.save(comment);
    }

    @Test
    @DirtiesContext
    void findAllByItems() {
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());

        assertThat(comments.get(0).getId(), notNullValue());
        assertThat(comments.get(0).getText(), equalTo(comment.getText()));
        assertThat(comments.size(), equalTo(1));
    }
}
