package net.chriswareham.scanner;

import java.io.IOException;
import java.io.Writer;

/**
 * This interface is implemented by scanner output writers.
 */
public interface ScannerOutputWriter {
    /**
     * Write scanner output.
     *
     * @param context the scanner context
     * @param writer the writer
     * @throws IOException if an error occurs
     */
    void write(ScannerContext context, Writer writer) throws IOException;
}
