package net.chriswareham.scanner;

import java.io.File;
import java.util.List;

/**
 * This class describes the scanner matches for a file.
 */
public class ScannerMatches {
    /**
     * The file that the scanner found matches in.
     */
    private final File file;

    /**
     * The matches the scanner found in a file.
     */
    private final List<ScannerMatch> matches;

    /**
     * Construct an instance of the scanner matches for a file.
     *
     * @param file the file that the scanner found matches in
     * @param matches the matches the scanner found in a file
     */
    public ScannerMatches(final File file, final List<ScannerMatch> matches) {
        this.file = file;
        this.matches = matches;
    }

    /**
     * Get the file that the scanner found matches in.
     *
     * @return the file that the scanner found matches in
     */
    public File getFile() {
        return file;
    }

    /**
     * Get the matches the scanner found in a file.
     *
     * @return the matches the scanner found in a file
     */
    public List<ScannerMatch> getMatches() {
        return matches;
    }

    /**
     * Get a description of the scanner matches for a file.
     *
     * @return a description of the scanner matches for a file
     */
    @Override
    public String toString() {
        return String.format("File %s contains %d match%s", file, matches.size(), matches.size() == 1 ? "" : "es");
    }
}
