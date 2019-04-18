package me.jiangew.dodekatheon.minerva.pattern.factory;

/**
 * Author: Jiangew
 * Date: 15/02/2017
 */
@SuppressWarnings("all")
public abstract class AbstractFactory<T extends AbstractProduct> implements Factory<T> {

    protected T createProduct(Class<? extends AbstractProduct> c) {
        T product = null;
        try {
            product = (T) Class.forName(c.getName()).newInstance();
        } catch (Exception e) {
            // handle exception
        }

        return product;
    }
}
