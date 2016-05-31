import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Hitesh on 24-May-16.
 */
public class crawlConfig {
    private int maxnumPages;

    private File crawlStorageDir;

    private BlockingQueue<url> urlsToCrawl = new LinkedBlockingQueue<>();

    private Set<url> crawledUrls = new HashSet<>(50);

    public File getCrawlStorageDir() {
        return crawlStorageDir;
    }

    public void setCrawlStorageDir(File crawlStorageDir) {
        this.crawlStorageDir = crawlStorageDir;
    }
}
