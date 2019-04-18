package me.jiangew.dodekatheon.minerva.pattern.decorator;

/**
 * Author: Jiangew
 * Date: 15/02/2017
 */
public class DecoratorB<T extends Origin> extends Decorator<T> {

    public DecoratorB(T t) {
        super(t);
    }

    private void before() {
        System.out.println("Decorator B do it before.");
    }

    private void after() {
        System.out.println("Decorator B do it after.");
    }

    @Override
    public void doSomeThing() {
        super.doSomeThing();
        after();
    }

    @Override
    public void doOtherThing() {
        before();
        super.doOtherThing();
    }
}
