import java.io.File;

/**
 * Created by Ayush on 5/31/2016.
 */
public class Multi_Thread_Main
{
    public static String storagePath = "/home/ayush/Desktop/insite/";

    public static url baseCrawlUrl;

    public static void main(String[] args) throws Exception
    {
        System.setProperty("http.proxyHost","10.8.1.2");
        System.setProperty("http.proxyPort","8080");

        crawlConfig mirror_engine = new crawlConfig(4);

        baseCrawlUrl =  new url("http://insite.iitmandi.ac.in/insite_wp/", mirror_engine, 0);
        mirror_engine.baseCrawlUrl = baseCrawlUrl;
        mirror_engine.urlsToCrawl.add(baseCrawlUrl);
        mirror_engine.baseCrawlDomain = url.getDomain(baseCrawlUrl);
        mirror_engine.setCrawlStorageDir(new File(storagePath));

    }
}
