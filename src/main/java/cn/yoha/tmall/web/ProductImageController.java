package cn.yoha.tmall.web;

import cn.yoha.tmall.pojo.Product;
import cn.yoha.tmall.pojo.ProductImage;
import cn.yoha.tmall.service.ProductImageService;
import cn.yoha.tmall.service.ProductService;
import cn.yoha.tmall.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductImageController {
    @Autowired
    private ProductImageService productImageService;
    @Autowired
    private ProductService productService;

    /**
     * 获取指定产品下的所有图片
     * @param pid 产品id
     * @param type 图片类型
     */
    @GetMapping("/products/{pid}/productImages")
    public List<ProductImage> list(@PathVariable Integer pid, @RequestParam(value = "type") String type) throws Exception {
        Product product = (Product) productService.get(pid);
        //判断要展示的图片类型
        if (ProductImageService.TYPE_SINGLE.equals(type)) {
            List<ProductImage> proImages = productImageService.listSingleProductImages(product);
            return proImages;
        } else if (ProductImageService.TYPE_DETAIL.equals(type)) {
            List<ProductImage> proImages = productImageService.listDetailProductImages(product);
            return proImages;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 给产品添加图片
     * @param pid 产品id
     * @param type 图片属性
     * @param image 图片
     */
    @PostMapping("/productImages")
    public Object addProductImage(@RequestParam(value = "pid") Integer pid,
                                  @RequestParam(value = "type") String type,
                                  MultipartFile image, HttpServletRequest request) throws Exception {
        //new一个产品图片类，对其添加属性
        ProductImage bean = new ProductImage();
        Product product =(Product) productService.get(pid);
        bean.setProduct(product);
        bean.setType(type);

        productImageService.add(bean);
        String folder = "img/";
        if(ProductImageService.TYPE_SINGLE.equals(bean.getType())){
            folder +="productSingle";
        }
        else{
            folder +="productDetail";
        }
        File  imageFolder= new File(request.getServletContext().getRealPath(folder));
        File file = new File(imageFolder,bean.getId()+".jpg");
        String fileName = file.getName();
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        try {
            image.transferTo(file);
            BufferedImage img = ImageUtil.change2jpg(file);
            ImageIO.write(img, "jpg", file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(ProductImageService.TYPE_SINGLE.equals(bean.getType())){
            String imageFolder_small= request.getServletContext().getRealPath("img/productSingle_small");
            String imageFolder_middle= request.getServletContext().getRealPath("img/productSingle_middle");
            File f_small = new File(imageFolder_small, fileName);
            File f_middle = new File(imageFolder_middle, fileName);
            f_small.getParentFile().mkdirs();
            f_middle.getParentFile().mkdirs();
            ImageUtil.resizeImage(file, 56, 56, f_small);
            ImageUtil.resizeImage(file, 217, 190, f_middle);
        }

        return bean;
    }

    /**
     * 删除产品图片
     */
    @DeleteMapping("/productImages/{id}")
    public String delete(@PathVariable Integer id, HttpServletRequest request) throws Exception {
        ProductImage bean = productImageService.get(id);
        productImageService.delete(id);

        String folder = "img/";
        if(ProductImageService.TYPE_SINGLE.equals(bean.getType()))
            folder +="productSingle";
        else
            folder +="productDetail";

        File  imageFolder= new File(request.getServletContext().getRealPath(folder));
        File file = new File(imageFolder,bean.getId()+".jpg");
        String fileName = file.getName();
        file.delete();
        if(ProductImageService.TYPE_SINGLE.equals(bean.getType())){
            String imageFolder_small= request.getServletContext().getRealPath("img/productSingle_small");
            String imageFolder_middle= request.getServletContext().getRealPath("img/productSingle_middle");
            File f_small = new File(imageFolder_small, fileName);
            File f_middle = new File(imageFolder_middle, fileName);
            f_small.delete();
            f_middle.delete();
        }

        return null;
    }

}
