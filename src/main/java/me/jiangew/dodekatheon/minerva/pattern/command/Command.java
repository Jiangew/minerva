package me.jiangew.dodekatheon.minerva.pattern.command;

/**
 * Author: Jiangew
 * Date: 14/02/2017
 */
@SuppressWarnings("all")
public abstract class Command<T extends Executor> {

    protected T t;

    public Command(Class<? extends Executor> c) {
        try {
            t = (T) Class.forName(c.getName()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void execute();

}
