package me.jiangew.dodekatheon.minerva.features.stream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringNumberMathFileExample {

    private Pattern splitPattern = Pattern.compile(":");
    private Pattern emailPattern = Pattern.compile(".*gmail\\.com");

    public static void main(String[] args) {
        StringNumberMathFileExample example = new StringNumberMathFileExample();

        example.distinctChars();
        example.compilePattern();
        example.predicatePattern();
        example.parseUnsignedInt();
    }

    private void distinctChars() {
        String str = String.join(":", "foobar", "foo", "bar");

        str.chars().distinct()
                .mapToObj(c -> String.valueOf((char) c))
                .sorted()
                .collect(Collectors.joining());

        System.out.println("chars str: " + str);
    }

    private void compilePattern() {
        String str = splitPattern.splitAsStream("foobar:foo:bar")
                .filter(s -> s.contains("bar"))
                .sorted()
                .collect(Collectors.joining());

        System.out.println("pattern str: " + str);
    }

    private void predicatePattern() {
        long count = Stream.of("jiangew@gmail.com", "alice#outlook.com")
                .filter(emailPattern.asPredicate())
                .count();

        System.out.println("pattern predicate count: " + count);
    }

    private void parseUnsignedInt() {
        long maxUnsignedInt = (1L << 32) - 1;
        String maxUnsignedStr = String.valueOf(maxUnsignedInt);
        int unsignedInt = Integer.parseUnsignedInt(maxUnsignedStr, 10);
        String unsignedStr = Integer.toUnsignedString(unsignedInt, 10);
        System.out.println(String.format("maxUnsignedInt toString: %s, unsignedInt: %s, unsignedString: %s", maxUnsignedStr, unsignedInt, unsignedStr));

        try {
            Integer.parseInt(maxUnsignedStr, 10);
        } catch (NumberFormatException e) {
            System.err.println("could not parse signed int of " + maxUnsignedInt);
        }
    }

    private void overflowInteger() {
        try {
            Math.addExact(Integer.MAX_VALUE, 1);
        } catch (ArithmeticException e) {
            System.err.println(e.getMessage());
        }

        try {
            Math.toIntExact(Long.MAX_VALUE);
        } catch (ArithmeticException e) {
            System.err.println(e.getMessage());
        }
    }

    private void listFiles() {
        // try with statement, Stream implement AutoCloseable,
        // we really have to close the stream explicitly since it's backed by IO operations.
        try (Stream<Path> stream = Files.list(Paths.get(""))) {
            String joined = stream.map(String::valueOf)
                    .filter(path -> !path.startsWith("."))
                    .sorted()
                    .collect(Collectors.joining("; "));

            System.out.println("files list: " + joined);
        } catch (IOException e) {
            System.err.println("files list err: " + e.getMessage());
        }
    }

    private void findFiles() {
        Path start = Paths.get("");
        int maxDepth = 5;
        try (Stream<Path> stream = Files.find(start, maxDepth, (path, attr) -> String.valueOf(path).endsWith(".java"))) {
            String joined = stream.sorted()
                    .map(String::valueOf)
                    .collect(Collectors.joining("; "));

            System.out.println("files found: " + joined);
        } catch (IOException e) {
            System.err.println("files found err: " + e.getMessage());
        }
    }

    private void walkFiles() {
        Path start = Paths.get("");
        int maxDepth = 5;
        try (Stream<Path> stream = Files.walk(start, maxDepth)) {
            String joined = stream.map(String::valueOf)
                    .filter(path -> path.endsWith(".java"))
                    .sorted()
                    .collect(Collectors.joining("; "));

            System.out.println("files walk: " + joined);
        } catch (IOException e) {
            System.err.println("files walk err: " + e.getMessage());
        }
    }

    /**
     * This method are not very memory-efficient because the whole file will be read into memory at once.
     */
    private void rwFiles() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("resources/hazelcast.xml"));
            lines.add("print('hazelcast config end');");
            Files.write(Paths.get("resources/hazelcast-bak.xml"), lines);
        } catch (IOException e) {
            System.err.println("files read and write err: " + e.getMessage());
        }
    }

    /**
     * memory-efficient
     */
    private void rwFilesMemEfficient() {
        try (Stream<String> stream = Files.lines(Paths.get("resources/hazelcast.xml"))) {
            stream.filter(line -> line.contains("print"))
                    .map(String::trim)
                    .forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("files read and write memory-efficient err: " + e.getMessage());
        }
    }

    /**
     * more fine-grained control
     */
    private void rwFilesBuffered() {
        Path readerPath = Paths.get("resources/hazelcast.xml");
        try (BufferedReader reader = Files.newBufferedReader(readerPath)) {
            System.out.println(reader.lines());
        } catch (IOException e) {
            System.err.println("files read with buffered err: " + e.getMessage());
        }

        Path writerPath = Paths.get("resources/hazelcast-bak.xml");
        try (BufferedWriter writer = Files.newBufferedWriter(writerPath)) {
            writer.write("print('hazelcast config end');");
        } catch (IOException e) {
            System.err.println("files write with buffered err: " + e.getMessage());
        }
    }

    private void rwFilesBuffered2() {
        Path readerPath = Paths.get("resources/hazelcast.xml");
        try (BufferedReader reader = Files.newBufferedReader(readerPath)) {
            long count = reader.lines()
                    .filter(line -> line.contains("print"))
                    .count();

            System.out.println("file contains print lines count: " + count);
        } catch (IOException e) {
            System.err.println("files read with buffered err: " + e.getMessage());
        }
    }

}
