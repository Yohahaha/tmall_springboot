package cn.yoha.tmall.comparator;

import cn.yoha.tmall.pojo.Product;

import java.util.Comparator;

public class ProductReviewComparator implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
        int cmp = o1.getReviewCount() - o2.getReviewCount();
        return -cmp;
    }
}
