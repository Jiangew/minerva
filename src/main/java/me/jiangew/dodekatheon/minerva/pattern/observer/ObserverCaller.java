package me.jiangew.dodekatheon.minerva.pattern.observer;

/**
 * Author: Jiangew
 * Date: 14/02/2017
 * <p>
 * 观察者模式：
 * 被观察者（subject）、观察者（observer）
 * 1 被观察者决定了谁可以获得它的变更通知，并且负责将变更通知给观察者
 * 2 观察者负责来执行这些变化
 * <p>
 * 结构：
 * 1 一个被观察者接口
 * 2 一个观察者接口
 * 3 一个或多个被观察者实现类
 * 4 一个或多个观察者实现类
 */
public class ObserverCaller {

    public static void main(String[] args) {
        DemoSubject<DemoObserver> subject = new DemoSubject<>();
        DemoObserver observer = new DemoObserver();

        subject.add(observer);
        subject.notifyObservers();

        subject.setName("JamesiWorks");
        subject.notifyObservers();
    }
}
