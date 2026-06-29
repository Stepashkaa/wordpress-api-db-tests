package ru.simbirsoft.test;

import org.junit.jupiter.api.AfterEach;
import ru.simbirsoft.db.CommentDataRepository;
import ru.simbirsoft.db.PageDataRepository;
import ru.simbirsoft.db.PostDataRepository;

import java.util.ArrayList;
import java.util.List;

public class DbDataTestBase extends BaseTest {
    protected final PostDataRepository postDataRepository = new PostDataRepository();
    protected final PageDataRepository pageDataRepository = new PageDataRepository();
    protected final CommentDataRepository commentDataRepository = new CommentDataRepository();

    private final List<Integer> createdPostIds = new ArrayList<>();
    private final List<Integer> createdPageIds = new ArrayList<>();
    private final List<Integer> createdCommentsIds = new ArrayList<>();

    @AfterEach
    void cleanUpDatabaseTestData(){
        createdCommentsIds.forEach(commentDataRepository::deleteCommentId);
        createdPostIds.forEach(postDataRepository::deletePostId);
        createdPageIds.forEach(pageDataRepository::deletePageId);

        createdCommentsIds.clear();
        createdPageIds.clear();
        createdPostIds.clear();
    }

    protected int createPublishPostInDatabase(String title, String content){
        int postId = postDataRepository.createPublishedPostEntity(title, content);
        createdPostIds.add(postId);
        return postId;
    }

    protected int createPublishPageInDatabase(String title, String content){
        int pageId = pageDataRepository.createPublishedPageEntity(title, content);
        createdPostIds.add(pageId);
        return pageId;
    }

    protected int createApprovedCommentInDatabase(int postId, String author, String email, String content){
        int commentId = commentDataRepository.createApprovedCommentEntity(postId, author, email, content);
        createdCommentsIds.add(commentId);
        return commentId;
    }
}
