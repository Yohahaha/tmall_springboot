package cn.yoha.tmall.dao;

import cn.yoha.tmall.pojo.Product;
import cn.yoha.tmall.pojo.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageDAO extends JpaRepository<ProductImage,Integer> {
    /**
     * 根据产品id和类别查询，结果按主键降序排列
     */
    List<ProductImage> findByProductAndTypeOrderByIdDesc(Product product, String type);
}
