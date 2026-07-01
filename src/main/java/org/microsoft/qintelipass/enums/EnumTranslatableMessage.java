package org.microsoft.qintelipass.enums;

import lombok.Getter;

@Getter
public enum EnumTranslatableMessage {
    USER_LOGIN_SUCCESSFUL("user.successful.login"),
    USER_LOGOUT_SUCCESSFUL("user.successful.logout"),
    USER_REGISTER_SUCCESSFUL("user.successful.register"),
    USER_LOGIN_WRONG_PASSWORD("user.failed.login.wrong."),
    USER_LOGIN_EMAIL_NOTFOUND("user.successful.login"),
    USER_LOGIN_BANNED("user.successful.login"),
    USER_INVALID_PHONE("user.successful.login"),
    ;
    private final String translationKey;
    EnumTranslatableMessage(String translationKey) {
        this.translationKey = translationKey;
    }
}
