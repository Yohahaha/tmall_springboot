package cn.yoha.tmall.service;


import cn.yoha.tmall.dao.OrderItemDAO;
import cn.yoha.tmall.pojo.Order;
import cn.yoha.tmall.pojo.OrderItem;
import cn.yoha.tmall.pojo.Product;
import cn.yoha.tmall.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "orderItems")
public class OrderItemService {
    @Autowired
    OrderItemDAO orderItemDAO;
    @Autowired
    ProductImageService productImageService;

    public void fill(List<Order> orders) {
        for (Order order : orders)
            fill(order);
    }

    public void fill(Order order) {
        List<OrderItem> orderItems = listByOrder(order);
        float total = 0;
        int totalNumber = 0;
        for (OrderItem oi : orderItems) {
            total += oi.getNumber() * oi.getProduct().getPromotePrice();
            totalNumber += oi.getNumber();
            productImageService.setFirstProdutImage(oi.getProduct());
        }
        order.setTotal(total);
        order.setOrderItems(orderItems);
        order.setTotalNumber(totalNumber);
    }
    @Cacheable(key = "'orderItem-oid'+#p0.id")
    public List<OrderItem> listByOrder(Order order) {
        return orderItemDAO.findByOrderOrderByIdDesc(order);
    }
    @Cacheable(key = "'orderItem-pid'+#p0.id")
    public List<OrderItem> listByProduct(Product bean) {
        return orderItemDAO.getByProduct(bean);
    }

    public Integer getSaleCount(Product product) {
        List<OrderItem> itemList = listByProduct(product);
        int count = 0;
        for (OrderItem item : itemList) {
            if (null != item.getOrder() && null != item.getOrder().getPayDate()) {
                count += item.getNumber();
            }
        }
        return count;
    }
    @CacheEvict(allEntries = true)
    public void delete(int oiid){
        orderItemDAO.deleteById(oiid);
    }

    @CacheEvict(allEntries = true)
    public OrderItem update(OrderItem item){
        return orderItemDAO.save(item);
    }

    @Cacheable(key = "'orderItem-uid'+#p0.id")
    public List<OrderItem> listByUser(User user){
        return orderItemDAO.findByUserAndOrderIsNull(user);
    }

    @Cacheable(key = "'orderItems-one-'+#p0")
    public OrderItem get(int id){
        return orderItemDAO.getOne(id);
    }
}
