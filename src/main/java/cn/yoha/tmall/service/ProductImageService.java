package cn.yoha.tmall.service;

import cn.yoha.tmall.dao.ProductImageDAO;
import cn.yoha.tmall.pojo.Product;
import cn.yoha.tmall.pojo.ProductImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductImageService {
    public static String TYPE_SINGLE = "single";
    public static String TYPE_DETAIL = "detail";
    @Autowired
    private ProductImageDAO productImageDAO;

    public void add(ProductImage bean) {
        productImageDAO.save(bean);
    }

    public Object get(Integer id) {
        return productImageDAO.getOne(id);
    }

    public void delete(Integer id) {
        productImageDAO.deleteById(id);
    }

    /**
     * 展示单一图片
     */
    public List<ProductImage> listSingleProImages(Product product) {
        return productImageDAO.findByProductAndTypeOrderByIdDesc(product, TYPE_SINGLE);
    }

    /**
     * 展示详情图片
     */
    public List<ProductImage> listDetailProImages(Product product) {
        return productImageDAO.findByProductAndTypeOrderByIdDesc(product, TYPE_DETAIL);
    }

    private void setFirstImg(Product product) {
        List<ProductImage> images = listSingleProImages(product);
        if (!images.isEmpty()) {
            product.setFirstImg(images.get(0));
        } else {
            product.setFirstImg(new ProductImage());
        }
    }

    /**
     * 设置产品中的图片属性
     */
    public void setFirstImgs(List<Product> productList) {
        for (Product product : productList) {
            setFirstImg(product);
        }
    }
}
