package com.sevenlist.javaplayground;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Java7Features {

    /**
     * https://docs.oracle.com/javase/7/docs/technotes/guides/language/binary-literals.html
     */
    public void binaryLiterals() {
        byte aByte = (byte) 0b00100001; // 8
        short aShort = (short) 0b1010000101000101; // 16
        int anInt = 0b10100001010001011010000101000101; // 32
        long aLong = 0b1010000101000101101000010100010110100001010001011010000101000101L; // 64, suffix L
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
    public void catchingMultipleExceptionTypes() {
        try {
            Class.forName(Java7Features.class.getName()).getMethod("main").invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * New base exception class: ReflectiveOperationException.
     */
    public void easierExceptionHandlingForReflectiveMethods() {
        try {
            Class.forName(Java7Features.class.getName()).getMethod("main").invoke(null);
        } catch (ReflectiveOperationException e) {
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

    public void readingAndWritingFiles() throws IOException {
        Path path = Paths.get("/home/sevenlist/names.txt");

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
}
