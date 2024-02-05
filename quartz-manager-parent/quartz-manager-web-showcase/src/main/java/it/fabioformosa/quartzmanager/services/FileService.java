package it.fabioformosa.quartzmanager.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class FileService {
  String uploadsDirectory;
  public FileService(@Value("${chista-scheduler.uploadFolder}") String uploadsDirectory) {
    this.uploadsDirectory = uploadsDirectory;
    createUploadsFolderIfNotExists();
  }

  public String getUploadsDirectory() {
    return this.uploadsDirectory;
  }

  public void uploadFile(MultipartFile file) throws Exception {
      if (file.isEmpty()) {
        throw new Exception("Please select a file to upload");
      }
      // Get the file's original name
      String originalFileName = file.getOriginalFilename();

      // Construct the path where you want to save the file
      Path filePath = Paths.get(uploadsDirectory, originalFileName);

      // Save the file to the specified location
      file.transferTo(filePath.toFile());
  }

  private void createUploadsFolderIfNotExists() {
    File uploadDir = new File(uploadsDirectory);
    if (!uploadDir.exists()) {
      uploadDir.mkdir();
    }
  }
}
