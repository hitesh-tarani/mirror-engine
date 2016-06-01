import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Created by Hitesh on 14-May-16.
 */
class url {
    private URL sourceUrl;

    private String contentType;

    public byte[] content;

    private crawlConfig config;

    private int fileDepth;

    private String filePath;

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

    public url(String sourceUrl, crawlConfig config, String linkTag)
    {
        try {
            this.sourceUrl = new URL(sourceUrl);
//            this.contentType = URLConnection.guessContentTypeFromName(this.getSourceUrl().toExternalForm());
            this.config = config;
//
//        int domainStartIdx = sourceUrl.indexOf("//") + 2;
//        int domainEndIdx = sourceUrl.indexOf('/', domainStartIdx);
//        domainEndIdx = (domainEndIdx > domainStartIdx) ? domainEndIdx : sourceUrl.length();
//        String restUrl = sourceUrl.substring(domainEndIdx);
            int fileStartIdx = 0;
//            if(sourceUrl.startsWith(config.baseCrawlUrl.getSourceUrl().toString()))

            String urlStart = this.sourceUrl.getProtocol() + "://" + config.baseCrawlDomain + '/';
            String restUrl;
            if (sourceUrl.startsWith(urlStart))
            {
//                fileStartIdx = urlStart.length();
//                restUrl = sourceUrl.substring(fileStartIdx);
//                System.out.println(this.contentType);
                if (!linkTag.startsWith("a"))
                {
                    String path = this.sourceUrl.getPath();
                    int dotIdx = path.lastIndexOf(".");
                    if(dotIdx > 0)
                    {
                        String pathWithoutExt = path.substring(0,dotIdx);
                        String ext = path.substring(dotIdx);
                        if (this.sourceUrl.getQuery() != null)
                        {
                            String query = "?" + this.sourceUrl.getQuery();
                            query = textReplace("?","qu",query);
                            query = textReplace("=","eq",query);
                            this.filePath = pathWithoutExt + query + ext;
                        }
                        else
                            this.filePath = pathWithoutExt + ext;
//                        System.out.println(pathWithoutExt+ " : "+ ext);
                    }
                    else
                    {
                        if (path.endsWith("/"))
                            this.filePath = this.sourceUrl.getPath() + "index.html";
                        else
                            this.filePath = this.sourceUrl.getPath() + "/index.html";
                    }
                    this.fileDepth = count(this.filePath,"/");
//                    System.out.println(sourceUrl+" : "+ this.filePath);
                }
                else
                {
                    if (this.sourceUrl.getQuery() == null)
                    {
                        if (this.sourceUrl.getPath().endsWith("/"))
                            this.filePath = this.sourceUrl.getPath() + "index.html";
                        else
                            this.filePath = this.sourceUrl.getPath() + "/index.html";
                    }
                    else
                    {
                        int dotIdx = this.sourceUrl.getPath().lastIndexOf(".");
                        if(dotIdx > 0)
                        {
                            String path = this.sourceUrl.getPath();
                            String pathWithoutExt = path.substring(0,dotIdx);
                            String ext = path.substring(dotIdx);
                            String query = "?" + this.sourceUrl.getQuery();
                            query = textReplace("?","qu",query);
                            query = textReplace("=","eq",query);
                            this.filePath = pathWithoutExt + query + ext;
                        }
                        else
                        {
                            String path = this.sourceUrl.getPath();
                            if (path.endsWith("/"))
                                path = path.substring(0,path.length()-1);
                            String query = "?" + this.sourceUrl.getQuery();
                            query = textReplace("?","qu",query);
                            query = textReplace("=","eq",query);
                            this.filePath = path + query + "/index.html";
                        }
                    }

//                    System.out.println(sourceUrl + " : " + this.filePath);
                    this.fileDepth = count(this.filePath,"/");
                }

//                System.out.println(sourceUrl + " : " + this.filePath);
            }
            else
            {
                restUrl = sourceUrl.substring(fileStartIdx);
                this.filePath = restUrl;
                this.fileDepth = count(restUrl,"/");
            }

        } catch (IOException e) {
            //e.printStackTrace();
//            System.out.println("Error in link href: " + sourceUrl);
        }
    }

    public url(String sourceUrl, crawlConfig config, int fileDepth)
    {
        try {
            this.sourceUrl = new URL(sourceUrl);
            this.config = config;
            this.fileDepth = fileDepth;

            String path = this.sourceUrl.getPath();
            int dotIdx = path.lastIndexOf(".");
            if(dotIdx > 0)
            {
                String pathWithoutExt = path.substring(0,dotIdx);
                String ext = path.substring(dotIdx);
                if (this.sourceUrl.getQuery() != null)
                {
                    String query = "?" + this.sourceUrl.getQuery();
                    query = textReplace("?","qu",query);
                    query = textReplace("=","eq",query);
                    this.filePath = pathWithoutExt + query + ext;
                }
                else
                    this.filePath = pathWithoutExt + ext;
//                        System.out.println(pathWithoutExt+ " : "+ ext);
            }
            else
            {
                if (path.endsWith("/"))
                    this.filePath = this.sourceUrl.getPath() + "index.html";
                else
                    this.filePath = this.sourceUrl.getPath() + "/index.html";
            }
            this.fileDepth = count(this.filePath,"/");


        } catch (IOException e) {
            //e.printStackTrace();
//            System.out.println("Error in link href: " + sourceUrl);
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

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
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
        String filename = config.getCrawlStorageDir().getPath()+ this.filePath;
        System.out.println(filename);
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

    String modifyUrl(String url, url Url, url parenturl)
    {
        url = url.replace("www.","");
//        System.out.println("in modify: " + url);

        URL sourceurl;
        String urlStart;
        if ((sourceurl = Url.sourceUrl) != null)
            urlStart =  sourceurl.getProtocol() + "://" + config.baseCrawlDomain + '/';
        else
            urlStart =  "http://" + config.baseCrawlDomain + '/';

        if (url.startsWith(urlStart))
        {
            String addStr = new String(new char[parenturl.fileDepth - 1]).replace("\0", "../");
//            System.out.println("url: " + url + " modified: " + addStr.concat(Url.filePath.substring(1)));
//            addStr = addStr.concat(url.substring(config.baseCrawlUrl.getSourceUrl().toString().length()));
            addStr = addStr.concat(Url.filePath.substring(1));
            return addStr;
        }
        return url;
    }
}
