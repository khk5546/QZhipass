package org.microsoft.qintelipass.dtos;

import lombok.*;
import org.microsoft.qintelipass.enums.UserStatus;
import org.microsoft.qintelipass.models.User;

import java.time.OffsetDateTime;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String phone;
    private String email;
    private UserStatus status;
    private String name;
    private OffsetDateTime joinedAt;

    public static UserDTO fromUser(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .email(user.getEmail())
                .status(user.getStatus())
                .name(user.getName())
                .joinedAt(user.getJoinedAt())
                .build();
    }

    public User toUser() {
        User user = new User();
        user.setId(this.id);
        user.setPhone(this.phone);
        user.setEmail(this.email);
        user.setStatus(this.status);
        user.setName(this.name);
        return user;
    }
}
