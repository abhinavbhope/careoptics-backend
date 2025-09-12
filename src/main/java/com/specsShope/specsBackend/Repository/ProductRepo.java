package com.specsShope.specsBackend.Repository;

import com.specsShope.specsBackend.Models.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends MongoRepository<Product,String> {
    @Override
    List<Product> findAll();

    // Find products by category
    List<Product> findByCategory(String category);

    // Find products by name containing keyword (case insensitive)
    List<Product> findByNameContainingIgnoreCase(String keyword);

    // Find products by price range
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);

    // Find products by gender
    List<Product> findByGender(String gender);

    // Find products by specs type
    List<Product> findBySpecstype(String specstype);

    // Custom query for filtered products
    @Query("{ " +
            "  $and: [" +
            "    { $or: [ { 'name': { $regex: ?0, $options: 'i' } }, { ?0: null } ] }, " +
            "    { $or: [ { 'category': ?1 }, { ?1: null } ] }, " +
            "    { $or: [ { 'price': { $gte: ?2 } }, { ?2: null } ] }, " +
            "    { $or: [ { 'price': { $lte: ?3 } }, { ?3: null } ] }" +
            "  ]" +
            "}")
    List<Product> findFilteredProducts(String keyword, String category, Double minPrice, Double maxPrice);

    // Find products with stock greater than 0
    @Query("{ 'stock': { $gt: 0 } }")
    List<Product> findAvailableProducts();

    // Find products by rating range
    @Query("{ 'averageRating': { $gte: ?0 } }")
    List<Product> findByMinimumRating(@Param("minRating") Double minRating);
}
