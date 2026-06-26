package ru.simbirsoft.test;

import org.junit.jupiter.api.AfterEach;
import ru.simbirsoft.db.DbDataRepository;

import java.util.ArrayList;
import java.util.List;

public class D2BaseTest extends BaseTest {
    protected final DbDataRepository dbDataRepository = new DbDataRepository();

    private final List<Integer> createdPostIds = new ArrayList<>();
    private final List<Integer> createdCommentsIds = new ArrayList<>();

    @AfterEach
    void cleanUpDatabaseTestData(){
        createdCommentsIds.forEach(dbDataRepository::deleteCommentId);
        createdPostIds.forEach(dbDataRepository::deletePostId);

        createdCommentsIds.clear();
        createdPostIds.clear();
    }

    protected int   createPublishPostInDatabase(String title, String content){
        int postId = dbDataRepository.createPublishedPost(title, content);
        createdPostIds.add(postId);
        return postId;
    }

    protected int createPublishPageInDatabase(String title, String content){
        int pageId = dbDataRepository.createPublishedPage(title, content);
        createdPostIds.add(pageId);
        return pageId;
    }

    protected int createApprovedCommentInDatabase(int postId, String author, String email, String content){
        int commentId = dbDataRepository.createApprovedCommentEntity(postId, author, email, content);
        createdCommentsIds.add(commentId);
        return commentId;
    }
}
