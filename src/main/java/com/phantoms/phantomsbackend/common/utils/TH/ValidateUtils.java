package com.phantoms.phantomsbackend.common.utils.TH;

import cn.hutool.core.collection.CollectionUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.Builder;
import org.hibernate.validator.HibernateValidator;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author panzhang
 */
public class ValidateUtils {
    /**
     * 校验结果
     */
    @Builder
    public static class Result {

        /**
         * 通过标识
         */
        private boolean isPassed;
        /**
         * 错误信息
         */
        private String errorMsg;

        public boolean isPassed() {
            return isPassed;
        }

        public String getErrorMsg() {
            return errorMsg;
        }
    }

    /**
     * 校验器
     */
    private static final Validator VALIDATOR = Validation.byProvider(HibernateValidator.class)
            .configure()
            .failFast(false)
            .buildValidatorFactory()
            .getValidator();

    public static <T> Result validate(T t, Class<?>... group) {
        String msg = "";
        Set<ConstraintViolation<T>> cvs = VALIDATOR.validate(t, group);
        if (CollectionUtil.isNotEmpty(cvs)) {
            msg = cvs.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(","));
        }
        if (StringUtils.hasLength(msg)) {
            return Result.builder()
                    .isPassed(false)
                    .errorMsg(msg)
                    .build();
        } else {
            return Result.builder()
                    .isPassed(true)
                    .errorMsg(msg)
                    .build();
        }
    }

    public static <T> Result validate(List<T> tList, Class<?>... group) {
        String msg = "";
        for (int i = 0; i < tList.size(); i++) {
            Result result = validate(tList.get(i), group);
            if (!result.isPassed()) {
                msg += "第" + (i + 1) + "行：";
                msg += result.getErrorMsg() + ";";
            }
        }
        if (StringUtils.hasLength(msg)) {
            return Result.builder()
                    .isPassed(false)
                    .errorMsg(msg.substring(0, msg.length() - 1))
                    .build();
        } else {
            return Result.builder()
                    .isPassed(true)
                    .errorMsg(msg)
                    .build();
        }
    }

    public static <T> void execute(T t, Class<?>... group) {
        Result result = validate(t, group);
        if (!result.isPassed()) {
            throw new RuntimeException(result.getErrorMsg());
        }
    }
}
