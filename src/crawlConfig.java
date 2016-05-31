import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Hitesh on 24-May-16.
 */
public class crawlConfig {
    public int maxnumPages;

    public int numCrawlers = 5; //number of crawlers

    String baseCrawlDomain;

    public int numberOfCrawlersRunning = 0;

    public url baseCrawlUrl;

    public boolean shutDownInitiated = false;

    public File crawlStorageDir;

    public BlockingQueue<url> urlsToCrawl = new LinkedBlockingQueue<>();

    public Set<String> crawledUrls = new HashSet<>(50);

    public File getCrawlStorageDir() {
        return crawlStorageDir;
    }

    public void setCrawlStorageDir(File crawlStorageDir) {
        this.crawlStorageDir = crawlStorageDir;
    }

    public synchronized int executed(url URL)
    {
        if(!crawledUrls.contains(URL.getSourceUrl().toString()))
        {
            crawledUrls.add(URL.getSourceUrl().toString());
            return 1;
        }
        return 0;
    }

    public synchronized void execute(url URL) throws Exception
    {
        if(shutDownInitiated)
            throw new Exception("ThreadPool has been shutDown, no further tasks can be added");
        else
        {
            if(!crawledUrls.contains(URL.getSourceUrl().toString()))
            {
                //System.out.println("task has been added.");
                urlsToCrawl.put(URL);
            }
        }
    }

    public synchronized void changeNumberOfRunningCrawlers(int i)
    {
        if(i>0)
            numberOfCrawlersRunning++;
        else
            numberOfCrawlersRunning--;
        if(numberOfCrawlersRunning == numCrawlers)
            shutDownInitiated=true;
    }

    public crawlConfig()
    {
        for (int i = 0; i < numCrawlers; i++)
        {
            crawler Crawler = new crawler();
            Crawler.setName("Crawler-:"+(i+1));
            Crawler.myParent = this;
            Crawler.start();
        }
    }

    public crawlConfig(int n)
    {
        numCrawlers = n;
        numberOfCrawlersRunning = 0;
        for (int i = 0; i < numCrawlers; i++)
        {
            crawler Crawler = new crawler();
            Crawler.setName("Crawler-:"+(i+1));
            Crawler.myParent = this;
            Crawler.start();
        }
    }

    public boolean processPage(url Url) throws IOException
    {
        String domain;
        url url = Url;
        if(!crawledUrls.contains(url))
        {
            String protoc = url.getSourceUrl().getProtocol();
            if (!protoc.equals("http") && !protoc.equals("https"))
            {
                System.out.println("protocol is "+ protoc + "url not in HTTP and HTTPS: "+ url);
                return false;
            }

            domain = main.getDomain(url);

            if (!domain.equals(baseCrawlDomain))
            {
                System.out.println("url not the same domain as in baseUrl: "+ url.getSourceUrl().toString());
                return false;
            }

            int isAdded = executed(url);

            if(isAdded == 0)
            {
                return false;
            }

            Document doc;
            //get useful information
            try
            {
                doc = Jsoup.connect(url.getSourceUrl().toString()).timeout(10000).get();

                url.setContent(doc.html().getBytes());

                //if(doc.text().contains("research"))
                {
                    System.out.println(url.getSourceUrl().toString());
                }

                url.writeToFile();

                //get all links and recursively call the processPage method
                Elements questions = doc.select("a[href]");
                Elements images = doc.select("img[src]");
                Element body = doc.body();
                for(Element link: questions)
                {
                    //if(link.attr("href").contains("mit.edu"))
                    //System.out.println(link.attr("abs:href"));
                    execute(new url(link.attr("abs:href"),this));
                }
                for(Element link: images)
                {
                    //if(link.attr("href").contains("mit.edu"))
                    execute(new url(link.attr("abs:src"),this));
                }
            }
            catch (Exception e)
            {
                System.out.println("Error in this url: " + url.getSourceUrl().toString() + " " +e.getMessage());
            }
        }
        return true;
    }

}


