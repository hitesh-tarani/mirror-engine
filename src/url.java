import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

class url {
    private URL sourceUrl;

    //private String contentType;

    public byte[] content;

    private crawlConfig config;

    public url(URL sourceUrl, byte[] content, crawlConfig config)
    {
        this.sourceUrl = sourceUrl;
        this.content = content;
        this.config = config;
    }

    public url(String sourceUrl, crawlConfig config)
    {
        try {
            this.sourceUrl = new URL(sourceUrl);
            this.config = config;
        } catch (MalformedURLException e) {
            //e.printStackTrace();
            System.out.println("Error in link href: " + sourceUrl);
        }
    }

    public URL getSourceUrl() {
        return sourceUrl;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public boolean existsOnDisk()
    {
        File f = new File(convertToFileName());
        return (f.exists() && !f.isDirectory());
    }

    public void writeToFile()
    {
        String filename = config.getCrawlStorageDir().getPath()+ sourceUrl.getFile();
        if(!filename.contains("/"))
            filename += "/index.html";
        else if(filename.endsWith("/"))
            filename += "index.html";
        writeToFile(filename);
    }

    public void writeToFile(String fileName)
    {
        //_logClass.debug("writeToFile(" + fileName + ")");
        try
        {
            File f = new File(fileName);
            f.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(fileName);
            out.write(content);
            out.flush();
            out.close();
        }
        catch(IOException ioe)
        {
            System.out.println("IO Exception writing to "+fileName);
            //_logClass.warn("IO Exception writing to " + fileName, ioe);
        }
    }

    private String convertToFileName()
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
        try {
            url = textReplace("?", URLEncoder.encode("?", StandardCharsets.UTF_8.toString()), url);
            url = textReplace("&", URLEncoder.encode("&", StandardCharsets.UTF_8.toString()), url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return config.getCrawlStorageDir().getPath() + "/" + url;
    }
    private String textReplace(String find, String replace, String input)
    {
        int startPos = 0;
        while(true)
        {
            int textPos = input.indexOf(find, startPos);
            if(textPos < 0)
            {
                break;
            }
            input = input.substring(0, textPos) + replace + input.substring(textPos + find.length());
            startPos = textPos + replace.length();
        }
        return input;
    }
}
