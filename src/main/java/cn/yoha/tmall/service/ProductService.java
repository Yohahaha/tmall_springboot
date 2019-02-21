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

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductDAO productDAO;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductImageService productImageService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private OrderItemService orderItemService;

    /**
     * 根据分类id查询对应产品列表，并且实现分页方法
     */
    public Page4Navigator<Product> list(int cid, int start, int size, int navigatorPages) {
        Category category = categoryService.get(cid);
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page<Product> page = productDAO.findByCategory(category, pageable);
        return new Page4Navigator<>(page, navigatorPages);
    }

    /**
     * 对产品的crud方法
     */
    public Object get(Integer id) {
        return productDAO.getOne(id);
    }

    public void add(Product product) {
        productDAO.save(product);
    }

    public void delete(Integer id) {
        productDAO.deleteById(id);
    }

    public void update(Product product) {
        productDAO.save(product);
    }

    public List<Product> listByCategory(Category category) {
        return productDAO.findByCategoryOrderById(category);
    }

    public void fill(List<Category> categories) {
        for (Category c : categories) {
            fill(c);
        }
    }

    public void fill(Category category) {
        List<Product> products = listByCategory(category);
        productImageService.setFirstProdutImages(products);
        category.setProducts(products);
    }

    public void fillByRow(List<Category> categories) {
        int productNumberEachRow = 8;
        for (Category category : categories) {
            List<Product> products = category.getProducts();
            List<List<Product>> productByRow = new ArrayList<>();
            for (int i = 0; i < products.size(); i += productNumberEachRow) {
                int size = i + productNumberEachRow;
                size = size > products.size() ? products.size() : size;
                List<Product> list = products.subList(i, size);
                productByRow.add(list);
            }
            category.setProductsByRow(productByRow);
        }
    }

    public void setSaleAndReview(List<Product> productList) {
        for (Product product : productList) {
            setSaleAndReview(product);
        }
    }

    public void setSaleAndReview(Product product) {
        int countReview = reviewService.countReview(product);
        product.setReviewCount(countReview);
        Integer saleCount = orderItemService.getSaleCount(product);
        product.setSaleCount(saleCount);
    }

    public List<Product> search(String key, int start, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        List<Product> productList = productDAO.findByNameLike("%" + key + "%", pageable);
        return productList;
    }
}
