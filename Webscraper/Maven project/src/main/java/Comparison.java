import javax.persistence.*;

/**
 * Bean that is mapped onto book format.
 * Contains fields id (Integer), format (Foreign key), price (float), url (String)
 */
@Entity
@Table(name = "comparison")
public class Comparison {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "format_id")
    private Format format;

    @Column(name = "price")
    private float price;

    @Column(name = "url")
    private String url;

    @Override
    public String toString() {
        return "Comparison{" +
                "id=" + id +
                ", format_id=" + format.getId() +
                ", price=" + price +
                ", url='" + url + '\'' +
                '}';
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
