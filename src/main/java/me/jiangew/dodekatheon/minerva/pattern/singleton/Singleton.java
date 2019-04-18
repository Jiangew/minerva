package me.jiangew.dodekatheon.minerva.pattern.singleton;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Jiangew
 * Date: 15/02/2017
 */
@SuppressWarnings("all")
public class Singleton {

    private static Map<Class<? extends Singleton>, Singleton> INSTANCE_MAP = new HashMap<>();

    protected Singleton() {
    }

    public synchronized static <E extends Singleton> Singleton getInstance(Class<E> instanceClass) throws Exception {
        if (INSTANCE_MAP.containsKey(instanceClass)) {
            return (E) INSTANCE_MAP.get(instanceClass);
        } else {
            E instance = instanceClass.newInstance();
            INSTANCE_MAP.put(instanceClass, instance);
            return instance;
        }
    }

    public void doSomeThing() {
        // do something ...
    }
}
