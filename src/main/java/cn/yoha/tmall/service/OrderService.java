package cn.yoha.tmall.service;


import cn.yoha.tmall.dao.OrderDAO;
import cn.yoha.tmall.pojo.Order;
import cn.yoha.tmall.pojo.OrderItem;
import cn.yoha.tmall.pojo.User;
import cn.yoha.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@CacheConfig(cacheNames = "orders")
public class OrderService {
    public static final String waitPay = "waitPay";
    public static final String waitDelivery = "waitDelivery";
    public static final String waitConfirm = "waitConfirm";
    public static final String waitReview = "waitReview";
    public static final String finish = "finish";
    public static final String delete = "delete";

    @Autowired
    private OrderDAO orderDAO;
    @Autowired
    private OrderItemService orderItemService;
    @Cacheable(key = "'orders-page-'+#p0+'-'+#p1")
    public Page4Navigator<Order> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size,sort);
        Page pageFromJPA =orderDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    public void removeOrderFromOrderItem(List<Order> orders) {
        for (Order order : orders) {
            removeOrderFromOrderItem(order);
        }
    }

    public void removeOrderFromOrderItem(Order order) {
        List<OrderItem> orderItems= order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(null);
        }
    }
    @Cacheable(key = "'orders-one-'+#p0")
    public Order get(int oid) {
        return orderDAO.getOne(oid);
    }
    @CacheEvict(allEntries = true)
    public void update(Order bean) {
        orderDAO.save(bean);
    }

    @Transactional(rollbackForClassName = "Exception")
    public Order add(Order order){
        if (false){
            throw new RuntimeException();
        }
        Order save = orderDAO.save(order);
        return save;
    }

    @Cacheable(key = "'orders-uid-'+#p0.id")
    public List<Order> listByUserWithoutDelete(User user){
        List<Order> orderList = orderDAO.findByUserAndStatusNotOrderByIdDesc(user, OrderService.delete);
        orderItemService.fill(orderList);
        return orderList;
    }
    @CacheEvict(allEntries = true)
    public void deleteOrder(Integer oid){
        orderDAO.deleteById(oid);
    }

    public void calc(Order order){
        float total = 0;
        List<OrderItem> itemList = order.getOrderItems();
        for (OrderItem item : itemList){
            total+=item.getNumber()*item.getProduct().getPromotePrice();
        }
        order.setTotal(total);
    }

}
