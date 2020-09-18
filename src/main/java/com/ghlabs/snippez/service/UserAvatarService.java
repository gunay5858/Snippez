package com.ghlabs.snippez.service;

import com.ghlabs.snippez.exception.FileStorageException;
import com.ghlabs.snippez.exception.FileTypeNotAllowedException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class UserAvatarService {
    public Path avatarUploadDir = Paths.get("static/uploads/avatars/");
    private static final List<String> contentTypes = Arrays.asList("image/png", "image/jpeg", "image/gif");


    public String uploadFile(MultipartFile file) throws FileStorageException, FileTypeNotAllowedException {
        // check if file is image
        String fileContentType = file.getContentType();
        if (!contentTypes.contains(fileContentType)) {
            throw new FileTypeNotAllowedException("please upload only images of type: " + contentTypes.toString());
        }

        if (!new File(avatarUploadDir.toString()).exists()) {
            new File(avatarUploadDir.toString()).mkdirs();
        }

        String fileName = StringUtils.cleanPath(new Date().getTime() + "_" + file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.avatarUploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!");
        }
    }

    public Resource loadFileAsResource(String fileName) throws FileNotFoundException {
        try {
            Path filePath = this.avatarUploadDir.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + fileName);
        }
    }
}
