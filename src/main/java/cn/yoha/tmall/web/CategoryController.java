package cn.yoha.tmall.web;

import cn.yoha.tmall.pojo.Category;
import cn.yoha.tmall.service.CategoryService;
import cn.yoha.tmall.util.ImageUtil;
import cn.yoha.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;


@RestController
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    public List<Category> list() throws Exception {
        List<Category> categoryList = categoryService.list();
        categoryList.forEach(category -> System.out.println(category));
        return categoryList;
    }

    /**
     * 查询类别数据，以分页的方式返回
     */
    @GetMapping("/categories")
    public Page4Navigator<Category> list(@RequestParam(value = "start", defaultValue = "0") int start,
                                         @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        start = start < 0 ? 0 : start;
        Page4Navigator<Category> page = categoryService.list(start, size, 5);
//        List<Category> content = page.getContent();
//        content.forEach(category -> System.out.println(category));
        return page;
    }

    /**
     * 添加类别数据并提供图片上传服务
     *
     * @param bean  类别信息
     * @param image 类别对应的图片
     * @throws Exception
     */
    @PostMapping("/categories")
    public Object add(Category bean, MultipartFile image, HttpServletRequest request) throws Exception {
        categoryService.add(bean);
        saveOrUpdateImageFile(bean, image, request);
        return bean;
    }

    private void saveOrUpdateImageFile(Category bean, MultipartFile image, HttpServletRequest request) throws IOException {
        File imageFolder = new File(request.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder, bean.getId() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        image.transferTo(file);
        BufferedImage img = ImageUtil.change2jpg(file);
        ImageIO.write(img, "jpg", file);
    }

    @DeleteMapping("/categories/{id}")
    public String delete(@PathVariable Integer id, HttpServletRequest request) throws Exception {
        categoryService.delete(id);
        File imageFolder = new File(request.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder, id + ".jpg");
        boolean delete = file.delete();
        if (delete) {
            System.out.println("--------删除图片成功----------");
        }
        return null;
    }

    @GetMapping("/categories/{id}")
    public Category edit(@PathVariable Integer id) throws Exception {
        return categoryService.editById(id);
    }

    @PutMapping("/categories/{id}")
    public String update(Category bean, MultipartFile image, HttpServletRequest request, @PathVariable Integer id) throws Exception {
        //看起来似乎springMVC自动将id绑定到bean上了
//        bean.setId(id);
//        System.out.println(bean);
        categoryService.updateCategory(bean);
        if(image!=null){
            saveOrUpdateImageFile(bean,image,request);
        }
        return null;
    }
}
