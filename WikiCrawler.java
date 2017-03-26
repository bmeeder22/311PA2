import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class WikiCrawler {
    private String seedUrl;
    private int max;
    private String fileName;

    WikiCrawler(String seedUrl, int max, String fileName) {
        this.seedUrl = seedUrl;
        this.max = max;
        this.fileName = fileName;
    }

    public ArrayList<String> extractLinks(String doc) {
        doc = getFirstParagraph(doc);
        String[] list = doc.split("\"/wiki/");
        ArrayList<String> out = new ArrayList<>();
        list[0] = "\"";

        for(String l: list) {
            String toAdd = "/wiki/" + l.substring(0, l.indexOf("\""));
            if(isValidURL(toAdd)) out.add(toAdd);
        }
        return out;
    }

    private String getFirstParagraph(String fileText) {
        int paragraphIndex = fileText.indexOf("<p>");
        return fileText.substring(paragraphIndex);
    }

    private boolean isValidURL(String url) {
        if(url.length() < 7) return false;
        else if(!url.substring(0, 6).equals("/wiki/")) return false;
        else if(url.contains("#") || url.contains(":")) return false;
        return true;
    }

    public void crawl() {

    }

    public static void main(String args[]) throws FileNotFoundException {
        WikiCrawler crawler = new WikiCrawler("test", 1, "test");
        System.out.println(crawler.isValidURL("/wiki/XXXX")); //True
        System.out.println(crawler.isValidURL("/wiki/test")); //True
        System.out.println(crawler.isValidURL("/wiki/test#test")); //False
        System.out.println(crawler.isValidURL("/wiki/test:test")); //False
        System.out.println(crawler.isValidURL("/hello/XXXX")); //False
        System.out.println(crawler.isValidURL("/test")); //False
        System.out.println(crawler.isValidURL("/wiki")); //False
        System.out.println(crawler.isValidURL("/wiki/")); //False

        System.out.println();
        String content = new Scanner(new File("sample.txt")).useDelimiter("\\Z").next();
        System.out.println(crawler.extractLinks(content));
    }
}
