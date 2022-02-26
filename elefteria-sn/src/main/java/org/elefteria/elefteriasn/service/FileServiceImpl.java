package org.elefteria.elefteriasn.service;

import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import org.elefteria.elefteriasn.dao.ImageRepository;
import org.elefteria.elefteriasn.dao.UserRepository;
import org.elefteria.elefteriasn.entity.Image;
import org.elefteria.elefteriasn.entity.User;
import org.elefteria.elefteriasn.exception.MyEntityNotFoundException;
import org.elefteria.elefteriasn.response.SuccessResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
public class FileServiceImpl implements FileService {

    private UserRepository userRepository;
    private ImageRepository imageRepository;

    @Value("${localstorage.images.path}")
    private String imageStorePath;

    @Autowired
    public FileServiceImpl(UserRepository userRepository, ImageRepository imageRepository) {
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<SuccessResponse> uploadUserAvatar(MultipartFile file) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> userOptional = userRepository.findByUsername(username);
        if(userOptional.isEmpty())
            throw new MyEntityNotFoundException("User " + username + " not found");

        User user = userOptional.get();

        String fileName = uploadImageToLocalFileSystem(file);

        if(fileName == null){
            throw new RuntimeException("Failed to upload image");
        }

        Image image = new Image(fileName);

        if(user.getUserInfo().getAvatar() != null){
            deleteImageByFileName(user.getUserInfo().getAvatar().getFileName());

            imageRepository.delete(user.getUserInfo().getAvatar());
        }

        user.getUserInfo().setAvatar(image);

        userRepository.save(user);

        SuccessResponse successResponse = new SuccessResponse(
                HttpStatus.OK.value(),
                "File " + file.getOriginalFilename() + " was successfully uploaded!",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }

    @Override
    public String uploadImageToLocalFileSystem(MultipartFile file) {
        String extension = Files.getFileExtension(file.getOriginalFilename());

        if(!(extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png")))
            throw new RuntimeException("Wrong file extension");

        String fileName = System.currentTimeMillis() + "." + extension;

        Path storageDirectory = Paths.get(imageStorePath);

        if(!java.nio.file.Files.exists(storageDirectory)){
            try {
                java.nio.file.Files.createDirectories(storageDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Path destination = Paths.get(imageStorePath + fileName);

        try {
            java.nio.file.Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileName;
    }

    @Override
    public void deleteImageByFileName(String fileName) {
        Path destination = Paths.get(imageStorePath + fileName);

        try {
            java.nio.file.Files.deleteIfExists(destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] getImageByName(String imageName) throws IOException {
        Path destination = Paths.get(imageStorePath + imageName);

        return IOUtils.toByteArray(destination.toUri());
    }
}
