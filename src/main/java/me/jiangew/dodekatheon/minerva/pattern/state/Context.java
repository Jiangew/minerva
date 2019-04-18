package me.jiangew.dodekatheon.minerva.pattern.state;

/**
 * Author: Jiangew
 * Date: 13/02/2017
 */
public class Context<T extends AbstractState> {

    private T t;

    public T getState() {
        return t;
    }

    public void setState(T t) {
        this.t = t;
    }

    public void doStateA() {
        this.t.doStateA();
    }

    public void doStateB() {
        this.t.doStateB();
    }
}
