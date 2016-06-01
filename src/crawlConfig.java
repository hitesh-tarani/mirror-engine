import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Hitesh on 24-May-16.
 */
public class crawlConfig {
    public Integer totalDownloadedBytes = 0;

    public int downloadLimit;

    public int downloadSpeed = 500;

    public int maxnumPages;

    public int numCrawlers = 5; //number of crawlers

    String baseCrawlDomain;

    public int numberOfCrawlersRunning = 5;

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

    public synchronized void incrementTotalDownloadedBytes(int n)
    {
        totalDownloadedBytes += n;
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

    public synchronized void execute(url URL)
    {
        if(shutDownInitiated)
            System.out.println("ThreadPool has been shutDown, no further tasks can be added");
        else
        {
            if(URL!=null && URL.getSourceUrl()!=null && !crawledUrls.contains(URL.getSourceUrl().toString()))
            {
                //System.out.println("task has been added.");
                try
                {
                    urlsToCrawl.put(URL);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void changeNumberOfRunningCrawlers(int i)
    {
        if(i>0)
            numberOfCrawlersRunning++;
        else
            numberOfCrawlersRunning--;
        if(numberOfCrawlersRunning == 0)
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
        Thread speedLimiter=new Thread(() -> {
            while(true)
            {
                downloadLimit += downloadSpeed*10;
                try
                {
                    Thread.sleep(10);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
        speedLimiter.start();
    }

    public crawlConfig(int n)
    {
        numCrawlers = n;
        numberOfCrawlersRunning = n;
        for (int i = 0; i < numCrawlers; i++)
        {
            crawler Crawler = new crawler();
            Crawler.setName("Crawler-:"+(i+1));
            Crawler.myParent = this;
            Crawler.start();
        }
        Thread speedLimiter=new Thread(() -> {
            while(true)
            {
                downloadLimit += downloadSpeed*10;
                try
                {
                    Thread.sleep(10);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
        speedLimiter.start();
    }

    public crawlConfig(int n, int speed)
    {
        downloadSpeed = speed;
        numCrawlers = n;
        numberOfCrawlersRunning = n;
        for (int i = 0; i < numCrawlers; i++)
        {
            crawler Crawler = new crawler();
            Crawler.setName("Crawler-:"+(i+1));
            Crawler.myParent = this;
            Crawler.start();
        }
        Thread speedLimiter=new Thread(() -> {
            while(true)
            {
                downloadLimit += downloadSpeed*10;
                try
                {
                    Thread.sleep(10);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
        speedLimiter.start();
    }

    public void processPage(url Url) throws IOException
    {
        String domain;
        url url = Url;
        if(url==null||url.getSourceUrl()==null)
            return ;
        if(!crawledUrls.contains(url))
        {
            String protoc = url.getSourceUrl().getProtocol();
            if (!protoc.equals("http") && !protoc.equals("https"))
            {
                System.out.println("protocol is "+ protoc + "url not in HTTP and HTTPS: "+ url);
                return ;
            }

            domain = main.getDomain(url);

            if (!domain.equals(baseCrawlDomain))
            {
                System.out.println("url not the same domain as in baseUrl: "+ url.getSourceUrl().toString());
                return ;
            }

            int isAdded = executed(url);

            if(isAdded == 0)
            {
                return ;
            }

            HttpURLConnection httpConn = (HttpURLConnection) url.getSourceUrl().openConnection();

            int responseCode = httpConn.getResponseCode();


            if (responseCode != HttpURLConnection.HTTP_OK)
            {
                return ;
            }
            url.setContentType(httpConn.getContentType());

            if (!url.getContentType().contains("text/html"))
            {
//                  byte[] content = new byte[4096];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                InputStream is = httpConn.getInputStream();
                byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
                int n;

                while ( (n = is.read(byteChunk)) > 0 )
                {
                    incrementTotalDownloadedBytes(n);
                    downloadLimit -= n;
                    baos.write(byteChunk, 0, n);
                    while(downloadLimit<0)
                    {
                        System.out.println("Download Limit exceeded so sleeping");
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                url.content = baos.toByteArray();
//                    System.out.println();
                url.writeToFile();
                return ;
            }

            InputStream is = httpConn.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
            int n;

            while ( (n = is.read(byteChunk)) > 0 )
            {
                incrementTotalDownloadedBytes(n);
                downloadLimit -= n;
                baos.write(byteChunk, 0, n);
                while(downloadLimit<0)
                {
                    System.out.println("Download Limit exceeded so sleeping");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            String content = baos.toString();

            Document doc;
            //get useful information
            try
            {
                doc = Jsoup.parse(content);
//                doc = Jsoup.connect(url.getSourceUrl().toString()).timeout(100000).get();

                //if(doc.text().contains("research"))
                {
                    System.out.println(url.getSourceUrl().toString());
                }


                //get all links
                ArrayList<Pair> allLinks = new ArrayList<>(5);
                allLinks.add(new Pair("a[href]","href"));
                allLinks.add(new Pair("img[src]","src"));
                allLinks.add(new Pair("link[href]","href"));
                allLinks.add(new Pair("script[src]","src"));
//                    allLinks.add(new Pair("meta","url"));

                for (Pair linkPair: allLinks)
                {

                    String attr = linkPair.attr;
                    Elements links = doc.select(linkPair.tag);

                    for (Element link: links)
                    {
//                            System.out.println("link : " + links.html());
                        url newUrl = new url(link.attr("abs:" + attr),this,linkPair.tag);
                        urlsToCrawl.add(newUrl);
//                            System.out.print("link : " + link.attr(attr));
                        link.attr(attr,url.modifyUrl(link.attr(attr),newUrl,url));

                    }
                }

                url.setContent(doc.html().getBytes());
                url.writeToFile();
            }
            catch (Exception e)
            {
                System.out.println("Error in this url: " + url.getSourceUrl().toString());
                e.printStackTrace();
            }
            return ;
        }
        return ;
    }

}


