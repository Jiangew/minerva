package me.jiangew.dodekatheon.minerva.pattern.builder;

/**
 * Author: Jiangew
 * Date: 15/02/2017
 */
public class BuilderB implements Builder {

    @Override
    public void buildPartA() {
        System.out.println("Builder B builds part A.");
    }

    @Override
    public void buildPartB() {
        System.out.println("Builder B builds part B.");
    }

    @Override
    public void returnResult() {
        System.out.println("Builder B returns part A and part B.");
    }
}
