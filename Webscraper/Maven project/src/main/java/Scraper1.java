import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Scraper for https://wordery.com
 */
public class Scraper1 extends WebScraper{

    public void scrape() {
        // book list url
        String booksListUrl = "https://wordery.com/fantasy-books-FM/bestsellers?viewBy=grid&resultsPerPage=20";
        int pageCounter = 1;

        while (!finishScraping){
            // adding page number to the url
            String booksListUrlPage = booksListUrl + "&page=" + pageCounter;

            Document document = null;
            try {
                // trying to get the page
                document = Jsoup.connect(booksListUrlPage).get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // checking if the page is valid
            boolean validPage = document.select(".c-zero-results").isEmpty();

            // parsing
            if (validPage){
                parsePage(document);
                pageCounter++;
            } else {
                break;
            }

        }
        System.out.println(scraperName + " finished its task.");
    }

    private void parsePage(Document document) {
        Elements parsedBooksList = document.body().select(".o-book-list").select(".o-book-list__book");
        for (Element parsedBook : parsedBooksList){
            if (finishScraping){
                break;
            }

            String parsedBookTitle, parsedBookAuthor, parsedBookFormatName, parsedBookIsbn, parsedBookImgUrl, parsedBookPrice, parsedBookUrl;

            if (parsedBook.select(".c-book__actions--none").isEmpty()){
                // parsed book
                parsedBookTitle = parsedBook.select(".c-book__title").text();
                parsedBookAuthor = parsedBook.select(".c-book__by").text();
                parsedBookFormatName = parsedBook.select(".c-book__meta").text();
                parsedBookIsbn = parsedBook.select("div").attr("data-isbn");
                parsedBookImgUrl = "https://" + domainName + parsedBook.select("img.c-book__img").attr("src");
                parsedBookPrice = parsedBook.select(".c-book__price").first().ownText();
                parsedBookUrl = "https://" + domainName + parsedBook.select(".c-book__title").attr("href");

                // Saving book data to the database
                saveParsedData(parsedBookTitle, parsedBookAuthor, parsedBookFormatName, parsedBookIsbn, parsedBookImgUrl, parsedBookPrice, parsedBookUrl);

                // Sleeping
                try {
                    Thread.sleep(sleepDuration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                continue;
            }
        }
    }
}
