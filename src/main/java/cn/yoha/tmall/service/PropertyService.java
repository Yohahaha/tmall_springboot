package cn.yoha.tmall.service;

import cn.yoha.tmall.dao.CategoryDAO;
import cn.yoha.tmall.dao.PropertyDAO;
import cn.yoha.tmall.dao.PropertyValueDAO;
import cn.yoha.tmall.pojo.Category;
import cn.yoha.tmall.pojo.Property;
import cn.yoha.tmall.pojo.PropertyValue;
import cn.yoha.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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


    public void add(Property property) {
        propertyDAO.save(property);
    }

    public Object get(Integer id) {
        return propertyDAO.getOne(id);
    }

    public void update(Property property) {
        propertyDAO.save(property);
    }

    public void delete(Integer id) {
        //先删除该属性下的所有属性值，再删除该属性
        Property property = propertyDAO.getOne(id);
        List<PropertyValue> valueList = propertyValueDAO.findByProperty(property);
        for (PropertyValue value : valueList) {
            propertyValueDAO.delete(value);
        }
        propertyDAO.deleteById(id);
    }

    public Page4Navigator<Property> list(int cid, int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Category category = categoryDAO.getOne(cid);
        Page<Property> page = propertyDAO.findByCategory(category, pageable);
        return new Page4Navigator<>(page, navigatePages);
    }
}
