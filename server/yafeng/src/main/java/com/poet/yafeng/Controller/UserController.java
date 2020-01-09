package com.poet.yafeng.Controller;

import com.poet.yafeng.Modal.CommonResult;
import com.poet.yafeng.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public CommonResult loginAndRegister(String account,String password)
    {
        return userService.loginAndRegister(account,password);
    }

    @RequestMapping(value = "getCollect",method = RequestMethod.GET)
    public CommonResult getCollect(BigInteger userId)
    {
        return userService.getCollect(userId);
    }

    @RequestMapping(value = "collect",method = RequestMethod.GET)
    public CommonResult collect(BigInteger userId,String poetryName,String author)
    {
        return userService.collect(userId,poetryName,author);
    }


    @RequestMapping(value = "isStoraged",method = RequestMethod.GET)
    public CommonResult isStoraged(BigInteger userId,String poetryName,String author)
    {
        return userService.isStoraged(userId,poetryName,author);
    }
}
