package cn.yoha.tmall.comparator;

import cn.yoha.tmall.pojo.Product;

import java.util.Comparator;

public class ProductDateComparator implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
        //新品排序，即将创建时间晚的放前面
        int cmp = o2.getCreateDate().compareTo(o1.getCreateDate());
        return cmp;
    }
}
