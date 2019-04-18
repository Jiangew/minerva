package me.jiangew.dodekatheon.minerva.pattern.proxy;

/**
 * Author: Jiangew
 * Date: 15/02/2017
 */
@SuppressWarnings("all")
public class Proxy<T extends Chinalove> {

    private T t;

    public Proxy(Class<? extends Chinalove> c) {
        try {
            t = (T) Class.forName(c.getName()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        t.doSomeThing();
        System.out.println("Proxy cannot do anything. Just let Chinalove do.");
    }
}
