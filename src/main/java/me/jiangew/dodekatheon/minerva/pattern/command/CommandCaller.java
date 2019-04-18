package me.jiangew.dodekatheon.minerva.pattern.command;

/**
 * Author: Jiangew
 * Date: 14/02/2017
 * <p>
 * 命令模式：
 * 简单理解为 司令 -> 传令官 -> 士兵
 * <p>
 * 1 一个抽象的命令接口
 * 2 一个抽象的命令执行者接口
 * 3 几个具体的命令实现类
 * 4 几个具体的命令执行者实现类
 * 5 一个统一调用类
 * 6 一个调用者
 */
public class CommandCaller {

    public static void main(String[] args) {
        Command<ExecutorA> ca = new CommandA<>(ExecutorA.class);
        Invoker<Command<ExecutorA>> in = new Invoker<>();
        in.setCommand(ca);
        in.doAction();
    }
}
