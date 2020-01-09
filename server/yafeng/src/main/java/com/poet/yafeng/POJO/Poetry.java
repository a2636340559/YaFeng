package com.poet.yafeng.POJO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.poet.yafeng.Modal.CommonResult;

import javax.persistence.*;
import javax.xml.soap.Detail;
import java.io.Serializable;

@Entity
@Table(name="poetryDetail")

public class Poetry implements Serializable {
    private static final long serialVersionUID = 2887345089715634256L;

    public interface SimpleView extends CommonResult.ResultView {};
    public interface DetailView extends SimpleView{};

    private Integer id;
    private String name;
    private String dynasty;
    private String author;
    @Id
    private String content;
    private String tag;

    public Poetry(String name, String dynasty, String author, String content, String tag) {
        this.name = name;
        this.dynasty = dynasty;
        this.author = author;
        this.content = content;
        this.tag = tag;
    }

    public Poetry(String name, String dynasty, String author, String content) {
        this.name = name;
        this.dynasty = dynasty;
        this.author = author;
        this.content = content;
    }

    private String translation;
    private String annotation;
    private String reference;
    private String appreciation;
    private String img;
    @Column(name ="detail" )
    private String authorDetail;

    @JsonView(DetailView.class)
    public String getImg() {
        return img;
    }
    public void setImg(String img) {
        this.img = img;
    }

    public Poetry() {
    }

    @JsonView(DetailView.class)
    public String getAuthorDetail() {
        return authorDetail;
    }
    public void setAuthorDetail(String authorDetail) {
        this.authorDetail = authorDetail;
    }

    @JsonIgnore
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonView(SimpleView.class)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @JsonView(SimpleView.class)
    public String getDynasty() {
        return dynasty;
    }
    public void setDynasty(String dynasty) {
        this.dynasty = dynasty;
    }

    @JsonView(SimpleView.class)
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    @JsonView(SimpleView.class)
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    @JsonView(SimpleView.class)
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }

    @JsonView(DetailView.class)
    public String getTranslation() {
        return translation;
    }
    public void setTranslation(String translation) {
        this.translation = translation;
    }

    @JsonView(DetailView.class)
    public String getAnnotation() {
        return annotation;
    }
    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    @JsonView(DetailView.class)
    public String getReference() {
        return reference;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }

    @JsonView(DetailView.class)
    public String getAppreciation() {
        return appreciation;
    }
    public void setAppreciation(String appreciation) {
        this.appreciation = appreciation;
    }
}
