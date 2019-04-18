package me.jiangew.dodekatheon.minerva.pattern.command;

/**
 * Author: Jiangew
 * Date: 14/02/2017
 */
public class CommandA<T extends Executor> extends Command<T> {

    public CommandA(Class<? extends Executor> c) {
        super(c);
    }

    @Override
    public void execute() {
        super.t.doSomething();
    }
}
