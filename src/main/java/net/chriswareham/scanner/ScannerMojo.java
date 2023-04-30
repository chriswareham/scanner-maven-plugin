package net.chriswareham.scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * This class implements a scanner.
 */
@Mojo(name = "scanner", threadSafe = true)
public class ScannerMojo extends AbstractMojo {
    /**
     * The pattern that splits includes for files to scan.
     */
    private static final Pattern INCLUDES_PATTERN = Pattern.compile("\\s*,\\s*");

    /**
     * The default scanner patterns file location.
     */
    private static final String DEFAULT_PATTERNS_LOCATION = "scanner-patterns.xml";

    /**
     * The default suppressions file location.
     */
    private static final String DEFAULT_SUPPRESSIONS_LOCATION = "scanner-suppressions.xml";

    /**
     * The default root directory to execute the scanner from.
     */
    private static final String DEFAULT_ROOT = "src";

    /**
     * The default includes for files to scan.
     */
    private static final String DEFAULT_INCLUDES = ".java,.properties,.yml";

    /**
     * The default output file.
     */
    private static final String DEFAULT_OUTPUT_FILE = "${project.build.directory}/scanner-result.xml";

    /**
     * The default output format.
     */
    private static final String DEFAULT_OUTPUT_FORMAT = "xml";

    /**
     * The default input encoding.
     */
    private static final String DEFAULT_INPUT_ENCODING = "${project.build.sourceEncoding}";

    /**
     * The default encoding.
     */
    private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

    /**
     * The Maven project.
     */
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    /**
     * The scanner patterns file location.
     */
    @Parameter(property = "scanner.patternsLocation", defaultValue = DEFAULT_PATTERNS_LOCATION)
    private String patternsLocation;

    /**
     * The scanner suppressions file location.
     */
    @Parameter(property = "scanner.suppressionsLocation", defaultValue = DEFAULT_SUPPRESSIONS_LOCATION)
    private String suppressionsLocation;

    /**
     * The root directory to execute the scanner from.
     */
    @Parameter(property = "scanner.root", defaultValue = DEFAULT_ROOT)
    private String root;

    /**
     * The includes for files to scan.
     */
    @Parameter(property = "scanner.includes", defaultValue = DEFAULT_INCLUDES)
    private String includes;

    /**
     * The path and filename to save the scanner output to.
     */
    @Parameter(property = "scanner.output.file", defaultValue = DEFAULT_OUTPUT_FILE)
    private File outputFile;

    /**
     * The format of the scanner output.
     */
    @Parameter(property = "scanner.output.format", defaultValue = DEFAULT_OUTPUT_FORMAT)
    private String outputFileFormat;

    /**
     * Whether to fail on matches to the scanner patterns.
     */
    @Parameter(property = "scanner.failOnMatches", defaultValue = "true")
    private boolean failOnMatches;

    /**
     * Whether to skip execution.
     */
    @Parameter(property = "scanner.skip", defaultValue = "false")
    private boolean skip;

    /**
     * The encoding to use when reading files.
     */
    @Parameter(property = "encoding", defaultValue = DEFAULT_INPUT_ENCODING)
    private String inputEncoding;

    /**
     * Execute a scan.
     *
     * @throws MojoExecutionException if an unexpected error occurs
     * @throws MojoFailureException if an expected error occurs
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Executing scan from " + root + " directory.");

        if (skip) {
            return;
        }

        String encoding = getEncoding();

        ScannerContext context = new ScannerContext(encoding, getOutputFormat(), parseIncludes(), parseSuppressions(encoding), readPatterns(encoding));

        processDir(context, getRootDir());

        if (!context.isMatchesEmpty()) {
            LogWriter logWriter = new LogWriter(context, getLog());
            logWriter.write();

            generateReports(context);

            if (failOnMatches) {
                throw new MojoFailureException(context.toString());
            }
        }
    }

    /**
     * Get the encoding to use.
     *
     * @return the encoding to use
     */
    private String getEncoding() {
        return inputEncoding != null && !inputEncoding.isBlank() ? inputEncoding : System.getProperty("file.encoding", DEFAULT_ENCODING.name());
    }

    /**
     * Get the includes for files to scan.
     *
     * @return the includes for files to scan
     * @throws MojoFailureException if an error occurs
     */
    private List<String> parseIncludes() throws MojoFailureException {
        return INCLUDES_PATTERN.splitAsStream(includes.trim())
            .filter(include -> !include.isEmpty())
            .collect(Collectors.toList());
    }

    /**
     * Get the scanner suppressions.
     *
     * @param encoding the encoding to use
     * @return the scanner suppressions
     * @throws MojoFailureException if an error occurs
     */
    private Map<File, Set<String>> parseSuppressions(final String encoding) throws MojoFailureException {
        File file = new File("src/main/resources", suppressionsLocation);

        if (!file.exists() || !file.isFile()) {
            return Map.of();
        }

        try {
            ScannerSuppressionsXmlParser parser = new ScannerSuppressionsXmlParser();
            return parser.parse(new InputStreamReader(new FileInputStream(file), encoding));
        } catch (IOException exception) {
            throw new MojoFailureException("Error reading suppressions file " + suppressionsLocation + ": " + exception.getMessage());
        }
    }

