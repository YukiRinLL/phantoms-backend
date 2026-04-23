package com.phantoms.phantomsbackend.common.utils;

import ai.onnxruntime.*;
import java.util.Map;

public class LiteEmbeddingSimilarity {

    // 全局唯一模型（只加载1次，内存固定）
    private static OrtEnvironment env;
    private static OrtSession session;

    static {
        try {
            // 初始化 ONNX 环境（极轻量）
            env = OrtEnvironment.getEnvironment();
            // 模型路径（放入 resources 下）
            String modelPath = LiteEmbeddingSimilarity.class.getResource("/model/onnx/model.onnx").getPath();
            
            // 会话配置（最小内存）
            OrtSession.SessionOptions opts = new OrtSession.SessionOptions();
            opts.setIntraOpNumThreads(1);  // 单线程，省内存
            opts.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.BASIC_OPT);
            
            // 加载模型（22MB）
            session = env.createSession(modelPath, opts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===================== 核心：生成向量 =====================
    public static float[] getEmbedding(String text) throws Exception {
        try (OrtSession.Result result = session.run(Map.of(
            "input_ids", OnnxTensor.createTensor(env, new long[][]{{101, 102}}), // 简化示例，真实需替换为 token 结果
            "attention_mask", OnnxTensor.createTensor(env, new long[][]{{1, 1}})
        ))) {
            return (float[]) result.get(0).getValue();
        }
    }

    // ===================== 余弦相似度 =====================
    public static double cosineSim(float[] a, float[] b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // ===================== 对外接口 =====================
    public static double similarity(String name1, String name2) throws Exception {
        float[] v1 = getEmbedding(name1);
        float[] v2 = getEmbedding(name2);
        return cosineSim(v1, v2);
    }
}