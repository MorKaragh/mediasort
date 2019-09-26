package ru.mewory.mediasort.service.socnet;

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
import com.vk.api.sdk.objects.wall.responses.GetCommentsResponse;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import com.vk.api.sdk.queries.users.UserField;
import com.vk.api.sdk.queries.wall.WallGetCommentsSort;
import com.vk.api.sdk.queries.wall.WallGetFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ru.mewory.mediasort.model.socnet.SocNet;
import ru.mewory.mediasort.model.socnet.SocnetDTO;

import javax.annotation.PostConstruct;
import java.util.*;


@Service
@PropertySource("classpath:application.properties")
public class VkService {

    public static final long TIMEOIT = 200L;
    @Value("${vk.appid}")
    private Integer APP_ID;
    @Value("${vk.postauthor}")
    private String POST_AUTHOR;
    @Value("${vk.clientsecret}")
    private String CLIENT_SECRET;
    @Value("${vk.code}")
    private String CODE;

    private static final int COUNT = 10;

    private TransportClient transportClient;
    private VkApiClient vk;
    private ServiceActor actor;

    @PostConstruct
    public void postConstruct() throws ClientException, ApiException {
        transportClient = HttpTransportClient.getInstance();
        vk = new VkApiClient(transportClient);
        actor = new ServiceActor(APP_ID, CLIENT_SECRET, CODE);
    }

    public List<List<SocnetDTO>> getPostWithCommentsById(Long id) throws ClientException, ApiException, InterruptedException {
        List<UserXtrCounters> andreyvorobiev = vk.users().get(actor).userIds(POST_AUTHOR).execute();
        Integer ownerId = andreyvorobiev.get(0).getId();
        List<WallpostFull> posts = vk.wall().getById(actor, ownerId + "_" + id).execute();
        return loadPosts(ownerId, posts);
    }

    public List<List<SocnetDTO>> getPostsWithComments(int offset) throws ClientException, ApiException, InterruptedException {
        List<UserXtrCounters> andreyvorobiev = vk.users().get(actor).userIds(POST_AUTHOR).execute();
        Integer ownerId = andreyvorobiev.get(0).getId();
        GetResponse posts = vk.wall().get(actor)
                .ownerId(ownerId)
                .count(COUNT)
                .offset(offset)
                .filter(WallGetFilter.OWNER)
                .execute();

        return loadPosts(ownerId, posts.getItems());
    }

    private List<List<SocnetDTO>> loadPosts(Integer ownerId, List<WallpostFull> items) throws InterruptedException, ApiException, ClientException {
        List<List<SocnetDTO>> result = new ArrayList<>();
        for (WallpostFull wallpostFull : items) {
            List<SocnetDTO> dtos = new ArrayList<>();
            SocnetDTO post = new SocnetDTO("andreyvorobiev", wallpostFull.getText());
            post.setId(Long.valueOf(wallpostFull.getId()));
            post.setSocnet(SocNet.VK);
            post.setDate(new Date(wallpostFull.getDate() * 1000L));
            Thread.sleep(TIMEOIT);

            GetCommentsResponse allComments = vk.wall().getComments(actor, wallpostFull.getId())
                    .sort(WallGetCommentsSort.ASC)
                    .count(Integer.MAX_VALUE)
                    .ownerId(ownerId)
                    .execute();

            dtos.add(post);

            Map<Integer, String> users = new HashMap<>();
            Set<String> userIds = new HashSet<>();
            Set<String> groupIds = new HashSet<>();
            for (WallComment c : allComments.getItems()) {
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
                List<String> userz = new ArrayList<>(userIds);
                allUsers = vk.users().get(actor).userIds(userz).fields(UserField.SCREEN_NAME).execute();
            }

            List<GroupFull> execute = null;
            if (!groupIds.isEmpty()) {
                List<String> groupz = new ArrayList<>(groupIds);
                execute = vk.groups().getById(actor).groupIds(groupz).execute();
            }

            Thread.sleep(TIMEOIT);
            if (allUsers != null) {
                allUsers.forEach(userXtrCounters -> users.put(userXtrCounters.getId()
                        , userXtrCounters.getFirstName() + " " + userXtrCounters.getLastName()));
            }
            if (execute != null) {
                execute.forEach(groupFull -> users.put(-Integer.valueOf(groupFull.getId()), groupFull.getName()));
            }
            for (int i = 1; i < dtos.size(); i++) {
                SocnetDTO dto = dtos.get(i);
                dto.setAuthor(users.get(dto.getUserId()));
            }
            result.add(dtos);
        }
        return result;
    }

}
