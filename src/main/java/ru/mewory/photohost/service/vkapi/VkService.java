package ru.mewory.photohost.service.vkapi;

import com.google.gson.JsonElement;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.groups.GroupFull;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.objects.wall.WallComment;
import com.vk.api.sdk.objects.wall.WallpostFull;
import com.vk.api.sdk.objects.wall.responses.GetCommentsExtendedResponse;
import com.vk.api.sdk.objects.wall.responses.GetCommentsResponse;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import com.vk.api.sdk.queries.groups.GroupField;
import com.vk.api.sdk.queries.users.UserField;
import com.vk.api.sdk.queries.users.UsersGetQuery;
import com.vk.api.sdk.queries.wall.WallGetCommentsSort;
import com.vk.api.sdk.queries.wall.WallGetFilter;
import org.apache.commons.collections4.CollectionUtils;
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
    private static final String CODE = "545a7f5a545a7f5a545a7f5ac8540520f95545a545a7f5a0e5b854efbb6597cddc55e1e";
    public static final int COUNT = 10;

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
                .count(COUNT)
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

            for (WallComment c : allComments.getItems()){
                System.out.println(c.getFromId() + " ::: " + c.getText());
            }

            dtos.add(post);

            Map<Integer, String> users = new HashMap<>();
            Set<String> userIds = new HashSet<>();
            Set<String> groupIds = new HashSet<>();
            for (WallComment c : allComments.getItems()){
                if (c.getFromId() >= 0) {
                    userIds.add(String.valueOf(c.getFromId()));
                } else {
                    groupIds.add(String.valueOf(-c.getFromId()));
                }
                SocnetDTO comment = new SocnetDTO(c.getFromId(), c.getText());
                comment.setUserId(c.getFromId());
                comment.setId(Long.valueOf(c.getId()));
                comment.setDate(new Date(c.getDate() * 1000L));
                dtos.add(comment);
            }

            List<UserXtrCounters> allUsers = null;
            if (!userIds.isEmpty()) {
                List<String> userz = new ArrayList<>();
                userz.addAll(userIds);
                allUsers = vk.users().get(actor).userIds(userz).fields(UserField.SCREEN_NAME).execute();
            }

            List<GroupFull> execute = null;
            if (!groupIds.isEmpty()) {
                List<String> groupz = new ArrayList<>();
                groupz.addAll(groupIds);
                execute = vk.groups().getById(actor).groupIds(groupz).execute();
            }

            Thread.sleep(200L);
            if (allUsers != null) {
                allUsers.forEach(userXtrCounters -> users.put(userXtrCounters.getId()
                        , userXtrCounters.getFirstName() + " " + userXtrCounters.getLastName()));
            }
            if (execute != null) {
                execute.forEach(groupFull -> users.put(-Integer.valueOf(groupFull.getId()),groupFull.getName()));
            }
            for (int i = 1; i < dtos.size(); i++){
                SocnetDTO dto = dtos.get(i);
                dto.setAuthor(users.get(dto.getUserId()));
            }
            result.add(dtos);
        }
        return result;
    }

}
