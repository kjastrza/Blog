package kj.rest.service;

import kj.rest.common.ResourceNotFoundException;
import kj.rest.dao.PostDao;
import kj.rest.domain.Comment;
import kj.rest.domain.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891891
 * Date: 8/28/13
 * Time: 11:07 PM
 */
@Stateless
@Path("/posts")
public class BlogService {
    @Inject
    private PostDao postDao;

    private static final Logger logger = LoggerFactory.getLogger(BlogService.class);

    @GET
    @Produces("application/json")
    public List<Post> getPosts() {
        return postDao.findAll();
    }

    @GET
    @Produces("application/json")
    @Path("/{param}")
    public Post getPost(@PathParam("param") String postId) {
        try {
            return postDao.find(postId);
        } catch (ResourceNotFoundException e) {
            throw new WebApplicationException(e, Response.Status.NOT_FOUND);
        }
    }

    @POST
    @Consumes("application/json")
    public Response createPost(Post post) {
        post.setCreationDate(new Date());
        logger.info("Create new Post: " + post);
        String postId = postDao.create(post);
        return Response.ok().entity("New Post Created with ID: [" + postId + "]").build();
    }

    @POST
    @Path("/{param}/comments")
    @Consumes("application/json")
    public Response addComment(@PathParam("param") String postId, Comment comment) {
        logger.info("Create new comment to post: " + postId);
        String commentId = postDao.createComment(postId, comment);
        return Response.status(200).entity("New Comment Created with ID: [" + commentId + "]").build();
    }

    @PUT
    @Path("/{param}")
    @Consumes("application/json")
    public Response updatePostContent(@PathParam("param") String postId, Post post) {
        String result = postDao.updateContent(postId, post.getContent());
        return Response.status(200).entity("Content changing status: " + result).build();
    }

    @DELETE
    @Path("/{param}")
    public Response deletePost(@PathParam("param") String postId) {
        postDao.delete(postId);
        return Response.status(200).entity("Post With ID: [" + postId + " ] deleted").build();
    }

    @DELETE
    @Path("/{postId}/comments/{commentId}")
    public Response deleteComment(@PathParam("postId") String postId, @PathParam("commentId") String commentId) {
        String result = postDao.deleteComment(postId, commentId);
        return Response.status(200).entity(result).build();
    }
}