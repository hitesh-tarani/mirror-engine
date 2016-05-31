import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class main
{
    //public static ArrayList<String> crawledUrls = new ArrayList<>();
//    public static int downloadLimit = 0;

//    public static int downloadSpeed=1;

    public static Set<url> crawledUrls = new HashSet<>();

    private static Queue<url> urlsToCrawl = new ArrayDeque<>();

    private static crawlConfig config = new crawlConfig(0);

    public static url baseCrawlUrl = new url("http://insite.iitmandi.ac.in/insite_wp/",config, 0); //"http://www.mit.edu"; //http://www.insite.iitmandi.ac.in";

    public static String storagePath = "C:\\mirror_engine\\";

    public static String baseCrawlDomain = getDomain(baseCrawlUrl);

    public static void main(String[] args) throws IOException
    {
        config.baseCrawlUrl = baseCrawlUrl;
        config.setCrawlStorageDir(new File(storagePath + baseCrawlDomain));
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
//        System.out.println("File of "+ baseCrawlUrl.getSourceUrl() + " is " + baseCrawlUrl.getSourceUrl().getFile() );
        processPage();
    }

    public static String getProtoc (String url)
    {
        return url.substring(0,url.indexOf(":"));
    }

    public static String getDomain (String url)
    {
        String domain;

        int domainStartIdx = url.indexOf("//") + 2;
        int domainEndIdx = url.indexOf('/', domainStartIdx);
        domainEndIdx = (domainEndIdx > domainStartIdx) ? domainEndIdx : url.length();
        domain = url.substring(domainStartIdx, domainEndIdx);
        return domain;
    }

    /*public static String getProtoc (url url)
    {
        String Url = url.toString();
        return Url.substring(0,Url.indexOf(":"));
    }*/

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
                if (!protoc.equals("http") && !protoc.equals("https"))
                {
                    System.out.println("protocol is "+ protoc + "url not in HTTP and HTTPS: "+ url);
                    return ;
                }

                domain = getDomain(url);

                if (!domain.equals(baseCrawlDomain))
                {
                    System.out.println("url not the same domain as in baseUrl: "+ url);
                    return ;
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
                Document doc;
                //get useful information
                try {
                    doc = Jsoup.connect(url.getSourceUrl().toString()).timeout(10000).get();



                    //if(doc.text().contains("research"))
                    {
                        System.out.println(url.getSourceUrl().toString());
                    }


                    //get all links and recursively call the processPage method
                    Elements links = doc.select("a[href]");
                    Elements images = doc.select("img[src]");
                    Elements css = doc.select("link[href]");

                    for(Element link: links)
                    {
                        //if(link.attr("href").contains("mit.edu"))
                        //System.out.println(link.attr("abs:href"));
                        urlsToCrawl.add(new url(link.attr("abs:href"),config));
                        link.attr("href",url.modifyUrl(link.attr("href"),url));
//                        System.out.println("link : " + link.attr("href"));
                    }

                    for(Element link: images)
                    {
                        //if(link.attr("href").contains("mit.edu"))
//                        System.out.println(link.attr("abs:src"));
                        urlsToCrawl.add(new url(link.attr("abs:src"),config));
                        link.attr("src",url.modifyUrl(link.attr("src"),url));
                    }

                    for(Element link: css) {
                        //if(link.attr("href").contains("mit.edu"))
//                        System.out.println(link.attr("abs:src"));
                        urlsToCrawl.add(new url(link.attr("abs:href"), config));
                        link.attr("href", url.modifyUrl(link.attr("href"), url));
                    }

                    url.setContent(doc.html().getBytes());
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
