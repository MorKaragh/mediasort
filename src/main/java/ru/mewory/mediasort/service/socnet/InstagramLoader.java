package ru.mewory.mediasort.service.socnet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mewory.mediasort.model.socnet.InstagramLoaderObject;
import ru.mewory.mediasort.model.socnet.Post;
import ru.mewory.mediasort.model.socnet.SocNet;
import ru.mewory.mediasort.model.socnet.SocnetDTO;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class InstagramLoader {

    private static final String INSTALOADE_FOLDER = System.getProperty("user.dir") + File.separator + "instaloader";

    @Autowired
    private PostService postService;

    static String extractIdFromPath(String path) {
        return StringUtils.removeEnd(path.replace("https://www.instagram.com/p/", ""), "/");
    }

    public Post load(String path) {
        try {
            String postId = extractIdFromPath(path);
            useInstaloaderToCreateFiles(postId);
            List<SocnetDTO> data = parseLoadedFile(postId, path);
            Optional<SocnetDTO> withMinDate = data.stream().min(Comparator.comparing(SocnetDTO::getDate));
            data.get(0).setDate(withMinDate.get().getDate());
            Post post = postService.savePost(data);
            clearFiles(postId);
            return post;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void clearFiles(String postId) throws IOException {
        FileUtils.deleteDirectory(new File(INSTALOADE_FOLDER + "/-" + postId));
    }

    private void useInstaloaderToCreateFiles(String postId) throws IOException, InterruptedException {
        ProcessBuilder ps = new ProcessBuilder("python3", "instaloader.py", "--comments", "--", "-" + postId + "");
        System.out.println(ps.command());
        ps.directory(new File(INSTALOADE_FOLDER));
        Process pr = ps.start();

        BufferedReader in = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }

        pr.waitFor();
        in.close();
    }

    private List<SocnetDTO> parseLoadedFile(String postId, String path) throws IOException {
        List<SocnetDTO> result = new ArrayList<>();

        File commentsFile = getParsedFile(postId, "*comments.json");
        File postTextFile = getParsedFile(postId, "*UTC.txt");

        if (postTextFile != null) {
            SocnetDTO head = new SocnetDTO("andreyvorobiev",
                    FileUtils.readFileToString(postTextFile, "UTF-8"));
            head.setSocnet(SocNet.INSTAGRAM);
            head.setDate(new Date());
            head.setLink(path);
            result.add(head);
        }

        if (commentsFile != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            List<InstagramLoaderObject> jsonObjects
                    = objectMapper.readValue(commentsFile, new TypeReference<List<InstagramLoaderObject>>() {
            });

            result.addAll(convertToDtos(jsonObjects));
        }

        return result;
    }

    private List<SocnetDTO> convertToDtos(List<InstagramLoaderObject> objects) {
        List<SocnetDTO> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(objects)) {
            for (InstagramLoaderObject object : objects) {
                result.add(SocnetDTO.fromInstagramLoaderObject(object));
                if (CollectionUtils.isNotEmpty(object.getAnswers())) {
                    for (InstagramLoaderObject answer : object.getAnswers()) {
                        result.add(SocnetDTO.fromInstagramLoaderObject(answer));
                    }
                }
            }
        }
        return result;
    }

    private File getParsedFile(String postId, String mask) throws IOException {
        final File[] commentsFile = {null};
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(
                Paths.get(System.getProperty("user.dir")
                        + File.separator + "instaloader/-" + postId), mask)) {
            dirStream.forEach(path -> commentsFile[0] = new File(String.valueOf(path)));
        }
        return commentsFile[0];
    }

}
