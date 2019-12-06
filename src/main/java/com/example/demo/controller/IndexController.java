package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName indexController
 * @Author lizhuoyuan
 * @Date 2019/11/26 12:28
 **/
@Controller
public class IndexController{

    @RequestMapping("/index")
    public String quick(){
        return "index.html";
    }

}