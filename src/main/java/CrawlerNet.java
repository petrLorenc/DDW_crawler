import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by petr.lorenc on 06.03.17.
 */
public class CrawlerNet extends WebCrawler {

    final static Logger logger = LoggerFactory.getLogger(CrawlerNet.class);

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp3|zip|gz))$");

    private final static Pattern ROBOTS = Pattern.compile(".*/f:.*:.*");
    private final static Pattern ROBOTS2 = Pattern.compile("/direct/mapa/.*");
    private final static Pattern ROBOTS3 = Pattern.compile("/direct/ajax/.*");
    private final static Pattern ROBOTS4 = Pattern.compile("/exit/.*");
    private final static Pattern ROBOTS5 = Pattern.compile("/direct/js/.*");

    private final static Pattern REVIEW = Pattern.compile(".*heureka.*/recenze/.*");


    public static ArrayList<Review> reviews = new ArrayList<>();
    private Object lock1 = new Object();

    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "http://www.ics.uci.edu/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        boolean status = href.contains(".heureka.cz") &&
                !FILTERS.matcher(href).find() &&
                !ROBOTS.matcher(href).find() &&
                !ROBOTS2.matcher(href).find() &&
                !ROBOTS3.matcher(href).find() &&
                !ROBOTS4.matcher(href).find() &&
                !ROBOTS5.matcher(href).find();
        logger.info(href + " -> " + status);
        return status;
    }

    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        if (REVIEW.matcher(url).find()) {
            logger.info("URL: " + url);
            if( page.getParseData() instanceof HtmlParseData){
                HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
                List<Review> temp = CrawlerNet.getReviewsForModel(url, htmlParseData.getHtml());
                synchronized (lock1) {
                    reviews.addAll(temp);
                }

            }
        }
    }


    public static void startCrawling(String[] args) throws Exception {

    /*
     * crawlStorageFolder is a folder where intermediate crawl data is
     * stored.
     */
        String crawlStorageFolder = "./outputJson";

    /*
     * numberOfCrawlers shows the number of concurrent threads that should
     * be initiated for crawling.
     */
        int numberOfCrawlers = 4;

        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder(crawlStorageFolder);

    /*
     * Be polite: Make sure that we don't send more than 1 request per
     * second (1000 milliseconds between requests).
     */
        config.setPolitenessDelay(1000);

        config.setUserAgentString("Mozilla/5.0 (compatible; lorenpe2bot/1.0)");
    /*
     * You can set the maximum crawl depth here. The default value is -1 for
     * unlimited depth
     */
        config.setMaxDepthOfCrawling(10);

    /*
     * You can set the maximum number of pages to crawl. The default value
     * is -1 for unlimited number of pages
     */
        config.setMaxPagesToFetch(1000);

        /**
         * Do you want crawler4j to crawl also binary data ?
         * example: the contents of pdf, or the metadata of images etc
         */
        config.setIncludeBinaryContentInCrawling(false);

    /*
     * Do you need to set a proxy? If so, you can use:
     * config.setProxyHost("proxyserver.example.com");
     * config.setProxyPort(8080);
     *
     * If your proxy also needs authentication:
     * config.setProxyUsername(username); config.getProxyPassword(password);
     */

    /*
     * This config parameter can be used to set your crawl to be resumable
     * (meaning that you can resume the crawl from a previously
     * interrupted/crashed crawl). Note: if you enable resuming feature and
     * want to start a fresh crawl, you need to delete the contents of
     * rootFolder manually.
     */
        config.setResumableCrawling(false);

    /*
     * Instantiate the controller for this crawl.
     */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        robotstxtConfig.setEnabled(true);
        robotstxtConfig.setUserAgentName("lorenpe2bot");

        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

    /*
     * For each crawl, you need to add some seed urls. These are the first
     * URLs that are fetched and then the crawler starts following links
     * which are found in these pages
     */
        controller.addSeed("https://telekomunikace.heureka.cz/");
        controller.addSeed("https://tv-video.heureka.cz/");
        controller.addSeed("https://foto.heureka.cz/");
        controller.addSeed("https://pocitace.heureka.cz/");

    /*
     * Start the crawl. This is a blocking operation, meaning that your code
     * will reach the line after this only when crawling is finished.
     */
        controller.start(CrawlerNet.class, numberOfCrawlers);

    /*
     * Crawling will be executed sequentially and it will gradually save reviews to List
     * The ArrayList of reviews will be save to JSON after stop crawling.
     */
        JSONHelper jsonHelper = new JSONHelper();
        jsonHelper.addReview(reviews);
    }

    public static List<Review> getReviewsForModel(String url, String html) {
        List<Review> reviews = new ArrayList<Review>();
        org.jsoup.nodes.Document doc = Jsoup.parse(html);
        org.jsoup.select.Elements newsHeadlines = doc.select(".review");
        for (org.jsoup.nodes.Element element : newsHeadlines) {
            String ratingText = element.select("big").text();
            String reviewText = element.select(".revtext").select("p").text();

            List<String> plus = new ArrayList<>();
            List<String> minus = new ArrayList<>();

            int usefulReview = 0;
            int uselessReview = 0;

            String date;
            String shop;

//                    System.out.println("PLUS");
            for (org.jsoup.nodes.Element e : element.select(".plus > ul >li")) {
                plus.add(e.text());
            }
//                    System.out.println("MINUS");
            for (org.jsoup.nodes.Element e : element.select(".minus > ul > li")) {
                minus.add(e.text());
            }

            Pattern p = Pattern.compile("[Ano|Ne].+(\\d).*");

            org.jsoup.select.Elements e = element.select(".evalreview > li:nth-child(2)");
            Matcher m = p.matcher(e.text());

            if (m.find()) {
                usefulReview = Integer.parseInt(m.group(1));
            }

            org.jsoup.select.Elements e2 = element.select(".evalreview > li:nth-child(3)");
            m = p.matcher(e2.text());

            if (m.find()) {
                uselessReview = Integer.parseInt(m.group(1));
            }

            org.jsoup.select.Elements e3 = element.select(".date");

            if (!e3.text().contains("2016")) {
                Calendar cal = Calendar.getInstance();
                DateFormat formatData = new SimpleDateFormat("d. MMMM yyyy");
                date = formatData.format(cal.getTime());
            } else {
                date = e3.text().substring(9);
            }

            org.jsoup.select.Elements e4 = element.select(".purchased > a");
            shop = e4.text();

            reviews.add(new Review(url, reviewText, ratingText, plus, minus, usefulReview, uselessReview, date, shop));
        }

        return reviews;
    }
}