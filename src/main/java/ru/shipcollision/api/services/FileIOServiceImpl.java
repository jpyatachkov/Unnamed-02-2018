package ru.shipcollision.api.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.shipcollision.api.exceptions.ApiException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
public class FileIOServiceImpl implements FileIOService {

    public static final String BASE_PATH = "uploads/";

    private String resolveDirectoryPath(String originalFilename) {
        final LocalDateTime now = LocalDateTime.now();
        final String extension = originalFilename.split("\\.")[1];
        final Path path = Paths.get(String.format("%s%d/%d/%d/",
                BASE_PATH,
                now.getYear(),
                now.getMonthValue(),
                now.getDayOfMonth()
        )).toAbsolutePath();

        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new ApiException(String.format("Impossible to create directory %s", path));
        }

        return String.format("%s/%d%d%d.%s",
                path.toString(),
                now.getHour(),
                now.getMinute(),
                now.getSecond(),
                extension
        );
    }

    @Override
    public String saveAndGetPath(MultipartFile file) {
        final String saveFilePath = resolveDirectoryPath(file.getOriginalFilename());

        try (FileOutputStream out = new FileOutputStream(saveFilePath)) {
            out.write(file.getBytes());
            out.close();
        } catch (FileNotFoundException e) {
            throw new ApiException(String.format("File %s not found", saveFilePath));
        } catch (IOException e) {
            throw new ApiException(String.format("Impossible to save to file %s", saveFilePath));
        }

        return saveFilePath;
    }

    @Override
    public MultipartFile load(String path) {
        return null;
    }
}
