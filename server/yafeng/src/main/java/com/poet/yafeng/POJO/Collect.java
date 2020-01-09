package com.poet.yafeng.POJO;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

@Entity
@Table(name="collect")
public class Collect implements Serializable {
    private static final long serialVersionUID = -1756842404052330534L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "user_id")
    private BigInteger userId;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }

    public String getPoetryName() {
        return poetryName;
    }

    public void setPoetryName(String poetryName) {
        this.poetryName = poetryName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Column(name="poetry_name")
    private String poetryName;
    private String author;
}
