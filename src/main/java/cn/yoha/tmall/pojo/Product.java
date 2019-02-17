package cn.yoha.tmall.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;

//`id` int(11) NOT NULL AUTO_INCREMENT,
//        `name` varchar(255) DEFAULT NULL COMMENT '产品名称',
//        `subTitle` varchar(255) DEFAULT NULL COMMENT '子标题',
//        `originalPrice` float DEFAULT NULL COMMENT '初始价格',
//        `promotePrice` float DEFAULT NULL COMMENT '优惠价格',
//        `stock` int(11) DEFAULT NULL COMMENT '库存',
//        `cid` int(11) DEFAULT NULL COMMENT '外键-类别表',
//        `createDate` datetime DEFAULT NULL COMMENT '创建时间',
@Entity
@Table(name = "product")
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer"})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    private String name;
    private String subTitle;
    private Float originalPrice;
    private Float promotePrice;
    private Integer stock;
    @ManyToOne
    @JoinColumn(name = "cid")
    private Category category;
    private Date createDate;
    @Transient
    private ProductImage firstImg;
    public ProductImage getFirstImg() {
        return firstImg;
    }

    public void setFirstImg(ProductImage firstImg) {
        this.firstImg = firstImg;
    }



    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", originalPrice=" + originalPrice +
                ", promotePrice=" + promotePrice +
                ", stock=" + stock +
                ", category=" + category +
                ", createDate=" + createDate +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public Float getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Float originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Float getPromotePrice() {
        return promotePrice;
    }

    public void setPromotePrice(Float promotePrice) {
        this.promotePrice = promotePrice;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
