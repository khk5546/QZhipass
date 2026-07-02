package org.microsoft.qintelipass.response;

import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
public class ResponseBody {
    public ResponseBody(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    private boolean success;
    private String message;
    private Map<String, String> data;  // 添加 data 字段，用于返回用户数据
}
