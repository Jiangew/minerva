package me.jiangew.dodekatheon.minerva.pattern.strategy;

/**
 * Author: Jiangew
 * Date: 15/02/2017
 * <p>
 * 策略执行的统一方式
 */
@SuppressWarnings("all")
public class Context<T extends Strategy> {

    private T t;

    public Context(Class<? extends Strategy> c) {
        try {
            t = (T) Class.forName(c.getName()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        t.doSomeThing();
    }
}
