package kj.rest.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import kj.rest.domain.Post;
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
    DB db;
    @Mock
    DBCollection postsCollection;

    PostDaoImpl underTest;

    @Before
    public void setUp() {
        underTest = new PostDaoImpl();
        underTest.setDb(db);
    }

    @Test
    public void testCreate() throws Exception {
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
}
