import java.io.File;

/**
 * Created by Hitesh on 24-May-16.
 */
public class crawlConfig {
    private int maxnumPages;

    private File crawlStorageDir;

    public File getCrawlStorageDir() {
        return crawlStorageDir;
    }

    public void setCrawlStorageDir(File crawlStorageDir) {
        this.crawlStorageDir = crawlStorageDir;
    }
}
