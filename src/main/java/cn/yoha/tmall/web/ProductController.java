package cn.yoha.tmall.web;

import cn.yoha.tmall.pojo.Product;
import cn.yoha.tmall.service.ProductService;
import cn.yoha.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 展示某分类下的所有产品
     * 映射listProduct页面中的list方法
     */
    @GetMapping("/categories/{cid}/products")
    public Page4Navigator<Product> list(@PathVariable Integer cid,
                                        @RequestParam(value = "start", defaultValue = "0") Integer start,
                                        @RequestParam(value = "size", defaultValue = "5") Integer size) {
        start = start > 0 ? start : 0;
        return productService.list(cid, start, size, 5);
    }

    /**
     * 添加产品，请求由listProduct页面中的add方法发出
     * 前端已经将product类包装好了，这里直接接受json数据
     */
    @PostMapping("/products")
    public void add(@RequestBody Product bean) {
//        System.out.println(bean);
        productService.add(bean);
    }

    /**
     * 删除产品，请求由listProduct页面中的deleteBean方法发出
     */
    @DeleteMapping("/products/{id}")
    public String delete(@PathVariable Integer id) {
        productService.delete(id);
        return null;
    }

    /**
     * 获取产品数据以在editProduct页面中回显
     */
    @GetMapping("/products/{id}")
    public Object get(@PathVariable Integer id) {
        return productService.get(id);
    }

    /**
     * 更新产品信息
     */
    @PutMapping("/products")
    public void update(@RequestBody Product bean) {
        productService.update(bean);
    }
}
