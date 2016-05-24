import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hitesh on 14-May-16.
 */
public class crawler implements Runnable{
    int numCrawlers = 5; //number of crawlers

    int nextFetchDelay; // wait for these ms to fetch next ur
    @Override
    public void run() {
        while (true)
        {
            List<url> urls = new ArrayList<>(50);
            if (urls.isEmpty())
            {
                System.out.print("Assigned empty urls");
                //return ;
            }


        }
    }

}
