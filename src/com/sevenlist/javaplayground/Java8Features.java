package com.sevenlist.javaplayground;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Repeatable;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Locale.LanguageRange;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Java is not quite a functional language as it uses functional interfaces (nominal typing).
 */
public class Java8Features {

    private final Person[] persons = new Person[] { new Person("Seven", "Map"), new Person("Seven", "List") };

    /**
     * The point of all lambdas is deferred execution.
     * <p>
     * Can consist of:
     * <ul>
     * <li>1. block of code</li>
     * <li>2. parameters</li>
     * <li>3. values of the "free parameters" that have been "captured" (= vars that are not passed as params and are not defined inside the code)</li>
     * </ul>
     * Block of code + free variables = closure (enclosing scope), i.e. a Java lambda expression is a closure (as well as an inner class is a closure)
     * <p>
     * Captured variables may not change (being effectively "final" w/o the need for that modifier) as this not would be thread-safe.
     * <p>
     * "this" refers to the "this" parameter of the method that creates the lambda
     */
    public void lambdaExpressions() {
        // single line
        Comparator<String> stringComparator = (String first, String second) -> Integer.compare(first.length(), second.length());

        // with inferred parameter types
        Comparator<String> stringComparator2 = (first, second) -> Integer.compare(first.length(), second.length());
        // a better usage would be: Comparator.comparing(String::length)

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

    /**
     * The {@link FunctionalInterface} annotation checks that a single abstract method is available. A special statement
     * will be visible in Javadoc as well.
     * <p>
     * For common functional interfaces see package {@link java.util.function}.
     * <p>
     * The usual name for the abstract method is "apply".
     * <p>
     * Remember: producer-extends, consumer-super (PECS).
     * <ul>
     * <li>Reading is covariant (subtypes are ok: List<? extends Person>)</li>
     * <li>Writing is contravariant (supertypes are ok: List<? super Employee>)</li>
     * <li>General rule: "super" for argument types, "extends" for return types</li>
     * </ul>
     */
    @FunctionalInterface
    private interface Greeter {

        String sayHelloTo(String name);

        /**
         * Can be overridden
         * <p>
         * Pattern "interface + abstract class" is no longer needed
         * <p>
         * If the method is defined elsewhere in the hierarchy:
         * <p>
         * 1. (super)classes win with their implementation! the default implementation will be ignored = compatability with Java 7
         * <p>
         * 2. Two interfaces with the same method? (at least one has the "default" modifier) -> the conflict has to be
         * resolved by overriding the method in the interface implementing class, choosing e.g. the to-use-default-implementation with
         * e.g. Greeter.super.sayHi();
         */
        default void sayHi() {
            System.out.println("Hi!");
        }

        /**
         * No need for utility methods in companion classes anymore
         */
        static void factoryMethodOrHigherOrderFunction() {
        }
    }

    public void biFunction() {
        BiFunction<String, String, String> function = (s1, s2) -> s1 + " " + s2 + "!";
        function.apply("Hello", "World");
    }

    /**
     * Also possible: this:: instanceMethod, super::instanceMethod, EnclosingClass.this::method, and EnclosingClass.super::method.
     */
    public void methodReferences() {
        // object::instanceMethod   x -> System.out.println(x)
        new Button().setOnAction(System.out::println);

        // Class::instanceMethod    (x, y) -> x.compareToIgnoreCase(y)
        Arrays.sort(new String[] { "b", "a" }, String::compareToIgnoreCase);

        // Class::staticMethod      (x, y) -> Math.pow(x, y)
        BiFunction<Double, Double, Double> f = Math::pow;
    }

    public void constructorReferences() {
        Stream<Button> buttonStream = Stream.of("OK", "Cancel").map(Button::new);

        Button[] buttons = buttonStream.toArray(Button[]::new);
    }

    public void optionalValues() {
        Optional<String> optionalValue = Optional.ofNullable("value");

        String value;
        value = optionalValue.orElse("otherValue"); // returns the optionalValue if present, otherwise returns other
        value = optionalValue.orElseGet(() -> "for calculating" + " another value");
        value = optionalValue.orElseThrow(IllegalArgumentException::new);

        Set<String> values = new HashSet<>();
        optionalValue.ifPresent(values::add);

        // If a value is present, apply the provided mapping function to it.
        // If the result is non-null, return an Optional describing the result. Otherwise return an empty Optional.
        Optional<Boolean> added = optionalValue.map(values::add);

        // flatMap(function) is like map(..) but it will not wrap the function's return value with an additional Optional
        // the return type of optionalValue.map(Optional::ofNullable) is Optional<Optional<String>> - you do not want that
        Optional<String> optionalString = optionalValue.flatMap(Optional::ofNullable);
    }

    public void createObjectStreams() {
        Stream<String> streamOfArray = Stream.of("seven", "seven", "list").distinct();

        Arrays.stream(persons, 0, 1); // from, to

        Stream<String> streamOfCollection = new ArrayList<String>().stream();

        // would be an infinite stream without calling limit(..)
        Stream<Double> streamOfOneHundredRandomNumbers = Stream.generate(Math::random).limit(100);

        // would be an infinite stream without calling limit(..)
        Stream<BigInteger> streamOfTwentyBigInts = Stream.iterate(BigInteger.ZERO, n -> n.add(BigInteger.ONE))
                .peek(System.out::println) // for debugging - can also be used after filter(..) or map(..)
                .limit(20);
    }

    /**
     * As usual, primitive type streams are more efficient then using streams with wrapped objects.
     */
    public void createPrimitiveTypeStreams(Stream<String> names) {
        // see also: LongStream, DoubleStream
        IntStream intStream = IntStream.of(1, 2, 3, 5, 8, 13);// range(..), rangeClosed(..)

        Stream<Integer> integerStream = intStream.boxed();

        IntStream lengthsOfNames = names.mapToInt(String::length);

        LongStream randomLongs = new Random().longs(); // ints(), doubles()
    }

    public void mapStreams(String words) {
        Stream<String> streamOfWords = Arrays.asList(words).stream();

        // here map(..) creates a stream of streams
        Stream<Stream<Character>> streamOfStreamOfCharacters = streamOfWords.map(w -> characterStream(w));

        // use flatMap(..) instead
        Stream<Character> streamOfCharacters = streamOfWords.flatMap(w -> characterStream(w));
    }

    private Stream<Character> characterStream(String s) {
        return IntStream.range(0, s.length()).mapToObj(s::charAt);
    }

    /**
     * Optional is returned because there is no valid result if the stream is empty.
     */
    public void reduceStreams(Stream<String> words, Stream<Integer> values) {
        Optional<String> largestWord = words.max(String::compareToIgnoreCase);

        Optional<String> firstStartingWithSev = words.filter(s -> s.startsWith("sev")).findFirst();

        Optional<String> anyStartingWithSev = words.parallel().filter(s -> s.startsWith("sev")).findAny();

        // see also allMatch(..) or noneMatch(..)
        boolean aWordStartingWithSev = words.parallel().anyMatch(s -> s.startsWith("sev"));

        // v0 op v1 op v2 op ... = vi op vi+1 0 = op(vi, vi+1)
        // operation should be associative to allow efficient reduction with parallel streams: (x op y) op z = x op (y op z)
        Optional<Integer> optionalSum = values.reduce((x, y) -> x + y); // better: values.reduce(Integer::sum)

        // using identity (e op x = x) there is no need to deal with Optional - the identity value will be returned if the stream is empty
        Integer sum = values.reduce(0, Integer::sum); // 0 is the identity for addition

        // calculate the total length of strings using parallalized computation:
        int identity = 0;
        BiFunction<Integer, String, Integer> accumulator = (total, word) -> total + word.length();
        BinaryOperator<Integer> combiner = (total1, total2) -> total1 + total2; // combines the results of the computations run in parallel
        words.parallel().reduce(identity, accumulator, combiner); // better: words.mapToInt(String::length).sum()
    }

    /**
     * All methods for collecting stream results are terminal operations, i.e. the stream cannot be used again after calling them.
     */
    public void collectStreamResultsAsArrayOrCollections(Stream<String> words) {
        String[] wordsAsArray = words.toArray(String[]::new);

        List<String> wordsAsList = words.collect(Collectors.toList());

        // using a supplier, accumulator, and combiner
        Set<String> wordsAsSet = words.parallel().collect(HashSet::new, HashSet::add, HashSet::addAll);
        // in short: words.collect(Collectors.toSet())

        Set<String> wordsAsTreeSet = words.collect(Collectors.toCollection(TreeSet::new));
    }

    public void collectStreamResultsAsMap() {
        Stream<Person> personStream = Arrays.stream(persons);

        Map<String, String> personIdToLastName = personStream.collect(Collectors.toMap(Person::getId, Person::getLastName));

        // Function.identity() returns the input argument, i.e. here the element of the stream which is Person
        Map<String, Person> idToPerson = personStream.collect(Collectors.toMap(Person::getId, Function.identity()));

        // if the mapped keys may have duplicates use:
        Stream<Person> personsWithEqualLastName = Arrays.asList(new Person("seven", "list"), new Person("eight", "list")).stream();
        Map<String, String> lastNameToFirstNames = personsWithEqualLastName.collect(Collectors.toMap(
                Person::getLastName,
                Person::getFirstName,
                (existingFirstName, newFirstName) -> existingFirstName + ", " + newFirstName));
        // see also: personsWithEqualLastName.collect(Collectors.groupingBy(Person::getLastName))

        Map<String, Person> idToPersonAsTreeMap = personStream.collect(Collectors.toMap(
                Person::getId,
                Function.identity(),
                (existingPerson, newPerson) -> {
                    throw new IllegalStateException();
                },
                TreeMap::new));
    }

    public void collectStreamResultsAsOtherTypes(Stream<String> words) {
        String commaSeparatedWords = words.collect(Collectors.joining(", "));

        String zeroToNineAsString = IntStream.rangeClosed(0, 9).mapToObj(Objects::toString).collect(Collectors.joining());

        IntSummaryStatistics summary = words.collect(Collectors.summarizingInt(String::length));
        double averageWordLength = summary.getAverage();
        double maxWordLength = summary.getMax();

        // remember: there is peek(..) to continue working with a stream
        words.forEach(System.out::println);
    }

    public void collectStreamResultsUsingGroupingByAndPartioningBy() {
        Stream<Locale> locales = Stream.of(Locale.getAvailableLocales());

        Map<String, List<Locale>> countryToLocaleList = locales.collect(Collectors.groupingBy(Locale::getCountry));
        // countryToLocales.get("CH") yields [it_CH, de_CH, fr_CH]

        Map<String, Set<Locale>> countryToLocaleSet = locales.collect(Collectors.groupingBy(Locale::getCountry, Collectors.toSet())); // downstream collector
        // using Collectors.toConcurrentMap() is analog to using groupingByConcurrent(..)

        // see also the downstream collectors: counting(), summingInt(..), minBy(..), maxBy(..)

        Map<String, Set<String>> countryToLanguagesAsSet = locales.collect(Collectors.groupingBy(
                l -> l.getDisplayCountry(),
                Collectors.mapping(l -> l.getDisplayLanguage(), Collectors.toSet())));

        Map<String, String> countryToLanguagesAsString = locales.collect(Collectors.groupingBy(
                l -> l.getDisplayCountry(),
                Collectors.mapping(l -> l.getDisplayLanguage(), Collectors.joining(", "))));

        Map<Boolean, List<Locale>> englishAndOtherLocales = locales.collect(Collectors.partitioningBy(l -> l.getLanguage().equals("en")));
        // englishAndOtherLocales.get(true) yields all english locales
    }

    /**
     * When the terminal method executes, all lazy intermediate stream operations will be parallelized.
     * <p>
     * Operations need to be (1) stateless and (2) executable in an arbitrary order.
     */
    public void parallelStreams() {
        Stream<String> writtenNumbers = Stream.of("one", "two", "three", "one");

        // parallelize a sequential stream
        writtenNumbers.parallel();

        // by default, most of the streams are ordered
        // making them unordered() speeds up parallel processing - e.g. distinct() or limit(..)
        // distinct() will not retaining the first of all equal elements anymore
        writtenNumbers.unordered().parallel().distinct();
    }

    /**
     * Since intermediate stream operations are lazy, it is possible to mutate the collection up to the point when the terminal operation executes.
     */
    public void modifyingTheCollectionBackingTheStream(List<String> namesList) {
        Stream<String> namesStream = namesList.stream();
        namesList.add("sevenlist"); // allowed
        namesStream.count();

        // This breaks noninterference!
        namesList.stream().forEach(name -> {
            if (name.length() > 13) {
                namesList.remove(name); // throws ConcurrentModificationException
            }
        });
    }

    /**
     * To sort a Collection use {@link Collections#sort}.
     */
    public void sortStreamElements(Stream<String> names) {
        Stream<String> longestNamesFirst = names.sorted(Comparator.comparing(String::length).reversed());
    }

    public <T> Stream<T> combineStreams(Stream<? extends T> stream1, Stream<? extends T> stream2) {
        return Stream.concat(stream1, stream2);
    }

    public void skipStreamElements(String content) {
        Stream<String> words = Stream.of(content.split("[\\P{L}]+")).skip(2);
    }

    public void splitAsStream(String content) {
        Stream<String> words = Pattern.compile("[\\P{L}]+").splitAsStream(content);
    }

    /**
     * See Evernote "Date and Time".
     */
    public void dateAndTime() {
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

    public void readLinesFromFile() throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get("/home/sevenlist/file.txt"))) {
            Optional<String> passwordEntry = lines.filter(s -> s.contains("password")).findFirst();
        }
    }

    /**
     * Unfortunately, the Scanner class has no lines() method.
     */
    public void readLinesFromURL(URL url) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            Stream<String> lines = reader.lines();
        }
    }

    /**
     * Does not enter subdirectories - use method walk(..) or find(..) instead.
     * <p>
     * DirectoryStream has nothing to do with Java 8 streams.
     */
    public void listFiles() throws IOException {
        try (Stream<Path> directoryEntries = Files.list(Paths.get("/home/sevenlist"))) {
        }
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
        // sort by first name then last name; returns: Seven List, Seven Map
        Arrays.sort(persons, Comparator.comparing(Person::getFirstName).thenComparing(Person::getLastName));

        // sort by length of last name; returns: Seven Map, Seven List
        Arrays.sort(persons, Comparator.comparingInt(p -> p.getLastName().length()));

        // sort by first name: list nulls first and then all by natural order; returns: null List, Seven Map, Seven List
        Person nullList = new Person(null, "List");
        Person[] threePersons = new Person[] { persons[0], persons[1], nullList };
        Arrays.sort(threePersons, Comparator.comparing(Person::getFirstName, Comparator.nullsFirst(Comparator.naturalOrder())));
        // see also reverseOrder()
    }

    private static class Person {

        private final String id = UUID.randomUUID().toString();
        private final String firstName;
        private final String lastName;

        public Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        // Here, Persons are equal if they have an equal last name!
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Person person = (Person) o;
            return Objects.equals(lastName, person.lastName);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(lastName);
        }

        @Override
        public String toString() {
            return "Person{" +
                    "id='" + id + '\'' +
                    ", firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    '}';
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
