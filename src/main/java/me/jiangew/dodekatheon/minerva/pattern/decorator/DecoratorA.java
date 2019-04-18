package me.jiangew.dodekatheon.minerva.pattern.decorator;

/**
 * Author: Jiangew
 * Date: 15/02/2017
 */
public class DecoratorA<T extends Origin> extends Decorator<T> {

    public DecoratorA(T t) {
        super(t);
    }

    private void doNothing() {
        System.out.println("Decorator A do nothing.");
    }

    private void doAnotherThing() {
        System.out.println("Decorator A do another thing.");
    }

    @Override
    public void doSomeThing() {
        doNothing();
        super.doSomeThing();
    }

    @Override
    public void doOtherThing() {
        super.doOtherThing();
        doAnotherThing();
    }
}
