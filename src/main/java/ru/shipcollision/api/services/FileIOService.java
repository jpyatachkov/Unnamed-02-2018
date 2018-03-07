package ru.shipcollision.api.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileIOService {

    String saveAndGetPath(MultipartFile file);

    MultipartFile load(String path);
}
