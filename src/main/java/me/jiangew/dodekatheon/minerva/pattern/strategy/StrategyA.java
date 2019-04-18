package me.jiangew.dodekatheon.minerva.pattern.strategy;

/**
 * Author: Jiangew
 * Date: 15/02/2017
 */
public class StrategyA implements Strategy {

    @Override
    public void doSomeThing() {
        System.out.println("Strategy A do something.");
    }
}
