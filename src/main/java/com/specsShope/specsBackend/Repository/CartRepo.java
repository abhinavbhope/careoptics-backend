package com.specsShope.specsBackend.Repository;

import com.specsShope.specsBackend.Models.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CartRepo extends MongoRepository<Cart,String> {

    Optional<Cart> findByUserId(String userId);
}
