import java.io.File;

/**
 * Created by Ayush on 5/31/2016.
 */
public class Multi_Thread_Main
{
    public static String storagePath = "C:\\Users\\Ayush\\Dropbox\\Hitesh_Ayush\\sem6\\Parallel_programming\\mini_proj";

    public static url baseCrawlUrl;

    public static void main(String[] args) throws Exception
    {
        System.setProperty("http.proxyHost","10.8.1.2");
        System.setProperty("http.proxyPort","8080");

        crawlConfig mirror_engine = new crawlConfig(3, baseCrawlUrl); //create 3 threads in ThreadPool

        baseCrawlUrl =  new url("http://insite.iitmandi.ac.in/insite_wp/",mirror_engine);
        mirror_engine.setCrawlStorageDir(new File(storagePath));
        mirror_engine.urlsToCrawl.add(baseCrawlUrl);


        //Runnable task=new Task();
        //threadPool.execute(task);
        //threadPool.execute(task);

        //threadPool.shutdown();
    }
}
