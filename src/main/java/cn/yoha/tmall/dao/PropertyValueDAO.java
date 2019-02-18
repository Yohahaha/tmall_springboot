package cn.yoha.tmall.dao;

import cn.yoha.tmall.pojo.Product;
import cn.yoha.tmall.pojo.Property;
import cn.yoha.tmall.pojo.PropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyValueDAO extends JpaRepository<PropertyValue, Integer> {
    /**
     * 查询某一产品的所有属性值
     */
    List<PropertyValue> findByProductOrderByIdDesc(Product product);

    /**
     * 根据产品和属性名称查询对应的属性值
     */
    PropertyValue getByProductAndPropertyOrderByIdDesc(Product product, Property property);

    List<PropertyValue> findByProperty(Property property);
}
