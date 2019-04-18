package me.jiangew.dodekatheon.minerva.pattern.facade;

/**
 * Author: Jiangew
 * Date: 15/02/2017
 * <p>
 * 门面模式：
 * 从调用者来看，接口很简单，通常是一个具有多参数的方法；各个参数是各个步骤所需要处理的输入
 * <p>
 * 结构：
 * 将做成一件事分成几个步骤，将这些步骤用一个统一的方法概括起来，由一个特定的类来负责这个事情的处理
 */
public class FacadeCaller {

    public static void main(String[] args) {
        Facade<String, Integer, Long> facade = new Facade<>();
        facade.doProcess("ganluo", 1, 1L);
    }
}
