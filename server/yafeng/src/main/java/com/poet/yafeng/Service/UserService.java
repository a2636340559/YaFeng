package com.poet.yafeng.Service;

import com.poet.yafeng.DAO.CollectDAO;
import com.poet.yafeng.DAO.PoetryDAO;
import com.poet.yafeng.DAO.UserDAO;
import com.poet.yafeng.Modal.CommonResult;
import com.poet.yafeng.POJO.Collect;
import com.poet.yafeng.POJO.Poetry;
import com.poet.yafeng.POJO.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    UserDAO userDAO;
    @Autowired
    CollectDAO collectDAO;
    @Autowired
    PoetryDAO poetryDAO;
    @Autowired
    PoetryService poetryService;

    //登录/注册
    public CommonResult loginAndRegister(String account,String password)
    {
        CommonResult result=new CommonResult();
        try {
            User user = userDAO.findUserByAccount(account);
            if (user == null) {
                user = new User();
                user.setAccount(account);
                user.setPassword(password);
                userDAO.save(user);
                result.setMsg("register");
                result.setData(user.getId());
            } else if(password.equals(user.getPassword())){
                result.setMsg("login");
                result.setData(user.getId());
            }
            else
                result.setData("密码错误");
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

    //返回用户收藏
    public CommonResult getCollect(BigInteger userId)
    {
        CommonResult result=new CommonResult();
        List<Poetry> poetryList=new ArrayList<>();
        try {
            List<Collect> collectList=null;
            collectList=collectDAO.findAllByUserId(userId);
            if(collectList.size()==0)
            {
                result.setMsg("你还没有收藏哦");
                result.setData(collectList);
            }
            else {
                for (Collect collect : collectList) {
                    List<Poetry> temp = poetryDAO.getPoetryBy(collect.getPoetryName(), collect.getAuthor());
                    poetryList.addAll(temp);
                }
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

    //收藏
    public CommonResult collect(BigInteger userId,String poetryName,String author)
    {
        CommonResult result=new CommonResult();
        try {
           Collect collect=new Collect();
           collect.setPoetryName(poetryName);
           collect.setAuthor(author);
           collect.setUserId(userId);
           collectDAO.save(collect);
           result.setMsg("收藏成功");
           result.setData(1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            result.setMsg("接口调用失败");
            result.setStatus(500);
            result.setResult("fail");
            result.setData(0);
        }
        return result;
    }

    //判断是否为用户收藏的诗
    public CommonResult isStoraged(BigInteger userId,String poetryName,String author)
    {
        CommonResult result=new CommonResult();
        try {
            Collect collect=collectDAO.isStoraged(userId,poetryName,author);
            if(collect==null)
            {
                result.setMsg("未收藏");
                result.setData(false);
            }
            else
            {
                result.setMsg("已收藏");
                result.setData(true);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            result.setMsg("接口调用失败");
            result.setStatus(500);
            result.setResult("fail");
            result.setData(false);
        }
        return result;
    }
}
