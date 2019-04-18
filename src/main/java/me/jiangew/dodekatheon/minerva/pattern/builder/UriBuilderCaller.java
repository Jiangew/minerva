package me.jiangew.dodekatheon.minerva.pattern.builder;

/**
 * Author: Jiangew
 * Date: 21/02/2017
 */
public class UriBuilderCaller {

    public static void main(String[] args) {
        String uri = new RequestUriBuilder("http://mogujie.com/service?").rate("uuid").le("page").audio("limit").build();
    }
}