    /**
     * Get the scanner patterns.
     *
     * @param encoding the encoding to use
     * @return the scanner patterns
     * @throws MojoFailureException if an error occurs
     */
    private List<ScannerPattern> readPatterns(final String encoding) throws MojoFailureException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(patternsLocation);

        if (inputStream == null) {
            throw new MojoFailureException("Patterns file " + patternsLocation + " not found");
        }

        try {
            ScannerPatternsXmlParser parser = new ScannerPatternsXmlParser();
            return parser.parse(new InputStreamReader(inputStream, encoding));
        } catch (IOException exception) {
            throw new MojoFailureException("Error reading patterns file " + patternsLocation + ": " + exception.getMessage());
        }
    }

    /**
     * Get the format of the scanner output.
     *
     * @return the format of the scanner output
     * @throws MojoFailureException if an error occurs
     */
    private ScannerOutputFormat getOutputFormat() throws MojoFailureException {
        if (!ScannerOutputFormat.isMnemonic(outputFileFormat)) {
            throw new MojoFailureException("Invalid output file format " + outputFileFormat);
        }
        return ScannerOutputFormat.valueOfMnemonic(outputFileFormat);
    }

    /**
     * Get the root directory to execute the scanner from.
     *
     * @return the root directory to execute the scanner from
     * @throws MojoFailureException if an error occurs
     */
    private File getRootDir() throws MojoFailureException {
        File rootDir = new File(root);

        if (!rootDir.exists() || !rootDir.isDirectory()) {
            throw new MojoFailureException("Root directory " + root + " not found");
        }

        if (!rootDir.isAbsolute()) {
            rootDir = new File(project.getBasedir(), rootDir.getPath());
        }

        return rootDir;
    }

    /**
     * Process a directory.
     *
     * @param context the scanner context
     * @param directory the directory to process
     * @throws MojoFailureException if an error occurs
     */
    private void processDir(final ScannerContext context, final File directory) throws MojoFailureException {
        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                processFile(context, file);
            } else if (file.isDirectory()) {
                processDir(context, file);
            }
        }
    }

    /**
     * Process a file.
     *
     * @param context the scanner context
     * @param file the file to process
     * @throws MojoFailureException if an error occurs
     */
    private void processFile(final ScannerContext context, final File file) throws MojoFailureException {
        if (matchesIncludes(context, file)) {
            List<ScannerMatch> matches = new ArrayList<>();

            Set<String> suppressions = context.getSuppressions().getOrDefault(file, Set.of());

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), context.getEncoding()))) {
                for (int n = 1; reader.ready(); ++n) {
                    String line = reader.readLine();

                    for (ScannerPattern pattern : context.getPatterns()) {
                        if (suppressions.contains(pattern.getName())) {
                            continue;
                        }

                        Matcher matcher = pattern.getPattern().matcher(line);

                        while (matcher.find()) {
                            matches.add(new ScannerMatch(pattern.getName(), n, matcher.start()));
                        }
                    }
                }
            } catch (IOException exception) {
                throw new MojoFailureException("Error reading file " + file + ": " + exception.getMessage());
            }

            if (!matches.isEmpty()) {
                context.addMatches(new ScannerMatches(file, matches));
            }
        }
    }

    /**
     * Check whether a file matches any of the includes for files to scan.
     *
     * @param context the scanner context
     * @param file the file to check
     * @return whether the file matches any of the includes for files to scan
     */
    private boolean matchesIncludes(final ScannerContext context, final File file) {
        String fileName = file.getName();
        return context.getIncludes().stream()
            .anyMatch(include -> fileName.endsWith(include));
    }

    /**
     * Generate reports for a scanner context.
     *
     * @param context the scanner context to generate reports for
     * @throws MojoFailureException if an error occurs
     */
    private void generateReports(final ScannerContext context) throws MojoFailureException {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(outputFile), context.getEncoding())) {
            ScannerOutputWriter outputWriter = context.getOutputFormat().getOutputWriter();
            outputWriter.write(context, writer);
        } catch (IOException exception) {
            throw new MojoFailureException("Error writing report: " + exception.getMessage());
        }
    }

    /**
     * This class provides a log writer.
     */
    private static final class LogWriter {
        /**
         * The scanner context.
         */
        private final ScannerContext context;

        /**
         * The log to write to.
         */
        private final Log log;

        /**
         * Construct an instance of a log writer.
         *
         * @param context the scanner context
         * @param log the log to write to
         */
        private LogWriter(final ScannerContext context, final Log log) {
            this.context = context;
            this.log = log;
        }

        /**
         * Write a summary of the scanner context to the log.
         */
        private void write() {
            log.info(context.toString());

            for (ScannerMatches matches : context.getMatches()) {
                log.info(matches.toString());

                for (ScannerMatch match : matches.getMatches()) {
                    log.info(match.toString());
                }
            }
        }
    }
}
