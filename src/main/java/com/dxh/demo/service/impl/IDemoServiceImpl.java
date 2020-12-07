package com.dxh.demo.service.impl;

import com.dxh.demo.service.IDemoService;
import com.dxh.edu.mvcframework.annotations.DxhService;

/**
 * @Author: Dengxh
 * @Date: 2020/12/6 18:47
 * @Description:
 */
@DxhService("demoService")
public class IDemoServiceImpl implements IDemoService {
    @Override
    public String get(String name) {
        System.out.println("Service实现类中的Name："+ name);
        return name;
    }
}
