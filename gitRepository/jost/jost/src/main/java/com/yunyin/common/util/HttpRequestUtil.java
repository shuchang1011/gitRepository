package com.yunyin.common.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * http请求工具类
 *
 * @author ：HeWeiLin
 * @date ：Created in 2019/4/2 14:25
 */
@Component
public class HttpRequestUtil {

    private static String HTTP_HOST;
    private static String HTTP_USERNAME;
    private static String HTTP_PASSWORD;
    private static int HTTP_PORT;
    private static String HTTP_PROTOCOL;
    private static String HTTP_PATH;
    private static String HTTP_TAIL_END;
    private static String JOST_HTTP_TAIL_END;
    private static String HTTP_HEADER_KEY;
    private static String HTTP_DOMAIN;
    private static String HTTP_EXTEND;
    private static String APPLICATION_JSON ;
    private static String CONTENT_TYPE_TEXT_JSON ;

    @Value("${HTTP_HOST}")
    public void setHttpHost(String httpHost) {
        HttpRequestUtil.HTTP_HOST = httpHost;
    }
    @Value("${HTTP_USERNAME}")
    public void setHttpUsername(String httpUsername) {
        HTTP_USERNAME = httpUsername;
    }
    @Value("${HTTP_PASSWORD}")
    public void setHttpPassword(String httpPassword) {
        HTTP_PASSWORD = httpPassword;
    }
    @Value("${HTTP_PORT}")
    public void setHttpPort(int httpPort) {
        HTTP_PORT = httpPort;
    }
    @Value("${HTTP_PROTOCOL}")
    public void setHttpProtocol(String httpProtocol) {
        HTTP_PROTOCOL = httpProtocol;
    }
    @Value("${HTTP_PATH}")
    public void setHttpPath(String httpPath) {
        HTTP_PATH = httpPath;
    }
    @Value("${HTTP_TAIL_END}")
    public void setHttpTailEnd(String httpTailEnd) {
        HTTP_TAIL_END = httpTailEnd;
    }
    @Value("${JOST_HTTP_TAIL_END}")
    public void setJostHttpTailEnd(String jostHttpTailEnd) {
        HTTP_TAIL_END = jostHttpTailEnd;
    }
    @Value("${HTTP_HEADER_KEY}")
    public void setHttpHeaderKey(String httpHeaderKey) {
        HTTP_HEADER_KEY = httpHeaderKey;
    }
    @Value("${HTTP_DOMAIN}")
    public void setHttpDomain(String httpDomain) {
        HTTP_DOMAIN = httpDomain;
    }
    @Value("${HTTP_EXTEND}")
    public void setHttpExtend(String httpExtend) {
        HTTP_EXTEND = httpExtend;
    }
    @Value("${APPLICATION_JSON}")
    public void setApplicationJson(String applicationJson) {
        APPLICATION_JSON = applicationJson;
    }
    @Value("${CONTENT_TYPE_TEXT_JSON}")
    public void setContentTypeTextJson(String contentTypeTextJson) {
        CONTENT_TYPE_TEXT_JSON = contentTypeTextJson;
    }



