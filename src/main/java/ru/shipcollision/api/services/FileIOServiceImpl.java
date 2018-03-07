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

    public static final String BASE_PATH = "uploads";

    @Override
    public String saveAndGetPath(MultipartFile file) {
        final UploadResourceResolver resolver = new UploadResourceResolver(file.getOriginalFilename());

        try (FileOutputStream out = new FileOutputStream(resolver.getSaveFilePath())) {
            out.write(file.getBytes());
            out.close();
        } catch (FileNotFoundException e) {
            throw new ApiException(String.format("File %s not found", resolver.getSaveFilePath()));
        } catch (IOException e) {
            throw new ApiException(String.format("Impossible to save to file %s", resolver.getSaveFilePath()));
        }

        return resolver.getResoursePath();
    }

    @Override
    public MultipartFile load(String path) {
        return null;
    }

    private static final class UploadResourceResolver {

        private String filename;

        private String resoursePath;

        private String saveFilePath;

        private UploadResourceResolver(String originalFilename) {
            final LocalDateTime now = LocalDateTime.now();

            final String fileExtension = originalFilename.split("\\.")[1];
            filename = String.format(
                    "%d%d%d.%s",
                    now.getHour(),
                    now.getMinute(),
                    now.getSecond(),
                    fileExtension
            );

            resoursePath = String.format(
                    "%d/%d/%d",
                    now.getYear(),
                    now.getMonthValue(),
                    now.getDayOfMonth()
            );

            final Path uploadPath = Paths.get(String.format(
                    "%s/%s",
                    BASE_PATH,
                    resoursePath
            )).toAbsolutePath();

            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new ApiException(String.format("Impossible to create directory %s", uploadPath));
            }

            resoursePath = String.format("/%s/%s/%s", BASE_PATH, resoursePath, filename);
            saveFilePath = String.format("%s/%s", uploadPath.toString(), filename);
        }

        public String getResoursePath() {
            return resoursePath;
        }

        public String getSaveFilePath() {
            return saveFilePath;
        }
    }
}
