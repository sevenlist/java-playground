package com.sevenlist.javaplayground;

import java.lang.annotation.Repeatable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Java8Features {

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
    public void useUnsignedValues() {
        Byte.toUnsignedInt((byte) 246); // Byte, Short, and Integer also provide toUnsignedLong(..).
        Integer.compareUnsigned(Integer.MAX_VALUE + 1, Integer.MAX_VALUE + 2);
        // divideUnsigned(..), remainedUnsigned(..)
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
    @interface Author {
        String name();
    }

    @interface Authors {
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

    public void useLazyMessage() {
        // only constructs the message when the finest log level is active
        Logger.getGlobal().finest(() -> "a message that is expensive to construct");
    }

    public void useRegExNamedCapturingGroup() {
        Pattern pattern = Pattern.compile("(?<city>[\\p{L} ]+),\\s*(?<state>[A-Z]{2})");
        Matcher matcher = pattern.matcher("New York City, NY");
        if (matcher.matches()) {
            String city = matcher.group("city");
        }
    }
}
