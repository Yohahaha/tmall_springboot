package cn.yoha.tmall.web;

import cn.yoha.tmall.pojo.Product;
import cn.yoha.tmall.pojo.PropertyValue;
import cn.yoha.tmall.service.ProductService;
import cn.yoha.tmall.service.PropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PropertyValueController {
    @Autowired
    private ProductService productService;
    @Autowired
    private PropertyValueService propertyValueService;

    @GetMapping("/products/{pid}/propertyValues")
    public List<PropertyValue> list(@PathVariable Integer pid) {
        //通过pid查到对应的产品
        Product product = (Product) productService.get(pid);
        //对该产品下的所有空属性值进行初始化
        propertyValueService.init(product);
        //得到所有的属性值
        List<PropertyValue> list = propertyValueService.list(product);
//        System.out.println(list);
        return list;
    }

    @PutMapping("/propertyValues")
    public PropertyValue update(@RequestBody PropertyValue bean) {
        return propertyValueService.update(bean);
    }
}
