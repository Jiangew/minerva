package me.jiangew.dodekatheon.minerva.pattern.factory;

/**
 * Author: Jiangew
 * Date: 15/02/2017
 */
public class NewFeatureProductFactory<T extends AbstractProduct> extends AbstractFactory<T> {

    @Override
    public T createProductA() {
        return super.createProduct(NewFeatureProductA.class);
    }

    @Override
    public T createProductB() {
        return super.createProduct(NewFeatureProductB.class);
    }
}