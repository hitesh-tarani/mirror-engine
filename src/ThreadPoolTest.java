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
 * ThreadPool is a class which creates a thread pool that reuses a fixed
 * number of threads to execute tasks.
 * At any point, at most nThreads threads will be active processing tasks.
 * If additional tasks are submitted when all threads are active,
 * they will wait in the queue until a thread is available.
 *
 * Once shutdown of ThreadPool is initiated, previously submitted tasks are
 * executed, but no new tasks will be accepted.
 *
 * @author AnkitMittal
 * Copyright (c), AnkitMittal . JavaMadeSoEasy.com
 * All Contents are copyrighted and must not be reproduced in any form.
 */
class ThreadPool
{
    private BlockingQueue<url> taskQueue;

    /*
     * Once pool shutDown will be initiated, poolShutDownInitiated will become true.
     */
    private boolean poolShutDownInitiated = false;

    /* Constructor of ThreadPool
     * nThreads= is a number of threads that exist in ThreadPool.
     * nThreads number of threads are created and started.  *
     */
    public ThreadPool(int nThreads){
        taskQueue = new LinkedBlockingQueue<>();

        //Create and start nThreads number of threads.
        for(int i=1; i<=nThreads; i++)
        {
            ThreadPoolsThread threadPoolsThread=new ThreadPoolsThread(taskQueue,this);
            threadPoolsThread.setName("Thread-"+i);
            System.out.println(ThreadPoolsThread.currentThread().getName() + " created in ThreadPool.");
            threadPoolsThread.start();   //start thread
        }

    }


    /**
     * Execute the task, task must be of Runnable type.
     */
    public synchronized void  execute(url task) throws Exception
    {
        if(this.poolShutDownInitiated)
            throw new Exception("ThreadPool has been shutDown, no further tasks can be added");

        /*
      * Add task in sharedQueue,
      * and notify all waiting threads that task is available.
            */
        if(!ThreadPoolTest.crawledUrls.contains(task))
            if(!this.taskQueue.contains(task))
            {
                //System.out.println("task has been added.");
                this.taskQueue.put(task);
            }
    }


    public boolean isPoolShutDownInitiated()
    {
        return poolShutDownInitiated;
    }


    /**
     * Initiates shutdown of ThreadPool, previously submitted tasks
     * are executed, but no new tasks will be accepted.
     */
    public synchronized void shutdown()
    {
        this.poolShutDownInitiated = true;
        System.out.println("ThreadPool SHUTDOWN initiated.");
    }

}


/**
 * These threads are created and started from constructor of ThreadPool class.
 */
class ThreadPoolsThread extends Thread
{

    private BlockingQueue<url> taskQueue;
    private ThreadPool threadPool;

    public ThreadPoolsThread(BlockingQueue<url> queue, ThreadPool threadPool)
    {
        taskQueue = queue;
        this.threadPool=threadPool;

    }

    public void run()
    {
        try
        {
                  /*
                   * ThreadPool's threads will keep on running
                   * until ThreadPool is not shutDown (shutDown will interrupt thread) and
                   * taskQueue contains some unExecuted tasks.
                   */
            while (true)
            {
                System.out.println(Thread.currentThread().getName() + " is READY to execute task.");
                        /*ThreadPool's thread will take() task from sharedQueue
                         * only if tasks are available else
                         * waits for tasks to become available.
                         */
                url task = taskQueue.take();
                System.out.println(Thread.currentThread().getName() + " has taken task.");

                try
                {
                    ThreadPoolTest.processPage(task,threadPool);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " has EXECUTED task.");

                        /*
                         * 1) Check whether pool shutDown has been initiated or not,
                         * if pool shutDown has been initiated and
                         * 2) taskQueue does not contain any
                         *    unExecuted task (i.e. taskQueue's size is 0 )
                         * than  interrupt() the thread.
                         */
                if(this.threadPool.isPoolShutDownInitiated() &&  this.taskQueue.size()==0)
                {
                    this.interrupt();
                             /*
                                *  Interrupting basically sends a message to the thread
                                *  indicating it has been interrupted but it doesn't cause
                                *  a thread to stop immediately,
                                *
                                *  if sleep is called, thread immediately throws InterruptedException
                                */
                    Thread.sleep(1);
                }

            }
        }
        catch (InterruptedException e)
        {
            System.out.println(Thread.currentThread().getName()+ " has been STOPPED.");
        }
    }
}

/**
class Task implements Runnable
{
    @Override
    public void run()
    {
        try
        {
            System.out.println(Thread.currentThread().getName() + " is executing task.");
            Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
};
*/

/**
 * Test ThreadPool.
 */
public class ThreadPoolTest
{
    public static Set<url> crawledUrls = new HashSet<>();

    //private static Queue<url> urlsToCrawl = new ArrayDeque<>();

    private static crawlConfig config = new crawlConfig();

    public static url baseCrawlUrl = new url("http://insite.iitmandi.ac.in/insite_wp/",config); //"http://www.mit.edu"; //http://www.insite.iitmandi.ac.in";

    public static String storagePath = "C:\\Users\\Ayush\\Dropbox\\Hitesh_Ayush\\sem6\\Parallel_programming\\mini_proj";

    public static String baseCrawlDomain = main.getDomain(baseCrawlUrl);

    public static void processPage(url Url, ThreadPool threadPool) throws IOException
    {
        String domain;
        url url = Url;
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
                System.out.println("url not the same domain as in baseUrl: "+ url);
                return ;
            }

            crawledUrls.add(url);

            int sizeOfUrl;
            try
            {
                sizeOfUrl = url.content.length;
            }
            catch (Exception e)
            {
                sizeOfUrl=0;
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
                    threadPool.execute(new url(link.attr("abs:href"),config));
                }
                for(Element link: images)
                {
                    //if(link.attr("href").contains("mit.edu"))
                    threadPool.execute(new url(link.attr("abs:src"),config));
                }
            }
            catch (Exception e)
            {
                System.out.println("Error in this url: " + url.getSourceUrl().toString() + " " +e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception
    {
        ThreadPool threadPool=new ThreadPool(2); //create 2 threads in ThreadPool
        config.setCrawlStorageDir(new File(storagePath));
        threadPool.execute(baseCrawlUrl);
        System.setProperty("http.proxyHost","10.8.0.1");
        System.setProperty("http.proxyPort","8080");

        //Runnable task=new Task();
        //threadPool.execute(task);
        //threadPool.execute(task);

        //threadPool.shutdown();
    }

}