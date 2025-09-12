package com.specsShope.specsBackend.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallbackStatsDTO {
    private long total;
    private long completed;
    private long pending;
}
