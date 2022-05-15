import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Scraper for https://blackwells.co.uk
 */
public class Scraper2 extends WebScraper{

    public void scrape() {
        // book list url
        String booksListUrl = "https://blackwells.co.uk/bookshop/category/FM";
        // book page url
        String booksListUrlPage = booksListUrl;

        while (!finishScraping){
            Document document = null;
            try {
                // trying to get the page
                document = Jsoup.connect(booksListUrlPage).get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // next button
            Elements nextButtons = document.select(".btn.btn--secondary.js-search").select(":containsOwn(Next)");

            // checking if the next page is valid
            boolean isNextPageValid = !nextButtons.isEmpty();

            // parsing
            parsePage(document);

            if (isNextPageValid){
                booksListUrlPage = booksListUrl + "?offset=" + nextButtons.attr("data-page");
            } else {
                break;
            }

        }
        System.out.println(scraperName + " finished its task.");
    }

    private void parsePage(Document document) {
        Elements parsedBooksList = document.body().select(".search-result").select(".search-result__item");
        for (Element parsedBook : parsedBooksList){
            if (finishScraping){
                break;
            }

            String parsedBookTitle, parsedBookAuthor, parsedBookFormatName, parsedBookIsbn, parsedBookImgUrl, parsedBookPrice, parsedBookUrl;

            if (!parsedBook.select(".product-info").select(".product-price--current").isEmpty()){
                // parsed book
                parsedBookTitle = parsedBook.select(".product-info").select(".product-name").text();
                parsedBookAuthor = parsedBook.select(".product-info").select(".product-author").text();
                parsedBookFormatName = parsedBook.select(".product-info").select(".product-format").text();
                parsedBookIsbn = parsedBook.select(".btn.btn--secondary.js-add-to-basket").attr("data-isbn");
                parsedBookImgUrl = "https://" + domainName + parsedBook.select(".product-picture").select("img").attr("src");
                parsedBookPrice = parsedBook.select(".product-info").select(".product-price--current").first().ownText();
                parsedBookUrl = "https://" + domainName + parsedBook.select(".product-info").select("a.product-name").attr("href");

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
