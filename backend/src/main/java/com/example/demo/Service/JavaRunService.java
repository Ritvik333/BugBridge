package com.example.demo.Service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

@Service
public class JavaRunService implements RunService {

    private static final int TIMEOUT_SECONDS = 5;

    @Override
    public String executeCode(String code) throws Exception {
        File tempDir = null;
        try {
            // Create a temporary directory for Java files
            tempDir = new File(System.getProperty("java.io.tmpdir"), "java_temp_" + System.nanoTime());
            tempDir.mkdir();

            File javaFile = new File(tempDir, "Main.java");
            try (FileWriter writer = new FileWriter(javaFile)) {
                writer.write(code);
            }

            String os = System.getProperty("os.name").toLowerCase();
            String javacCommand = os.contains("win") ? "javac.exe" : "javac";
            String javaCommand = os.contains("win") ? "java.exe" : "java";

            // Compile the code
            ProcessBuilder compileBuilder = new ProcessBuilder(javacCommand, javaFile.getAbsolutePath());
            compileBuilder.directory(tempDir);
            Process compileProcess = compileBuilder.start();
            compileProcess.waitFor();

            BufferedReader errorReader = new BufferedReader(new InputStreamReader(compileProcess.getErrorStream()));
            StringBuilder errorOutput = new StringBuilder();
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                errorOutput.append(errorLine).append("\n");
            }

            if (compileProcess.exitValue() != 0) {
                return "Compilation Error:\n" + errorOutput.toString();
            }

            // Run the compiled code
            ProcessBuilder runBuilder = new ProcessBuilder(javaCommand, "-cp", tempDir.getAbsolutePath(), "Main");
            runBuilder.directory(tempDir);
            runBuilder.redirectErrorStream(true);
            Process runProcess = runBuilder.start();

            // Execution with timeout
            boolean finished = runProcess.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finished) {
                runProcess.destroy();
                return "Execution timed out (Limit: " + TIMEOUT_SECONDS + "s)";
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            return output.toString();
        } finally {
            // Cleanup temporary directory and files
            if (tempDir != null && tempDir.exists()) {
                for (File file : tempDir.listFiles()) {
                    file.delete();
                }
                tempDir.delete();
            }
        }
    }
    @Override
    public String getLanguage() {
        return "java";
    }
}
