//package com.phantoms.phantomsbackend;
//
//import com.phantoms.phantomsbackend.common.bean.ApiResponse;
//import com.phantoms.phantomsbackend.common.bean.SearchResponse;
//import com.phantoms.phantomsbackend.common.utils.XIVAPIClient;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.util.Assert;
//
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutionException;
//
//@SpringBootTest
//class XIVAPIExample {
//
//    @Autowired
//    private XIVAPIClient xivapiClient;
//
//    @Test
//    void contextLoads() {
//        // 空测试确保上下文加载正常
//    }
//
//    @Test
//    void testGetAsset() throws ExecutionException, InterruptedException {
//        CompletableFuture<byte[]> iconFuture = xivapiClient.getAsset(
//                "ui/icon/051000/051474_hr1.tex",
//                "png",
//                null);
//
//        byte[] iconData = iconFuture.get();
//        Assert.notNull(iconData, "Icon data should not be null");
//        Assert.isTrue(iconData.length > 0, "Icon data should not be empty");
//        System.out.println("Successfully retrieved icon with size: " + iconData.length + " bytes");
//    }
//
//    @Test
//    void testSearch() throws ExecutionException, InterruptedException {
//        CompletableFuture<SearchResponse> searchFuture = xivapiClient.search(
//                "Name~\"Sword\"",
//                "Item",
//                null,
//                10,
//                "en",
//                null,
//                "Name,Icon",
//                null);
//
//        SearchResponse response = searchFuture.get();
//        Assert.notNull(response, "Search response should not be null");
//        Assert.notEmpty(response.getResults(), "Search results should not be empty");
//
//        System.out.println("Found " + response.getResults().size() + " items");
//        response.getResults().forEach(result -> {
//            System.out.printf("Item ID: %d, Name: %s, Score: %.2f%n",
//                    result.getRowId(),
//                    ((Map<?,?>)result.getFields()).get("Name"),
//                    result.getScore());
//        });
//    }
//
//    @Test
//    void testGetSheetData() throws ExecutionException, InterruptedException {
//        CompletableFuture<ApiResponse<List<Map<String, Object>>>> itemsFuture = xivapiClient.getSheetData(
//                "Item",
//                "1,2,3",
//                null,
//                null,
//                "en",
//                null,
//                "Name,Description,Icon",
//                null);
//
//        ApiResponse<List<Map<String, Object>>> response = itemsFuture.get();
//        Assert.notNull(response, "Sheet data response should not be null");
//        Assert.notEmpty(response.getData(), "Sheet data should not be empty");
//
//        System.out.println("Retrieved " + response.getData().size() + " items");
//        response.getData().forEach(item -> {
//            System.out.println("Item: " + item);
//        });
//    }
//
//    @Test
//    void testGetVersions() throws ExecutionException, InterruptedException {
//        CompletableFuture<ApiResponse<List<String>>> versionsFuture = xivapiClient.getVersions();
//        ApiResponse<List<String>> response = versionsFuture.get();
//
//        Assert.notNull(response, "Versions response should not be null");
//        Assert.notEmpty(response.getData(), "Versions list should not be empty");
//
//        System.out.println("Available versions: " + response.getData());
//    }
//}