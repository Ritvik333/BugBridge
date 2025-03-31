package com.example.demo.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RunServiceFactory {
    private final Map<String, RunService> runServices;

    @Autowired
    public RunServiceFactory(List<RunService> runServices) {
        this.runServices = new HashMap<>();

        for (RunService service : runServices) {
            String languageKey = service.getLanguage().toLowerCase();
            this.runServices.put(languageKey, service);
        }
    }

    public RunService getRunService(String language) {
        RunService service = runServices.get(language.toLowerCase());
        if (service == null) {
            throw new UnsupportedOperationException("Unsupported language: " + language);
        }
        return service;
    }
    }

