import java.util.*;
import java.net.*;
import java.io.*;

/**
 * @author Benjamin Meeder
 * @author Adam De Gala
 */
public class WikiCrawler {
    private String seedUrl;
    private int max;
    private String fileName;

    static final String BASE_URL = "https://en.wikipedia.org";

    Queue<String> bfsQueue = new ArrayDeque<>();
    HashMap<String, String> visited = new HashMap<>();

    WikiCrawler(String seedUrl, int max, String fileName) throws Exception {
        if(!isValidURL(seedUrl)) throw new Exception("Invalid Seed URL");
        this.seedUrl = seedUrl;
        this.max = max;
        this.fileName = fileName;
    }

    public ArrayList<String> extractLinks(String doc, String currentPage) {
        doc = getFirstParagraph(doc);
        String[] list = doc.split("\"/wiki/");
        ArrayList<String> out = new ArrayList<>();
        list[0] = "\"";

        for(String l: list) {
            String toAdd = "/wiki/" + l.substring(0, l.indexOf("\""));
            if(isValidURL(toAdd) && !out.contains(toAdd) && !currentPage.equals(toAdd)) out.add(toAdd);
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
        int processed = 1;
        PrintWriter writer = initializePrintWriter();

        while(!bfsQueue.isEmpty()) {
            String currentPage = bfsQueue.poll();
            visited.put(currentPage,currentPage);
            String html = getHTML(currentPage);

            ArrayList<String> links = extractLinks(html, currentPage);

            for (String s: links) {
                if(!visited.containsKey(s)) {
                    if (processed < max) {
                        processed++;
                        if(processed%100 == 0) {
                            try {Thread.sleep(3000);}
                            catch (InterruptedException e) {e.printStackTrace();}
                        }
                        visited.put(s,s);
                        bfsQueue.add(s);
                        writer.println(currentPage + " " + s);
                        writer.flush();
                    }
                }
                else {
                    writer.println(currentPage + " " + s);
                    writer.flush();
                }
            }
        }
        writer.close();
    }

    private PrintWriter initializePrintWriter() {
        PrintWriter writer = null;
        try{
            writer = new PrintWriter(fileName, "UTF-8");
            writer.println(max);
            return writer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer;
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
        WikiCrawler crawler = new WikiCrawler("/wiki/Computer_Science", 500, "WikiCS.txt");
        crawler.crawl();
//        GraphProcessor processor = new GraphProcessor("WikiCS.txt");
//        System.out.println(processor.generateReport());
    }
}
