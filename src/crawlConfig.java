import java.io.File;
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

    public int numberOfCrawlersRunning;

    public url baseCrawlUrl;

    public boolean shutDownInitiated = false;

    public File crawlStorageDir;

    public BlockingQueue<url> urlsToCrawl = new LinkedBlockingQueue<>();

    public Set<url> crawledUrls = new HashSet<>(50);

    public File getCrawlStorageDir() {
        return crawlStorageDir;
    }

    public void setCrawlStorageDir(File crawlStorageDir) {
        this.crawlStorageDir = crawlStorageDir;
    }

    public synchronized void execute(url URL) throws Exception
    {
        if(shutDownInitiated)
            throw new Exception("ThreadPool has been shutDown, no further tasks can be added");
        else
        {
            if(!crawledUrls.contains(URL) && !urlsToCrawl.contains(URL))
            {
                //System.out.println("task has been added.");
                urlsToCrawl.add(URL);
            }
        }
    }

    public synchronized void changeNumberOfRunningCrawlers(int i)
    {
        if(i>0)
            numberOfCrawlersRunning++;
        else
            numberOfCrawlersRunning--;
        if(numberOfCrawlersRunning==0)
            shutDownInitiated=true;
    }

    public crawlConfig()
    {
        for (int i = 0; i < numCrawlers; i++)
        {
            crawler Crawler = new crawler();
            Crawler.setName("Crawler-:"+(i+1));
            Crawler.start();
        }
        urlsToCrawl.add(baseCrawlUrl);
    }

    public crawlConfig(int n)
    {
        numCrawlers = n;
        for (int i = 0; i < numCrawlers; i++)
        {
            crawler Crawler = new crawler();
            Crawler.setName("Crawler-:"+(i+1));
            Crawler.start();
        }
        urlsToCrawl.add(baseCrawlUrl);
    }

}


