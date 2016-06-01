import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.*;

public class main
{
    //public static ArrayList<String> crawledUrls = new ArrayList<>();
//    public static int downloadLimit = 0;

//    public static int downloadSpeed=1;

    public static Set<url> crawledUrls = new HashSet<>();

    private static Queue<url> urlsToCrawl = new ArrayDeque<>();

    private static crawlConfig config = new crawlConfig(0);

    public static url baseCrawlUrl = new url("http://insite.iitmandi.ac.in/insite_wp/",config, 0); //"http://www.mit.edu"; //http://www.insite.iitmandi.ac.in";

    public static String storagePath = "mirror_engine/";

    public static String baseCrawlDomain = getDomain(baseCrawlUrl);

    public static void main(String[] args) throws IOException
    {
        config.baseCrawlUrl = baseCrawlUrl;
        config.setCrawlStorageDir(new File(storagePath + baseCrawlDomain));
        config.baseCrawlDomain = url.getDomain(baseCrawlUrl);
        urlsToCrawl.add(baseCrawlUrl);
        System.setProperty("http.proxyHost","10.8.0.1");
        System.setProperty("http.proxyPort","8080");
//        Thread speedLimiter = new Thread(() ->
//        {
//            if(downloadLimit >= 0)
//            {
//                downloadLimit = downloadSpeed*10;
//            }
//        });
//        speedLimiter.start();
//        System.out.println("File of "+ baseCrawlUrl.getSourceUrl() + " is " + baseCrawlUrl.getSourceUrl().getQuery() );
        processPage();
    }

    public static String getProtoc (String url)
    {
        return url.substring(0,url.indexOf(":"));
    }

    public static String getDomain (url Url)
    {
        String domain;

        String url = Url.getSourceUrl().toString();

        int domainStartIdx = url.indexOf("//") + 2;
        int domainEndIdx = url.indexOf('/', domainStartIdx);
        domainEndIdx = (domainEndIdx > domainStartIdx) ? domainEndIdx : url.length();
        domain = url.substring(domainStartIdx, domainEndIdx);
        return domain;
    }

    public static void processPage() throws IOException
    {
        String domain;
        while(!urlsToCrawl.isEmpty())
        {
            url url = urlsToCrawl.remove();
            if(!crawledUrls.contains(url))
            {
                String protoc = url.getSourceUrl().getProtocol();
//                if (!protoc.equals("http") && !protoc.equals("https"))
//                {
//                    System.out.println("protocol is "+ protoc + "url not in HTTP and HTTPS: "+ url);
//                    return ;
//                }

                domain = getDomain(url);

                if (!domain.equals(baseCrawlDomain))
                {
                    System.out.println("url not the same domain as in baseUrl: "+ url.getSourceUrl().toExternalForm());
                    continue;
                }
//              subDomain = "";

                /*
                String[] parts = domain.split("\\.");

                for (int i = 0; i < (parts.length - limit); i++) {
                    if (!subDomain.isEmpty()) {
                        subDomain += ".";
                    }
                    subDomain += parts[i];
                }
                */

                crawledUrls.add(url);

//                int sizeOfUrl;
//                try
//                {
//                    sizeOfUrl = url.content.length;
//                }
//                catch (Exception e)
//                {
//                    sizeOfUrl=0;
//                }
//                if(downloadLimit < sizeOfUrl)
//                {
//                    try
//                    {
//                        Thread.currentThread().wait(((sizeOfUrl-downloadLimit)*10)/downloadSpeed);
//                    }
//                    catch (InterruptedException e)
//                    {
//                        e.printStackTrace();
//                    }
//                }
                HttpURLConnection httpConn = (HttpURLConnection) url.getSourceUrl().openConnection();

                int responseCode = httpConn.getResponseCode();


                if (responseCode != HttpURLConnection.HTTP_OK) {
                    continue;
                }
                url.setContentType(httpConn.getContentType());

                if (!url.getContentType().startsWith("text/html"))
                {
//                  byte[] content = new byte[4096];
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    InputStream is = httpConn.getInputStream();
                    byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
                    int n;

                    while ( (n = is.read(byteChunk)) > 0 ) {
                        baos.write(byteChunk, 0, n);
                    }
                    url.content = baos.toByteArray();
                    url.writeToFile();
                    continue;
                }

                Document doc;
                //get useful information
                try {
                    doc = Jsoup.connect(url.getSourceUrl().toString()).timeout(10000).get();



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
//                        System.out.println(links.size());

                        for (Element link: links)
                        {
//                            System.out.println("link : " + links.html());
                            url newUrl = new url(link.attr("abs:" + attr),config,linkPair.tag);
                            urlsToCrawl.add(newUrl);
//                            System.out.print("link : " + link.attr(attr));
                            link.attr(attr,url.modifyUrl(link.attr(attr),newUrl,url));

                        }
//                        System.out.println("reached");
                    }

//                    Elements links = doc.select("a[href]");
//                    Elements images = doc.select("img[src]");
//                    Elements css = doc.select("link[href]");
//                    Elements js = doc.select("script[src]");
//                    Elements meta = doc.select("");

                    url.setContent(doc.html().getBytes());
//                    System.out.println("here!!!!!!!!!!!!");
                    url.writeToFile();
                }
                catch (Exception e)
                {
                    System.out.println("Error in this url: " + url.getSourceUrl().toString());
                    e.printStackTrace();
                }
            }
        }
    }
}
