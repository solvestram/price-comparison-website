/**
 * Abstract class for Scraper classes. Contains necessary methods and for starting scrapers.
 */
public abstract class WebScraper extends Thread {
    BookDao dao;
    String scraperName;
    String domainName;
    int sleepDuration;

    protected boolean finishScraping = false;

    /**
     * Starts web scraper
     */
    public abstract void scrape();

    // Saves parsed book data to the database
    protected void saveParsedData(String parsedBookTitle, String parsedBookAuthor, String parsedBookFormatName, String parsedBookIsbn, String parsedBookImgUrl, String parsedBookPrice, String parsedBookUrl){
        Book book = new Book();
        Format format = new Format();
        Comparison comparison = new Comparison();

        // standardizing format names
        if (parsedBookFormatName.contains("Paperback") || parsedBookFormatName.contains("Softback") || parsedBookFormatName.contains("Softcover")){
            parsedBookFormatName = "Paperback";
        } else if (parsedBookFormatName.contains("Hardback") || parsedBookFormatName.contains("Hardcover")){
            parsedBookFormatName = "Hardback";
        }

        book.setTitle(parsedBookTitle);
        book.setAuthor(parsedBookAuthor);

        format.setName(parsedBookFormatName);
        format.setIsbn(parsedBookIsbn);
        format.setImageUrl(parsedBookImgUrl);
        format.setBook(book);

        comparison.setPrice(Float.parseFloat(parsedBookPrice.replace("£", "")));
        comparison.setUrl(parsedBookUrl);
        comparison.setFormat(format);

        dao.saveBook(book, format, comparison);
    }

    /**
     * Starts web scraper as a thread
     */
    public void run(){
        System.out.println(scraperName + " is running...");
        scrape();
    }

    public BookDao getDao() {
        return dao;
    }

    public void setDao(BookDao dao) {
        this.dao = dao;
    }

    public int getSleepDuration() {
        return sleepDuration;
    }

    public void setSleepDuration(int sleepDuration) {
        this.sleepDuration = sleepDuration;
    }

    public boolean isFinishScraping() {
        return finishScraping;
    }

    public void setFinishScraping(boolean finishScraping) {
        this.finishScraping = finishScraping;
    }

    public String getScraperName() {
        return scraperName;
    }

    public void setScraperName(String scraperName) {
        this.scraperName = scraperName;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }
}
