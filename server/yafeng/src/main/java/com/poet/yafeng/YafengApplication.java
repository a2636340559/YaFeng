package com.poet.yafeng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableCaching
@ServletComponentScan
@SpringBootApplication
public class YafengApplication {
//    @Overrideextends SpringBootServletInitializer
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        return builder.sources(YafengApplication.class);
//    }

    public static void main(String[] args) {
        SpringApplication.run(YafengApplication.class, args);
    }

}
