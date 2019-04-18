package me.jiangew.dodekatheon.minerva.misc;

import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Desc: xxx
 * <p>
 * Author: Jiangew
 * Date: 19/12/2017
 */
public class StringSample {

    public static void main(String[] args) {
        testJoin();
        testChars();
        testPatternPredicate();
        testPatternSplit();
    }

    private static void testChars() {
        String string = "foobar:foo:bar"
                .chars()
                .distinct()
                .mapToObj(c -> String.valueOf((char) c))
                .sorted()
                .collect(Collectors.joining());
        System.out.println(string);
    }

    private static void testPatternSplit() {
        String string = Pattern.compile(":")
                               .splitAsStream("foobar:foo:bar")
                               .filter(s -> s.contains("bar"))
                               .sorted()
                               .collect(Collectors.joining(":"));
        System.out.println(string);
    }

    private static void testPatternPredicate() {
        long count = Stream.of("jiangew@gmail.com", "jiangerwei@hotmail.com")
                           .filter(Pattern.compile(".*@gmail\\.com")
                                          .asPredicate()
                           ).count();
        System.out.println(count);
    }

    private static void testJoin() {
        String string = String.join(":", "foobar", "foo", "bar");
        System.out.println(string);
    }

}
