package cn.yoha.tmall.web;

import cn.yoha.tmall.pojo.Property;
import cn.yoha.tmall.service.PropertyService;
import cn.yoha.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    /**
     * 根据cid查询其对应的属性数据，并实现分页功能
     *
     * @param cid 类别数据的id
     */
    @GetMapping("/categories/{cid}/properties")
    public Page4Navigator<Property> list(@PathVariable Integer cid,
                                         @RequestParam(value = "start", defaultValue = "0") int start) {
        start = start > 0 ? start : 0;
        return propertyService.list(cid, start, 5, 5);
    }

    /**
     * 添加属性数据
     * 由于前端页面不是通过表单post方式提交，所有请求数据是json数据，通过@RequestBody注解绑定到property上
     */
    @PostMapping("/properties")
    public void add(@RequestBody Property bean) {
//        System.out.println(bean);
        propertyService.add(bean);
    }

    /**
     * 编辑属性数据，主要是提供回显功能
     */
    @GetMapping("/properties/{id}")
    public Object edit(@PathVariable Integer id) {
        return propertyService.get(id);
    }

    /**
     * 修改属性数据
     * 前端页面提交时不是通过表单post提交，传来的是json数据，要通过@RequestBody注解来接收
     */
    @PutMapping("/properties")
    public void update(@RequestBody Property bean) {
//        System.out.println(bean);
        propertyService.update(bean);
    }

    /**
     * 删除属性数据
     */
    @DeleteMapping("/properties/{id}")
    public void delete(@PathVariable Integer id) {
        propertyService.delete(id);
    }
}
