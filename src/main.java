import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class main
{
    //public static ArrayList<String> crawledUrls = new ArrayList<>();
    public static Set<String> crawledUrls = new HashSet<>();

    public static String baseCrawlUrl = "http://insite.iitmandi.ac.in/insite_wp"; //"http://www.mit.edu"; //http://www.insite.iitmandi.ac.in";

    public static String storagePath = "";

    public static String baseCrawlDomain = getDomain(baseCrawlUrl);

    public static void main(String[] args) throws IOException
    {
        System.setProperty("http.proxyHost","10.8.0.1");
        System.setProperty("http.proxyPort","8080");
        processPage(baseCrawlUrl);
    }

    public static String getProtoc (String url)
    {
        return url.substring(0,url.indexOf(":"));
    }

    public static String getDomain (String url)
    {
        String domain,subDomain;

        int domainStartIdx = url.indexOf("//") + 2;
        int domainEndIdx = url.indexOf('/', domainStartIdx);
        domainEndIdx = (domainEndIdx > domainStartIdx) ? domainEndIdx : url.length();
        domain = url.substring(domainStartIdx, domainEndIdx);
        return domain;
    }

    public static void processPage(String url) throws IOException
    {
        String domain,subDomain;
        if(!crawledUrls.contains(url))
        {
            String protoc = getProtoc(url);
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
            subDomain = "";

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
                doc = Jsoup.connect(url).get();

                //if(doc.text().contains("research"))
                {
                    System.out.println(url);
                }

                    //get all links and recursively call the processPage method
                    Elements questions = doc.select("a[href]");
                    for(Element link: questions)
                    {
                        //if(link.attr("href").contains("mit.edu"))
                        processPage(link.attr("abs:href"));
                    }
            }
            catch (Exception e)
            {
                System.out.println("Error in this url: " + url);
            }
        }
    }
}
