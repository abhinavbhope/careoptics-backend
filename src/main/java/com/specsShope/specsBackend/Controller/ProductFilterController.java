package com.specsShope.specsBackend.Controller;

import com.specsShope.specsBackend.Dtos.ProductDTO;
import com.specsShope.specsBackend.Dtos.ProductFilter;
import com.specsShope.specsBackend.Mappers.ProductMapper;
import com.specsShope.specsBackend.Models.Product;
import com.specsShope.specsBackend.Repository.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;   // <- fix 1
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/products/")
public class ProductFilterController {

    private final com.specsShope.specsBackend.Services.ProductFilterRepository filterRepo; // <-- new

    @GetMapping("/filterPage")
    public Page<ProductDTO> filter(ProductFilter filter, Pageable pageable) {
        List<Product> filtered = filterRepo.findByFilter(filter);
        int start = (int) pageable.getOffset();
        int end   = Math.min((start + pageable.getPageSize()), filtered.size());
        List<ProductDTO> dtoPage = filtered.subList(start, end)
                .stream()
                .map(ProductMapper::toDTO)   // static
                .collect(Collectors.toList());
        return new PageImpl<>(dtoPage, pageable, filtered.size());
    }
}