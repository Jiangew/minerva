package me.jiangew.dodekatheon.minerva.features.stream;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Desc: xxx
 * <p>
 * Author: Jiangew
 * Date: 19/12/2017
 */
public class LambdaExample {

    public static void main(String[] args) {
        new LambdaExample().testCollections();
    }

    private void testCollections() {
        List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");
        Collections.sort(names, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return b.compareTo(a);
            }
        });
        Collections.sort(names, (String a, String b) -> {
            return b.compareTo(a);
        });
        Collections.sort(names, (String a, String b) -> b.compareTo(a));
        Collections.sort(names, (a, b) -> b.compareTo(a));
        names.sort((a, b) -> b.compareTo(a));
        names.sort(Collections.reverseOrder());

        List<String> names2 = Arrays.asList("peter", null, "anna", "mike", "xenia");
        names2.sort(Comparator.nullsLast(String::compareTo));
        System.out.println(names2);

        List<String> names3 = null;
        Optional.ofNullable(names3).ifPresent(list -> list.sort(Comparator.naturalOrder()));
        System.out.println(names3);
    }

    @FunctionalInterface
    interface Convertor<F, T> {
        T convert(F from);
    }

    static class Something {
        String startWith(String s) {
            return String.valueOf(s.charAt(0));
        }
    }

    private void testConvertor() {
        Convertor<String, Integer> convertor1 = (from) -> Integer.valueOf(from);
        Integer converted1 = convertor1.convert("123");
        System.out.println(converted1);

        // method reference
        Convertor<String, Integer> convertor2 = Integer::valueOf;
        Integer converted2 = convertor2.convert("123");
        System.out.println(converted2);

        Something something = new Something();
        Convertor<String, String> convertor3 = something::startWith;
        String converted3 = convertor3.convert("jiangew");
        System.out.println(converted3);
    }

    class Person {

        String firstName;
        String lastName;

        Person() {
        }

        Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    interface PersonFactory<P extends Person> {
        P create(String firstName, String lastName);
    }

    private void testConstructorReference() {
        PersonFactory<Person> personFactory = Person::new;
        Person person = personFactory.create("jiang", "ew");
        System.out.println(person.toString());
    }

    @FunctionalInterface
    interface Fun {
        void doFun();
    }

    private void testPredicate() throws Exception {
        // Predicates
        Predicate<String> predicate = (s) -> s.length() > 0;

        predicate.test("foo");              // true
        predicate.negate().test("foo");     // false

        Predicate<Boolean> nonNull = Objects::nonNull;
        Predicate<Boolean> isNull = Objects::isNull;

        Predicate<String> isEmpty = String::isEmpty;
        Predicate<String> isNotEmpty = isEmpty.negate();

        // Functions
        Function<String, Integer> toInteger = Integer::valueOf;
        Function<String, String> backToString = toInteger.andThen(String::valueOf);
        backToString.apply("123");  // "123"

        // Suppliers
        Supplier<Person> personSupplier = Person::new;
        personSupplier.get();   // new Person

        // Consumers
        Consumer<Person> greeter = (p) -> System.out.println("Hello, " + p.firstName);
        greeter.accept(new Person("Luke", "Skywalker"));

        // Comparators
        Comparator<Person> comparator = (p1, p2) -> p1.firstName.compareTo(p2.firstName);
        Person p1 = new Person("John", "Doe");
        Person p2 = new Person("Alice", "Wonderland");
        comparator.compare(p1, p2);             // > 0
        comparator.reversed().compare(p1, p2);  // < 0

        // Runnables
        Runnable runnable = () -> System.out.println(UUID.randomUUID());
        runnable.run();

        // Callables
        Callable<UUID> callable = UUID::randomUUID;
        callable.call();
    }

}
