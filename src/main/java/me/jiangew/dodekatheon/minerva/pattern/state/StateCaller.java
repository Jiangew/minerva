package me.jiangew.dodekatheon.minerva.pattern.state;

/**
 * Author: Jiangew
 * Date: 13/02/2017
 * <p>
 * 状态模式：
 * 当你有多个不同状态，且有复杂的变化时，状态模式能够帮你把视线分离开
 * <p>
 * 结构：
 * 1 一个抽象的状态类，定义一个统一的调用句柄（handler），而且它定义了一系列对应状态的行为
 * 2 几个实现类继承这个抽象类
 * 3 一个统一调用句柄（handler）
 * 4 一个调用者
 */
public class StateCaller {

    public static void main(String[] args) {
        Context<AbstractState> context = new Context<>();
        context.setState(new DemoStateA());
        context.doStateA();
        context.doStateB();

        context.setState(new DemoStateB());
        context.doStateA();
        context.doStateB();
    }

}