    /**
     * rest post 方式调用HTTP接口
     * @param HTTP_PORT   端口号
     * @param HTTP_PATH   请求地址
     * @param jsonParams  json格式字符串
     * @param apiUrl      接口名称
     * @param HTTP_USERNAME 用户名
     * @param HTTP_PASSWORD 密码
     * @date 2019/4/12 17:38
     */
    public static JSONObject httpRequestPost(int HTTP_PORT, String HTTP_PATH, String jsonParams, String apiUrl, String HTTP_USERNAME, String HTTP_PASSWORD) {
        HttpHost targetHost = new HttpHost(HTTP_HOST, HTTP_PORT, HTTP_PROTOCOL);
        //认证提供者
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        //ntlm认证
        NTCredentials creds = new NTCredentials(HTTP_USERNAME, HTTP_PASSWORD, "", HTTP_DOMAIN);
        credsProvider.setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort(), null, "ntlm"), creds);

        //设置路径
        String url = HTTP_PROTOCOL + "://" + HTTP_HOST + ":" + HTTP_PORT + HTTP_PATH + "/" + apiUrl + JOST_HTTP_TAIL_END;
        System.out.println("url:" + url);
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", APPLICATION_JSON);
        StringEntity se = new StringEntity(jsonParams, "UTF-8");
        se.setContentType(CONTENT_TYPE_TEXT_JSON);
        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
        httpPost.setEntity(se);
        BufferedReader reader = null;
        StringBuffer sb = new StringBuffer();
        CloseableHttpResponse response = null;
        HttpContext context = new BasicHttpContext();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        try {
            response = httpClient.execute(targetHost, httpPost, context);
            System.out.println("status=" + response.getStatusLine().getStatusCode());
            //调用HttpResponse的getEntity()方法可获取HttpEntity对象，该对象包装了服务器的响应内容。程序可通过该对象获取服务器的响应内容。
            HttpEntity entity = response.getEntity();
            if(204 == response.getStatusLine().getStatusCode()){
                //因为成功以后,CRM并没有返回信息,所以手动拼接一个
                sb.append("{" + "\"" + "Success" + "\"" + ":" + "true" + "}");
            }else {
                if(null != entity){
                    reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
                    if(null != reader){
                        String buffer;
                        while ((buffer = reader.readLine()) != null) {
                            sb.append(buffer);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                response.close();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        JSONObject o = JSONObject.parseObject(sb.toString());
        return o;
    }

    /**
     * @param HTTP_PORT   端口号
     * @param HTTP_PATH   请求地址
     * @param paramsMap   传入map
     * @param apiUrl      接口名称
     * @param HTTP_USERNAME 用户名
     * @param HTTP_PASSWORD 密码
     * @date 2019/4/12 17:41
     */
    public static JSONObject httpRequestGet(int HTTP_PORT, String HTTP_PATH, Map<String, Object> paramsMap, String apiUrl, String HTTP_USERNAME, String HTTP_PASSWORD) {

        HttpHost targetHost = new HttpHost(HTTP_HOST, HTTP_PORT, HTTP_PROTOCOL);
        //认证提供者
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        //ntlm认证
        NTCredentials creds = new NTCredentials(HTTP_USERNAME, HTTP_PASSWORD, "", HTTP_DOMAIN);
        credsProvider.setCredentials(new AuthScope(HTTP_HOST, HTTP_PORT, null, "ntlm"), creds);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();

        //设置路径
        StringBuilder result = new StringBuilder();
        String url = HTTP_PROTOCOL + "://" + HTTP_HOST + ":" + HTTP_PORT + HTTP_PATH + "/" + apiUrl + HTTP_TAIL_END;
        System.out.println("url:" + url);
        result.append(url);

        // 判断参数map是否为非空
        if (paramsMap != null) {
            // 遍历参数
            for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
                // 设置参数
                result.append("&");
                result.append(entry.getKey() + "=" + entry.getValue());
            }
        }
        HttpGet httpGet = new HttpGet(result.toString());
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        CloseableHttpResponse response = null;
        HttpContext context = new BasicHttpContext();
        try {
            response = httpClient.execute(targetHost, httpGet, context);
            System.out.println("status=" + response.getStatusLine().getStatusCode());
            //请求和响应都成功了
            if (response.getStatusLine().getStatusCode() == 200) {
                //调用HttpResponse的getEntity()方法可获取HttpEntity对象，该对象包装了服务器的响应内容。程序可通过该对象获取服务器的响应内容。
                HttpEntity entity = response.getEntity();
                reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
                String buffer = "";
                while ((buffer = reader.readLine()) != null) {
                    sb.append(buffer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                response.close();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        JSONObject o = JSONObject.parseObject(sb.toString());
        return o;
    }


    /**
     * rest patch 方式调用HTTP接口
     *
     * @param HTTP_PORT   端口号
     * @param HTTP_PATH   请求地址
     * @param jsonParams  json格式字符串
     * @param apiUrl      接口名称
     * @param HTTP_USERNAME 用户名
     * @param HTTP_PASSWORD 密码
     * @date 2019/4/12 17:38
     */
    public static JSONObject httpRequestPatch(int HTTP_PORT, String HTTP_PATH, String jsonParams, String apiUrl, String HTTP_USERNAME, String HTTP_PASSWORD) {
        HttpHost targetHost = new HttpHost(HTTP_HOST, HTTP_PORT, HTTP_PROTOCOL);
        //认证提供者
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        //ntlm认证
        NTCredentials creds = new NTCredentials(HTTP_USERNAME, HTTP_PASSWORD, "", HTTP_DOMAIN);
        credsProvider.setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort(), null, "ntlm"), creds);

        //设置路径
        String url = HTTP_PROTOCOL + "://" + HTTP_HOST + ":" + HTTP_PORT + HTTP_PATH + "/" + apiUrl;

        System.out.println(url);
        StringBuffer sb = new StringBuffer();
        HttpPatch httpPatch = new HttpPatch(url);
        httpPatch.addHeader("If-Match", "*");
        httpPatch.setHeader("Content-type", "application/json");

        StringEntity se = new StringEntity(jsonParams, "UTF-8");
        se.setContentType(CONTENT_TYPE_TEXT_JSON);
        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));

        httpPatch.setEntity(se);
        BufferedReader reader = null;
        CloseableHttpResponse response = null;
        HttpContext context = new BasicHttpContext();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        try {
            response = httpClient.execute(targetHost, httpPatch, context);
            System.out.println("status=" + response.getStatusLine().getStatusCode());
            //调用HttpResponse的getEntity()方法可获取HttpEntity对象，该对象包装了服务器的响应内容。程序可通过该对象获取服务器的响应内容。
            HttpEntity entity = response.getEntity();
            if(204 == response.getStatusLine().getStatusCode()){
                //因为成功以后,CRM并没有返回信息,所以手动拼接一个
                sb.append("{" + "\"" + "Success" + "\"" + ":" + "true" + "}");
            }else {
                if(null != entity){
                    reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
                    if(null != reader){
                        String buffer;
                        while ((buffer = reader.readLine()) != null) {
                            sb.append(buffer);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                response.close();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        JSONObject o = JSONObject.parseObject(sb.toString());
        return o;
    }


    /**
     * rest delete 方式调用HTTP接口
     *
     * @param HTTP_PORT   端口号
     * @param HTTP_PATH   请求地址
     * @param apiUrl      接口名称
     * @param HTTP_USERNAME 用户名
     * @param HTTP_PASSWORD 密码
     * @date 2019/9/25 14:38
     */
    public static JSONObject httpRequestDelete(int HTTP_PORT, String HTTP_PATH, String apiUrl, String HTTP_USERNAME, String HTTP_PASSWORD) {
        HttpHost targetHost = new HttpHost(HTTP_HOST, HTTP_PORT, HTTP_PROTOCOL);
        //认证提供者
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        //ntlm认证
        NTCredentials creds = new NTCredentials(HTTP_USERNAME, HTTP_PASSWORD, "", HTTP_DOMAIN);
        credsProvider.setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort(), null, "ntlm"), creds);

        //设置路径
        String url = HTTP_PROTOCOL + "://" + HTTP_HOST + ":" + HTTP_PORT + HTTP_PATH + "/" + apiUrl;

        System.out.println(url);
        StringBuffer sb = new StringBuffer();
        HttpDelete httpDelete = new HttpDelete(url);
        BufferedReader reader = null;
        CloseableHttpResponse response = null;
        HttpContext context = new BasicHttpContext();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        try {
            response = httpClient.execute(targetHost, httpDelete, context);
            System.out.println("status=" + response.getStatusLine().getStatusCode());
            //调用HttpResponse的getEntity()方法可获取HttpEntity对象，该对象包装了服务器的响应内容。程序可通过该对象获取服务器的响应内容。
            HttpEntity entity = response.getEntity();
            if(204 == response.getStatusLine().getStatusCode()){
                //因为成功以后,CRM并没有返回信息,所以手动拼接一个
                sb.append("{" + "\"" + "Success" + "\"" + ":" + "true" + "}");
            }else {
                if(null != entity){
                    reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
                    if(null != reader){
                        String buffer;
                        while ((buffer = reader.readLine()) != null) {
                            sb.append(buffer);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                response.close();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        JSONObject o = JSONObject.parseObject(sb.toString());
        return o;
    }

    /**
     * ntlm认证，patch请求
     *
     * @param params 参数，json格式
     * @param apiUrl 接口名称
     */
    public static String patch(String params, String apiUrl) throws Exception {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope("navapp.weihansoft.com", 7128, null, "ntlm"),
                new NTCredentials(HTTP_USERNAME, HTTP_PASSWORD, "", HTTP_DOMAIN));
        CloseableHttpClient client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
        try {
            // 设置参数实体
            StringEntity se = new StringEntity(params, "UTF-8");
            se.setContentType("text/json");
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

            String url = "/Allegion/OData/Company('Allegion%20Fu%20Hsing%20Limited')/"
                    + apiUrl
                    + "?company=Allegion%20Fu%20Hsing%20Limited&$format=json";
            HttpPatch patch = new HttpPatch(url);
            patch.addHeader("Content-Type", "application/json");
            patch.addHeader("If-Match", "*");
            patch.setEntity(se);

            HttpHost host = new HttpHost("navapp.weihansoft.com", 7128, "http");
            // 保证相同的内容来用于执行逻辑相关的请求
            HttpContext context = new BasicHttpContext();
            CloseableHttpResponse response = client.execute(host, patch, context);
            System.out.println("status=" + response.getStatusLine().getStatusCode());
            try {
                String result = EntityUtils.toString(response.getEntity(), "utf-8");
                return result;
            } finally {
                response.close();
            }
        } finally {
            client.close();
        }
    }

}
