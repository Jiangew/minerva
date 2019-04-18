package me.jiangew.dodekatheon.minerva.pattern.command;

/**
 * Author: Jiangew
 * Date: 14/02/2017
 */
public class ExecutorB implements Executor {

    @Override
    public void doSomething() {
        System.out.println("Executor B do something.");
    }

    @Override
    public void doOtherthing() {
        System.out.println("Executor B other thing.");
    }
}
