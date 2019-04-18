package me.jiangew.dodekatheon.minerva.pattern.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Jiangew
 * Date: 14/02/2017
 */
public class DemoSubject<T extends CustomObserver> implements CustomSubject<T> {

    private List<T> observers;
    private String name = "Mi Max";

    public DemoSubject() {
        observers = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void add(T t) {
        observers.add(t);
    }

    @Override
    public void remove(T t) {
        observers.remove(t);
    }

    @Override
    public void notifyObservers() {
        for (T observer : observers) {
            observer.update(name);
        }
    }
}