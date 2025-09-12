package com.specsShope.specsBackend.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueDTO {
    private String month;       // e.g. "2025-AUGUST"
    private double totalRevenue; // unique per-user sum
}
