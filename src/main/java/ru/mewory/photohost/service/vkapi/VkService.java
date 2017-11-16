package ru.mewory.photohost.service.vkapi;

import com.google.gson.JsonElement;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.objects.wall.WallComment;
import com.vk.api.sdk.objects.wall.WallpostFull;
import com.vk.api.sdk.objects.wall.responses.GetCommentsExtendedResponse;
import com.vk.api.sdk.objects.wall.responses.GetCommentsResponse;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import com.vk.api.sdk.queries.users.UsersGetQuery;
import com.vk.api.sdk.queries.wall.WallGetCommentsSort;
import com.vk.api.sdk.queries.wall.WallGetFilter;
import org.springframework.stereotype.Service;
import ru.mewory.photohost.model.socnet.SocNet;
import ru.mewory.photohost.model.socnet.SocnetDTO;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by tookuk on 11/7/17.
 */
@Service
public class VkService {

    private static final Integer APP_ID = 6250403;
    private static final String CLIENT_SECRET = "Eymrv81FaqLwSgO74Q62";
    private static final String REDIRECT_URI = "";
    private static final String CODE = "545a7f5a545a7f5a545a7f5ac8540520f95545a545a7f5a0e5b854efbb6597cddc55e1e";

    private TransportClient transportClient;
    private VkApiClient vk;
    private ServiceActor actor;

    @PostConstruct
    public void postConstruct() throws ClientException, ApiException {
        transportClient = HttpTransportClient.getInstance();
        vk = new VkApiClient(transportClient);
        actor = new ServiceActor(APP_ID, CLIENT_SECRET, CODE);
    }

    public List<List<SocnetDTO>> getPostsWithComments(int offset) throws ClientException, ApiException, InterruptedException {
        List<List<SocnetDTO>> result = new ArrayList<>();
        List<UserXtrCounters> andreyvorobiev = vk.users().get(actor).userIds("andreyvorobiev").execute();
        Integer ownerId = andreyvorobiev.get(0).getId();
        GetResponse posts = vk.wall().get(actor)
                .ownerId(ownerId)
                .count(10)
                .offset(offset)
                .filter(WallGetFilter.OWNER)
                .execute();
        for (WallpostFull wallpostFull : posts.getItems()){
            List<SocnetDTO> dtos = new ArrayList<>();
            SocnetDTO post = new SocnetDTO("andreyvorobiev",wallpostFull.getText());
            post.setId(Long.valueOf(wallpostFull.getId()));
            post.setSocnet(SocNet.VK);
            post.setDate(new Date(wallpostFull.getDate() * 1000L));
            Thread.sleep(200L);
            GetCommentsResponse allComments = vk.wall().getComments(actor, wallpostFull.getId())
                    .sort(WallGetCommentsSort.ASC)
                    .count(100500)
                    .ownerId(ownerId)
                    .execute();

            dtos.add(post);

            Map<Integer, String> users = new HashMap<>();
            String[] userIds = new String[allComments.getItems().size()];
            int i = 0;
            for (WallComment c : allComments.getItems()){
                userIds[i++] = String.valueOf(c.getFromId());
                SocnetDTO comment = new SocnetDTO(c.getFromId(), c.getText());
                comment.setUserId(c.getFromId());
                comment.setId(Long.valueOf(c.getId()));
                comment.setDate(new Date(c.getDate() * 1000L));
                dtos.add(comment);
            }
            List<UserXtrCounters> allUsers = vk.users().get(actor).userIds(userIds).execute();

            Thread.sleep(200L);
            allUsers.forEach(userXtrCounters -> users.put(userXtrCounters.getId()
                    ,userXtrCounters.getFirstName() + " " + userXtrCounters.getLastName()));
            for (int z = 1; i < dtos.size(); i++){
                SocnetDTO dto = dtos.get(z);
                dto.setAuthor(users.get(dto.getUserId()));
            }
            result.add(dtos);
        }
        return result;
    }

}
