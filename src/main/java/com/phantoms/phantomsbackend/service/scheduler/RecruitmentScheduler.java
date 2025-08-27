package com.phantoms.phantomsbackend.service.scheduler;

import com.phantoms.phantomsbackend.common.utils.LittlenightmareClient;
import com.phantoms.phantomsbackend.pojo.entity.RecruitmentResponse;
import com.phantoms.phantomsbackend.pojo.entity.primary.Recruitment;
import com.phantoms.phantomsbackend.repository.primary.RecruitmentRepository;
import com.phantoms.phantomsbackend.service.OneBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecruitmentScheduler {

    @Autowired
    private OneBotService oneBotService;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Scheduled(fixedRate = 300000) // 每300秒执行一次
    public void fetchAndFilterRecruitments() {
        try {
            List<RecruitmentResponse> allResponses = LittlenightmareClient.fetchAllRecruitmentListings(
                    100, // perPage
                    null, // category
                    null, // world
                    null, // search
                    "莫古力", // datacenter
                    null, // jobs
                    null  // duties
            );

            // Flatten the list of responses into a single list of recruitments
            List<Recruitment> allRecruitments = allResponses.stream()
                    .flatMap(response -> response.getData().stream())
                    .collect(Collectors.toList());

            // 保存所有获取到的招募信息到数据库
            recruitmentRepository.saveAll(allRecruitments);

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
            e.printStackTrace();
        }
    }
}