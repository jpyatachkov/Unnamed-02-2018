package ru.shipcollision.api.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.shipcollision.api.exceptions.ApiException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileIOService {

    public static final String BASE_PATH = "uploads";

    public boolean fileExists(String resoursePath) {
        return Files.exists(UploadResourceResolver.toAbsolutePath(resoursePath));
    }

    public String saveFileAndGetResourcePath(MultipartFile file) {
        final UploadResourceResolver resolver = new UploadResourceResolver(
                Objects.requireNonNull(file.getOriginalFilename())
        );

        //noinspection TryWithIdenticalCatches,LocalCanBeFinal
        try (FileOutputStream out = new FileOutputStream(resolver.getSaveFilePath())) {
            out.write(file.getBytes());
        } catch (FileNotFoundException e) {
            throw new ApiException(e);
        } catch (IOException e) {
            throw new ApiException(e);
        }

        return resolver.getResoursePath();
    }

    public void deleteFile(String resoursePath) {
        try {
            Files.delete(UploadResourceResolver.toAbsolutePath(resoursePath));
        } catch (IOException e) {
            throw new ApiException(e);
        }
    }

    private static final class UploadResourceResolver {

        private String filename;

        private String resoursePath;

        private String saveFilePath;

        private UploadResourceResolver(String originalFilename) {
            final LocalDateTime now = LocalDateTime.now();

            final String fileExtension = originalFilename.split("\\.")[1];
            filename = String.format(
                    "%s.%s",
                    UUID.randomUUID().toString().replace("-", ""),
                    fileExtension
            );

            final String resourseDirectoryPath = String.format(
                    "%d/%d/%d",
                    now.getYear(),
                    now.getMonthValue(),
                    now.getDayOfMonth()
            );

            final Path uploadPath = Paths.get(BASE_PATH, resourseDirectoryPath).toAbsolutePath();

            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new ApiException(e);
            }

            resoursePath = '/' + Paths.get(BASE_PATH, resourseDirectoryPath, filename).toString();
            saveFilePath = Paths.get(uploadPath.toString(), filename).toString();
        }

        public static Path toAbsolutePath(String resoursePath) {
            resoursePath =
                    (resoursePath != null && resoursePath.charAt(0) == '/') ? resoursePath.substring(1) : resoursePath;
            return Paths.get(String.format("%s", resoursePath)).toAbsolutePath();
        }

        public String getResoursePath() {
            return resoursePath;
        }

        public String getSaveFilePath() {
            return saveFilePath;
        }
    }
}
