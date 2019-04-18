package me.jiangew.dodekatheon.minerva.pattern.decorator;

/**
 * Author: Jiangew
 * Date: 15/02/2017
 * <p>
 * 装饰器模式：
 * 在原有已经实现的功能上添加新的功能实现
 * <p>
 * 结构：
 * 1 一个原有的抽象层（接口或抽象类）
 * 2 对应既有抽象层的是实现层
 * 3 一个Decorator类来实现原有抽象层
 * 4 一个或多个具体Decorator类扩展Decorator实现
 * 4 一个调用者
 */
public class DecoratorCaller {

    public static void main(String[] args) {
        Origin origin = new OriginImpl();
        DecoratorA<Origin> a = new DecoratorA<>(origin);
        DecoratorB<Origin> b = new DecoratorB<>(origin);
        a.doSomeThing();
        b.doOtherThing();
    }
}
