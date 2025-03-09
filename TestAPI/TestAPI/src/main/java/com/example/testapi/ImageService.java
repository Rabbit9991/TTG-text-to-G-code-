package com.example.testapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public Image saveImage(String fileName, String prompt) {
        Image image = new Image();
        image.setFileName(fileName);
        image.setPrompt(prompt);
        Image savedImage = imageRepository.save(image);
        manageImageLimit();
        return savedImage;
    }

    public Image getImageByFileName(String fileName) {
        return imageRepository.findById(fileName).orElse(null);
    }

    public List<Image> getAllImages() {
        return imageRepository.findAllOrderByCreationDateAsc();
    }

    public void deleteImage(String fileName) {
        imageRepository.deleteById(fileName);
    }

    private void manageImageLimit() {
        List<Image> images = imageRepository.findAllOrderByCreationDateAsc();
        if (images.size() > 24) {
            Image oldestImage = images.get(0);
            deleteImage(oldestImage.getFileName());
        }
    }
}
