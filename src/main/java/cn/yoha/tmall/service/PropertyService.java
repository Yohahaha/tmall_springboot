package cn.yoha.tmall.service;

import cn.yoha.tmall.dao.CategoryDAO;
import cn.yoha.tmall.dao.PropertyDAO;
import cn.yoha.tmall.dao.PropertyValueDAO;
import cn.yoha.tmall.pojo.Category;
import cn.yoha.tmall.pojo.Property;
import cn.yoha.tmall.pojo.PropertyValue;
import cn.yoha.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "properties")
public class PropertyService {

    @Autowired
    private CategoryDAO categoryDAO;
    @Autowired
    private PropertyValueDAO propertyValueDAO;
    private PropertyDAO propertyDAO;

    @Autowired
    public PropertyService(PropertyDAO propertyDAO) {
        this.propertyDAO = propertyDAO;
    }

    @CacheEvict(allEntries = true)
    public void add(Property property) {
        propertyDAO.save(property);
    }
    @Cacheable(key = "'properties-one-'+#p0")
    public Object get(Integer id) {
        return propertyDAO.getOne(id);
    }
    @CacheEvict(allEntries = true)
    public void update(Property property) {
        propertyDAO.save(property);
    }
    @CacheEvict(allEntries = true)
    public void delete(Integer id) {
        //先删除该属性下的所有属性值，再删除该属性
        Property property = propertyDAO.getOne(id);
        List<PropertyValue> valueList = propertyValueDAO.findByProperty(property);
        for (PropertyValue value : valueList) {
            propertyValueDAO.delete(value);
        }
        propertyDAO.deleteById(id);
    }
    @Cacheable(key = "'properties-cid-'+@p0+'-page'+#p1+'-'+#p2")
    public Page4Navigator<Property> list(int cid, int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Category category = categoryDAO.getOne(cid);
        Page<Property> page = propertyDAO.findByCategory(category, pageable);
        return new Page4Navigator<>(page, navigatePages);
    }
}
