package ru.simbirsoft.test;

import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import ru.simbirsoft.requests.CommentRequests;
import ru.simbirsoft.requests.PageRequests;
import ru.simbirsoft.requests.PostRequests;
import ru.simbirsoft.db.CommentRepository;
import ru.simbirsoft.db.PostRepository;
import ru.simbirsoft.model.CommentRequestBody;
import ru.simbirsoft.model.PageRequestBody;
import ru.simbirsoft.model.PostRequestBody;

import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_CREATED;

abstract class BaseTest {
    protected final PostRequests postRequests = new PostRequests();
    protected final PageRequests pageRequests = new PageRequests();
    protected final CommentRequests commentRequests = new CommentRequests();

    protected final PostRepository postRepository = new PostRepository();
    protected final CommentRepository commentRepository = new CommentRepository();

    private final List<Integer> createdPostIds = new ArrayList<>();
    private final List<Integer> createdPageIds = new ArrayList<>();
    private final List<Integer> createdCommentIds = new ArrayList<>();

    @AfterEach
    void cleanUp(){
        createdCommentIds.stream()
                .filter(commentRepository::existsById)
                .forEach(id -> commentRequests.delete(id, true));

        createdPageIds.stream()
                .filter(postRepository::existsById)
                .forEach(id -> pageRequests.delete(id, true));

        createdPostIds.stream()
                .filter(postRepository::existsById)
                .forEach(id -> postRequests.delete(id, true));

        createdCommentIds.clear();
        createdPageIds.clear();
        createdPostIds.clear();
    }

    protected int createPublishedPost(String title, String content){
        Response response = postRequests.create(new PostRequestBody(title, content, "publish"))
                .then()
                .statusCode(HTTP_CREATED)
                .extract().response();
        int id = response.jsonPath().getInt("id");
        createdPostIds.add(id);
        return id;
    }

    protected int createPublishedPage(String title, String content){
        Response response = pageRequests.create(new PageRequestBody(title, content, "publish"))
                .then()
                .statusCode(HTTP_CREATED)
                .extract().response();
        int id = response.jsonPath().getInt("id");
        createdPageIds.add(id);
        return id;
    }

    protected int createComment(int postId, String author, String email, String content, String status){
        Response response = commentRequests.create(new CommentRequestBody(postId, author, email, content, status))
                .then()
                .statusCode(HTTP_CREATED)
                .extract().response();
        int id = response.jsonPath().getInt("id");
        createdCommentIds.add(id);
        return id;
    }

    protected void rememberCreatedPost(int id) {
        createdPostIds.add(id);
    }

    protected void rememberCreatedPage(int id) {
        createdPageIds.add(id);
    }

    protected void rememberCreatedComment(int id) {
        createdCommentIds.add(id);
    }
}
