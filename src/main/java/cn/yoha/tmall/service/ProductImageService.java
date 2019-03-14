package cn.yoha.tmall.service;

import cn.yoha.tmall.dao.ProductImageDAO;
import cn.yoha.tmall.pojo.OrderItem;
import cn.yoha.tmall.pojo.Product;
import cn.yoha.tmall.pojo.ProductImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "productImages")
public class ProductImageService   {

    public static final String TYPE_SINGLE = "single";
    public static final String TYPE_DETAIL = "detail";

    @Autowired
    ProductImageDAO productImageDAO;
    @CacheEvict(allEntries = true)
    public void add(ProductImage bean) {
        productImageDAO.save(bean);
    }
    @CacheEvict(allEntries = true)
    public void delete(int id) {
        productImageDAO.deleteById(id);
    }
    @Cacheable(key = "'productImages-one-'+#p0")
    public ProductImage get(int id) {
        return productImageDAO.getOne(id);
    }
    @Cacheable(key = "'productImages-single-pid-'+#p0.id")
    public List<ProductImage> listSingleProductImages(Product product) {
        return productImageDAO.findByProductAndTypeOrderByIdDesc(product, TYPE_SINGLE);
    }
    @Cacheable(key = "'productImages-detail-pid-'+#p0.id")
    public List<ProductImage> listDetailProductImages(Product product) {
        return productImageDAO.findByProductAndTypeOrderByIdDesc(product, TYPE_DETAIL);
    }

    public void setFirstProdutImage(Product product) {
        List<ProductImage> singleImages = listSingleProductImages(product);
        if(!singleImages.isEmpty())
            product.setFirstProductImage(singleImages.get(0));
        else
            product.setFirstProductImage(new ProductImage()); //这样做是考虑到产品还没有来得及设置图片，但是在订单后台管理里查看订单项的对应产品图片。

    }
    public void setFirstProdutImages(List<Product> products) {
        for (Product product : products)
            setFirstProdutImage(product);
    }

    public void setFirstProductImagesOnOrderItems(List<OrderItem> itemList){
        for (OrderItem item : itemList){
            setFirstProdutImage(item.getProduct());
        }
    }

}
