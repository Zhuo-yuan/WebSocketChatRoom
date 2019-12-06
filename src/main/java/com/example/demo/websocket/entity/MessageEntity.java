package com.example.demo.websocket.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName MessageEntity
 * @Author lizhuoyuan
 * @Date 2019/12/6 9:59
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity {
    /**
     * 1：连接；2：消息
     */
    private String type;
    private String content;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
