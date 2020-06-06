package ru.mewory.mediasort.service.socnet;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.*;

public class InstagramExcelServiceTest {

    @Test
    public void extractIdFromPath() {
        assertEquals("https://www.instagram.com/p/CAfq3AfH5EI/",
                InstagramExcelService.extractLink("Media Link : https://www.instagram.com/p/CAfq3AfH5EI/"));
    }

    @Test
    public void dateFormat() throws ParseException {
        String[] dates = new String[]{
          "Jun 1st, 2020 , 7:57 AM",
          "May 22nd, 2020 , 3:24 PM",
          "May 30th, 2020 , 11:47 AM",
          "May 23rd, 2020 , 4:19 PM",
        };

        for (String date : dates) {
            assertNotNull(InstagramExcelService.parseDate(date));
        }

    }
}