package cn.yoha.tmall.service;

import cn.yoha.tmall.dao.PropertyDAO;
import cn.yoha.tmall.dao.PropertyValueDAO;
import cn.yoha.tmall.pojo.Product;
import cn.yoha.tmall.pojo.Property;
import cn.yoha.tmall.pojo.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "propertyValues")
public class PropertyValueService {
    @Autowired
    private PropertyValueDAO propertyValueDAO;
    @Autowired
    private PropertyDAO propertyDAO;


    /**
     * 对该分类已经有的属性值进行初始化
     */
    public void init(Product product) {
        List<Property> propertyList = propertyDAO.findByCategory(product.getCategory());
        for (Property property : propertyList) {
            PropertyValue value = propertyValueDAO.getByProductAndPropertyOrderByIdDesc(product, property);
            if (value == null) {
                //如果该属性还没有对于的值，则将其初始化
                PropertyValue bean = new PropertyValue();
                bean.setProduct(product);
                bean.setProperty(property);
                propertyValueDAO.save(bean);
            }
        }
    }

    /**
     * 查询该产品下的所有属性值
     */
    @Cacheable(key = "'propertyValues-pid-'+#p0.id")
    public List<PropertyValue> list(Product product) {
        return propertyValueDAO.findByProductOrderByIdDesc(product);
    }

    /**
     * 更新属性值
     */
    @CacheEvict(allEntries = true)
    public PropertyValue update(PropertyValue bean) {
        return propertyValueDAO.save(bean);
    }

}
