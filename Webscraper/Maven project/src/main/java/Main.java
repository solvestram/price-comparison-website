import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Scanner;

/**
 * Main class
 */
public class Main {
    public static void main(String [] args){
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        // Starting web scrapers
        List<WebScraper> scraperList = (List<WebScraper>) context.getBean("scraperList");
        for (WebScraper scraper: scraperList){
            scraper.start();
        }

        // Stopping web scrapers when needed
        Scanner scanner = new Scanner(System.in);
        while(true){
            String scannerInput = scanner.nextLine();
            if (scannerInput.equals("")){
                System.out.println("Stopping all the scrapers...");
                for (WebScraper scraper: scraperList){
                    System.out.println("Sending stop signal to " + scraper.getScraperName());
                    scraper.setFinishScraping(true);
                    try {
                        scraper.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }
}
