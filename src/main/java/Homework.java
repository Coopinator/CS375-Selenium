import java.time.Duration;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Homework {
    public static void RunHomework() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Scrape Main Video Details? (Press enter)");
            scanner.nextLine();
            System.out.println(DisplayMainYoutubeVideoDetails());
        }
    }

    // HOMEWORK: Use Selenium and/or Jsoup to pull the url and title of the main youtube video on the page
    // HINT: You will need to click the button for the video to open so you can access the src attribute on the iframe that has the url
    // Both of these are implemented in a similar fashion in the methods defined above so use those as a guide
    static String DisplayMainYoutubeVideoDetails() {
        String url = "";
        String title = "";

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");

        WebDriver driver =  new ChromeDriver(options);

        try {
            driver.get("https://acusports.com/");

            WebElement mainYoutubeGrid = driver.findElement(By.cssSelector("#main-youtube-grid"));
            WebElement playButton = mainYoutubeGrid.findElement(By.cssSelector("button"));

            String fullAriaLabel = playButton.getAttribute("aria-label");

            if (fullAriaLabel != null) {
                title = fullAriaLabel.replace("Play video ", "").trim();
            }

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", playButton);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("iframe[class='showcase-video-player']")));

            WebElement videoIframe = driver.findElement(By.cssSelector("iframe[class='showcase-video-player']"));

            String rawEmbedUrl = videoIframe.getAttribute("src");

            String[] urlParts = rawEmbedUrl.split("youtube=");

            if (urlParts.length > 1) {
                url = java.net.URLDecoder.decode(urlParts[1], "UTF-8");
            } else {
                url = rawEmbedUrl;
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        } finally {
            driver.quit();
        }

        return "Title: " + title + " Url: " + url;
    }
}