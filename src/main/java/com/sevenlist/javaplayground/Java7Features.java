package com.sevenlist.javaplayground;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Java7Features {

    private String str;

    /**
     * https://docs.oracle.com/javase/7/docs/technotes/guides/language/binary-literals.html
     */
    public void binaryLiterals() {
        byte aByte = (byte) 0b00100001; // 8 bit
        short aShort = (short) 0b1010000101000101; // 16 bit
        int anInt = 0b10100001010001011010000101000101; // 32 bit
        long aLong = 0b1010000101000101101000010100010110100001010001011010000101000101L; // 64 bit, suffix L
    }

    /**
     * https://docs.oracle.com/javase/7/docs/technotes/guides/language/underscores-literals.html
     * <p>
     * Underscore can appear anywhere between digits in a numerical literal -> improves readability.
     */
    public void underscoresInNumericLiterals() {
        long creditCardNumber = 1234_5678_9012_3456L;
        long socialSecurityNumber = 999_99_9999L;
        float pi = 3.14_15F;
        long hexBytes = 0xFF_EC_DE_5E;
        long hexWords = 0xCAFE_BABE;
        long maxLong = 0x7fff_ffff_ffff_ffffL;
        byte nybbles = 0b0010_0101;
        long bytes = 0b11010010_01101001_10010100_10010010;
    }

    /**
     * https://docs.oracle.com/javase/7/docs/technotes/guides/language/strings-switch.html
     */
    public String stringInSwitchStatement() {
        String dayOfWeek = "Thursday";
        switch (dayOfWeek) {
            case "Monday":
                return "Start of work week";
            case "Tuesday":
            case "Wednesday":
            case "Thursday":
                return "Midweek";
            case "Friday":
                return "End of work week";
            case "Saturday":
            case "Sunday":
                return "Weekend";
            default:
                throw new IllegalArgumentException("Invalid day of the week: " + dayOfWeek);
        }
    }

    /**
     * https://docs.oracle.com/javase/7/docs/technotes/guides/language/type-inference-generic-instance-creation.html
     */
    public void typeInferenceForGenericInstanceCreation() {
        Map<String, List<String>> myMap = new HashMap<>(); // instead of: new HashMap<String, List<String>>();
    }

    /**
     * https://docs.oracle.com/javase/7/docs/technotes/guides/language/try-with-resources.html
     * <p>
     * The close() method of an AutoCloseable object is called automatically when exiting a try-with-resources block.
     * <p>
     * Also consider "suppressed" exceptions.
     */
    public void tryWithResourcesStatement() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("~/myFile.txt"))) {
            br.readLine();
        }
    }

    /**
     * https://docs.oracle.com/javase/7/docs/technotes/guides/language/catch-multiple.html
     * <p>
     * Also consider "Rethrowing Exceptions with More Inclusive Type Checking".
     */
    public void catchMultipleExceptionTypes() {
        try {
            Class.forName(Java7Features.class.getName()).getMethod("main").invoke(null);
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * New base exception class: ReflectiveOperationException.
     */
    public void easierExceptionHandlingForReflectiveMethods() {
        try {
            Class.forName(Java7Features.class.getName()).getMethod("main").invoke(null);
        }
        catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Dealing with legacy APIs: Path.toFile() or File.toPath().
     */
    public void pathInterfaceReplacesFileObject() {
        Path absolute = Paths.get("/", "home", "sevenlist");
        Path relative = Paths.get("subdir", "next-subdir", "some.properties");
        Path homeDirectory = Paths.get("/home/sevenlist");

        Path propertiesPath = homeDirectory.resolve("subdir/nextsubdir/some.properties");

        homeDirectory.resolveSibling("acme"); // yields "/home/acmce"

        Paths.get("/home/sevenlist").relativize(Paths.get("/home/acme/foo")); // yields "../acme/foo"

        Paths.get("/home/sevenlist/../acme/./foo").normalize(); // yields "/home/acme/foo"

        homeDirectory.getParent();
        propertiesPath.getFileName();
        absolute.getRoot();
    }

    public void readAndWriteSmallFiles() throws IOException {
        Path path = Paths.get("/home/sevenlist/small-file.txt");

        // read
        byte[] bytes = Files.readAllBytes(path);
        String content = new String(bytes, StandardCharsets.UTF_8);

        // write
        Files.write(path, content.getBytes(StandardCharsets.UTF_8));

        // read lines
        List<String> lines = Files.readAllLines(path);

        // write lines
        Files.write(path, lines);

        // append lines to a given file
        Files.write(path, lines, StandardOpenOption.APPEND);
    }

    public void readAndWriteLargeFiles() throws IOException {
        Path path = Paths.get("/home/sevenlist/large-file.bin");
        Reader reader = Files.newBufferedReader(path);
        Writer writer = Files.newBufferedWriter(path);

        InputStream in = Files.newInputStream(path);
        OutputStream out = Files.newOutputStream(path);
    }

    public void saveInputStreamDataAsFile() throws IOException {
        InputStream in = new URL("http://www.google.de").openStream();
        Path path = Paths.get("/home/sevenlist/index.html");
        Files.copy(in, path);
    }

    public void writeFileContentsToOutputStream() throws IOException {
        Path path = Paths.get("/home/sevenlist/index.html");
        Files.copy(path, System.out);
    }

    public void createFilesAndDirectories() throws IOException {
        Path path = Paths.get("/exists/does-not-exist");
        Files.createDirectory(path);

        path = Paths.get("/does-not-exist-1/does-not-exist-2");
        Files.createDirectories(path);

        path = Paths.get("file.txt");
        Files.createFile(path);

        Files.createTempFile(null, ".txt"); // might return /tmp/1234405522364837194.txt as Path
    }

    /**
     * Delete a nonempty directory: see {@link FileVisitor}.
     */
    public void copyMoveDeleteFiles() throws IOException {
        Path fromPath = Paths.get("/from/path/file.txt");
        Path toPath = Paths.get("/to/path/file.txt");

        Files.move(fromPath, toPath, StandardCopyOption.ATOMIC_MOVE);

        Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);

        Path path = Paths.get("/a/file");
        boolean deleted = Files.deleteIfExists(path); // delete(..) throws an exception instead
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Java7Features other = (Java7Features) o;
        return Objects.equals(str, other.str); // short for: str != null ? str.equals(other.str) : other.str == null;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(str); // or: Objects.hash(one, two, three);
    }

    public void callToStringInANullSafeWay() {
        String s = null;
        String.valueOf(s); // instead of: if (s != null) s.toString(); -> returns "null"
        Objects.toString(s, ""); // --> returns "" instead of "null"
    }

    public void compareNumbericTypes() {
        Integer.compare(-1, Integer.MIN_VALUE); // x - y would cause a numeric overflow instead
    }

    public void logMessage() {
        Logger.getGlobal().info("message");
    }

    public void checkForNull() {
        Object obj = null;
        Objects.requireNonNull(obj, "obj must not be null");
    }

    public void startProcess() throws Exception {
        ProcessBuilder builder = new ProcessBuilder("grep", "-o", "[A-Za-z_][A-Za-z_0-9]*");
        builder.redirectInput(Paths.get("src/com/sevenlist/javaplayground/Java7Features.java").toFile());
        builder.redirectOutput(Paths.get("identifiers.txt").toFile());
        Process process = builder.start();
        boolean completed = process.waitFor(3, TimeUnit.SECONDS); // from Java SE 8

        builder = new ProcessBuilder("ls", "-al");
        builder.inheritIO(); // sets the stdin, stdout, and stderr streams of the process to those of the Java program
        builder.start().waitFor();
    }

    public void useTryWithResourcesStatementWhenUsingURLClassLoader() throws Exception {
        URL[] urls = {
                new URL("file:junit-4.11.jar"),
                new URL("file:hamcrest-core-1.3.jar")
        };
        try (URLClassLoader loader = new URLClassLoader(urls)) {
            Class<?> klass = loader.loadClass("org.junit.runner.JUnitCore");
        }
    }

    public void bitSet() {
        byte firstByte = (byte) 0b10101100; // has the 2nd, 3rd, 5th, and 7th bit set
        byte secondByte = (byte) 0b00101000; // has the 11th and 13th bit set
        byte[] bytes = { firstByte, secondByte }; // little-endian (bytes from left to right)
        BitSet.valueOf(bytes); // = {2, 3, 5, 7, 11, 13} bits are set
    }
}
