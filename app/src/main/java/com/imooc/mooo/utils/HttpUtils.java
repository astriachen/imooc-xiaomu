package com.imooc.mooo.utils;
/**
 发送一个消息，得到返回的消息
 */

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.imooc.mooo.bean.ChatMessage;
import com.imooc.mooo.bean.Result;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

public class HttpUtils {

    private static final String URL="http://openapi.tuling123.com/openapi/api/v2";
    private static final String API_KEY="b55c9bb5dbcd4fb9b1f13f6b9354a914";

    public static ChatMessage sendMessage(String msg){
        ChatMessage chatMessage=new ChatMessage();
        String jsonRes=doGet(msg);
        Gson gson=new Gson();
        Result result=null;
        try {
            result = gson.fromJson(jsonRes, Result.class);
            chatMessage.setMsg(result.getText());
        }catch(JsonSyntaxException e){
            chatMessage.setMsg("服务器繁忙，请稍后再试");
        }
        chatMessage.setDate(new Date());
        chatMessage.setType(ChatMessage.Type.INCOMING);
        return chatMessage;
    }

    public static String doGet(String msg){
        String result="";
        String url=setParams(msg);
        InputStream is=null;
        ByteArrayOutputStream baos=null;
        try {
            java.net.URL urlNet = new URL(url);
            HttpURLConnection conn=(HttpURLConnection) urlNet.openConnection();
            conn.setReadTimeout(5*1000);
            conn.setConnectTimeout(5*1000);
            conn.setRequestMethod("GET");
            is=conn.getInputStream();
            int len=-1;
            byte[]buf=new byte[128];
            baos=new ByteArrayOutputStream();
            while((len=is.read(buf))!=-1){
                baos.write(buf,0,len);
            }
            baos.flush();
           result=new String(baos.toByteArray());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            try {
                if (is != null) {
                    is.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return result;
    }
    private static String setParams(String msg)  {
        String url="";
        try {
             url = URL + "?key=" + API_KEY + "&info=" + URLEncoder.encode(msg, "UTF-8");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return url;
    }
}