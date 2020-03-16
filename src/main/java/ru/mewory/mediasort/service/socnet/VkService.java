package ru.mewory.mediasort.service.socnet;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.groups.GroupFull;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.objects.wall.WallComment;
import com.vk.api.sdk.objects.wall.WallpostAttachment;
import com.vk.api.sdk.objects.wall.WallpostFull;
import com.vk.api.sdk.objects.wall.responses.GetCommentsResponse;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import com.vk.api.sdk.queries.users.UserField;
import com.vk.api.sdk.queries.wall.WallGetCommentsSort;
import com.vk.api.sdk.queries.wall.WallGetFilter;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final long TIME_WAIT_BEFORE_REQUEST = 200L;
    private static final int POSTS_BATCH_SIZE = 10;
    private Logger logger = LoggerFactory.getLogger(VkService.class);

    @Value("${vk.appid}")
    private Integer APP_ID;

    @Value("${vk.postauthor}")
    private String POST_AUTHOR;

    @Value("${vk.clientsecret}")
    private String CLIENT_SECRET;

    @Value("${vk.code}")
    private String CODE;

    @Value("${self.domain}")
    private String selfDomain;

    private VkApiClient vk;
    private ServiceActor actor;
    private UserActor userActor;
    private Long userActorExpiresAt;

    @PostConstruct
    public void postConstruct() {
        TransportClient transportClient = HttpTransportClient.getInstance();
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
                .count(POSTS_BATCH_SIZE)
                .offset(offset)
                .filter(WallGetFilter.OWNER)
                .execute();

        return loadPosts(ownerId, posts.getItems());
    }

    private List<List<SocnetDTO>> loadPosts(Integer ownerId, List<WallpostFull> items) throws InterruptedException, ApiException, ClientException {
        List<List<SocnetDTO>> result = new ArrayList<>();

        for (WallpostFull wallpostFull : items) {
            List<SocnetDTO> dtos = new ArrayList<>();
            Map<Integer, String> users = new HashMap<>();
            Set<String> userIds = new HashSet<>();
            Set<String> groupIds = new HashSet<>();

            extractComments(ownerId, wallpostFull, dtos, userIds, groupIds);
            extractAttachmentComments(ownerId, wallpostFull, dtos, userIds, groupIds);
            fillUsernames(dtos, users, userIds, groupIds);

            result.add(dtos);
        }

        return result;
    }

    private void extractComments(Integer ownerId, WallpostFull wallpostFull, List<SocnetDTO> dtos, Set<String> userIds, Set<String> groupIds) throws InterruptedException, ApiException, ClientException {
        SocnetDTO post = new SocnetDTO("andreyvorobiev", wallpostFull.getText());
        post.setId(Long.valueOf(wallpostFull.getId()));
        post.setSocnet(SocNet.VK);
        post.setDate(new Date(wallpostFull.getDate() * 1000L));

        dtos.add(post);

        Thread.sleep(TIME_WAIT_BEFORE_REQUEST);

        int offset = 0;
        while (true) {
            GetCommentsResponse allComments = vk.wall()
                    .getComments(actor, wallpostFull.getId())
                    .sort(WallGetCommentsSort.ASC)
                    .count(100)
                    .ownerId(ownerId)
                    .offset(offset)
                    .execute();

            for (WallComment c : allComments.getItems()) {
                putComment(dtos, userIds, groupIds, c);
            }

            offset += 100;

            if (allComments.getItems().size() < 100) {
                return;
            }
        }
    }

    private void fillUsernames(List<SocnetDTO> dtos, Map<Integer, String> users, Set<String> userIds, Set<String> groupIds) throws ApiException, ClientException, InterruptedException {
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

        Thread.sleep(TIME_WAIT_BEFORE_REQUEST);
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
    }

    private void extractAttachmentComments(Integer ownerId, WallpostFull wallpostFull, List<SocnetDTO> dtos, Set<String> userIds, Set<String> groupIds) throws InterruptedException, ApiException, ClientException {
        if (userActor == null || System.currentTimeMillis() / 1000 >= userActorExpiresAt) {
            return;
        }

        List<WallpostAttachment> attachments = wallpostFull.getAttachments();

        if (CollectionUtils.isNotEmpty(attachments)) {
            for (WallpostAttachment a : attachments) {
                if (a.getVideo() != null && a.getVideo().getId() != null) {
                    Integer comments = a.getVideo().getComments();
                    int offset = 0;
                    while (comments > 0) {
                        Thread.sleep(TIME_WAIT_BEFORE_REQUEST);
                        com.vk.api.sdk.objects.video.responses.GetCommentsResponse videoComments
                                = vk.videos()
                                .getComments(userActor, a.getVideo().getId())
                                .ownerId(ownerId)
                                .count(100)
                                .offset(offset)
                                .execute();
                        for (WallComment c : videoComments.getItems()) {
                            putComment(dtos, userIds, groupIds, c);
                        }
                        offset += 100;
                        comments -= 100;
                    }
                }
            }
        }
    }

    private void putComment(List<SocnetDTO> dtos, Set<String> userIds, Set<String> groupIds, WallComment c) {
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

    public void setAuthCode(String code) {
        try {
            UserAuthResponse authResponse = vk.oauth().userAuthorizationCodeFlow(APP_ID, CLIENT_SECRET,
                    selfDomain + "/vkload", code).execute();
            userActorExpiresAt = System.currentTimeMillis() / 1000 + authResponse.getExpiresIn() - 1;
            userActor = new UserActor(APP_ID, authResponse.getAccessToken());
        } catch (ApiException | ClientException e) {
            logger.error("error while creating UserActor", e);
        }

    }
}
