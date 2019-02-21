package cn.yoha.tmall.comparator;

import cn.yoha.tmall.pojo.Product;

import java.util.Comparator;

public class ProductPriceComparator implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
        int cmp = (int) ((int) o1.getPromotePrice() - o2.getPromotePrice());
        return cmp;
    }
}
