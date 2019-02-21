package cn.yoha.tmall.comparator;

import cn.yoha.tmall.pojo.Product;

import java.util.Comparator;

public class ProductAllComparator implements Comparator<Product> {

    @Override
    public int compare(Product o1, Product o2) {
        //要将综合评价高的放在前面，就是谁大把谁放在前面，使得返回值为正数
        int cmp = o1.getSaleCount() * o1.getReviewCount() - o2.getReviewCount() * o2.getSaleCount();
        return -cmp;
    }
}
