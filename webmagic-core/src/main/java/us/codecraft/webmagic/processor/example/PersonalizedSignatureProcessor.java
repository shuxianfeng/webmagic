package us.codecraft.webmagic.processor.example;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

/**
 * 获取页面文本
 *
 * @Author zhurui
 * @Date 2017/9/26 10:28
 */
public class PersonalizedSignatureProcessor implements PageProcessor{

    private Site site = Site.me()//.setHttpProxy(new HttpHost("127.0.0.1",8888))
            .setRetryTimes(3).setSleepTime(1000).setUseGzip(true);

    @Override
    public void process(Page page) {
        page.putField("description", page.getHtml().css("div.mbox div.l-panel div.listbg div.ddtxt p","text").all());
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider spider = Spider.create(new BaiduBaikePageProcessor()).thread(2);
        for (int k = 1; k < 227; k++) {
            ResultItems resultItems = Spider.create(new PersonalizedSignatureProcessor()).addUrl("http://www.qqyou.com/qianming/new/list"+k+".html")
                    .get("http://www.qqyou.com/qianming/new/list"+k+".html");
            Map l = resultItems.getAll();
            String str = l.get("description").toString();
            int end = str.indexOf("[");
            int beg = str.lastIndexOf("]");
            str = str.substring(end + 1, beg);
            System.out.println(str.lastIndexOf(0));
            //切分字符串
            String[] strs = str.split(",");
            for (int i = 0; i < strs.length; i++) {
                System.out.println(strs[i]);
                appendByRandomAccessFile1("C:\\Users\\Administrator\\Desktop\\12.txt",strs[i]);
            }
        }
        spider.close();
    }

    public static void appendByRandomAccessFile1(String fileName, String content) {
        RandomAccessFile randomFile = null;
        content += "\n";
        try {
            // 以读写方式打开
            randomFile = new RandomAccessFile(fileName, "rw");
            // 获取文件长度
            long len = randomFile.length();
            // 移动文件指针到文件末
            randomFile.seek(len);
            randomFile.write(content.getBytes("gbk"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != randomFile) {
                try {
                    randomFile.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        System.out.println("randomAccessFile追加操作完成");
    }
}
