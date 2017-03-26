import java.util.ArrayList;

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
        return null;
    }

    public void crawl() {

    }

    public static void main(String args[]) {

    }
}
