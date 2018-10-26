package com.example.python.service;

import com.example.python.dao.CountryDao;
import com.example.python.pojo.Code;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class CountryService {

    @Autowired
    CountryDao countryDao;

    public void queryData(){
        String baseUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2017/index.html";
        List<Code> codes = new ArrayList<Code>();
        Code code = new Code();
        code.setName("全国");
        code.setCode("全国");
        code.setUrl(baseUrl);
        code.setVersion("2017");
        getCodeByUrl(codes,code);
        System.out.println("执行完成");
    }

    public void getCodeByUrl(List<Code> codes,Code parent){
        String parentUrl = parent.getUrl();
        if(parentUrl==null || "".equals(parentUrl)){
            return ;
        }
        try {
            String beforeUrl = parentUrl.substring(0,parentUrl.lastIndexOf("/")+1);
            String selfUrl = "";
            Document doc = getDoc(parentUrl,1);
            if(doc == null)return;
            int level = 1;
            Code code = null;
            Elements tempTrs = null;
            Element tr = null;
            Elements tds = null;
            Element td = null;
            Elements links = null;
            Element link = null;
            tempTrs = doc.select("tr[class=provincetr]");//省
            if(tempTrs.isEmpty()){
                tempTrs = doc.select("tr[class=citytr]");//市
                level = 2;
            }
            if(tempTrs.isEmpty()){
                tempTrs = doc.select("tr[class=countytr]");//区
                level = 3;
            }
            if(tempTrs.isEmpty()){
                tempTrs = doc.select("tr[class=towntr]");//镇
                level = 4;
            }
            if(tempTrs.isEmpty()){
                tempTrs = doc.select("tr[class=villagetr]");//村
                level = 5;
            }
            if(tempTrs.isEmpty()){
                return ;
            }
            if(level==1){//省
                for(Iterator iterator = tempTrs.iterator(); iterator.hasNext();) {
                    tr = (Element) iterator.next();
                    tds = tr.select("td");
                    for(Iterator tdit= tds.iterator();tdit.hasNext();){
                        td = (Element)tdit.next();
                        links = td.select("a[href]");
                        if(links.isEmpty())continue;
                        link = links.iterator().next();
                        code = new Code();
                        code.setParentName(parent.getName());
                        code.setParentCode(parent.getCode());
                        code.setVersion(parent.getVersion());
                        code.setName(link.text());
                        code.setCode(link.text());
                        code.setLevel(level);
                        List<Code> childCodes = new ArrayList<Code>();
                        code.setParentUrl(parentUrl);
                        selfUrl = beforeUrl+link.attr("href");
                        code.setUrl(selfUrl);
                        getCodeByUrl(childCodes,code);
                        code.setCodes(childCodes);
                        System.out.println(code.getName());
                        codes.add(code);
                    }
                }
            }else if(level==5){//居委会
                for(Iterator iterator  = tempTrs.iterator();iterator.hasNext();){
                    tr = (Element)iterator.next();
                    tds = tr.select("td");
                    int i=0;
                    for(Iterator tdit= tds.iterator();tdit.hasNext();){
                        i++;
                        td = (Element)tdit.next();
                        if(i==1){
                            code = new Code();
                            code.setVersion(parent.getVersion());
                            code.setParentCode(parent.getCode());
                            code.setParentName(parent.getName());
                            code.setCode(td.text());
                            code.setLevel(level);
                            code.setUrl(parentUrl);
                            code.setParentUrl(parentUrl);
                        }
                        if(i==2){
                            code.setType(td.text());
                        }
                        if(i==3){
                            code.setName(td.text());
                            codes.add(code);
                            System.out.println(code.getName());
                        }
                    }
                }
            }else{//市、区、城镇
                for(Iterator iterator  = tempTrs.iterator();iterator.hasNext();) {
                    tr = (Element) iterator.next();
                    tds = tr.select("td");
                    int i=0;
                    for(Iterator tdit= tds.iterator();tdit.hasNext();){
                        i++;
                        td = (Element)tdit.next();
                        links = td.select("a[href]");
                        if(i==1){
                            code = new Code();
                            code.setVersion(parent.getVersion());
                            code.setParentCode(parent.getCode());
                            code.setParentName(parent.getName());
                            if(links.isEmpty()){
                                code.setCode(td.text());
                            }else{
                                link = links.iterator().next();
                                code.setCode(link.text());
                            }
                            code.setLevel(level);
                        }
                        if(i==2){
                            if(links.isEmpty()){
                                code.setName(td.text());
                            }else{
                                link = links.iterator().next();
                                code.setName(link.text());
                                List<Code> childCodes = new ArrayList<Code>();
                                code.setParentUrl(parentUrl);
                                selfUrl = beforeUrl+link.attr("href");
                                code.setUrl(selfUrl);
                                getCodeByUrl(childCodes,code);
                                code.setCodes(childCodes);
                                System.out.println(code.getName());
                            }
                            codes.add(code);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        countryDao.saveAll(codes);
    }

    public Document getDoc(String location,int flag){
        Document doc = null;
        try {
            URL url = new URL(location);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            //默认就是Get，可以采用post，大小写都行，因为源码里都toUpperCase了。
            connection.setRequestMethod("GET");
            //是否允许缓存，默认true。
            connection.setUseCaches(Boolean.FALSE);
            //是否开启输出输入，如果是post使用true。默认是false
            //connection.setDoOutput(Boolean.TRUE);
            //connection.setDoInput(Boolean.TRUE);
            //设置请求头信息
            connection.addRequestProperty("Connection", "close");
            //设置连接主机超时（单位：毫秒）
            connection.setConnectTimeout(8000);
            //设置从主机读取数据超时（单位：毫秒）
            connection.setReadTimeout(8000);
            //设置Cookie
            connection.addRequestProperty("Cookie","myCookie" );
            //开始请求
            doc = Jsoup.parse(connection.getInputStream(), "GBK", location);
        } catch (IOException e) {
            e.printStackTrace();
            if(flag==4){
                return null;
            }
            flag++;
            doc = getDoc(location,flag);

        }
        return doc;
    }
}
