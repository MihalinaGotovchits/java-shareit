package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentMapperTest {

    private final long id = 1L;
    private final String text = "Great item!";
    private final String authorName = "User Name";
    private final LocalDateTime createdDate = LocalDateTime.now();

    @Test
    void toCommentDtoOut_ShouldMapCommentToCommentDtoOut() {
        User author = User.builder().name(authorName).build();
        Comment comment = Comment.builder()
                .id(id)
                .text(text)
                .author(author)
                .created(createdDate)
                .build();

        CommentDtoOut commentDtoOut = CommentMapper.toCommentDtoOut(comment);

        assertEquals(id, commentDtoOut.getId());
        assertEquals(text, commentDtoOut.getText());
        assertEquals(authorName, commentDtoOut.getAuthorName());
        assertEquals(createdDate, commentDtoOut.getCreated());
    }

    @Test
    void toComment_ShouldMapCommentDtoInAndItemAndUserToComment() {
        Item item = new Item();
        User author = User.builder().id(id).name(authorName).build();
        CommentDtoIn commentDtoIn = new CommentDtoIn();
        commentDtoIn.setText(text);

        Comment comment = CommentMapper.toComment(commentDtoIn, item, author);

        assertEquals(item, comment.getItem());
        assertEquals(author, comment.getAuthor());
        assertEquals(text, comment.getText());
        assertEquals(comment.getCreated().toLocalDate(), LocalDateTime.now().toLocalDate());
    }
}
