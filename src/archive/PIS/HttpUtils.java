package com.phantoms.phantomsbackend.common.utils.PIS;

import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by 01457141 on 2017/8/8.
 */
public class HttpUtils {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    //public static final ConnectionPool CONNECTION_POOL = new ConnectionPool(512, 5l, TimeUnit.MINUTES);
    private static OkHttpClient client = new OkHttpClient();

    //.newBuilder().connectionPool(CONNECTION_POOL).build();

    public static OkHttpClient getClient() {

        return client;
    }


    public static Response post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public static Response postaddHeaderappId(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url).addHeader("appId", "dzzyp_jy").addHeader("appKey", "Jy_2022^04#01")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public static Response get(String url, String header) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", header)
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }

    /**
     * 用于传输海顿专用
     *
     * @param url  地址
     * @param json 数据
     * @return 结果
     * @throws IOException 异常
     */
    public static Response postHaiDun(String url, String json) throws IOException {
        OkHttpClient clientHaiDun = new OkHttpClient().newBuilder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.SECONDS)
                .writeTimeout(1, TimeUnit.SECONDS)
                .build();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = clientHaiDun.newCall(request).execute();
        return response;
    }

    public static Response postWithHeader(String url, String json, Map<String,String> headers) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request.Builder  builder = new Request.Builder();
        builder.url(url).post(body);
        headers.forEach((key, value) -> {

            builder.header(key,value);
        });

        Request request = builder.build();
        Response response = client.newCall(request).execute();
        return response;
    }

    public static Response postFormDataWithHeader(String url, Map<String,String> formData,Map<String,String> headers) throws IOException {
        MultipartBody.Builder builder =  new MultipartBody.Builder();
        formData.forEach((k,v)->{
            builder.addFormDataPart(k,v);
        });

        RequestBody body = builder.build();
        Request.Builder  reqBuilder = new Request.Builder();
        headers.forEach((key, value) -> {
            reqBuilder.header(key,value);
        });
        Request request = reqBuilder
                // post 方法中传入 构造的对象
                .post(body)
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response;
    }
}
