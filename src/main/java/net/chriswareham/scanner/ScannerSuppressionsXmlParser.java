package net.chriswareham.scanner;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class provides an XML parser for scanner suppressions.
 */
public class ScannerSuppressionsXmlParser {
    /**
     * Parse scanner suppressions from an XML input stream.
     *
     * @param xml the XML input stream to parse the scanner suppressions from
     * @return the scanner suppressions parsed from the XML input stream
     * @throws IOException if an error occurs
     */
    public Map<File, Set<String>> parse(final Reader xml) throws IOException {
        try {
            ScannerSuppressionsHandler handler = new ScannerSuppressionsHandler();

            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser parser = parserFactory.newSAXParser();
            parser.parse(new InputSource(xml), handler);

            return handler.getSuppressions();
        } catch (SAXException | ParserConfigurationException | IOException exception) {
            throw new IllegalStateException("Failed to parse patterns");
        }
    }

    /**
     * This class provides a handler for parsing suppression patterns.
     */
    private static class ScannerSuppressionsHandler extends DefaultHandler {
        /**
         * The suppressions element.
         */
        private static final String SUPPRESSIONS = "suppressions";

        /**
         * The suppression element.
         */
        private static final String SUPPRESSION = "suppression";

        /**
         * The file attribute.
         */
        private static final String FILE = "file";

        /**
         * The pattern element.
         */
        private static final String PATTERN = "pattern";

        /**
         * The pattern name attribute.
         */
        private static final String NAME = "name";

        /**
         * The current file.
         */
        private File file;

        /**
         * The current pattern names.
         */
        private Set<String> names;

        /**
         * The parsed scanner suppressions.
         */
        private Map<File, Set<String>> suppressions;

        /**
         * Get the parsed scanner suppressions.
         *
         * @return the parsed scanner patterns
         */
        public Map<File, Set<String>> getSuppressions() {
            return suppressions;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void startDocument() throws SAXException {
            suppressions = new HashMap<>();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void endDocument() throws SAXException {
            if (suppressions.isEmpty()) {
                throw new SAXException("No suppressions parsed");
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
            switch (qName) {
            case SUPPRESSIONS:
                break;
            case SUPPRESSION:
                String fileName = atts.getValue(FILE);
                if (fileName == null || fileName.isBlank()) {
                    throw new SAXException("Missing file attribute");
                }
                file = new File(fileName).getAbsoluteFile();
                if (suppressions.containsKey(file)) {
                    throw new SAXException("Duplicate file '" + file + "'");
                }
                suppressions.put(file, names = new HashSet<>());
                break;
            case PATTERN:
                String name = atts.getValue(NAME);
                if (name == null || name.isBlank()) {
                    throw new SAXException("Missing pattern name attribute for file '" + file + "'");
                }
                if (names.contains(name)) {
                    throw new SAXException("Duplicate pattern name '" + name + "' for file '" + file + "'");
                }
                names.add(name);
                break;
            default:
                throw new SAXException("Invalid element '" + qName + "'");
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {
            switch (qName) {
            case SUPPRESSIONS:
                break;
            case SUPPRESSION:
                if (names.isEmpty()) {
                    throw new SAXException("No suppressions for file '" + file + "'");
                }
                break;
            case PATTERN:
                break;
            default:
                throw new SAXException("Invalid element '" + qName + "'");
            }
        }
    }
}
