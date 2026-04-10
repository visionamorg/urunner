package com.runhub.events.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttendanceDto {
    private Long eventId;
    private long registeredCount;
    private long checkedInCount;
}
