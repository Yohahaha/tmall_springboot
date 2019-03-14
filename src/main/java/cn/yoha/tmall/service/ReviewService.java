package cn.yoha.tmall.service;

import cn.yoha.tmall.dao.ReviewDAO;
import cn.yoha.tmall.pojo.Product;
import cn.yoha.tmall.pojo.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "reviews")
public class ReviewService {
    @Autowired
    private ReviewDAO reviewDAO;

    public List<Review> listAllReview(Product product){
        return reviewDAO.findByProductOrderByIdDesc(product);
    }
    @CacheEvict(allEntries = true)
    public void addReview(Review bean){
        reviewDAO.save(bean);
    }

    public int countReview(Product product){
        return reviewDAO.countByProduct(product);
    }
}
