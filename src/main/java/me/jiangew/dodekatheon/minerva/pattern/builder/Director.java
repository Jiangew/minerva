package me.jiangew.dodekatheon.minerva.pattern.builder;

/**
 * Author: Jiangew
 * Date: 15/02/2017
 */
@SuppressWarnings("all")
public class Director<T extends Builder> {

    private T t;

    public Director(Class<? extends Builder> c) {
        try {
            t = (T) Class.forName(c.getName()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void build() {
        t.buildPartA();
        t.buildPartB();
        t.returnResult();
    }
}
