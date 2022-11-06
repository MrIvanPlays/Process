# Process
A library for running processes in parallel

Docs available [here](https://jd.mrivanplays.com/process-api/com/mrivanplays/process/package-summary.html)

# Code example
```java
ProcessScheduler scheduler = new ProcessScheduler();
scheduler
    .runProcesses(
        Process.fromRunnable(
            () -> {
              try {
                Thread.sleep(20);
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
              }
              System.out.println("foo");
            }),
        Process.fromRunnable(() -> System.out.println("bar")),
        Process.fromRunnable(() -> System.out.println("lorem ipsum dolor sit amet")))
    .whenDoneAsync(
        errors -> {
          if (!errors.isEmpty()) {
            System.out.println("done with errors");
            for (ProcessException e : errors) {
              e.printStackTrace();
            }
            return;
          }
          System.out.println("done");
        });
```

# How To (Maven)
```xml
<build>
  <plugins>
    <plugin>
      <version>3.7.0</version>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <configuration>
        <source>1.8</source>
        <target>1.8</target>
        <compilerArgs>
          <arg>-parameters</arg>
        </compilerArgs>
      </configuration>
    </plugin>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-shade-plugin</artifactId>
      <version>3.1.1</version>
      <configuration>
        <relocations>
          <!-- Relocating is only necessary if you're shading for other library addition -->
          <relocation>
            <pattern>com.mrivanplays.process</pattern>
            <shadedPattern>[YOUR PLUGIN PACKAGE].process</shadedPattern> <!-- Replace this -->
          </relocation>
        </relocations>
      </configuration>
      <executions>
        <execution>
          <phase>package</phase>
          <goals>
            <goal>shade</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>

<repositories>
  <repository>
    <id>ivan</id>
    <url>https://repo.mrivanplays.com/repository/ivan/</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.mrivanplays</groupId>
    <artifactId>process-api</artifactId>
    <version>VERSION</version> <!-- Replace with latest version -->
    <scope>compile</scope>
  </dependency>
</dependencies>
```