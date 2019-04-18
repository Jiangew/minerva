package me.jiangew.dodekatheon.minerva.pattern.iterator;

/**
 * Author: Jiangew
 * Date: 14/02/2017
 */
public interface CommonObject {

    void add(String name, String desc);

    void print();

    CommonIterator<CommonObject> iterator();

}
