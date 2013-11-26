package kj.rest.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.WriteResult;
import kj.rest.domain.Post;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

import static kj.rest.dao.PostDaoImpl.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891891
 * Date: 9/1/13
 * Time: 6:54 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class PostDaoImplTest {
    @Mock
    WriteResult writeResult;
    @Mock
    DB db;
    @Mock
    DBCollection postsCollection;
    @Mock
    ObjectId objectId;

    private static final String POST_ID = "POST_ID";
    private static final String WRITE_RESULT_MESSAGE = "write result message";

    PostDaoImpl underTest;

    @Before
    public void setUp() {
        underTest = new PostDaoImpl() {
            @Override
            ObjectId createObjectId(String postId) {
                if (POST_ID.equals(postId)) {
                    return objectId;
                } else {
                    return null;
                }
            }
        };
        underTest.setDb(db);
        when(db.getCollection(POSTS_COLLECTION)).thenReturn(postsCollection);
        when(writeResult.toString()).thenReturn(WRITE_RESULT_MESSAGE);
    }

    @Test
    public void shouldCreatePost() throws Exception {
        //given
        final String createdId = "idPutAtInsertTime";
        underTest = new PostDaoImpl() {
            @Override
            protected String getCreatedId(BasicDBObject document) {
                return createdId;
            }
        };
        underTest.setDb(db);
        when(db.getCollection(POSTS_COLLECTION)).thenReturn(postsCollection);
        Post post = new Post();
        post.setTitle("test title");
        post.setContent("test content");
        post.setCreationDate(new Date());

        BasicDBObject document = new BasicDBObject();
        document.put(CONTENT, post.getContent());
        document.put(TITLE, post.getTitle());
        document.put(CREATION_DATE, post.getCreationDate());

        //when
        String postId = underTest.create(post);

        //then
        verify(postsCollection).insert(document);
        Assert.assertEquals(createdId, postId);
    }

    @Test
    public void shouldUpdatePostContent() {
        //given
        String postContent = "postContent";
        BasicDBObject query = new BasicDBObject(ID, objectId);
        BasicDBObject newDocument = new BasicDBObject();
        newDocument.put(CONTENT, postContent);
        BasicDBObject updateObj = new BasicDBObject();
        updateObj.put("$set", newDocument);

        when(postsCollection.update(query, updateObj)).thenReturn(writeResult);

        //when
        String updateMessage = underTest.updateContent(POST_ID, postContent);

        //then
        verify(postsCollection).update(query, updateObj);
        Assert.assertEquals(WRITE_RESULT_MESSAGE, updateMessage);
    }

    @Test
    public void shouldDeletePost() {
        //given
        BasicDBObject query = new BasicDBObject(ID, objectId);
        when(postsCollection.remove(query)).thenReturn(writeResult);

        //when
        String deleteMessage = underTest.delete(POST_ID);

        //then
        verify(postsCollection).remove(query);
        Assert.assertEquals(WRITE_RESULT_MESSAGE, deleteMessage);
    }
}
