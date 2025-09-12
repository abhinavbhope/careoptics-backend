package com.specsShope.specsBackend.Dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentReasonStatsDTO {
    private String reason;
    private long count;
}
