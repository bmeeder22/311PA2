import java.net.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Queue;
import java.io.*;

public class WikiCrawler {
    private String seedUrl;
    private int max;
    private String fileName;

    private final String BASE_URL = "https://en.wikipedia.org";

    Queue<String> bfsQueue = new ArrayDeque<>();
    ArrayList<String> visited = new ArrayList<>();

    WikiCrawler(String seedUrl, int max, String fileName) throws Exception {
        if(!isValidURL(seedUrl)) throw new Exception("Invalid Seed URL");
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
        bfsQueue.add(seedUrl);
        int processed = 0;

        PrintWriter writer = null;

        try{
            writer = new PrintWriter(fileName, "UTF-8");
            writer.println(max);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(!bfsQueue.isEmpty()) {
            if(processed>=max) break;
            processed++;
            String currentPage = bfsQueue.poll();
            visited.add(currentPage);
            String html = getHTML(currentPage);

            ArrayList<String> links = extractLinks(html);
            ArrayList<String> toRemove = new ArrayList<>();

            for (String s: links) {
                if(visited.contains(s))
                    toRemove.add(s);
                else {
                    bfsQueue.add(s);
                    writer.println(currentPage + " " + s);
                    writer.flush();
                }
            }
            links.removeAll(toRemove);
        }
        writer.close();
    }

    private String getHTML(String urlToRead) {
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL(BASE_URL + urlToRead);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            return result.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String args[]) throws Exception {
        WikiCrawler crawler = new WikiCrawler("/wiki/test", 2, "WikiCS.txt");
        crawler.crawl();
    }
}
