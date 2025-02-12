package com.example.demo.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;

@Service
public class RunService {

    public String executeCode(String code, String language) throws Exception {
        ProcessBuilder processBuilder;
        File tempFile;

        switch (language.toLowerCase()) {
            case "javascript":
                tempFile = File.createTempFile("script", ".js");
                try (FileWriter writer = new FileWriter(tempFile)) {
                    writer.write(code);
                }
                processBuilder = new ProcessBuilder("node", tempFile.getAbsolutePath());
                break;

            case "python":
                tempFile = File.createTempFile("script", ".py");
                try (FileWriter writer = new FileWriter(tempFile)) {
                    writer.write(code);
                }
                processBuilder = new ProcessBuilder("python3", tempFile.getAbsolutePath());
                break;

            case "java":
                // Create a temporary directory for Java source and compiled files
                File tempDir = new File(System.getProperty("java.io.tmpdir"), "java_temp_" + System.nanoTime());
                tempDir.mkdir();

                // Java source file
                File javaFile = new File(tempDir, "Main.java");
                try (FileWriter writer = new FileWriter(javaFile)) {
                    writer.write(code);
                }

                // Compile the Java file
                processBuilder = new ProcessBuilder("javac", javaFile.getAbsolutePath());
                processBuilder.directory(tempDir); // Ensure it compiles in the correct directory
                Process compileProcess = processBuilder.start();
                compileProcess.waitFor();

                if (compileProcess.exitValue() != 0) {
                    return "Compilation Error";
                }

                // Run the Java class
                processBuilder = new ProcessBuilder("java", "-cp", tempDir.getAbsolutePath(), "Main");
                break;

            default:
                return "Error: Unsupported language";
        }

        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        process.waitFor();
        return output.toString();
    }
}
