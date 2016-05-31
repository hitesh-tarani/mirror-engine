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

    private int fileDepth;

    public url(URL sourceUrl, byte[] content, crawlConfig config)
    {
        this.sourceUrl = sourceUrl;
        this.content = content;
        this.config = config;

        String url = sourceUrl.toExternalForm();
        int fileStartIdx = 0;
        if(url.startsWith(config.baseCrawlUrl.getSourceUrl().toString()))
            fileStartIdx = config.baseCrawlUrl.getSourceUrl().toString().length();
        String restUrl = url.substring(fileStartIdx);

        this.fileDepth = count(restUrl,"/");
    }

    private int count(String s,String match)
    {
        return s.length() - s.replace(match,"").length();
    }

    public url(String sourceUrl, crawlConfig config)
    {
        try {
            this.sourceUrl = new URL(sourceUrl);
            this.config = config;
//
//        int domainStartIdx = sourceUrl.indexOf("//") + 2;
//        int domainEndIdx = sourceUrl.indexOf('/', domainStartIdx);
//        domainEndIdx = (domainEndIdx > domainStartIdx) ? domainEndIdx : sourceUrl.length();
//        String restUrl = sourceUrl.substring(domainEndIdx);
            int fileStartIdx = 0;
            if(sourceUrl.startsWith(config.baseCrawlUrl.getSourceUrl().toString()))
                fileStartIdx = config.baseCrawlUrl.getSourceUrl().toString().length();
            String restUrl = sourceUrl.substring(fileStartIdx);

            this.fileDepth = count(restUrl,"/");
        } catch (MalformedURLException e) {
            //e.printStackTrace();
            System.out.println("Error in link href: " + sourceUrl);
        }
    }

    public url(String sourceUrl, crawlConfig config, int fileDepth)
    {
        try {
            this.sourceUrl = new URL(sourceUrl);
            this.config = config;
            this.fileDepth = fileDepth;
        } catch (MalformedURLException e) {
            //e.printStackTrace();
            System.out.println("Error in link href: " + sourceUrl);
        }
    }

    static String getDomain (url Url)
    {
        String domain;

        String url = Url.getSourceUrl().toString();

        int domainStartIdx = url.indexOf("//") + 2;
        int domainEndIdx = url.indexOf('/', domainStartIdx);
        domainEndIdx = (domainEndIdx > domainStartIdx) ? domainEndIdx : url.length();
        domain = url.substring(domainStartIdx, domainEndIdx);
        return domain;
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

    String modifyUrl(String url, url parenturl)
    {
        url = url.replace("www.","");
        if (url.startsWith(config.baseCrawlUrl.getSourceUrl().toString()))
        {
            String addStr = new String(new char[parenturl.fileDepth]).replace("\0", "../");

            addStr = addStr.concat(url.substring(config.baseCrawlUrl.getSourceUrl().toString().length()));

//            System.out.println("here: " + addStr);
            return addStr;
        }
        return url;
    }
}
