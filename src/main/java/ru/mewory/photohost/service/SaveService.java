package ru.mewory.photohost.service;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mewory.photohost.dao.ImagesRepository;
import ru.mewory.photohost.dao.TagRepository;
import ru.mewory.photohost.model.Image;
import ru.mewory.photohost.model.Tag;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Created by tookuk on 9/16/17.
 */
@Service
public class SaveService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ImagesRepository imagesRepository;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy");


    public void save(Image pic){
        byte[] data = Base64.decodeBase64(pic.getPic());
        LocalDate today = LocalDate.now();
        String folderName = today.format(formatter);

        String folderPath = "photos/" + folderName;
        createFolder(folderPath);

        String finalPath = folderPath + "/" + randomName() + "." + pic.getExtension();
        try (OutputStream stream = new FileOutputStream(finalPath)) {
            stream.write(data);
            Long imageId = saveImage(finalPath);
            saveTags(imageId,pic.getTags());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveTags(Long imageId, List<String> tags) {
        for (String s : tags){
            Tag tag = new Tag();
            tag.setName(s);
            tagRepository.save(tag);
        }
    }

    private Long saveImage(String finalPath) {
        Image image = new Image();
        image.setPath(finalPath);

        Image save = imagesRepository.save(image);
        return save.getId();
    }

    private void createFolder(String folderPath) {
        File folder = new File(folderPath);
        if(!folder.exists()){
            folder.mkdirs();
        }
    }

    private String randomName() {
        return UUID.randomUUID().toString();
    }

}
