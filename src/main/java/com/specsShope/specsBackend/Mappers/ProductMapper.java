package com.specsShope.specsBackend.Mappers;

import com.specsShope.specsBackend.Dtos.ProductDTO;
import com.specsShope.specsBackend.Models.Product;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class ProductMapper {
    public static ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .specstype(product.getSpecstype())
                .gender(product.getGender())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .category(product.getCategory())
                .tags(product.getTags())
                .stock(product.getStock())
                .averageRating(product.getAverageRating())
                .reviewIds(product.getReviewIds() != null ? product.getReviewIds() : List.of())
                .reviewCount(product.getReviewIds() != null ? product.getReviewIds().size() : 0)
                .build();
    }

    public static Product toEntity(ProductDTO productDTO) {
        return Product.builder()
                .id(productDTO.getId())
                .name(productDTO.getName())
                .specstype(productDTO.getSpecstype())
                .gender(productDTO.getGender())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .imageUrl(productDTO.getImageUrl())
                .category(productDTO.getCategory())
                .tags(productDTO.getTags())
                .stock(productDTO.getStock() != null ? productDTO.getStock() : 0)
                .averageRating(productDTO.getAverageRating() != null ? productDTO.getAverageRating() : 0.0)
                .reviewIds(productDTO.getReviewIds())
                .build();
    }
}