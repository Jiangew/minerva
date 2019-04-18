package me.jiangew.dodekatheon.minerva.pattern.proxy;

/**
 * Author: Jiangew
 * Date: 15/02/2017
 */
public class ProxyCaller {

    public static void main(String[] args) {
        Proxy<ChinaloveA> proxy = new Proxy<>(ChinaloveA.class);
        proxy.execute();
    }
}
