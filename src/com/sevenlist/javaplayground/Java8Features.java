package com.sevenlist.javaplayground;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

import java.lang.annotation.Repeatable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Locale.LanguageRange;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Java8Features {

    /**
     * Can consist of:
     * 1. block of code
     * 2. parameters
     * 3. values of the "free parameters" that have been "captured" (= vars that are not passed as params and are not defined inside the code)
     * <p>
     * block of code + free variables = closure (enclosing scope), i.e. a Java lambda expression is a closure (as well as an inner class is a closure)
     * <p>
     * captured variables may not change (being effectively "final" w/o the need for that modifier) as this not would be thread-safe
     * <p>
     * "this" refers to the "this" parameter of the method that creates the lambda
     */
    public void lambdaExpressions() {
        // single line
        Comparator<String> stringComparator = (String first, String second) -> Integer.compare(first.length(), second.length());

        // with inferred parameter types
        Comparator<String> stringComparator2 = (first, second) -> Integer.compare(first.length(), second.length());

        // multiple lines
        ((Greeter) name -> {
            String msg = "Hello, " + name + "!";
            Logger.getGlobal().info(msg);
            return msg;
        }).sayHelloTo("sevenlist");

        // without parameters
        Runnable runnable = () -> System.out.println("Hi!");

        // method has single parameter: parameter parantheses can be omitted
        EventHandler<ActionEvent> listener = event -> System.out.println("Thanks for clicking!");
    }

    @FunctionalInterface
    // This annotation checks that a single abstract method is available + Javadoc statement.
    private interface Greeter {
        String sayHelloTo(String name);

        /**
         * can be overridden
         * <p>
         * pattern "interface + abstract class" is no longer needed
         * <p>
         * if the method is defined elsewhere in the hierarchy:
         * <p>
         * 1. (super)classes win with their implementation! the default implementation will be ignored = compatability with Java 7
         * <p>
         * 2. two interfaces with the same method? (at least one has the "default" modifier) -> the conflict has to be
         * resolved by overriding the method in the interface implementing class, choosing e.g. the to-use-default-implementation with
         * e.g. Greeter.super.sayHi();
         */
        default void sayHi() {
            System.out.println("Hi!");
        }
    }

    public void biFunction() {
        BiFunction<String, String, String> function = (s1, s2) -> s1 + " " + s2 + "!";
        function.apply("Hello", "World");
    }

    /**
     * Also possible: super::instanceMethod, EnclosingClass.this::method, and EnclosingClass.super::method.
     */
    public void methodReferences() {
        // object::instanceMethod   x -> System.out.println(x)
        new Button().setOnAction(System.out::println);

        // Class::instanceMethod    (x, y) -> x.compareToIgnoreCase(y)
        Arrays.sort(new String[]{"b", "a"}, String::compareToIgnoreCase);

        // Class::staticMethod      (x, y) -> Math.pow(x, y)
        BiFunction<Double, Double, Double> f = Math::pow;
    }

    public void constructorReferences() {
        Stream<Button> buttonStream = Stream.of("OK", "Cancel").map(Button::new);

        Button[] buttons = buttonStream.toArray(Button[]::new);
    }

    public void generalizedTargetTypeInference() {
        List<String> names = Collections.emptyList(); // instead of: Collections.<String>emptyList()
    }

    /**
     * The opposite of String.split(..).
     */
    public void joinStrings() {
        String.join(", ", "seven", "list"); // returns "seven, list"
    }

    public void convenienceFeaturesOfNumericClasses() {
        int bytes = Integer.valueOf(5).BYTES;

        Integer.hashCode(5); // no need for boxing

        // for reduction functions in streams
        Integer.sum(1, 2);
        Long.max(3, 4);
        Float.min(1.2f, 1.1f);
        Boolean.logicalOr(true, false); // logicalAnd(..), logicalXor(..)

        BigInteger.valueOf(7).byteValueExact();
    }

    /**
     * For file formats or network protocols that require them.
     */
    public void unsignedValues() {
        Byte.toUnsignedInt((byte) 246); // Byte, Short, and Integer also provide toUnsignedLong(..).
        Integer.compareUnsigned(Integer.MAX_VALUE + 1, Integer.MAX_VALUE + 2);
        // divideUnsigned(..), remainedUnsigned(..)
    }

    public void comparators() {
        Person sevenList = new Person("Seven", "List");
        Person sevenMap = new Person("Seven", "Map");
        Person[] persons = {sevenMap, sevenList};

        // sort by first name then last name; returns: Seven List, Seven Map
        Arrays.sort(persons, Comparator.comparing(Person::getFirstName).thenComparing(Person::getLastName));

        // sort by length of last name; returns: Seven Map, Seven List
        Arrays.sort(persons, Comparator.comparingInt(p -> p.getLastName().length()));

        // sort by first name: list nulls first and then all by natural order; returns: null List, Seven Map, Seven List
        Person nullList = new Person(null, "List");
        persons = new Person[]{sevenMap, sevenList, nullList};
        Arrays.sort(persons, Comparator.comparing(Person::getFirstName, Comparator.nullsFirst(Comparator.naturalOrder()))); // see also reverseOrder()
    }

    private static class Person {

        private final String firstName;
        private final String lastName;

        public Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }

    public void navigableAndCheckedSet() {
        NavigableSet s = new TreeSet();
        s.addAll(Arrays.asList(1, 2, 3));

        s.lower(2); // returns: 1

        Collections.checkedNavigableSet(s, Integer.class);
        s.add("4"); // throws ClassCastException
    }

    public void encodeWithBase64() {
        String original = "username" + ":" + "password";
        Base64.getEncoder().encode(original.getBytes(StandardCharsets.UTF_8));
        // see also decode(..) or wrap(..)
    }

    @Author(name = "seven")
    @Author(name = "list")
    public void annotateWithRepeatableAnnotation() {
    }

    @Repeatable(Authors.class)
    private @interface Author {
        String name();
    }

    private @interface Authors {
        Author[] value();
    }

    /**
     * Annotations can be applied to any type uses: new, type casts, instanceof checks, generic type arguments, implements, throws.
     * <p>
     * Class literals and import statements cannot be annotated.
     */
    public void annotateTypeUse() {
        List</* @NonNull */ String> names = new ArrayList<>();
    }

    /**
     * Requires a compilation like javac -parameters SourceFile.java.
     */
    public void reflectMethodParameter(String param) throws Exception {
        Java8Features.class.getDeclaredMethod("reflectMethodParameter", String.class).getParameters()[0].getName(); // returns "param"
    }

    public void lazyMessage() {
        // only constructs the message when the finest log level is active
        Logger.getGlobal().finest(() -> "a message that is expensive to construct");
    }

    public void regExNamedCapturingGroup() {
        Pattern pattern = Pattern.compile("(?<city>[\\p{L} ]+),\\s*(?<state>[A-Z]{2})");
        Matcher matcher = pattern.matcher("New York City, NY");
        if (matcher.matches()) {
            String city = matcher.group("city");
        }
    }

    public void findMatchingLocales() {
        List<LanguageRange> ranges = Stream.of("de", "*-CH")
                .map(LanguageRange::new)
                .collect(Collectors.toList());

        // or simply:
        // List<LanguageRange> ranges = Arrays.asList(new LanguageRange("de"), new LanguageRange("*-CH"));

        List<Locale> matches = Locale.filter(ranges, Arrays.asList(Locale.getAvailableLocales()));
        // contains: de, de_CH, de_AT, de_LU, de_DE, de_GR, fr_CH, it_CH
    }
}
