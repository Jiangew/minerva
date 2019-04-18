package me.jiangew.dodekatheon.minerva.pattern.facade;

/**
 * Author: Jiangew
 * Date: 15/02/2017
 */
public class ProcessImpl<T, M, N> implements Process<T, M, N> {

    @Override
    public void doProcessA(T inputA) {
        System.out.println("Process A");
    }

    @Override
    public void doProcessB(M inputB) {
        System.out.println("Process B");
    }

    @Override
    public void doProcessC(N inputC) {
        System.out.println("Process C");
    }
}
