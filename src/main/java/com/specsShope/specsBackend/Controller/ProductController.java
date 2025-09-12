package com.specsShope.specsBackend.Controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.specsShope.specsBackend.Dtos.ProductDTO;
import com.specsShope.specsBackend.Services.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/products")
@SecurityRequirement(name = "bearerAuth")

public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductDTO createProduct(
            @RequestPart("product") String productJson,
            @RequestPart("image") MultipartFile image
    ) throws IOException {
        // Parse JSON string to ProductDTO
        ObjectMapper objectMapper = new ObjectMapper();
        ProductDTO productDTO = objectMapper.readValue(productJson, ProductDTO.class);

        return productService.createProduct(productDTO, image);
    }

    @GetMapping("/allItems")
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable String id) {
        ProductDTO product = productService.getProductById(id);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        return product;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductDTO updateProduct(
            @PathVariable String id,
            @RequestBody ProductDTO productDTO
    ) {
        ProductDTO updated = productService.updateProduct(id, productDTO);
        if (updated == null) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        return updated;
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }
    @GetMapping("/filter")
    public List<ProductDTO> filterProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        return productService.getFilteredProducts(keyword, category, minPrice, maxPrice);
    }
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProduct(@RequestParam("name") String name) {
        List<ProductDTO> results = productService.searchProductByName(name);
        return ResponseEntity.ok(results);
    }



}
