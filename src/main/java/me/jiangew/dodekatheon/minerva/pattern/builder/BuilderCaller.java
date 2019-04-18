package me.jiangew.dodekatheon.minerva.pattern.builder;

/**
 * Author: Jiangew
 * Date: 15/02/2017
 * <p>
 * 建造者模式：
 * 一件事情可以分为有限的几个步骤，或几个部分；规定了各个步骤或部分的建造方法；然后得到一个具体的事物或完成该事件
 * <p>
 * 结构：
 * 1 一个抽象的建造者接口
 * 2 几个具体的建造者实现类
 * 3 一个指挥者
 * 4 一个调用者
 */
public class BuilderCaller {

    public static void main(String[] args) {
        Director<BuilderA> a = new Director<>(BuilderA.class);
        a.build();

        Director<BuilderB> b = new Director<>(BuilderB.class);
        b.build();
    }
}
