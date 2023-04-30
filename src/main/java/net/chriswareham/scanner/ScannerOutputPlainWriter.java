package net.chriswareham.scanner;

import java.io.IOException;
import java.io.Writer;

/**
 * This class implements a plain scanner output writer.
 */
public class ScannerOutputPlainWriter implements ScannerOutputWriter {
    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final ScannerContext context, final Writer writer) throws IOException {
        String lineSeparator = System.lineSeparator();

        writer.write(context.toString());
        writer.write(lineSeparator);

        for (ScannerMatches matches : context.getMatches()) {
            writer.write(matches.toString());
            writer.write(lineSeparator);

            for (ScannerMatch match : matches.getMatches()) {
                writer.write(match.toString());
                writer.write(lineSeparator);
            }
        }
    }
}
