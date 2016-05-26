
import java.net.URL;

/**
 * Created by Hitesh on 14-May-16.
 */
public class url {
    private URL sourceUrl;

    private String contentType;

    private byte[] content;

    public url(URL sourceUrl, String contentType, byte[] content)
    {
        this.sourceUrl = sourceUrl;
        this.contentType = contentType;
        this.content = content;
    }

    public URL getSourceUrl() {
        return sourceUrl;
    }

    /*private String convertToFileName()
    {
        String url = sourceUrl.toExternalForm();
        int httpIdx = url.indexOf("http://");
        if(httpIdx == 0)
        {
            url = url.substring(7);
        }
        // Check for at least one slash -- otherwise host name (e.g. sourceforge.net)
        if(!url.contains("/"))
        {
            url = url + "/";
        }
        // If trailing slash, add index.html as default
        if(url.endsWith("/"))
        {
            url = url + "index.html";
        }
        url = textReplace("?", URLEncoder.encode("?"), url);
        url = textReplace("&", URLEncoder.encode("&"), url);
        return crawlConfig.getCrawlStorageDir().getPath() + "/" + url;
    }*/
}
