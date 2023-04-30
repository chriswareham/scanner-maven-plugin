package net.chriswareham.scanner;

import java.util.HashMap;
import java.util.Map;

/**
 * This enumeration describes the valid scanner output formats.
 */
public enum ScannerOutputFormat {
    /**
     * The XML scanner output format.
     */
    XML("xml", new ScannerOutputXmlWriter()),
    /**
     * The plain scanner output format.
     */
    PLAIN("plain", new ScannerOutputPlainWriter());

    /**
     * The map of mnemonics to enumeration values.
     */
    private static final Map<String, ScannerOutputFormat> VALUES = new HashMap<>();

    static {
        for (ScannerOutputFormat value : values()) {
            VALUES.put(value.mnemonic, value);
        }
    }

    /**
     * Get whether a mnemonic is valid.
     *
     * @param mnemonic the mnemonic
     * @return whether the mnemonic is valid
     */
    public static boolean isMnemonic(final String mnemonic) {
        return VALUES.containsKey(mnemonic);
    }

    /**
     * Get the enumeration value for a mnemonic.
     *
     * @param mnemonic the mnemonic
     * @return the enumeration value for the mnemonic
     */
    public static ScannerOutputFormat valueOfMnemonic(final String mnemonic) {
        if (!VALUES.containsKey(mnemonic)) {
            throw new IllegalArgumentException("Invalid mnemonic " + mnemonic);
        }
        return VALUES.get(mnemonic);
    }

    /**
     * The mnemonic.
     */
    private final String mnemonic;

    /**
     * The output writer.
     */
    private final ScannerOutputWriter outputWriter;

    /**
     * Construct an enumeration value.
     *
     * @param mnemonic the mnemonic
     * @param outputWriter the output writer
     */
    ScannerOutputFormat(final String mnemonic, final ScannerOutputWriter outputWriter) {
        this.mnemonic = mnemonic;
        this.outputWriter = outputWriter;
    }

    /**
     * Get the mnemonic.
     *
     * @return the mnemonic
     */
    public String getMnemonic() {
        return mnemonic;
    }

    /**
     * Get the output writer.
     *
     * @return the output writer
     */
    public ScannerOutputWriter getOutputWriter() {
        return outputWriter;
    }
}
