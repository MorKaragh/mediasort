package ru.mewory.photohost.service.vkapi;

import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.mewory.photohost.model.Author;
import ru.mewory.photohost.model.socnet.SocnetDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tookuk on 11/13/17.
 */
public class InstagramParser {

    public static List<SocnetDTO> parse(String data){
        Document parse = Jsoup.parse(data);
        Elements allElements = parse.getElementsByAttributeValueMatching("class",".*notranslate.*");
        Element postHead = allElements.get(0);
        List<SocnetDTO> result = new ArrayList<>();
        SocnetDTO head = new SocnetDTO(postHead.text(), postHead.nextElementSibling().text());
        result.add(head);

        for (int i = 1; i < allElements.size(); i++){
            Element e = allElements.get(i);
            if (e.text().startsWith("@")) continue;
            Element textElement  = e.nextElementSibling();
            if (textElement != null) {
                String authorName = e.text();
                SocnetDTO dto = new SocnetDTO(authorName,textElement.text());
                result.add(dto);
            }
        }

        return result;
    }

}
