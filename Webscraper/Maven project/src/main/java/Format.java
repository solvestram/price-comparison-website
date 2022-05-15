import javax.persistence.*;

/**
 * Bean that is mapped onto book format.
 * Contains fields id (Integer), name (String), book_id (Foreign key), isbn (String), image_url (String)
 */
@Entity
@Table(name = "format")
public class Format {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "image_url")
    private String imageUrl;

    @Override
    public String toString() {
        return "Format{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", book_id=" + book.getId() +
                ", isbn='" + isbn + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
