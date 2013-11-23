package kj.rest.dao;

import kj.rest.domain.Comment;
import kj.rest.domain.Post;
import kj.rest.common.ResourceNotFoundException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891891
 * Date: 8/31/13
 * Time: 8:28 PM
 */
public interface PostDao {
    String create(Post post);

    String createComment(String postId, Comment comment);

    String deleteComment(String postId, String commentId);

    Post find(String postId) throws ResourceNotFoundException;

    List<Post> findAll();

    String updateContent(String postId, String content);

    String delete(String postId);
}
