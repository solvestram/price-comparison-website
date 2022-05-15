import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains methods for managing database.
 */
public class BookDao {
    SessionFactory sessionFactory;

    /**
     * Saves a new book with format and comparison linked to it into database.
     *
     * Does not create a new duplicate book if a book with same title and author exists.
     * Does not add a new format if a format with same isbn already exits.
     * Updates existing comparison with same URL if one with same url already exits.
     *
     * @param book Book object with book information
     * @param format Format object with format information with a link to the book
     * @param comparison Comparison objecti with comparison information with a link to the format
     */
    public void saveBook(Book book, Format format, Comparison comparison){
        // printing the book to save
        printBook(book, format, comparison);

        // check if this does not cause issues in multithreading
        Book foundBook = findBook(book);
        Format foundFormat = findFormat(format);
        List<Comparison> foundComparisons = new ArrayList<>();
        if (foundFormat != null){
            foundComparisons = findComparisons(foundFormat);
        }

        Session session = sessionFactory.getCurrentSession();

        if (foundBook != null){
            if (foundFormat != null){
                // check if existing comparison with same url exists
                boolean foundExistingComparison = false;
                for (Comparison foundComparison: foundComparisons){
                    if (foundComparison.getUrl().equals(comparison.getUrl())){
                        // update existing comparison price
                        session.beginTransaction();
                        foundComparison.setPrice(comparison.getPrice());
                        session.getTransaction().commit();

                        // change the "found" to true
                        foundExistingComparison = true;
                        break;
                    }
                }

                // save a new comparison if existing comparison not found
                if (!foundExistingComparison){
                    // updating foreign key of the comparison to the existing format
                    comparison.setFormat(foundFormat);

                    // adding new comparison (does not update previous comparison from the same website)
                    session.beginTransaction();
                    session.save(comparison);
                    session.getTransaction().commit();
                }
            } else {
                // updating foreign key of the format to the existing book
                format.setBook(foundBook);

                // adding new format and comparison
                session.beginTransaction();
                session.save(format);
                session.save(comparison);
                session.getTransaction().commit();
            }
        } else {
            // adding new book
            session.beginTransaction();
            session.save(book);
            session.save(format);
            session.save(comparison);
            session.getTransaction().commit();
        }

        session.close();

    }

    protected Book findBook(Book book){
        Session session = sessionFactory.getCurrentSession();

        session.beginTransaction();

        String queryStr = "from Book where title=:title AND author=:author";

        List<Book> bookList = session.createQuery(queryStr).setParameter("title", book.getTitle()).setParameter("author", book.getAuthor()).getResultList();

        session.getTransaction().rollback();

        session.close();

        if (bookList.size() != 0) return bookList.get(0);
        else return null;
    }

    protected Format findFormat(Format format){
        Session session = sessionFactory.getCurrentSession();

        session.beginTransaction();

        String queryStr = "from Format where isbn=:isbn";

        List<Format> formatList = session.createQuery(queryStr).setParameter("isbn", format.getIsbn()).getResultList();

        session.getTransaction().rollback();

        session.close();

        if (formatList.size() != 0) return formatList.get(0);
        else return null;
    }

    protected List<Comparison> findComparisons(Format foundFormat){
        Session session = sessionFactory.getCurrentSession();

        session.beginTransaction();

        String queryStr = "from Comparison where format=:format";

        List<Comparison> foundComparisons = session.createQuery(queryStr).setParameter("format", foundFormat).getResultList();

        session.getTransaction().rollback();

        session.close();

        return foundComparisons;
    }

    protected void printBook(Book book, Format format, Comparison comparison){
        System.out.println("-------------------");
        System.out.println(book);
        System.out.println(format);
        System.out.println(comparison);
        System.out.println("-------------------");
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
