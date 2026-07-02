package org.microsoft.qintelipass.logins;

import lombok.extern.slf4j.Slf4j;
import org.microsoft.qintelipass.ILoginStrategy;
import org.microsoft.qintelipass.enums.UserStatus;
import org.microsoft.qintelipass.models.User;
import org.microsoft.qintelipass.response.ResponseBody;
import org.microsoft.qintelipass.services.RedisService;
import org.microsoft.qintelipass.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class MobileCodeLoginStrategy implements ILoginStrategy {
    @Autowired
    private RedisService redisService;
    
    @Autowired
    private UserService userService;
    
    public boolean validate(String phone, String smsCode) {
        return phone == null || smsCode == null || phone.length() != 11 || smsCode.length() != 6;
    }

    @Override
    public String getType() {
        return "mobile";  // 与前端的 loginType 匹配
    }

    @Override
    public ResponseBody authenticate(Map<String, Object> params) {
        // 支持多种参数名：phone/phone_number, code/sms
        String phone = (String) params.get("phone");
        if (phone == null) {
            phone = (String) params.get("phone_number");
        }
        
        String smsCode = (String) params.get("code");
        if (smsCode == null) {
            smsCode = (String) params.get("sms");
        }
        
        log.info("User phone: {}, User smsCode: {}", phone, smsCode);
        
        if (smsCode == null || phone == null){
            return new ResponseBody(false, "smsCode or phone number could not be NULL.");
        }
        if (this.validate(phone, smsCode)){
            return new ResponseBody(false, "Invalid smsCode or phone.");
        }
        
        User user = userService.getUserByPhone(phone);
        if (user != null && UserStatus.CANCELLED.equals(user.getStatus())) {
            return new ResponseBody(false, "Your account has been cancelled");
        }
        
        String targetSmsCode = (String) redisService.getValue(phone);

        if (targetSmsCode != null) {
            if (targetSmsCode.equals(smsCode)){
                // 登录成功，返回用户信息（使用前面已经查询的user对象）
                if (user != null) {
                    ResponseBody response = new ResponseBody(true, "Login Successful.");
                    response.setData(Map.of(
                        "id", String.valueOf(user.getId()),
                        "name", user.getName(),
                        "phone", user.getPhone(),
                        "status", user.getStatus().name()
                    ));
                    return response;
                }
                return new ResponseBody(true, "Login Successful.");
            }
        }
        return new ResponseBody(false, "Wrong smsCode.");
    }
}
