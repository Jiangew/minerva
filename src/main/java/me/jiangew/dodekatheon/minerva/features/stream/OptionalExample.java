package me.jiangew.dodekatheon.minerva.features.stream;

import java.util.Optional;
import java.util.function.Supplier;

public class OptionalExample {

    public static void main(String[] args) {
        new OptionalExample().testMethod();
    }

    private void testMethod() {
        Optional<String> optional = Optional.of("bam");

        optional.isPresent();                   // true
        optional.get();                         // "bam"
        optional.orElse("fallback");     // "bam"

        optional.ifPresent((s) -> System.out.println(s.charAt(0)));     // "b"
    }

    static class Outer {
        Nested nested = new Nested();

        public Nested getNested() {
            return nested;
        }
    }

    static class Nested {
        Inner inner = new Inner();

        public Inner getInner() {
            return inner;
        }
    }

    static class Inner {
        String foo = "boo";

        public String getFoo() {
            return foo;
        }
    }

    private static <T> Optional<T> resolve(Supplier<T> resolver) {
        try {
            T result = resolver.get();
            return Optional.ofNullable(result);
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    private static void test3() {
        Outer outer = new Outer();
        resolve(() -> outer.getNested().getInner().getFoo()).ifPresent(System.out::println);
    }

    private static void test2() {
        Optional.of(new Outer()).map(Outer::getNested).map(Nested::getInner).map(Inner::getFoo).ifPresent(System.out::println);
    }

    private static void test1() {
        Optional.of(new Outer())
                .flatMap(o -> Optional.ofNullable(o.nested))
                .flatMap(n -> Optional.ofNullable(n.inner))
                .flatMap(i -> Optional.ofNullable(i.foo))
                .ifPresent(System.out::println);
    }
}
