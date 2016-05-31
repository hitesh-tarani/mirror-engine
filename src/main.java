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
    public static Set<url> crawledUrls = new HashSet<>();

    private static Queue<url> urlsToCrawl = new ArrayDeque<>();

    private static crawlConfig config = new crawlConfig();

    public static url baseCrawlUrl = new url("http://insite.iitmandi.ac.in/insite_wp/",config); //"http://www.mit.edu"; //http://www.insite.iitmandi.ac.in";

    public static String storagePath = "C:\\Users\\Ayush\\Dropbox\\Hitesh_Ayush\\sem6\\Parallel_programming\\mini_proj";

    public static String baseCrawlDomain = getDomain(baseCrawlUrl);

    public static void main(String[] args) throws IOException
    {
        config.setCrawlStorageDir(new File(storagePath));
        urlsToCrawl.add(baseCrawlUrl);
        System.setProperty("http.proxyHost","10.8.0.1");
        System.setProperty("http.proxyPort","8080");
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
                Document doc;
                //get useful information
                try {
                    doc = Jsoup.connect(url.getSourceUrl().toString()).get();

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
                        urlsToCrawl.add(new url(link.attr("abs:href"),config));
                    }
                    for(Element link: images)
                    {
                        //if(link.attr("href").contains("mit.edu"))
                        urlsToCrawl.add(new url(link.attr("abs:src"),config));
                    }
                }
                catch (Exception e)
                {
                    System.out.println("Error in this url: " + url.getSourceUrl().toString() + " " +e.getMessage());
                }
            }
        }
    }
}
