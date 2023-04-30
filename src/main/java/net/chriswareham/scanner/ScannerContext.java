package net.chriswareham.scanner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class describes the context for a scanner.
 */
public class ScannerContext {
    /**
     * The encoding to use when reading files.
     */
    private final String encoding;

    /**
     * The output format.
     */
    private final ScannerOutputFormat outputFormat;

    /**
     * The includes for files to scan.
     */
    private final List<String> includes;

    /**
     * The scanner suppressions.
     */
    private final Map<File, Set<String>> suppressions;

    /**
     * The scanner patterns.
     */
    private final List<ScannerPattern> patterns;

    /**
     * The scanner matches.
     */
    private final List<ScannerMatches> matches;

    /**
     * Construct an instance of the context for a scanner.
     *
     * @param encoding the encoding to use when reading files
     * @param outputFormat the output format
     * @param includes the includes for files to scan
     * @param suppressions the scanner suppressions
     * @param patterns the scanner patterns
     */
    public ScannerContext(
        final String encoding,
        final ScannerOutputFormat outputFormat,
        final List<String> includes,
        final Map<File, Set<String>> suppressions,
        final List<ScannerPattern> patterns
    ) {
        this.encoding = encoding;
        this.outputFormat = outputFormat;
        this.includes = includes;
        this.suppressions = suppressions;
        this.patterns = patterns;
        this.matches = new ArrayList<>();
    }

    /**
     * Get the encoding to use when reading files.
     *
     * @return the encoding to use when reading files
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Get the output format.
     *
     * @return the output format
     */
    public ScannerOutputFormat getOutputFormat() {
        return outputFormat;
    }

    /**
     * Get the includes for files to scan.
     *
     * @return the includes for files to scan
     */
    public List<String> getIncludes() {
        return includes;
    }

    /**
     * Get the scanner suppressions.
     *
     * @return the scanner suppressions
     */
    public Map<File, Set<String>> getSuppressions() {
        return suppressions;
    }

    /**
     * Get the scanner patterns.
     *
     * @return the scanner patterns
     */
    public List<ScannerPattern> getPatterns() {
        return patterns;
    }

    /**
     * Get whether the scanner matches is empty.
     *
     * @return whether the scanner matches is empty
     */
    public boolean isMatchesEmpty() {
        return matches.isEmpty();
    }

    /**
     * Get the scanner matches.
     *
     * @return the scanner matches
     */
    public List<ScannerMatches> getMatches() {
        return matches;
    }

    /**
     * Add the scanner matches for a file.
     *
     * @param scannerMatches the scanner matches for a file
     */
    public void addMatches(final ScannerMatches scannerMatches) {
        matches.add(scannerMatches);
    }

    /**
     * Get a description of the scanner context.
     *
     * @return a description of the scanner context
     */
    @Override
    public String toString() {
        return String.format("Scanned %d files with matches", matches.size());
    }
}
