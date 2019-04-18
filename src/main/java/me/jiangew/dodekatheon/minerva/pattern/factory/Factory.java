package me.jiangew.dodekatheon.minerva.pattern.factory;

/**
 * Author: Jiangew
 * Date: 15/02/2017
 */
public interface Factory<T extends AbstractProduct> {

    T createProductA();

    T createProductB();
}
