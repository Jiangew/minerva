package me.jiangew.dodekatheon.minerva.pattern.command;

/**
 * Author: Jiangew
 * Date: 14/02/2017
 */
public class ExecutorA implements Executor {

    @Override
    public void doSomething() {
        System.out.println("Executor A do something.");
    }

    @Override
    public void doOtherthing() {
        System.out.println("Executor A do otherthing.");
    }
}
