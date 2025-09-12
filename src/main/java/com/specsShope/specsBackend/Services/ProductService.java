package com.specsShope.specsBackend.Services;

import com.specsShope.specsBackend.Dtos.ProductDTO;
import com.specsShope.specsBackend.Models.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    ProductDTO createProduct(ProductDTO productDTO, MultipartFile image) throws IOException;
    List<ProductDTO> getAllProducts();
    ProductDTO getProductById(String id);
    ProductDTO updateProduct(String id, ProductDTO productDTO);
    void deleteProduct(String id);
    List<ProductDTO> getFilteredProducts(String keyword, String category, Double minPrice, Double maxPrice);
    List<ProductDTO> searchProductByName(String keyword);

}