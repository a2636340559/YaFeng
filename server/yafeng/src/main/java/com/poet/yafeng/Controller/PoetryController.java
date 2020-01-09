package com.poet.yafeng.Controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.poet.yafeng.DAO.PoetryDAO;
import com.poet.yafeng.Modal.CommonResult;
import com.poet.yafeng.POJO.Poetry;
import com.poet.yafeng.Service.PoetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.validation.constraints.Null;

import java.util.List;

@RestController
@RequestMapping("/poetry")
public class PoetryController {

    @Autowired
    private PoetryService poetryService;
    @Resource
    private PoetryDAO poetryDAO;

    //随机获取一首古诗
    @JsonView(Poetry.SimpleView.class)
    @RequestMapping("/getOnePoetry")
    public CommonResult getOnePoetry()
    {
        return poetryService.getOnePoetry();
    }

    //根据诗名和作者获取诗词详情
    @JsonView(Poetry.DetailView.class)
    @RequestMapping(value = "/getPoetryDetail",method = RequestMethod.GET)
    public CommonResult getPoetryDetail(String name,String author)
    {
        return poetryService.getPoetryDetail(name,author);
    }

    //根据诗名和作者获取诗词
    @JsonView(Poetry.SimpleView.class)
    @RequestMapping(value = "/getPoetryBy",method = RequestMethod.GET)
    public CommonResult getPoetryBy(String name,String author)
    {
        return poetryService.getPoetryBy(name,author);
    }

    //根据内容获取诗词详情
    @JsonView(Poetry.DetailView.class)
    @RequestMapping(value = "/getPoetryDetailBy",method = RequestMethod.GET)
    public CommonResult getPoetryDetailBy(String content)
    {
        return poetryService.getPoetryDetailBy(content);
    }

  //获取所有朝代
    @RequestMapping(value = "/getAllDynasty",method = RequestMethod.GET)
    public CommonResult getAllDynasty()
    {
        return poetryService.getAllDynasty();
    }

    //获取所有诗词类型
    @RequestMapping(value = "/getAllPoemStyle",method =RequestMethod.GET)
    public CommonResult getAllPoemStyle()
    {
        return poetryService.getAllPoemStyle();
    }

    //根据搜索关键字获取诗词
    @JsonView(Poetry.SimpleView.class)
    @RequestMapping(value = "/getPoetryBySearch",method=RequestMethod.GET)
    public CommonResult getPoetryBySearch(String key,Integer page,Integer size)
    {
        return poetryService.getPoetryBySearch(key,page,size);
    }

    //根据标签获取诗词
    @JsonView(Poetry.SimpleView.class)
    @RequestMapping(value = "/getPoetryByTag",method = RequestMethod.GET)
    public CommonResult getPoetryByTag(@RequestParam(name="tag",defaultValue = "暂无标签") String tag,@RequestParam(name = "dynasty",defaultValue = "null") String dynasty,Integer page,Integer size)
    {
        return poetryService.getPoetryByTag(tag,dynasty,page,size);
    }

    @RequestMapping(value = "/get",method = RequestMethod.GET)
    public Page<Poetry> get(Integer page)
    {

        PageRequest pageRequest = PageRequest.of(page,2);
        Page<Poetry>poetries= poetryDAO.get(pageRequest);
       poetries.getContent();
        return poetries;
    }


    @RequestMapping(value = "/spiltBySpace",method = RequestMethod.GET)
    public void spiltBySpace()
    {
        String tag="写景 抒情 送别";
        String[] tags=tag.split(" ");
        for(int i=0;i<tags.length;i++)
            System.out.println("SSSSSSSSSSSSSSSSSSS----------------:"+tags[i]);

    }

    @RequestMapping(value = "/getVideoList",method = RequestMethod.GET)
    public String[] getVideoList()
    {
        String videos[]={"mv1.mp4","mv2.mp4","mv3.mp4"};
        return videos;
    }
}
