import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/*import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;*/


public class main
{
    /*public static DB db = new DB();*/
    public static ArrayList<String> crawledUrls = new ArrayList<>();

    public static void main(String[] args) throws IOException
    {
        /*db.runSql2("TRUNCATE Record;");*/
        System.setProperty("http.proxyHost","10.8.0.1");
        System.setProperty("http.proxyPort","8080");
        processPage("http://www.mit.edu");
    }

    public static void processPage(String URL) throws IOException
    {
        //check if the given URL is already in database
        /*String sql = "select * from Record where URL = '"+URL+"'";
        ResultSet rs = db.runSql(sql);*/

        if(!crawledUrls.contains(URL))
        {
            /*//store the URL to database to avoid parsing again
            sql = "INSERT INTO  `Crawler`.`Record` " + "(`URL`) VALUES " + "(?);";
            PreparedStatement stmt = db.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, URL);
            stmt.execute();*/

            crawledUrls.add(URL);
            //get useful information
            Document doc = Jsoup.connect("http://www.mit.edu/").get();

            if(doc.text().contains("research"))
            {
                System.out.println(URL);
            }

            //get all links and recursively call the processPage method
            Elements questions = doc.select("a[href]");
            for(Element link: questions)
            {
                if(link.attr("href").contains("mit.edu"))
                    processPage(link.attr("abs:href"));
            }
        }
    }
}