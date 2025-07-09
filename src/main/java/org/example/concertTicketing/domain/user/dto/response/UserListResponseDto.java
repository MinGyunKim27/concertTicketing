package org.example.concertTicketing.domain.user.dto.response;

import java.util.List;

public class UserListResponseDto {
    private List<UserDto> userList;

    public UserListResponseDto(List<UserDto> userList) {
        this.userList = userList;
    }

    public List<UserDto> getUserList() {
        return userList;
    }
}
