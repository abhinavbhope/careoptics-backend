package com.specsShope.specsBackend.Services;

import com.cloudinary.Cloudinary;
import com.specsShope.specsBackend.Dtos.ProductDTO;
import com.specsShope.specsBackend.Mappers.ProductMapper;
import com.specsShope.specsBackend.Models.Product;
import com.specsShope.specsBackend.Models.Review;
import com.specsShope.specsBackend.Repository.ProductRepo;
import com.specsShope.specsBackend.Repository.ReviewRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService{
    @Autowired
    private ProductRepo productRepository;
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private ReviewRepo reviewRepo;
    @Autowired
    private ProductRepo productRepo;

    @Override
    public ProductDTO createProduct(ProductDTO productDTO, MultipartFile image) throws IOException {
        // Upload image
        Map uploadResult = cloudinary.uploader().upload(image.getBytes(), Map.of());
        String imageUrl = uploadResult.get("secure_url").toString();

        // Convert DTO to Entity
        Product product = ProductMapper.toEntity(productDTO);
        product.setImageUrl(imageUrl);

        // Save and return DTO
        Product savedProduct = productRepository.save(product);
        return ProductMapper.toDTO(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
    }
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
    @Override
    public ProductDTO getProductById(String id) {
        return productRepository.findById(id)
                .map(ProductMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    public ProductDTO updateProduct(String id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct == null) return null;

        // Update fields from DTO
        existingProduct.setName(productDTO.getName());
        existingProduct.setSpecstype(productDTO.getSpecstype());
        existingProduct.setGender(productDTO.getGender());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setCategory(productDTO.getCategory());
        existingProduct.setTags(productDTO.getTags());
        existingProduct.setStock(productDTO.getStock());
        existingProduct.setAverageRating(productDTO.getAverageRating());
        existingProduct.setReviewIds(productDTO.getReviewIds());

        Product updatedProduct = productRepository.save(existingProduct);
        return ProductMapper.toDTO(updatedProduct);
    }
    @Override
    public List<ProductDTO> getFilteredProducts(String keyword, String category, Double minPrice, Double maxPrice) {
        List<Product> allProducts = productRepository.findAll();

        return allProducts.stream()
                .filter(product -> keyword == null || product.getName().toLowerCase().contains(keyword.toLowerCase()))
                .filter(product -> category == null || product.getCategory().equalsIgnoreCase(category))
                .filter(product -> minPrice == null || product.getPrice() >= minPrice)
                .filter(product -> maxPrice == null || product.getPrice() <= maxPrice)
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<ProductDTO> searchProductByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Search keyword cannot be null or empty");
        }

        List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword);
        return products.stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
    }
    private void validateProductDTO(ProductDTO productDTO) {
        if (productDTO == null) {
            throw new IllegalArgumentException("Product data cannot be null");
        }

        if (productDTO.getName() == null || productDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }

        if (productDTO.getPrice() == null || productDTO.getPrice() <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0");
        }

        if (productDTO.getStock() == 0 || productDTO.getStock() < 0) {
            throw new IllegalArgumentException("Product stock cannot be negative");
        }
    }


    @Override
    public void deleteProduct(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }

        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }

        productRepository.deleteById(id);
    }



}
