package me.jiangew.dodekatheon.minerva.pattern.singleton;

/**
 * Author: Jiangew
 * Date: 15/02/2017
 * <p>
 * 代理模式：
 * 让代理类做委托类的事情；实际上只是代理类赋值初始化一个委托类来完成执行
 */
public class SingletonCaller {

    public static void main(String[] args) throws Exception {
        SingletonA single = (SingletonA) Singleton.getInstance(SingletonA.class);
        single.doOtherThing();
    }
}
