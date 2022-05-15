import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.context.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring Beans
 */
@Configuration
public class AppConfig {
    SessionFactory sessionFactory;

    @Bean
    public List<WebScraper> scraperList(){
        List<WebScraper> scraperList = new ArrayList<>();
        scraperList.add(scraper1());
        scraperList.add(scraper2());
        scraperList.add(scraper3());

        return scraperList;
    }

    @Bean
    public WebScraper scraper1(){
        WebScraper scraper1 = new Scraper1();
        scraper1.setDao(bookDao());
        scraper1.setDomainName("wordery.com");
        scraper1.setScraperName("Scraper 1 (" + scraper1.domainName + ")");
        scraper1.setSleepDuration(5000);

        return scraper1;
    }

    @Bean
    public WebScraper scraper2(){
        WebScraper scraper2 = new Scraper2();
        scraper2.setDao(bookDao());
        scraper2.setDomainName("blackwells.co.uk");
        scraper2.setScraperName("Scraper 2 (" + scraper2.domainName + ")");
        scraper2.setSleepDuration(5000);

        return scraper2;
    }

    @Bean
    public WebScraper scraper3(){
        WebScraper scraper3 = new Scraper3();
        scraper3.setDao(bookDao());
        scraper3.setDomainName("hive.co.uk");
        scraper3.setScraperName("Scraper 3 (" + scraper3.domainName + ")");
        scraper3.setSleepDuration(5000);

        return scraper3;
    }

    @Bean
    public BookDao bookDao(){
        BookDao bookDao = new BookDao();
        bookDao.setSessionFactory(sessionFactory());

        return bookDao;
    }

    @Bean
    public SessionFactory sessionFactory(){
        if (sessionFactory == null){
            // Building session factory
            StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder();
            standardServiceRegistryBuilder.configure("hibernate.cfg.xml");

            StandardServiceRegistry registry = standardServiceRegistryBuilder.build();

            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        }

        return sessionFactory;
    }
}
