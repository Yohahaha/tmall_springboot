package cn.yoha.tmall.dao;

import cn.yoha.tmall.pojo.Category;
import cn.yoha.tmall.pojo.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDAO extends JpaRepository<Product, Integer> {
    Page<Product> findByCategory(Category category, Pageable Pageable);
}
