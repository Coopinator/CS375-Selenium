public class Program {
    static boolean runDemo = true;      // When running the material from the demo make sure this is true
    static boolean runHomework = false; // When testing your homework make sure this is true

    public static void main(String[] args) {
        if (runDemo) {
            Demo.RunDemo();
        }

        if (runHomework) {
            Homework.RunHomework();
        }
    }
}