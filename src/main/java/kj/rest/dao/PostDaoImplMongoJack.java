package kj.rest.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mongodb.*;
import com.mongodb.DBCursor;
import com.mongodb.WriteResult;
import kj.rest.common.ConfigValue;
import kj.rest.common.ResourceNotFoundException;
import kj.rest.domain.Comment;
import kj.rest.domain.Post;
import org.bson.types.ObjectId;
import org.mongojack.*;
import org.mongojack.internal.MongoJackModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891891
 * Date: 8/31/13
 * Time: 8:46 PM
 */
@Default
public class PostDaoImplMongoJack implements PostDao {
    static final String CONTENT = "content";
    static final String TITLE = "title";
    static final String CREATION_DATE = "creationDate";
    static final String ID = "_id";
    static final String POSTS_COLLECTION = "posts";
    static final String COMMENTS = "comments";

    Logger logger = LoggerFactory.getLogger(PostDaoImpl.class);
    @Inject
    @ConfigValue(key = "mongodb.host")
    private String host;
    @Inject
    @ConfigValue(key = "mongodb.port")
    private String port;
    @Inject
    @ConfigValue(key = "mongodb.name")
    private String dbName;

    private DB db;
    private JacksonDBCollection<Post, String> coll;

    @VisibleForTesting
    public void setDb(DB db) {
        this.db = db;
    }

    @PostConstruct
    public void afterConstruction() {
        try {
            db = new MongoClient(host, Integer.parseInt(port)).getDB(dbName);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.addMixInAnnotations(ObjectId.class, String.class);
            MongoJackModule.configure(objectMapper);
            coll = JacksonDBCollection.wrap(db.getCollection(POSTS_COLLECTION), Post.class, String.class, objectMapper);
        } catch (UnknownHostException e) {
            logger.error("Error while creating mongo client", e);
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String create(Post post) {
        org.mongojack.WriteResult<Post,String> insertResult = coll.insert(post);
        String postId = insertResult.getSavedId();
        logger.info("New Post Created With Id: [ " + postId + " ]");

        return postId;
    }

    @VisibleForTesting
    protected String getCreatedId(BasicDBObject document) {
        return String.valueOf(document.get(ID));
    }

    @Override
    public String createComment(String postId, Comment comment) {
        DBCollection collection = db.getCollection(POSTS_COLLECTION);
        BasicDBObject query = new BasicDBObject(ID, createObjectId(postId));
        ObjectId newCommentId = new ObjectId();
        BasicDBObject newDocument = new BasicDBObject();
        newDocument.put(ID, newCommentId);
        newDocument.put(CONTENT, comment.getContent());
        BasicDBObject updateObj = new BasicDBObject();
        updateObj.put("$push", new BasicDBObject(COMMENTS, newDocument));
        collection.update(query, updateObj);

        return String.valueOf(newCommentId);
    }

    @VisibleForTesting
    ObjectId createObjectId(String postId) {
        return new ObjectId(postId);
    }

    @Override
    public String deleteComment(String postId, String commentId) {
        DBCollection collection = db.getCollection(POSTS_COLLECTION);
        BasicDBObject query = new BasicDBObject(ID, createObjectId(postId));
        ObjectId commentObjectId = createObjectId(commentId);
        BasicDBObject commentToRemove = new BasicDBObject();
        commentToRemove.put(ID, commentObjectId);
        BasicDBObject updateObj = new BasicDBObject();
        updateObj.put("$pull", new BasicDBObject(COMMENTS, commentToRemove));
        WriteResult result = collection.update(query, updateObj);

        return String.valueOf(result);
    }

    @Override
    public String updateContent(String postId, String content) {
        DBCollection collection = db.getCollection(POSTS_COLLECTION);
        BasicDBObject query = new BasicDBObject(ID, createObjectId(postId));
        BasicDBObject newDocument = new BasicDBObject();
        newDocument.put(CONTENT, content);
        BasicDBObject updateObj = new BasicDBObject();
        updateObj.put("$set", newDocument);
        WriteResult update = collection.update(query, updateObj);

        return update.toString();
    }

    @Override
    public String delete(String postId) {
        DBCollection collection = db.getCollection(POSTS_COLLECTION);
        BasicDBObject query = new BasicDBObject(ID, createObjectId(postId));
        WriteResult result = collection.remove(query);

        return result.toString();
    }

    @Override
    public Post find(String postId) throws ResourceNotFoundException {
        DBCollection collection = db.getCollection(POSTS_COLLECTION);
        BasicDBObject searchQuery;
        try {
            searchQuery = new BasicDBObject(ID, createObjectId(postId));
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException(String.format("Post with id [%s] not found", postId));
        }
        DBObject found = collection.findOne(searchQuery);
        Post post = new Post();
        post.setId(String.valueOf(found.get(ID)));
        post.setCreationDate((Date) found.get(CREATION_DATE));
        post.setTitle(String.valueOf(found.get(TITLE)));
        post.setContent(String.valueOf(found.get(CONTENT)));
        post.setComments(getComments(found));

        return post;
    }

    @Override
    public List<Post> findAll() {
        List<Post> posts = Lists.newArrayList();
        DBCollection collection = db.getCollection(POSTS_COLLECTION);
        DBCursor cursor = collection.find();

        while (cursor.hasNext()) {
            DBObject found = cursor.next();
            Post post = new Post();
            post.setId(String.valueOf(found.get(ID)));
            post.setCreationDate((Date) found.get(CREATION_DATE));
            post.setTitle(String.valueOf(found.get(TITLE)));
            post.setContent(String.valueOf(found.get(CONTENT)));
            post.setComments(getComments(found));
            posts.add(post);
        }

        return posts;
    }

    List<Comment> getComments(DBObject post) {
        List<Comment> comments = null;
        BasicDBList commentsAsDBObjects = (BasicDBList) post.get(COMMENTS);

        if (commentsAsDBObjects != null) {
            comments = Lists.newArrayListWithCapacity(commentsAsDBObjects.size());

            for (Object commentsAsDBObject : commentsAsDBObjects) {
                BasicDBObject basicDBObject = (BasicDBObject) commentsAsDBObject;
                Comment comment = new Comment();
                comment.setId(String.valueOf(basicDBObject.get(ID)));
                comment.setContent(String.valueOf(basicDBObject.get(CONTENT)));
                comments.add(comment);
            }
        }

        return comments;
    }
}
