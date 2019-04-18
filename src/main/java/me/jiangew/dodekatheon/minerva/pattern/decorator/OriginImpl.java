package me.jiangew.dodekatheon.minerva.pattern.decorator;

/**
 * Author: Jiangew
 * Date: 15/02/2017
 */
public class OriginImpl implements Origin {

    @Override
    public void doSomeThing() {
        System.out.println("Origin do something.");
    }

    @Override
    public void doOtherThing() {
        System.out.println("Origin do other thing.");
    }
}
