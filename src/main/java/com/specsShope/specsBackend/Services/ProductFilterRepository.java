package com.specsShope.specsBackend.Services;

import com.specsShope.specsBackend.Dtos.ProductFilter;
import com.specsShope.specsBackend.Models.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Repository   // or @Service, either works
@RequiredArgsConstructor
public class ProductFilterRepository {

    private final MongoTemplate mongo;

    public List<Product> findByFilter(ProductFilter f) {
        List<Criteria> c = new ArrayList<>();

        if (f.getSearch() != null && !f.getSearch().isBlank()) {
            Pattern p = Pattern.compile(f.getSearch(), Pattern.CASE_INSENSITIVE);
            c.add(new Criteria().orOperator(
                    Criteria.where("name").regex(p),
                    Criteria.where("description").regex(p)
            ));
        }
        if (f.getCategory() != null && !f.getCategory().isEmpty())
            c.add(Criteria.where("category").in(f.getCategory()));
        if (f.getGender() != null && !f.getGender().isEmpty())
            c.add(Criteria.where("gender").in(f.getGender()));
        if (f.getSpecsType() != null && !f.getSpecsType().isEmpty())
            c.add(Criteria.where("specsType").in(f.getSpecsType()));
        if (f.getTags() != null && !f.getTags().isEmpty())
            c.add(Criteria.where("tags").in(f.getTags()));
        if (f.getMinPrice() != null)
            c.add(Criteria.where("price").gte(f.getMinPrice()));
        if (f.getMaxPrice() != null)
            c.add(Criteria.where("price").lte(f.getMaxPrice()));
        if (Boolean.TRUE.equals(f.getInStock()))
            c.add(Criteria.where("stock").gt(0));
        if (f.getMinRating() != null)
            c.add(Criteria.where("averageRating").gte(f.getMinRating()));

        Query q = new Query();
        if (!c.isEmpty()) q.addCriteria(new Criteria().andOperator(c.toArray(new Criteria[0])));

        return mongo.find(q, Product.class);
    }
}