
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Demo {
    public static void RunDemo() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Scrape YouTube Links? (Press enter)");
            scanner.nextLine();
            for (String link : PullYoutubeUrls()) {
                System.out.println(link);
            }

            System.out.print("Pull youtube button descriptions from Acu Sports? (Press enter)");
            scanner.nextLine();
            for (String desc : PullVideoActionDescriptions()) {
                System.out.println(desc);
            }

            System.out.print("Scrape YouTube Links (This time in headless mode)? (Press enter)");
            scanner.nextLine();
            for (String link : PullYoutubeUrlsWithHeadlessBrowser()) {
                System.out.println(link);
            }

            System.out.print("Pull youtube button links from Acu Sports? (Press enter)");
            scanner.nextLine();
            for (String link : PullVideoLinksUsingSelenium()) {
                System.out.println(link);
            }

            System.out.print("Pull news details Acu Sports? (Press enter)");
            scanner.nextLine();
            PullNewsData();
        }
    }

    // Basic site retrieval and scrapping
    static List<String> PullYoutubeUrls() {
        // Sets up the browser that selenium uses to retrieve content
        WebDriver driver = new ChromeDriver(); 

        // Retrieves the site based on the url
        driver.get("https://acu.edu/academics/business-administration/information-technology-computing/");

        // Maps individual WebElements that are found from the css selector to the contents of their href attributes
        List<String> youtubeLinks = driver.findElements(By.cssSelector("a[href*='youtube.com']")).stream()
            .map(element -> element.getAttribute("href")).collect(Collectors.toCollection(ArrayList::new));

        // Ends the browser session
        driver.quit();

        return youtubeLinks;
    }

    // Showing Jsoup's Limitations
    static List<String> PullVideoActionDescriptions() {
        try {
            // Get the site content
            Document doc = Jsoup.connect("https://acusports.com/").get();

            // Get the slider element that contains the buttons
            Element youtubeSlider = doc.selectFirst("#main-youtube-slider");

            // Gets the individual button elements
            Elements youtubeButtons = youtubeSlider.select("button").stream()
                .filter(element -> element.attr("id").contains("video-modal-play"))
                .collect(Collectors.toCollection(Elements::new));

            // Returns the mapping of the button elements to their aria-labels which contain descriptions of what the buttons do
            return youtubeButtons.stream().map(element -> element.attr("aria-label")).collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    // Using browser options
    /* Common options used for chrome include the following:
     * --headless=new           (Starts the browser in headless mode)
     * --start-maximized        (Starts the browser maximized)
     * --incognito              (Starts the browser in Incognito mode)
     * --disable-extensions     (Disables existing Chrome extensions)
     * --disable-popup-blocking (Disables pop-up blocking)
     * --disable-gpu            (Disables GPU hardware acceleration. This is often helpful when running in headless mode)
     */ 
    static List<String> PullYoutubeUrlsWithHeadlessBrowser() {
        // Setting up chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");

        // Sets up the browser that selenium uses to retrieve content (This time in headless mode)
        WebDriver driver = new ChromeDriver(options); 

        // Retrieves the site based on the url
        driver.get("https://acu.edu/academics/business-administration/information-technology-computing/");

        // Maps individual WebElements that are found from the css selector to the contents of their href attributes
        List<String> youtubeLinks = driver.findElements(By.cssSelector("a[href*='youtube.com']")).stream()
            .map(element -> element.getAttribute("href")).collect(Collectors.toCollection(ArrayList::new));

        // Ends the browser session
        driver.quit();

        return youtubeLinks;
    }

    // Scraping Dynamically Loaded Content
    static List<String> PullVideoLinksUsingSelenium() {
        // Setting up chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");

        // Sets up the browser that selenium uses to retrieve content
        WebDriver driver = new ChromeDriver(options); 
        driver.get("https://acusports.com/");

        // Find the youtube slider and buttons like we did with Jsoup
        WebElement youtubeSlider = driver.findElement(By.cssSelector("#main-youtube-slider"));
        List<WebElement> youtubeButtons = youtubeSlider.findElements(By.cssSelector("button")).stream()
            .filter(element -> element.getAttribute("id").contains("video-modal-play"))
            .collect(Collectors.toCollection(ArrayList::new));

        // Loops through all of the buttons in order to get the links on the iframes that generate once they are clicked
        // Using a wait with a 1 second timeout to allow for the iframes to load before pulling their links
        List<String> videoLinks = new ArrayList<>();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(1));
        for (WebElement button : youtubeButtons) {
            try {
                // Clicks the button so the iframe can render
                // Using JavaScript to execute this is necessary as the scrollability of 
                // the swiper and the cookie consent window can prevent button.click() 
                // from working
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);

                // Waits until the iframe has loaded once the button has been clicked
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("iframe[class='showcase-video-player']")));
                WebElement videoIframe = driver.findElement(By.cssSelector("iframe[class='showcase-video-player']"));

                // Gets the link from the iframe
                videoLinks.add(videoIframe.getAttribute("src"));

                // Finds the close button that also was loaded when the iframe loaded so the modal can be closed before clicking the next one
                WebElement closeButton = driver.findElement(By.cssSelector("button[id*='video-modal-close']"));
                closeButton.click();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }

        return videoLinks;
    }

    // Integrating with Jsoup
    static void PullNewsData() {
        // Setting up chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");

        // Sets up the browser that selenium uses to retrieve content
        WebDriver driver = new ChromeDriver(options); 
        driver.get("https://acusports.com/");

        // Finds the news grid and pulls links from individual articles
        WebElement newsGrid = driver.findElement(By.cssSelector("#main-stories-grid"));
        List<String> newsLinks = newsGrid.findElements(By.cssSelector("article div.c-stories__details a")).stream()
            .map(element -> element.getAttribute("href")).collect(Collectors.toCollection(ArrayList::new));

        // Uses Selenium to connect to the links and Jsoup to parse the static page contents to get title and the author
        for (String link : newsLinks) {
            driver.get(link);
            Document doc = Jsoup.parse(driver.getPageSource());
            String title = doc.select("div[class*='story-page__content__title-box']").first()
                .children().first().text();
            String author = doc.select("p").stream()
                .filter(element -> element.text().contains("By:"))
                .collect(Collectors.toCollection(Elements::new))
                .first().children().first().text();

            System.out.println("Title: " + title + ", Author: " + author);
        }

        driver.quit();
    }
}
