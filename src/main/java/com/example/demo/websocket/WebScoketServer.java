package com.example.demo.websocket;


import com.alibaba.fastjson.JSONObject;
import com.example.demo.util.DateUtil;
import com.example.demo.util.NameBuilder;
import com.example.demo.websocket.entity.MessageEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 * @ClassName WebScoketServer
 * @Author lizhuoyuan
 * @Date 2019/11/26 9:53
 **/
@ServerEndpoint("/websocket/{name}")
@Component
public class WebScoketServer {

    //初始在线人数
    private static int online_num = 0;

    /**
     * 与某个客户端的连接对话，需要通过它来给客户端发送消息
     */
    private Session session;

    /**
     * 标识当前连接客户端的用户名
     */
    private String name;

    /**
     * 用于存所有的连接服务的客户端，这个对象存储是安全的
     */
    private static ConcurrentHashMap<String, WebScoketServer> webSocketSet = new ConcurrentHashMap<>();


    @OnOpen
    public void OnOpen(Session session, @PathParam(value = "name") String name) {
        this.session = session;
        if("undenfind".equals(name) || "null".equals(name) || name == null){
            System.out.println(name);
            this.name = NameBuilder.build();
        }else{
            this.name = name;
        }
        // name是用来表示唯一客户端，如果需要指定发送，需要指定发送通过name来区分
        webSocketSet.put(name, this);
        addOnlineCount();
        onlineCountChangeIf();
        System.out.println("[WebSocket] 连接成功，当前连接人数为：" + online_num);
    }

    public static String stringFilter(String str) {
        String regEx = "(?!<(img|p|span).*?>)<.*?>";
        Pattern p_html = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(str);
        str = m_html.replaceAll("");
        // 返回文本字符串
        return str.trim();
    }


    @OnClose
    public void OnClose() {
        webSocketSet.remove(this.name);
        subOnlineCount();
        onlineCountChangeIf();
        System.out.println("[WebSocket] 退出成功，当前连接人数为："+ online_num);
    }

    /**
     * 连接错误
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.err.println("发生错误！");
        error.printStackTrace();
    }

    @OnMessage
    public void OnMessage(String message) throws IOException{
        System.out.println("[WebSocket] 收到消息："+ message);
        //message = stringFilter(message);
        StringBuffer str = new StringBuffer(this.name +"  "+ DateUtil.getDateTime() + message);
        //判断是否需要指定发送，具体规则自定义
        if (message.indexOf("TOUSER") == 0) {
            String name = message.substring(message.indexOf("TOUSER") + 6, message.indexOf(";"));
            AppointSending(name, message.substring(message.indexOf(";") + 1, message.length()));
        } else {
            GroupSending(str.toString());
        }

    }

    /**
     * 群发
     *
     * @param message
     */
    public void GroupSending(String message) {
        for (String name : webSocketSet.keySet()) {
            try {
                MessageEntity messageEntity = new MessageEntity();
                messageEntity.setType("1");
                messageEntity.setContent(message);
                String messageString = JSONObject.toJSONString(messageEntity);
                webSocketSet.get(name).session.getBasicRemote().sendText(messageString);
                //webSocketSet.get(name).session.getBasicRemote().sendText(String.valueOf(getOnlineCount()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 指定发送
     *
     * @param name
     * @param message
     */
    public void AppointSending(String name, String message) {
        try {
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setType("1");
            messageEntity.setContent(message);
            String messageString = JSONObject.toJSONString(messageEntity);
            webSocketSet.get(name).session.getBasicRemote().sendText(messageString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized int getOnlineCount() {
        return online_num;
    }

    public static synchronized void addOnlineCount() {
        WebScoketServer.online_num++;
    }

    public static synchronized void subOnlineCount() {
        WebScoketServer.online_num--;
    }

    /**
     * 在线人数是否改变
     */
    private static void onlineCountChangeIf() {
        int size = webSocketSet.size();
        if ((String.valueOf(online_num)).equals(size)) {
            // 在线人数没有变
            return;
        }
        // 向admin发送消息
        setMessageToAdmin();
    }

    public static void setMessageToAdmin() {
        for (String name : webSocketSet.keySet()) {
            try {
                MessageEntity messageEntity = new MessageEntity();
                messageEntity.setType("2");
                messageEntity.setContent(String.valueOf(getOnlineCount()));
                String messageString = JSONObject.toJSONString(messageEntity);
                webSocketSet.get(name).session.getBasicRemote().sendText(messageString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

