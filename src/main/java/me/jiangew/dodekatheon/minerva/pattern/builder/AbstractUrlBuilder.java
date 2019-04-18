package me.jiangew.dodekatheon.minerva.pattern.builder;

/**
 * Author: Jiangew
 * Date: 21/02/2017
 */
public abstract class AbstractUrlBuilder {
    protected String params = "";
    protected String prefix = "";

    protected void appendParams(String name, String param) {
        if (name == null || param == null)
            return;
        if (params.length() == 0) {
            params += name + "=" + param;
        } else {
            params += "&" + name + "=" + param;
        }
    }

    public AbstractUrlBuilder(String prefix) {
        this.prefix = prefix;
    }

    public abstract String build();
}
