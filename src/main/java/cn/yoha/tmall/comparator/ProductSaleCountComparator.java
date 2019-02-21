package cn.yoha.tmall.comparator;

import cn.yoha.tmall.pojo.Product;

import java.util.Comparator;

public class ProductSaleCountComparator implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
        int cmp = o1.getSaleCount() - o2.getSaleCount();
        return -cmp;
    }
}
