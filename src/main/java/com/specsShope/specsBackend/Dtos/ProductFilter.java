package com.specsShope.specsBackend.Dtos;

import lombok.Value;
import java.util.List;

@Value
public class ProductFilter {
    String search;             // free text search in name or description
    List<String> category;
    List<String> gender;
    List<String> specsType;
    List<String> tags;
    Double minPrice;
    Double maxPrice;
    Boolean inStock;
    Double minRating;
}

