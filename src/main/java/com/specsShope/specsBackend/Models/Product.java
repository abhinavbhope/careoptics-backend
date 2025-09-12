package com.specsShope.specsBackend.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    private String id;

    private String name;
    private String specstype;
    private String gender;//for men or women;
    private String description;
    private Double price;
    private String imageUrl;
    private String category;
    private List<String> tags;

    private int stock; // Inventory count
    private double averageRating;

    private List<String> reviewIds;
    private int reviewCount; // total number of reviews

    @CreatedDate
    private LocalDateTime createdAt;

}
