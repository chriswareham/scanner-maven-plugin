# Scanner Maven Plugin

Maven plugin to scan projects for pattern matches. Can be used to prevent
secrets being committed to code repositories.

## Requirements

To build, the following are required:

* Java JDK, version 17 or later
* Apache Maven, version 3.9 or later

The plugin can be built and installed with the following command:

```
mvn install
```

## Usage

Patterns are declared in an XML file. This can be in a separate project that can
be then used across multiple other projects that require scanning. A sample POM
file for a patterns project is shown below:

    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

        <modelVersion>4.0.0</modelVersion>

        <groupId>net.chriswareham</groupId>
        <artifactId>scanner-patterns</artifactId>
        <version>1.0-SNAPSHOT</version>

        <name>Scanner Patterns</name>

        <properties>
            <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        </properties>

    </project>

The patterns can be declared in a file within the `src/main/resources`
directory. A sample patterns file is shown below:

    <patterns>
        <pattern name="SSH DSA Key">ssh-dss AAAAB3NzaC1kc3[0-9A-Za-z+/]+[=]{0,3}(\s.*)?</pattern>
        <pattern name="SSH ECDSA Key">ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNT[0-9A-Za-z+/]+[=]{0,3}(\s.*)?</pattern>
        <pattern name="SSH ED25519 Key">ssh-ed25519 AAAAC3NzaC1lZDI1NTE5[0-9A-Za-z+/]+[=]{0,3}(\s.*)?</pattern>
        <pattern name="SSH RSA Key">ssh-rsa AAAAB3NzaC1yc2[0-9A-Za-z+/]+[=]{0,3}(\s.*)?</pattern>
        <pattern name="JDBC Connection String With Password">jdbc:db://[a-z0-9][-a-z0-9\.]*(:[0-9]+)?/[^\?]+\?user=[^&amp;]+&amp;password=.+</pattern>
    </patterns>

Then in the POM of each project that requires scanning, or in a parent POM,
declare the plugin as shown below:

    <plugin>
        <groupId>net.chriswareham</groupId>
        <artifactId>scanner-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <executions>
            <execution>
                <phase>compile</phase>
                <goals>
                  <goal>scanner</goal>
                </goals>
            </execution>
        </executions>
        <dependencies>
             <dependency>
                <groupId>net.chriswareham</groupId>
                <artifactId>scanner-patterns</artifactId>
                <version>1.0-SNAPSHOT</version>
            </dependency>
        </dependencies>
        <configuration>
            <failOnMatches>false</failOnMatches>
        </configuration>
    </plugin>

False positives can be suppressed on a per project basis with a suppressions
file. A sample suppressions file is shown below:

    <suppressions>
        <suppression file="src/main/java/net/chriswareham/scanner/Test.java">
            <pattern name="SSH ED25519 Key"/>
        </suppression>
    </suppressions>

## Properties

| Name                           | Description                                        | Default                   |
| ------------------------------ | -------------------------------------------------- | ------------------------- |
| `scanner.patternsLocation`     | scanner patterns file location                     | scanner-patterns.xml      |
| `scanner.suppressionsLocation` | scanner suppressions file location                 | scanner-suppressions.xml  |
| `scanner.root`                 | root directory to execute the scanner from         | src                       |
| `scanner.includes`             | includes for files to scan                         | .java,.properties,.yml    |
| `scanner.output.file`          | path and filename to save the scanner output to    | target/scanner-result.xml |
| `scanner.output.format`        | format of the scanner output (xml or plain)        | xml                       |
| `scanner.failOnMatches`        | whether to fail on matches to the scanner patterns | true                      |
| `scanner.skip`                 | whether to skip execution                          | false                     |
