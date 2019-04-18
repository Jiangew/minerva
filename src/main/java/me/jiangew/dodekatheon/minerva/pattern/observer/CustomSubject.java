package me.jiangew.dodekatheon.minerva.pattern.observer;

/**
 * Author: Jiangew
 * Date: 14/02/2017
 */
public interface CustomSubject<T extends CustomObserver> {

    void add(T t);

    void remove(T t);

    void notifyObservers();

}
