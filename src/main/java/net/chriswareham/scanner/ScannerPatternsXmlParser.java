package net.chriswareham.scanner;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class provides an XML parser for scanner patterns.
 */
public class ScannerPatternsXmlParser {
    /**
     * Parse scanner patterns from an XML input stream.
     *
     * @param xml the XML input stream to parse the scanner patterns from
     * @return the scanner patterns parsed from the XML input stream
     * @throws IOException if an error occurs
     */
    public List<ScannerPattern> parse(final Reader xml) throws IOException {
        try {
            ScannerPatternsHandler handler = new ScannerPatternsHandler();

            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser parser = parserFactory.newSAXParser();
            parser.parse(new InputSource(xml), handler);

            return handler.getPatterns();
        } catch (SAXException | ParserConfigurationException | IOException exception) {
            throw new IllegalStateException("Failed to parse patterns");
        }
    }

    /**
     * This class provides a handler for parsing scanner patterns.
     */
    private static class ScannerPatternsHandler extends DefaultHandler {
        /**
         * The patterns element.
         */
        private static final String PATTERNS = "patterns";

        /**
         * The pattern element.
         */
        private static final String PATTERN = "pattern";

        /**
         * The name attribute.
         */
        private static final String NAME = "name";

        /**
         * The buffered characters.
         */
        private StringBuilder buf;

        /**
         * The name of the current pattern.
         */
        private String name;

        /**
         * The parsed scanner patterns.
         */
        private Map<String, ScannerPattern> patterns;

        /**
         * Get the parsed scanner patterns.
         *
         * @return the parsed scanner patterns
         */
        public List<ScannerPattern> getPatterns() {
            return new ArrayList<>(patterns.values());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void startDocument() throws SAXException {
            patterns = new LinkedHashMap<>();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void endDocument() throws SAXException {
            if (patterns.isEmpty()) {
                throw new SAXException("No patterns parsed");
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
            switch (qName) {
            case PATTERNS:
                break;
            case PATTERN:
                name = atts.getValue(NAME);
                if (name == null || name.isBlank()) {
                    throw new SAXException("Missing pattern name attribute");
                }
                if (patterns.containsKey(name)) {
                    throw new SAXException("Duplicate pattern name '" + name + "'");
                }
                startBuf();
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
            case PATTERNS:
                break;
            case PATTERN:
                String characters = stopBuf();
                if (characters.isBlank()) {
                    throw new SAXException("Missing pattern '" + name + "'");
                }
                try {
                    Pattern pattern = Pattern.compile(characters);
                    patterns.put(name, new ScannerPattern(name, pattern));
                } catch (PatternSyntaxException exception) {
                    throw new SAXException("Invalid pattern '" + name + "'");
                }
                break;
            default:
                throw new SAXException("Invalid element '" + qName + "'");
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void characters(final char[] ch, final int off, final int len) throws SAXException {
            if (buf != null) {
                buf.append(ch, off, len);
            }
        }

        /**
         * Start buffering characters.
         */
        private void startBuf() {
            buf = new StringBuilder();
        }

        /**
         * Stop buffering characters.
         *
         * @return the characters that have been buffered
         */
        private String stopBuf() {
            String characters = buf.toString();
            buf = null;
            return characters;
        }
    }
}
