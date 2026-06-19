package ru.simbirsoft.endpoint;

public class WordPressEndpoints {

    public static final String INDEX = "/index.php";

    public static final String POSTS_ROUTE = "/wp/v2/posts";
    public static final String PAGE_ROUTE = "/wp/v2/pages";
    public static final String COMMENTS_ROUTE = "/wp/v2/comments";

    public WordPressEndpoints() {
    }

    public static String postByIdRoute(int id){
        return POSTS_ROUTE + "/" + id;
    }

    public static String pageByIdRoute(int id){
        return PAGE_ROUTE + "/" + id;
    }

    public static String commentByIdRoute(int id){
        return COMMENTS_ROUTE + "/" + id;
    }
}
