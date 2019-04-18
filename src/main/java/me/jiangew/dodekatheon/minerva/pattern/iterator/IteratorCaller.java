package me.jiangew.dodekatheon.minerva.pattern.iterator;

/**
 * Author: Jiangew
 * Date: 14/02/2017
 * <p>
 * 迭代器模式：
 * 通常处理一组相同对象的集合，你会怎么做？把它们放进List里面，然后再Iterator方法遍历
 * <p>
 * 如何让设计的对象具有iterator()方法，不用一个个new它们
 * <p>
 * 结构：
 * 1 一个抽象对象接口（具有初始化具体对象的方法、迭代器方法）
 * 2 几个具体实现类来实现这样的接口，并具有传参构造函数
 * 3 一个继承了Iterator接口的抽象接口
 * 4 一个具体实现类实现抽象的Iterator接口
 * 5 一个调用者
 */
public class IteratorCaller {

    public static void main(String[] args) {
        CommonObject co = new DemoCommonObject();
        for (int i = 0; i < 10; i++) {
            co.add("Seq No. : " + i, "This is DemoCommonObject(" + i + ")");
        }

        CommonIterator<CommonObject> iterator = co.iterator();
        while (iterator.hasNext()) {
            CommonObject c = iterator.next();
            c.print();
        }
    }
}
