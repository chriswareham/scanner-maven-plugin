package net.chriswareham.scanner;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * This class implements an XML scanner output writer.
 */
public class ScannerOutputXmlWriter implements ScannerOutputWriter {
    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final ScannerContext context, final Writer writer) throws IOException {
        try {
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter streamWriter = outputFactory.createXMLStreamWriter(writer);

            streamWriter.writeStartDocument(context.getEncoding().toLowerCase(), "1.0");
            streamWriter.writeStartElement("scanner");

            streamWriter.writeStartElement("summary");
            streamWriter.writeCharacters(context.toString());
            streamWriter.writeEndElement();

            for (ScannerMatches matches : context.getMatches()) {
                streamWriter.writeStartElement("file");
                streamWriter.writeAttribute("name", matches.getFile().toString());
                streamWriter.writeAttribute("matches", Integer.toString(matches.getMatches().size()));

                streamWriter.writeStartElement("matches");

                for (ScannerMatch match : matches.getMatches()) {
                    streamWriter.writeStartElement("match");
                    streamWriter.writeAttribute("line", Integer.toString(match.getLine()));
                    streamWriter.writeAttribute("column", Integer.toString(match.getColumn()));
                    streamWriter.writeCharacters(match.getName());
                    streamWriter.writeEndElement();
                }

                streamWriter.writeEndElement();

                streamWriter.writeEndElement();
            }

            streamWriter.writeEndElement();

            streamWriter.close();
        } catch (XMLStreamException exception) {
            throw new IOException(exception.getMessage());
        }
    }
}
