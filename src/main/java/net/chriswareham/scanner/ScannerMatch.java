package net.chriswareham.scanner;

/**
 * This class describes a scanner match.
 */
public class ScannerMatch {
    /**
     * The name of the pattern that matched.
     */
    private final String name;

    /**
     * The line number of the match.
     */
    private final int line;

    /**
     * The column number of the match.
     */
    private final int column;

    /**
     * Construct an instance of a scanner match.
     *
     * @param name the name of the pattern that matched
     * @param line the line number of the match
     * @param column the column number of the match
     */
    public ScannerMatch(final String name, final int line, final int column) {
        this.name = name;
        this.line = line;
        this.column = column;
    }

    /**
     * Get the name of the pattern that matched.
     *
     * @return the name of the pattern that matched
     */
    public String getName() {
        return name;
    }

    /**
     * Get the line number of the match.
     *
     * @return the line number of the match
     */
    public int getLine() {
        return line;
    }

    /**
     * Get the column number of the match.
     *
     * @return the column number of the match
     */
    public int getColumn() {
        return column;
    }

    /**
     * Get a description of the scanner match.
     *
     * @return a description of the scanner match
     */
    @Override
    public String toString() {
        return String.format("Line %d column %d matches %s", line, column, name);
    }
}
