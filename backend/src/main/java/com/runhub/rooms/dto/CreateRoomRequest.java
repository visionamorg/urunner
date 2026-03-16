package com.runhub.rooms.dto;

import lombok.Data;

@Data
public class CreateRoomRequest {
    private String name;
    private String description;
    private Boolean isPrivate = true;
}
