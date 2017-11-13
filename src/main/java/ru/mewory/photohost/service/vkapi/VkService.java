package ru.mewory.photohost.service.vkapi;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.objects.wall.responses.GetCommentsResponse;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import com.vk.api.sdk.queries.wall.WallGetFilter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

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

    public Object getSomething() throws ClientException, ApiException {
        List<UserXtrCounters> andreyvorobiev = vk.users().get(actor).userIds("andreyvorobiev").execute();
        andreyvorobiev.toString();
        GetResponse getResponse = vk.wall().get(actor)
                .ownerId(andreyvorobiev.get(0).getId())
                .count(1)
                .offset(0)
                .filter(WallGetFilter.OWNER)
                .execute();
        GetCommentsResponse execute = vk.wall().getComments(actor, getResponse.getItems().get(0).getId())
                .ownerId(andreyvorobiev.get(0).getId())
                .execute();
        getResponse.toString();
        return null;
    }

}
