package cn.yoha.tmall.service;

import cn.yoha.tmall.dao.ProductDAO;
import cn.yoha.tmall.pojo.Category;
import cn.yoha.tmall.pojo.Product;
import cn.yoha.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    @Autowired
    private ProductDAO productDAO;
    @Autowired
    private CategoryService categoryService;

    /**
     * 根据分类id查询对应产品列表，并且实现分页方法
     */
    public Page4Navigator<Product> list(int cid, int start, int size, int navigatorPages){
        Category category = categoryService.get(cid);
        Sort sort = new Sort(Sort.Direction.DESC,"id");
        Pageable pageable = new PageRequest(start,size,sort);
        Page<Product> page = productDAO.findByCategory(category, pageable);
        return new Page4Navigator<>(page,navigatorPages);
    }

    /**
     * 对产品的crud方法
     */
    public Object get(Integer id){
        return productDAO.getOne(id);
    }
    public void add(Product product){
        productDAO.save(product);
    }
    public void delete(Integer id){
        productDAO.deleteById(id);
    }
    public void update(Product product){
        productDAO.save(product);
    }
}
