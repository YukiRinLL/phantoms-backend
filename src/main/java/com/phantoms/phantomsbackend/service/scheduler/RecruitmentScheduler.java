package com.phantoms.phantomsbackend.service.scheduler;

import com.phantoms.phantomsbackend.common.utils.LittlenightmareClient;
import com.phantoms.phantomsbackend.pojo.entity.RecruitmentResponse;
import com.phantoms.phantomsbackend.pojo.entity.primary.Recruitment;
import com.phantoms.phantomsbackend.repository.primary.RecruitmentRepository;
import com.phantoms.phantomsbackend.service.OneBotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecruitmentScheduler {

    private static final Logger logger = LoggerFactory.getLogger(RecruitmentScheduler.class);

    @Autowired
    private OneBotService oneBotService;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

//    @Scheduled(fixedRate = 300000) // 每300秒执行一次
    @Scheduled(cron = "0 30 6,11,15 * * ?")
    /* TODO: Cloudflare 返回了一个 HTML 页面，提示用户“Just a moment...”，这表明 Cloudflare 正在对请求进行安全检查。
        这种页面通常包含 JavaScript 代码，用于验证请求是否来自真实的浏览器。
        部署在Render上的实例没法通过验证（本地运行没有问题），目前使用限额的scrapestack
    */
    public void fetchAndFilterRecruitments() {
        try {
            List<RecruitmentResponse> allResponses = LittlenightmareClient.fetchAllRecruitmentListings(
                    100, // perPage
                    null, // category
                    null, // world
                    null, // search
                    null, // datacenter
                    null, // jobs
                    null  // duties
            );

            // Flatten the list of responses into a single list of recruitments
            List<Recruitment> allRecruitments = allResponses.stream()
                    .flatMap(response -> response.getData().stream())
                    .collect(Collectors.toList());

            // 保存所有获取到的招募信息到数据库
            recruitmentRepository.saveAll(allRecruitments);

            logger.info("Successfully fetched and saved {} recruitments", allRecruitments.size());

//            // 筛选符合条件的招募信息
//            List<Recruitment> filteredRecruitments = allRecruitments.stream()
//                    .filter(recruitment -> recruitment.getDescription().contains("HQ"))
//                    .collect(Collectors.toList());
//
//            if (!filteredRecruitments.isEmpty()) {
//                System.out.println("找到符合条件的招募信息：");
//
//                filteredRecruitments.forEach(recruitment -> {
//                    System.out.println(recruitment);
//                    // 发送通知
//                    try {
//                        oneBotService.sendGroupMessageWithDefaultGroup(
//                                "[招募信息] (" + recruitment.getName() + ")\n" +
//                                        "Category: " + recruitment.getCategory() + "\n" +
//                                        "Duty: " + recruitment.getDuty() + "\n" +
//                                        recruitment.getDescription() + "\n" +
//                                        "HomeWorld: " + recruitment.getHomeWorld() + "\n" +
//                                        "Posted: " + recruitment.getDatacenter() + "-" + recruitment.getCreatedWorld() + "\n" +
//                                        "UpdatedAt: " + recruitment.getUpdatedAt() + "\n" +
//                                        "TimeLeft: " + recruitment.getTimeLeft() + "s\n",
//                                null//"787909466"
//                        );
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                });
//            } else {
//                System.out.println("未找到符合条件的招募信息");
//            }
        } catch (Exception e) {
            logger.error("Failed to fetch and filter recruitments", e);
        }
    }
}