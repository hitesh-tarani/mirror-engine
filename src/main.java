import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;

public class main
{
    public static ArrayList<String> crawledUrls = new ArrayList<>();

    public static String baseCrawlUrl = "http://www.mit.edu"; //http://www.insite.iitmandi.ac.in";

    public static void main(String[] args) throws IOException
    {
        System.setProperty("http.proxyHost","10.8.0.1");
        System.setProperty("http.proxyPort","8080");
        processPage(baseCrawlUrl);
    }

    public static void processPage(String URL) throws IOException
    {
        if(!crawledUrls.contains(URL))
        {
            crawledUrls.add(URL);
            Document doc;
            //get useful information
            try {
                doc = Jsoup.connect(URL).get();

                //if(doc.text().contains("research"))
                {
                    System.out.println(URL);
                }

                //get all links and recursively call the processPage method
                Elements questions = doc.select("a[href]");
                for(Element link: questions)
                {
                    //if(link.attr("href").contains("mit.edu"))
                    processPage(link.attr("abs:href"));
                }
            } catch (java.net.MalformedURLException e) {
                System.out.println("Error in this url: " + URL);
            }


        }
    }
}