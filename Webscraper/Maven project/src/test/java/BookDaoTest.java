import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.jupiter.api.Assertions.*;


import java.util.List;

@DisplayName("Test Book dao")
public class BookDaoTest {
    SessionFactory sessionFactory;
    BookDao dao;
    Book testBook;
    Format testFormat;
    Comparison testComparison;

    @BeforeEach
    void setupEach(){
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        sessionFactory = (SessionFactory) context.getBean("sessionFactory");

        dao = (BookDao) context.getBean("bookDao");

        // Creating test book
        testBook = new Book();
        testBook.setTitle("Test title");
        testBook.setAuthor("Test author name");

        testFormat =  new Format();
        testFormat.setName("Paperback");
        testFormat.setIsbn("123456789");
        testFormat.setImageUrl("https://example.com/image");
        testFormat.setBook(testBook);

        testComparison = new Comparison();
        testComparison.setPrice(99);
        testComparison.setUrl("https://example.com");
        testComparison.setFormat(testFormat);
    }

    @Test
    @DisplayName("Test save book")
    void saveBookTest(){
        // Calling save book method
        dao.saveBook(testBook, testFormat, testComparison);

        // Checking if the book was saved successfully
        Session session = sessionFactory.getCurrentSession();

        session.beginTransaction();

        List<Book> bookList = session.createQuery("FROM Book WHERE title='" + testBook.getTitle() + "'").getResultList();
        List<Format> formatList = session.createQuery("FROM Format WHERE isbn='" + testFormat.getIsbn() + "'").getResultList();
        List<Comparison> comparisonList = session.createQuery("FROM Comparison WHERE url='" + testComparison.getUrl() + "'").getResultList();

        // Checking if there is only one result
        if(bookList.size() != 1)
            // There was an issue with saving the book
            fail("Book was not successfully stored. Book list size: " + bookList.size());

        if(formatList.size() != 1)
            // There was an issue with saving the format
            fail("Format was not successfully stored. Format list size: " + formatList.size());

        if(comparisonList.size() != 1)
            // There was an issue with saving the comparison
            fail("Comparison was not successfully stored. Comparison list size: " + comparisonList.size());

        session.getTransaction().commit();

        session.close();
    }

    @Test
    @DisplayName("Test findBook")
    void findBookTest(){
        // Saving test book
        dao.saveBook(testBook, testFormat, testComparison);

        if (dao.findBook(testBook) == null){
            fail("findBook method could not find existing book.");
        }
    }

    @Test
    @DisplayName("Test findFormat")
    void findFormatTest(){
        // Saving test book
        dao.saveBook(testBook, testFormat, testComparison);

        if (dao.findFormat(testFormat) == null){
            fail("findBook method could not find existing format.");
        }
    }

    @Test
    @DisplayName("Test findComparison")
    void findComparisonsTest(){
        // Saving test book
        dao.saveBook(testBook, testFormat, testComparison);
        if (dao.findComparisons(testFormat).size() == 0){
            fail("findBook method could not find existing book.");
        }
    }

    @AfterEach
    void tearDownAll(){
        // Delete test book data from database

        Session session = sessionFactory.getCurrentSession();

        session.beginTransaction();

        List<Book> bookList = session.createQuery("FROM Book WHERE title='" + testBook.getTitle() + "'").getResultList();
        List<Format> formatList = session.createQuery("FROM Format WHERE isbn='" + testFormat.getIsbn() + "'").getResultList();
        List<Comparison> comparisonList = session.createQuery("FROM Comparison WHERE url='" + testComparison.getUrl() + "'").getResultList();

        for (Comparison comparison: comparisonList){
            session.delete(comparison);
        }

        for (Format format: formatList){
            session.delete(format);
        }

        for (Book book: bookList){
            session.delete(book);
        }

        session.getTransaction().commit();

        session.close();
    }
}
