package com.experiment.yafeng.Modal;

public class Poetry {
    private String name;
    private String dynasty;
    private String author;
    private String content;
    private String tag;

    public Poetry() {
    }

    public Poetry(String name, String dynasty, String author, String content, String tag) {
        this.name = name;
        this.dynasty = dynasty;
        this.author = author;
        this.content = content;
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDynasty() {
        return dynasty;
    }

    public void setDynasty(String dynasty) {
        this.dynasty = dynasty;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
