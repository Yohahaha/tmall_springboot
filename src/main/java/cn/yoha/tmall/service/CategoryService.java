package cn.yoha.tmall.service;

import cn.yoha.tmall.dao.CategoryDAO;
import cn.yoha.tmall.pojo.Category;
import cn.yoha.tmall.pojo.Product;
import cn.yoha.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryDAO categoryDAO;

    @Autowired
    public CategoryService(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    /**
     * 降序查询所有分类数据
     */
    public List<Category> list() {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return categoryDAO.findAll(sort);
    }

    /**
     * 查询分页数据
     *
     * @param start         起始下标
     * @param size          查询数量
     * @param navigatePages 分页显示格式
     * @return 分页数据
     */
    public Page4Navigator<Category> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page pageFromJPA = categoryDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA, navigatePages);
    }

    /**
     * 新增分类数据
     */
    public void add(Category bean) {
        categoryDAO.save(bean);
    }

    /**
     * 删除分类数据
     */
    public void delete(int id) {
        categoryDAO.deleteById(id);
    }

    /**
     * 编辑类别
     */
    public Category editById(Integer id) {
        return categoryDAO.getOne(id);
    }

    /**
     * 更新类别名称
     */
    public void updateCategory(Category category) {
        categoryDAO.save(category);
    }

    public Category get(int id) {
        return categoryDAO.getOne(id);
    }

    public void removeCategoryFromProduct(List<Category> cs) {
        for (Category category : cs) {
            removeCategoryFromProduct(category);
        }
    }

    public void removeCategoryFromProduct(Category category) {
        List<Product> products = category.getProducts();
        if (null != products) {
            for (Product product : products) {
                product.setCategory(null);
            }
        }
        List<List<Product>> productsByRow = category.getProductsByRow();
        if (null != productsByRow) {
            for (List<Product> ps : productsByRow) {
                for (Product p : ps) {
                    p.setCategory(null);
                }
            }
        }
    }
}
