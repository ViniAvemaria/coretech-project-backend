package com.vinicius.coretech.specs;

import com.vinicius.coretech.entity.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecs {
    public static Specification<Product> hasCategory(String categoryName) {
        return (root, query, cb) -> {
            if (categoryName == null || categoryName.isEmpty()) return null;
            return cb.equal(cb.lower(root.get("category").get("name")), categoryName.toLowerCase());
        };
    }

    public static Specification<Product> hasSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isEmpty()) return null;
            String like = "%" + search.toLowerCase() + "%";
            return cb.like(cb.lower(root.get("name")), like);
        };
    }
}
