package me.jiangew.dodekatheon.minerva.pattern.iterator;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Jiangew
 * Date: 14/02/2017
 */
public class DemoCommonObject implements CommonObject {

    private List<CommonObject> list = new ArrayList<>();
    private String name;
    private String desc;

    public DemoCommonObject() {

    }

    public DemoCommonObject(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    @Override
    public void add(String name, String desc) {
        this.list.add(new DemoCommonObject(name, desc));
    }

    @Override
    public void print() {
        System.out.println("----------------");
        System.out.println("Name: " + this.name + ";\nDescription: " + this.desc);
    }

    @Override
    public CommonIterator<CommonObject> iterator() {
        return new DemoCommonIterator<>(this.list);
    }
}
