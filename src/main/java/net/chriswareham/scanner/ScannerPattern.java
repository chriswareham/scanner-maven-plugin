package net.chriswareham.scanner;

import java.util.regex.Pattern;

/**
 * This class describes a scanner pattern.
 */
public class ScannerPattern {
    /**
     * The name.
     */
    private final String name;

    /**
     * The pattern.
     */
    private final Pattern pattern;

    /**
     * Construct an instance of a scanner pattern.
     *
     * @param name the name
     * @param pattern the pattern
     */
    public ScannerPattern(final String name, final Pattern pattern) {
        this.name = name;
        this.pattern = pattern;
    }

    /**
     * Get the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the pattern.
     *
     * @return the pattern
     */
    public Pattern getPattern() {
        return pattern;
    }

    /**
     * Get a description of the scanner pattern.
     *
     * @return a description of the scanner pattern
     */
    @Override
    public String toString() {
        return name;
    }
}
