package com.bean.baidu.img.crawler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaiduImageCrawlerService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BaiduImageCrawlerService.class);
    
    //百度图片url字符串转换字典
    private static final Map<Character, Character> dic = new HashMap<Character, Character>();
    
    static{
        dic.put('0', '7');
        dic.put('1', 'd');
        dic.put('2', 'g');
        dic.put('3', 'j');
        dic.put('4', 'm');
        dic.put('5', 'o');
        dic.put('6', 'r');
        dic.put('7', 'u');
        dic.put('8', '1');
        dic.put('9', '4');
        dic.put('a', '0');
        dic.put('b', '8');
        dic.put('c', '5');
        dic.put('d', '2');
        dic.put('e', 'v');
        dic.put('f', 's');
        dic.put('g', 'n');
        dic.put('h', 'k');
        dic.put('i', 'h');
        dic.put('j', 'e');
        dic.put('k', 'b');
        dic.put('l', '9');
        dic.put('m', '6');
        dic.put('n', '3');
        dic.put('o', 'w');
        dic.put('p', 't');
        dic.put('q', 'q');
        dic.put('r', 'p');
        dic.put('s', 'l');
        dic.put('t', 'i');
        dic.put('u', 'f');
        dic.put('v', 'c');
        dic.put('w', 'a');
    }
    
    public Set<String> crawl(String keyword, int start) {
        String url = null;
        try {
            url = String.format("http://image.baidu.com/search/acjson?tn=resultjson_com&ipn=rj&ct=201326592&is=&fp=result&queryWord=%s"
                    + "&cl=2&lm=-1&ie=utf-8&oe=utf-8&adpicid=&st=-1&z=1&ic=0&word=%s"
                    + "&s=&se=&tab=&width=0&height=0&face=0&istype=2&qc=&nc=1&fr=ala&pn=%d&rn=60&gsm=3c&1451963431405=",
                    URLEncoder.encode(keyword + " ", "UTF-8"), URLEncoder.encode(keyword + " ", "UTF-8"), start);
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Set<String> urls = new HashSet<String>();
        Document doc = null;
        try {
            doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) "
                    + "Chrome/42.0.2311.135 Safari/537.36").timeout(10000).get();
        } catch (Exception e) {
            LOGGER.info("fetch homepage urls occur exception, home:{}, reason:{}", url, e.getMessage());
            return urls;
        }
        String text = StringEscapeUtils.unescapeHtml(doc.html());
        Pattern pattern = Pattern.compile("\"objURL\":\"([\\s\\S]*?)\",");
        Matcher matcher = pattern.matcher(text);
        while(matcher.find()){  
            String s = parseUrl(matcher.group(1));
            urls.add(s);
        }
        return urls;
    }
    
    /**
     * 转换baidu图片url
     * @param url
     * @return
     */
    public String parseUrl(String url) {

        StringBuffer buffer = new StringBuffer();
        url = url.replaceAll("_z2C\\$q", ":");
        url = url.replaceAll("AzdH3F", "/");
        url = url.replaceAll("_z&e3B", ".");
        for(int i=0; i<url.length();i++) {
            char ch = url.charAt(i);
            if(dic.containsKey(ch)) {
                buffer.append(dic.get(ch));
            } else {
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }
//    public static void main(String[] args) {
//        crawl("http://image.baidu.com/search/index?tn=baiduimage&ps=1&ct=201326592&lm=-1&cl=2&nc=1&ie=utf-8&word=%E6%9A%B4%E6%BC%AB");
//    }
}
