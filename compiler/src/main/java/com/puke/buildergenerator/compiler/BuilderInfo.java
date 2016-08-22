package com.puke.buildergenerator.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.lang.model.type.TypeMirror;

/**
 * @author zijiao
 * @version 16/8/22
 */
public class BuilderInfo {

    public String prefix;
    public StringBuilder values = new StringBuilder();
    public List<Item> builders;

    public BuilderInfo add(Item... item) {
        if (builders == null) {
            builders = new ArrayList<>();
        }
        builders.addAll(Arrays.asList(item));
        return this;
    }

    public static class Item {
        public String property;
        public TypeMirror type;
    }

}
