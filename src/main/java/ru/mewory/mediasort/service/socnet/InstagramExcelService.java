package ru.mewory.mediasort.service.socnet;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.mewory.mediasort.model.socnet.SocNet;
import ru.mewory.mediasort.model.socnet.SocnetDTO;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class InstagramExcelService {

    @Autowired
    private PostService postService;

    public void savePostFromXls(String post, MultipartFile excel) {
        String contentType = excel.getContentType();
        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType)) {
            throw new RuntimeException("не распознан формат файла");
        }
        parseFile(excel, post);
    }

    private void parseFile(MultipartFile excel, String postText) {
        try {
            Workbook sheets = WorkbookFactory.create(new ByteArrayInputStream(excel.getBytes()));
            Sheet sheet = sheets.getSheet("ExportGram.com");
            List<Row> rows = IteratorUtils.toList(sheet.rowIterator());
            if (validXls(rows.get(0))) {
                List<SocnetDTO> post = new ArrayList<>();
                Row thirdRow = rows.get(2);
                String link = extractLink(thirdRow.getCell(1).getStringCellValue());
                SocnetDTO head = new SocnetDTO("andreyvorobiev", postText);
                head.setSocnet(SocNet.INSTAGRAM);
                head.setLink(link);
                post.add(head);

                Date minDate = new Date();
                int rownum = 1;
                for (Row row : rows) {
                    if (rownum++ < 5) {
                        continue;
                    }
                    Date date = parseDate(row.getCell(2).getStringCellValue());
                    if (date != null && date.compareTo(minDate) < 0) {
                        minDate = date;
                    }
                    String author = StringUtils.trim(StringUtils.replace(row.getCell(1).getStringCellValue(),"Replies : ",""));
                    SocnetDTO dto = new SocnetDTO(author, row.getCell(4).getStringCellValue())
                            .setDate(date);
                    dto.setSocnet(SocNet.INSTAGRAM);
                    post.add(dto);
                }
                head.setDate(minDate);
                postService.savePost(post);
            } else {
                throw new RuntimeException("не распознан формат файла");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private boolean validXls(Row firstRow) {
        return "username".equalsIgnoreCase(firstRow.getCell(1).getStringCellValue())
                && "date".equalsIgnoreCase(firstRow.getCell(2).getStringCellValue())
                && "comment".equalsIgnoreCase(firstRow.getCell(4).getStringCellValue());
    }

    static String extractLink(String path) {
        return StringUtils.trim(path.substring(path.indexOf("https://www.instagram.com/p/")));
    }

    static Date parseDate(String date) throws ParseException {
        String toParse = StringUtils.trim(date
                .replace("st,",",")
                .replace("nd,",",")
                .replace("rd,",",")
                .replace("th,",","));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d, yyyy , h:mm a", Locale.US);
        return simpleDateFormat.parse(toParse);
    }

}
