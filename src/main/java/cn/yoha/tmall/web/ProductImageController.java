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
            List<ProductImage> proImages = productImageService.listSingleProImages(product);
            return proImages;
        } else if (ProductImageService.TYPE_DETAIL.equals(type)) {
            List<ProductImage> proImages = productImageService.listDetailProImages(product);
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
        Product product = (Product) productService.get(pid);
        bean.setProduct(product);
        bean.setType(type);
        productImageService.add(bean);
        //根据不同图片类型拼接存放图片的路径
        String folder = "img/";
        if (ProductImageService.TYPE_SINGLE.equals(bean.getType())) {
            folder += "proImgSingle";
        } else {
            folder += "proImgDetail";
        }
        File imgFolder = new File(request.getServletContext().getRealPath(folder));
        File file = new File(imgFolder, bean.getId() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        image.transferTo(file);
        BufferedImage bufferedImage = ImageUtil.change2jpg(file);
        ImageIO.write(bufferedImage, "jpg", file);
        //对于单一图片还要存储其不同尺寸的图片
        if (ProductImageService.TYPE_SINGLE.equals(bean.getType())) {
            String imgSmallFolder = request.getServletContext().getRealPath("img/proImgSingle/small");
            String imgMiddleFolder = request.getServletContext().getRealPath("img/proImgSingle/middle");
            File f_small = new File(imgSmallFolder, bean.getId() + ".jpg");
            File f_middle = new File(imgMiddleFolder, bean.getId() + ".jpg");
            if (!f_small.getParentFile().exists()) {
                f_small.getParentFile().mkdirs();
            }
            if (!f_middle.getParentFile().exists()) {
                f_middle.getParentFile().mkdirs();
            }
            ImageUtil.resizeImage(file, 56, 56, f_small);
            ImageUtil.resizeImage(file, 56, 56, f_middle);
        }
        return bean;
    }

    /**
     * 删除产品图片
     */
    @DeleteMapping("/productImages/{id}")
    public void delete(@PathVariable Integer id, HttpServletRequest request) throws Exception {
        ProductImage bean = (ProductImage) productImageService.get(id);
        String folder = "img/";
        if (ProductImageService.TYPE_SINGLE.equals(bean.getType())) {
            folder += "proImgSingle";
        } else {
            folder += "proImgDetail";
        }
        File imgFolder = new File(request.getServletContext().getRealPath(folder));
        File file = new File(imgFolder, bean.getId() + ".jpg");
        String fileName = file.getName();
        file.delete();
        if (ProductImageService.TYPE_SINGLE.equals(bean.getType())) {
            String smallImgFolder = request.getServletContext().getRealPath("img/proImgSingle/small");
            String middleImgFolder = request.getServletContext().getRealPath("img/proImgSingle/middle");
            File sImg = new File(smallImgFolder, fileName);
            File mImg = new File(middleImgFolder, fileName);
            sImg.delete();
            mImg.delete();
        }
        productImageService.delete(id);
    }

}
