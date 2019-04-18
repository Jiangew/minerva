package me.jiangew.dodekatheon.minerva.pattern.decorator;

/**
 * Author: Jiangew
 * Date: 15/02/2017
 */
public abstract class Decorator<T extends Origin> implements Origin {

    private T t;

    public Decorator(T t) {
        this.t = t;
    }

    @Override
    public void doSomeThing() {
        t.doSomeThing();
    }

    @Override
    public void doOtherThing() {
        t.doOtherThing();
    }
}
