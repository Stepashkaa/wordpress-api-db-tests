package ru.simbirsoft.endpoint;

public final class WordPressEndpoints {

    public static final String INDEX = "/index.php";

    public static final String POSTS_ROUTE = "/wp/v2/posts";
    public static final String PAGES_ROUTE = "/wp/v2/pages";
    public static final String COMMENTS_ROUTE = "/wp/v2/comments";

    private WordPressEndpoints() {
    }

    public static String postByIdRoute(int id){
        return POSTS_ROUTE + "/" + id;
    }

    public static String pageByIdRoute(int id){
        return PAGES_ROUTE + "/" + id;
    }

    public static String commentByIdRoute(int id){
        return COMMENTS_ROUTE + "/" + id;
    }
}
