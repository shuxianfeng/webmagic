package us.codecraft.webmagic.processor.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 网站图片爬取工具
 * @Author shuxf
 * @Date 2017/9/18 15:55
 */
public class BaiImgDownloadProcessor implements PageProcessor{
    private static Logger logger = LoggerFactory.getLogger(BaiImgDownloadProcessor.class);

    private Site site = Site.me()//.setHttpProxy(new HttpHost("127.0.0.1",8888))
            .setRetryTimes(3).setSleepTime(1000).setUseGzip(true);

    @Override
    public void process(Page page) {
        page.putField("name", "测试图片爬取");
        page.putField("description", page.getHtml().regex("((((https|http)://)|/)[0-9a-zA-Z/.@-_%]*?.(jpg|png))").all());
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) throws IOException {
        //single download
        Spider spider = Spider.create(new BaiImgDownloadProcessor()).thread(2);
        String urlTemplate = "http://jiaoyou.58.com/photowall/?PGTID=0d300003-0000-072c-814c-5eaa45c6b8c9&ClickID=1";
        ResultItems resultItems = spider.<ResultItems>get(String.format(urlTemplate, ""));
        System.out.println(resultItems.get("description"));

        //根据这个图片url集合，遍历下载保存图片
        String path = "G:\\robotPhoto";//在本地电脑桌面上创建一个文件夹C:\Users\hh\Desktop\obotPhoto
        //创建存储的文件夹
        File file = new File(path);
        file.mkdir();

        ArrayList urlsArray = resultItems.get("description");
        for (int i=0; i<urlsArray.size(); i++){
            String urlstr = "";
            if (urlsArray.get(i).toString().indexOf("http") == -1) {
                urlstr = "http:" + urlsArray.get(i).toString();
            }else {
                urlstr = urlsArray.get(i).toString();
            }
            logger.info("下载第" + (i+1) + "个图片下载地址：" + urlstr);

            URL url = new URL(urlstr);
            URLConnection conn = url.openConnection();
            //设置超市时间10秒
            conn.setConnectTimeout(10*1000);
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            InputStream in = conn.getInputStream();
            //定义保存的文件名
            String filename = UUID.randomUUID().toString().replaceAll("-", "");

            OutputStream os = new FileOutputStream(path + "\\" + filename + urlstr.substring(urlstr.length()-4));
            byte[] buffer = new byte[4 * 1024];//4k的数据缓冲
            int read;//读取到的数据长度
            while ((read = in.read(buffer)) > 0) {
                os.write(buffer, 0, read);
            }
            os.close();
            in.close();
            logger.info("下载成功！");
        }

        //multidownload//--------------------整体模块下载
//        List<String> list = new ArrayList<String>();
//        list.add(String.format(urlTemplate,"行摄"));
//        list.add(String.format(urlTemplate,"女神"));
//        List<ResultItems> resultItemses = spider.<ResultItems>getAll(list);
//        for (ResultItems resultItemse : resultItemses) {
//            System.out.println(resultItemse.get("description"));
//        }
        spider.close();
    }

}
