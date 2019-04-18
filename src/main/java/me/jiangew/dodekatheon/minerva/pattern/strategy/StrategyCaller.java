package me.jiangew.dodekatheon.minerva.pattern.strategy;

/**
 * Author: Jiangew
 * Date: 15/02/2017
 * <p>
 * 策略模式：
 * 做同一类型的事情，有不同的做法，这依赖于不用的上下文环境，但是有一个统一的模式
 * <p>
 * 结构：
 * 场景类似电商对不同等级的用户有不同的折扣
 */
public class StrategyCaller {

    public static void main(String[] args) {
        Context<StrategyA> ca = new Context<>(StrategyA.class);
        ca.execute();

        Context<StrategyB> cb = new Context<>(StrategyB.class);
        cb.execute();
    }
}
