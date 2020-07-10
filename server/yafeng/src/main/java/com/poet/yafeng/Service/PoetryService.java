package com.poet.yafeng.Service;


import com.poet.yafeng.DAO.PoetryDAO;
import com.poet.yafeng.Modal.CommonResult;
import com.poet.yafeng.POJO.Poetry;
import org.omg.CORBA.PolicyError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectSerializer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import sun.reflect.annotation.ExceptionProxy;
import com.alibaba.fastjson.JSONObject;
import javax.persistence.criteria.CriteriaBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PoetryService {

    @Autowired
    private PoetryDAO poetryDAO;
    @Autowired
    private RestTemplate restTemplate;


    //随机获取一首诗
    public CommonResult getOnePoetry() {
        CommonResult result = new CommonResult();
        try {
            Poetry poetry = poetryDAO.getOnePoetry();
            poetry=formate(poetry);
            if(poetry==null)
                result.setMsg("暂无数据");
            else
                result.setData(poetry);
        } catch (Exception e) {
            e.printStackTrace();
            result.setStatus(500);
            result.setMsg("接口调用失败");
            result.setResult("fail");
        }

        return result;
    }

    //获取诗的详细信息
    public CommonResult getPoetryDetail(String name,String author)
    {
        CommonResult result=new CommonResult();
        try {
            System.out.println("name:"+name);
            System.out.println("name:"+author);
            Poetry poetryDetail=poetryDAO.getPoetryDetail(name,author);
            poetryDetail=formate(poetryDetail);
            if(poetryDetail==null)
                result.setMsg("暂无数据");
            else
                result.setData(poetryDetail);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            result.setMsg("接口调用失败");
            result.setStatus(500);
            result.setResult("fail");
        }

        return result;
    }

    //根据诗词内容获取诗词详情
    public CommonResult getPoetryDetailBy(String content)
    {
        CommonResult result=new CommonResult();
        try {
            String format="%";
            String tempTexts[] = content.split("、|，|。|；|？|！");
            System.out.println("content:"+content);
            for(int i=0;i<tempTexts.length;i++) {
                System.out.println("分割："+tempTexts[i]);
                format += tempTexts[i] + "%";
            }
            System.out.println("FORMATE:"+format);
            Poetry poetryDetail=poetryDAO.getPoetryDetailBy(format);
            poetryDetail=formate(poetryDetail);
            if(poetryDetail==null)
                result.setMsg("暂无数据");
            else
                result.setData(poetryDetail);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            result.setMsg("接口调用失败");
            result.setStatus(500);
            result.setResult("fail");
        }
        return result;
    }

    //根据诗名和作者获取诗词
    public CommonResult getPoetryBy(String name,String author)
    {
        CommonResult result=new CommonResult();
        try {
            List<Poetry> poetryList=null;
            poetryList=poetryDAO.getPoetryBy(name,author);
            if(poetryList==null)
                result.setMsg("暂无数据");
            else
            {
                result.setData(poetryList);
                result.setMsg("获取诗词成功");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            result.setMsg("获取诗词失败");
            result.setStatus(500);
            result.setResult("fail");
        }
        return result;
    }

    //根据搜索关键字获取诗词列表
    public CommonResult getPoetryBySearch(String key,Integer page,Integer size)
    {
        CommonResult result=new CommonResult();
        PageRequest pageRequest = PageRequest.of(page,size);
        List<Poetry> poetryList=new ArrayList<Poetry>();
        Page<Poetry> poetryPage=null;
        int isAuthor=0;
        int isTitle=0;
        try {
//            isAuthor=;
//            isTitle=;
            if(poetryDAO.isExistAuthor(key)!=null)
                poetryPage=poetryDAO.getPoetryBySearch("null",key,"null",pageRequest);
            else if(poetryDAO.isExistTitle(key)!=null)
                 poetryPage=poetryDAO.getPoetryBySearch(key,"null","null",pageRequest);
            else
                poetryPage=poetryDAO.getPoetryBySearch("null","null",key,pageRequest);
            poetryList=poetryPage.getContent();
            if(poetryList.size()==0)
                result.setMsg("暂无数据");
            else {
                for(Poetry poetry:poetryList)
                    formate(poetry);
                result.setData(poetryList);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            result.setMsg("接口调用失败");
            result.setStatus(500);
            result.setResult("fail");
        }
        return result;
    }

    //根据标签分类获取诗词内容
    public CommonResult getPoetryByTag(String tag, String dynasty, Integer page,Integer size)
    {
        CommonResult result=new CommonResult();
        System.out.println("TAGGGGGGGGGGGG:"+tag);
        PageRequest pageRequest = PageRequest.of(page,size);
        String[] temptags=tag.split(" ");
        String temp="%";
        for(int i=0;i<temptags.length;i++)
        {
            System.out.println("TAG:"+temptags[i]);
            temp+=temptags[i]+"%";
        }
        tag=temp;
        System.out.println("TAGS:"+tag);
        List<Poetry> poetryList=new ArrayList<Poetry>();
        try {
            List<Poetry> tempList;
            Page<Poetry> poetryPage=poetryDAO.getPoetryByTag(tag,dynasty,pageRequest);
            tempList=poetryPage.getContent();
            if(tempList.size()>0) {
                for(Poetry poetry:tempList)
                {
                    formate(poetry);
                }
                if (!dynasty.equals("null")) {
                    for (Poetry poetry : tempList) {
                        if (poetry.getDynasty().equals(dynasty))
                        {
                            poetryList.add(poetry);
                        }
                    }
                } else
                {
                    poetryList = tempList;
                }
                result.setData(poetryList);
                result.setMsg("获取诗词成功");
            }
            else
                result.setMsg("暂无数据");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            result.setMsg("接口调用失败");
            result.setStatus(500);
            result.setResult("fail");
        }
        return result;
    }

    //根据长度分割字符串
    public List<String> getListStr(String str, int len) {
        List<String> listStr = new ArrayList<>();
        int strLen = str.length();
        int start = 0;
        int num = len;
        String temp = null;
        while (true) {
            try {
                if (num >= strLen) {
                    temp = str.substring(start, strLen);
                } else {
                    temp = str.substring(start, num);
                }
            } catch (Exception e) {
                System.out.println("拆分完毕");
                break;
            }
            listStr.add(temp);
            start = num;
            num = num + len;
        }
        return listStr;
    }

    //合并字符串并在每一句后加换行
    public String integrateStr(List<String> strList) {
        String result = "";
        for (int i=0;i<strList.size()-1; i++) {
            result += strList.get(i) + "\n";
        }
        return result;
    }

    //判断诗的类型
    public boolean judge(String[] texts, int length) {
        int j = 0;
        for (int i = 0; i < texts.length; i++) {
            j = i + 1;
            if (j >= texts.length - 1)
                return true;
            //判断每句诗的字数是否相等
            if (texts[i].length() == length && texts[j].length() == length)
                continue;
            else
                break;
        }
        return false;
    }

    //获取所有的朝代
    public CommonResult getAllDynasty()
    {
        CommonResult result=new CommonResult();
        try {
            List<String> dynastyList = null;
            dynastyList = poetryDAO.getAllDynasty();
            result.setMsg("获取朝代成功");
            result.setData(dynastyList);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            result.setMsg("获取朝代失败");
            result.setStatus(500);
            result.setResult("fail");
        }
        return result;
    }

    //获取所有的诗词类型
    public CommonResult getAllPoemStyle()
    {
        CommonResult result=new CommonResult();
        try {
            List<String> styleList = null;
            styleList = poetryDAO.getAllPoemStyle();
            result.setData(styleList);
            result.setMsg("获取诗词类型成功");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            result.setMsg("获取朝代失败");
            result.setStatus(500);
            result.setResult("fail");
        }
        return result;
    }

    //格式处理
    private Poetry formate(Poetry poetry)
    {
        String contentText = poetry.getContent();
        String tempText = poetry.getContent();
        /*
        @去除原文本中所有换行符
         */
        Pattern p = Pattern.compile("\n");
        Matcher m = p.matcher(tempText);
        tempText = m.replaceAll("");
        /*
        @去除原文本中所有括弧注释
         */
        p=Pattern.compile("[(].+[)]$");
        m=p.matcher(tempText);
        tempText=m.replaceAll("");

        //按标点分割成句子
        String tempTexts[] = tempText.split("、|，|。|；|？|！");


        boolean isFive = judge(tempTexts, 5);
        boolean isSeven = judge(tempTexts, 7);
        boolean isFour = judge(tempTexts,4);

        if(isFour==true)//四字短句
        {
            if(poetry.getTag()==null)
                poetry.setTag("短句 ");
            //两个单句加上两个标点
            List<String> texts = getListStr(tempText, 10);
            tempText = integrateStr(texts);
            contentText = tempText;
        }
        if (isFive == true) //五言
        {
            if(poetry.getTag()==null)
                poetry.setTag("五言 ");
            List<String> texts = getListStr(tempText, 12);
            tempText = integrateStr(texts);
            contentText = tempText;
        }
        if (isSeven == true) //七言
        {
            if(poetry.getTag()==null)
                poetry.setTag("七言 ");
            List<String> texts = getListStr(tempText, 16);
            tempText = integrateStr(texts);
            contentText = tempText;
        }
        else
            if(poetry.getTag()==null)
                poetry.setTag("词 曲 文言文");

        contentText = tempText;
        poetry.setContent(contentText);

        return poetry;
    }

    //诗词自动创作
    public CommonResult autoCreating(String style,int len_of_sentences,String keyword)
    {
        CommonResult result=new CommonResult();
        String poem="";
        String script="python /usr/local/CPGS/main.py -style "+style+" -keyword "+keyword+" --len "+len_of_sentences;
        try {
            System.out.println("Script :"+script);
            Process pr = Runtime.getRuntime().exec(script);
            InputStreamReader ir = new InputStreamReader(pr.getInputStream(),"gbk");
            LineNumberReader input = new LineNumberReader(ir);
            poem = input.readLine();
            input.close();
            ir.close();
            System.out.println("Poem:"+poem);
            result.setData(poem);
            result.setMsg("诗词创作完成");
        } catch (Exception e) {
            e.printStackTrace();
            result.setStatus(500);
            result.setResult("fail");
            result.setMsg("创作失败");
        }

        return result;
    }


//    public List<Poetry> get(int pagenum)
//    {
//        PageHelper.startPage(pagenum,2);
//        return poetryDAO.get();
//    }

    
}
