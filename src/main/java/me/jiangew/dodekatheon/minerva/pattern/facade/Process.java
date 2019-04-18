package me.jiangew.dodekatheon.minerva.pattern.facade;

/**
 * Author: Jiangew
 * Date: 15/02/2017
 */
public interface Process<T, M, N> {

    void doProcessA(T inputA);

    void doProcessB(M inputB);

    void doProcessC(N inputC);
}
