package me.jiangew.dodekatheon.minerva.pattern.command;

/**
 * Author: Jiangew
 * Date: 14/02/2017
 */
public class Invoker<T extends Command<? extends Executor>> {

    private T t;

    public void setCommand(T t) {
        this.t = t;
    }

    public void doAction() {
        t.execute();
    }
}
