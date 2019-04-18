package me.jiangew.dodekatheon.minerva.pattern.factory;

/**
 * Author: Jiangew
 * Date: 15/02/2017
 * <p>
 * 抽象工厂模式：
 * 是对工厂模式在产品扩展上的抽象化；抽象化的目的是为了对产品特性进行扩展
 * <p>
 * 结构：
 * 1 一个抽象产品接口
 * 2 几个产品抽象类去实现产品抽象接口
 * 3 几个产品具体类去分别继承这几个产品抽象类
 * 4 一个工厂接口
 * 5 一个工厂抽象类
 * 6 几个新特性的工厂实现类去实现工厂接口
 * 7 一个调用类
 */
public class FactoryCaller {

    public static void main(String[] args) {
        Factory<NewFeatureProductA> fa = new NewFeatureProductFactory<>();
        Product productA = fa.createProductA();
        Factory<NewFeatureProductB> fb = new NewFeatureProductFactory<>();
        Product productB = fb.createProductB();

        productA.doOtherThing();
        productB.doOtherThing();
    }
}
