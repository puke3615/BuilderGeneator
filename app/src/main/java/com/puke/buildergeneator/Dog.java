package com.puke.buildergeneator;

import android.graphics.Color;

import com.puke.buildergenerator.api.Builder;
import com.puke.buildergenerator.api.Item;


/**
 * @author zijiao
 * @version 16/8/22
 */
public class Dog {

    private String name;
    private int age;
    private String sex;
    private Color color;

    @Builder("configure")
    Dog(@Item("customName") String name, @Item int age, @Item String sex, Color color) {
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.color = color;
    }
}
