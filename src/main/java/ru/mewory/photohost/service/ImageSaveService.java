package ru.mewory.photohost.service;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mewory.photohost.dao.AuthorRepository;
import ru.mewory.photohost.dao.ImagesRepository;
import ru.mewory.photohost.dao.LocationRepository;
import ru.mewory.photohost.dao.TagRepository;
import ru.mewory.photohost.model.Author;
import ru.mewory.photohost.model.Image;
import ru.mewory.photohost.model.dictionaries.Location;
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
public class ImageSaveService {

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private ImagesRepository imagesRepository;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy");

    public void save(Image pic){
        String finalPath = getFinalPath(pic);
        try (OutputStream stream = new FileOutputStream(finalPath)) {
            stream.write(Base64.decodeBase64(pic.getPic()));
            Long imageId = saveImage(finalPath);
            Location l = locationRepository.findByName(pic.getLocation());
            if (l == null) {
                locationRepository.save(new Location(pic.getLocation()));
            }
            Author a = authorRepository.findByName(pic.getAuthor());
            if (a == null) {
                authorRepository.save(new Author(pic.getAuthor()));
            }
            saveTags(imageId,pic.getTags());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFinalPath(Image pic) {
        LocalDate today = LocalDate.now();
        String folderName = today.format(formatter);

        String folderPath = "photos/" + folderName;
        createFolder(folderPath);

        return folderPath + "/" + randomName() + "." + pic.getExtension();
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
