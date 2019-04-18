package me.jiangew.dodekatheon.minerva.pattern.observer;

/**
 * Author: Jiangew
 * Date: 14/02/2017
 */
public class DemoObserver implements CustomObserver {

    private String name;

    @Override
    public void update(String name) {
        this.name = name;
    }
}
