package me.jiangew.dodekatheon.minerva.pattern.builder;

/**
 * Author: Jiangew
 * Date: 15/02/2017
 */
public class BuilderA implements Builder {

    @Override
    public void buildPartA() {
        System.out.println("Builder A builds part A.");
    }

    @Override
    public void buildPartB() {
        System.out.println("Builder A builds part B.");
    }

    @Override
    public void returnResult() {
        System.out.println("Builder A returns part A and part B.");
    }
}
