package com.example.usermanagement.util;

import com.example.usermanagement.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Component
public class JsonFileUtil {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private File jsonFile;
    private static final String DATA_FILE_NAME = "users.json";

    private File getJsonFile() throws IOException {
        if (jsonFile == null || !jsonFile.exists()) {
            Path dataDir = Paths.get("data");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
            
            Path dataFilePath = dataDir.resolve(DATA_FILE_NAME);
            
            if (!Files.exists(dataFilePath)) {
                ClassPathResource resource = new ClassPathResource(DATA_FILE_NAME);
                try (InputStream inputStream = resource.getInputStream()) {
                    Files.copy(inputStream, dataFilePath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            
            this.jsonFile = dataFilePath.toFile();
        }
        return jsonFile;
    }

    public List<User> readUsers() {
        try {
            File file = getJsonFile();
            if (file.length() == 0) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(file, new TypeReference<List<User>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void writeUsers(List<User> users) {
        try {
            File file = getJsonFile();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
