package com.littlejenny.gulimall.ware.service;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(5);
        list.add(6);
        list.add(7);
        list.forEach(item ->{
            if(item == 6)return;
        });
        System.out.println(8);
    }
}
