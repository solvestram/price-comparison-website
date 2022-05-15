import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Scraper for https://hive.co.uk
 */
public class Scraper3 extends WebScraper{
    // Scraper for https://hive.co.uk

    public void scrape(){
        // book list url
        String booksListUrl = "https://www.hive.co.uk/Search/Books/Science-Fiction-Fantasy-Horror/Fantasy/In-Stock?fq=01120-122883-1228832885-4260";
        int urlPageCounter = 1;

        while(!finishScraping){
            // Page url
            String booksListUrlPage = booksListUrl + "&pg=" + urlPageCounter;

            // Getting the page with URLs
            Document documentUrlPage = null;
            try {
                // trying to get the page
                documentUrlPage = Jsoup.connect(booksListUrlPage).get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<String> listUrls = parseUrls(documentUrlPage);

            // End the task if there are no URLs found
            if (listUrls.size() == 0) {
                break;
            }

            // Opening pages with parsed URLs and sending to the book page parser
            for(String url: listUrls){
                if (finishScraping){
                    break;
                }

                Document documentBookPage = null;
                try {
                    // trying to get the page
                    documentBookPage = Jsoup.connect(url).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                parseBookPage(documentBookPage);
            }

            // updating url page counter
            urlPageCounter++;
        }
        System.out.println(scraperName + " finished its task.");
    }

    private List<String> parseUrls(Document document){
        List<String> urls = new ArrayList<>();

        // Getting URLs
        Elements parsedUrlsList = document.body().select(".searchResultItems").select(".search-item.search-item--inStock");
        for (Element parsedUrl: parsedUrlsList){
            String url = parsedUrl.select(".search-item__title").select("a").attr("href");
            url = "https://" + domainName + url;
            urls.add(url);
        }
        return urls;
    }

    private void parseBookPage(Document document){
        Elements parsedBook = document.body().select(".product.book");

        // Parsing book data
        String parsedBookTitle, parsedBookAuthor, parsedBookFormatName, parsedBookIsbn, parsedBookImgUrl, parsedBookPrice, parsedBookUrl;
        parsedBookTitle = parsedBook.select(".titleAuthorContributor").select("*[itemprop='name']").first().ownText();
        parsedBookAuthor = parsedBook.select(".titleAuthorContributor").select("h2[itemprop='author']").select("a").text();
        parsedBookFormatName = parsedBook.select(".productInfo").select(".format").select(".info").first().text();
        parsedBookIsbn = parsedBook.select(".productInfo").select(".EAN").select(".info").first().text();
        parsedBookImgUrl = "https:" + parsedBook.select(".productImage").attr("src");
        parsedBookPrice = parsedBook.select(".priceArea").select(".price").select(".sitePrice").text();
        parsedBookUrl = document.location();
        //

        // Saving book data to the database
        saveParsedData(parsedBookTitle, parsedBookAuthor, parsedBookFormatName, parsedBookIsbn, parsedBookImgUrl, parsedBookPrice, parsedBookUrl);

        // Sleeping
        try {
            Thread.sleep(sleepDuration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
