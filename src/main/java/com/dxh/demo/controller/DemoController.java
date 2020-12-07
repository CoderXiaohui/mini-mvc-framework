package com.dxh.demo.controller;

import com.dxh.demo.service.IDemoService;
import com.dxh.edu.mvcframework.annotations.DxhAutowired;
import com.dxh.edu.mvcframework.annotations.DxhController;
import com.dxh.edu.mvcframework.annotations.DxhRequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: Dengxh
 * @Date: 2020/12/6 18:45
 * @Description:
 */
@DxhController
@DxhRequestMapping("/demo")
public class DemoController {
    @DxhAutowired
    private IDemoService demoService;

    /**
     * URL:/demo/query
     */
    @DxhRequestMapping("/query")
    public String query(HttpServletRequest request, HttpServletResponse response, String name){
        return demoService.get(name);
    }
}
