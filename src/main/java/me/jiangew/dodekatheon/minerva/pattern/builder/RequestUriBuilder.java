package me.jiangew.dodekatheon.minerva.pattern.builder;

/**
 * Author: Jiangew
 * Date: 21/02/2017
 */
public class RequestUriBuilder extends AbstractUrlBuilder {

    public RequestUriBuilder(String prefix) {
        super(prefix);
    }

    public RequestUriBuilder rate(String rate) {
        appendParams("rate", rate);
        return this;
    }

    public RequestUriBuilder le(String le) {
        appendParams("le", le);
        return this;
    }

    public RequestUriBuilder audio(String audio) {
        appendParams("audio", audio);
        return this;
    }

    @Override
    public String build() {
        return prefix + params;
    }
}
