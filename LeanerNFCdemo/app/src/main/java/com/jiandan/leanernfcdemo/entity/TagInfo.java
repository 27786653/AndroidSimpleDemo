package com.jiandan.leanernfcdemo.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 李森林 on 2016/2/29.
 */
public class TagInfo implements Serializable {

    private int id;
    private String tagid;
    private String key;
    private String info;
    private String username;
    private Long  curtime;

    public String  getCurTimeForDate(){
      return  new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS").format(new Date(curtime));
    }

    public Long getCurtime() {
        return curtime;
    }

    public void setCurtime(Long curtime) {
        this.curtime = curtime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTagid() {
        return tagid;
    }

    public void setTagid(String tagid) {
        this.tagid = tagid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }




    @Override
    public String toString() {
        return "TagInfo{" +
                "id=" + id +
                ", tagid='" + tagid + '\'' +
                ", key='" + key + '\'' +
                ", info='" + info + '\'' +
                ", username='" + username + '\'' +
                ", curtime=" +getCurTimeForDate() +
                '}';
    }
}
