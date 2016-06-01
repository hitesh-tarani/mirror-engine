import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Ayush on 5/31/2016.
 */
public class Multi_Thread_Main
{
    public static String storagePath = "/home/mirror_engine/";

    public static url baseCrawlUrl;

    public static void main(String[] args) throws Exception
    {
        System.setProperty("http.proxyHost","10.8.1.2");
        System.setProperty("http.proxyPort","8080");

        crawlConfig mirror_engine = new crawlConfig(3, 10000);

        baseCrawlUrl =  new url("http://insite.iitmandi.ac.in/moodle/", mirror_engine, 0);
        mirror_engine.baseCrawlUrl = baseCrawlUrl;
        mirror_engine.baseCrawlDomain = url.getDomain(baseCrawlUrl);
        mirror_engine.setCrawlStorageDir(new File(storagePath + mirror_engine.baseCrawlDomain));
        mirror_engine.urlsToCrawl.add(baseCrawlUrl);
        FileOutputStream out = null;

        try
        {
            out = new FileOutputStream("1_output_2_100.txt");
        }
        catch (IOException e)
        {
            ;
        }

        while(true)
        {
            if (out != null) {
                out.write(mirror_engine.totalDownloadedBytes.toString().getBytes());
                out.write("\n".getBytes());
            }
            Thread.sleep(5000);
        }
    }
}
