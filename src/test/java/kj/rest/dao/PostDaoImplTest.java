package kj.rest.dao;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891891
 * Date: 9/1/13
 * Time: 6:54 PM
 */
@RunWith(MockitoJUnitRunner.class)
public class PostDaoImplTest {
    private DB db;
    private DBCollection dbCollection;
    private PostDaoImpl underTest;

    @Before
    public void setUp() throws Exception {
        db = mock(DB.class);
        dbCollection = mock(DBCollection.class);
        underTest = new PostDaoImpl() {
            @Override
            protected DB getDb() {
                return db;
            }
        };
        underTest.setDbName("testDBName");
        underTest.setHost("testHost");
        underTest.setPort("12345");
    }

    @Test
    public void doNothing() {
        System.out.println("test");
    }

//    @Test
//    public void testCreate() throws Exception {
//        //given
//        when(db.getCollection("posts")).thenReturn(dbCollection);
//        when(dbCollection.insert())
//        Post post = new Post();
//        post.setTitle("test title");
//        post.setContent("test content");
//
//        //when
//        underTest.create(post);
//
//        //then
//
//    }
}
