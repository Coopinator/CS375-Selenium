import java.util.Scanner;

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

        // Your code goes here

        return "Title: " + title + " Url: " + url;
    }
}
