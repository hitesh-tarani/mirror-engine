import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hitesh on 14-May-16.
 */
public class crawler extends Thread
{
    String name;
    crawlConfig myParent;
    url URL;
    List<url> urls = new ArrayList<>(10);
    int nextFetchDelay; // wait for these ms to fetch next url

    @Override
    public void run() {
        while (true)
        {
            try
            {
                //myParent.changeNumberOfRunningCrawlers(1);
                URL = myParent.urlsToCrawl.take();
                //myParent.changeNumberOfRunningCrawlers(-1);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            try
            {
                myParent.processPage(URL);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

}
