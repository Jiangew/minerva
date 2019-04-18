package me.jiangew.dodekatheon.minerva.pattern.state;

/**
 * Author: Jiangew
 * Date: 13/02/2017
 */
public abstract class AbstractState {

    protected Context<? extends AbstractState> context;

    public void setContext(Context<? extends AbstractState> context) {
        this.context = context;
    }

    public abstract void doStateA();

    public abstract void doStateB();
}
