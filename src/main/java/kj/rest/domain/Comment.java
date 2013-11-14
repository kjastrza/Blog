package kj.rest.domain;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891891
 * Date: 9/1/13
 * Time: 10:40 AM
 */
public class Comment {
    private String id;
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
