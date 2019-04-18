package me.jiangew.dodekatheon.minerva.pattern.facade;

/**
 * Author: Jiangew
 * Date: 15/02/2017
 */
public class Facade<T, M, N> {

    private Process<T, M, N> process = new ProcessImpl<>();

    public void doProcess(T t, M m, N n) {
        process.doProcessA(t);
        process.doProcessB(m);
        process.doProcessC(n);
    }
}
