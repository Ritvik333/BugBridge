package com.example.demo.Service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

@Service
public class PythonRunService implements RunService {

    private static final int TIMEOUT_SECONDS = 5;

    @Override
    public String executeCode(String code) throws Exception {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("script", ".py");
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(code);
            }

            ProcessBuilder processBuilder = new ProcessBuilder("python3", tempFile.getAbsolutePath());
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // Execution with timeout
            boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finished) {
                process.destroy();
                return "Execution timed out (Limit: " + TIMEOUT_SECONDS + "s)";
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            return output.toString();
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
    @Override
    public String getLanguage() {
        return "python";
    }
}
