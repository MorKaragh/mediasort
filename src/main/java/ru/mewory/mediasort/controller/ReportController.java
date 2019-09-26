package ru.mewory.mediasort.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.mewory.mediasort.model.report.ReportTheme;
import ru.mewory.mediasort.model.socnet.Post;
import ru.mewory.mediasort.service.ReportService;
import ru.mewory.mediasort.service.socnet.PostService;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class ReportController {

    @Autowired
    private PostService postService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private ControllerUtils controllerUtils;

    @RequestMapping(method = POST, value = {"/reportedit"})
    public ModelAndView reportedit(@RequestBody Map<String,String> allRequestParams){
        ModelAndView mav = new ModelAndView("reportedit");
        controllerUtils.fillDictionaries(mav);

        Post post = postService.getPostForEditFromReport(
                allRequestParams.get("startDate"),
                allRequestParams.get("endDate"),
                allRequestParams.get("theme"),
                allRequestParams.get("location"),
                allRequestParams.get("description"),
                allRequestParams.get("address")
        );

        mav.addObject("realpost","false");
        mav.addObject("post", post);
        return mav;
    }

    @RequestMapping(method = GET, value = "report")
    public @ResponseBody
    ModelAndView getReport(@RequestParam Map<String,String> allRequestParams) {
        ModelAndView mav = new ModelAndView("report");

        Date startDate = controllerUtils.parseDate(allRequestParams.get("startDate"));
        Date endDate = controllerUtils.parseDate(allRequestParams.get("endDate"));

        List<ReportTheme> records = reportService.getReport(startDate,endDate);
        int totalInstagramCount = reportService.getTotalInstagramCount(startDate, endDate);
        int totalVkCount = reportService.getTotalVkCount(startDate, endDate);

        mav.addObject("countVedomstva",reportService.getVedomstvaCount(startDate,endDate));
        mav.addObject("countDistinctUsers",reportService.getDistinctUsersCount(startDate,endDate));
        mav.addObject("countTotal", totalVkCount + totalInstagramCount);
        mav.addObject("countTotalVk", totalVkCount);
        mav.addObject("countTotalInstagram", totalInstagramCount);
        mav.addObject("report",records);
        mav.addObject("startDate",allRequestParams.get("startDate"));
        mav.addObject("endDate",allRequestParams.get("endDate"));

        return mav;
    }


}
