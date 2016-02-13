package com.example.gsonorfastjson.entity;

import java.util.List;

/**
 * 慕课网课程列表实体类
 *    1.插件GsonFormat的使用
 *    2.右键点击（生成（alt+Insert））选择GsonFormat
 */
public class imoocClassInfo {

    private int status;
    private String msg;

    private List<DataEntity> data;


    @Override
    public String toString() {
        return "imoocClassInfo{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(List<DataEntity> data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public List<DataEntity> getData() {
        return data;
    }


    public static class DataEntity {
        private int id;
        private String name;
        private String picSmall;
        private String picBig;
        private String description;
        private int learner;

        @Override
        public String toString() {
            return "DataEntity{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", picSmall='" + picSmall + '\'' +
                    ", picBig='" + picBig + '\'' +
                    ", description='" + description + '\'' +
                    ", learner=" + learner +
                    '}';
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPicSmall(String picSmall) {
            this.picSmall = picSmall;
        }

        public void setPicBig(String picBig) {
            this.picBig = picBig;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setLearner(int learner) {
            this.learner = learner;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPicSmall() {
            return picSmall;
        }

        public String getPicBig() {
            return picBig;
        }

        public String getDescription() {
            return description;
        }

        public int getLearner() {
            return learner;
        }
    }
}
