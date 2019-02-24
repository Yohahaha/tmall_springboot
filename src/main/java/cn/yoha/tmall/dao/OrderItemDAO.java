package cn.yoha.tmall.dao;

import cn.yoha.tmall.pojo.Order;
import cn.yoha.tmall.pojo.OrderItem;
import cn.yoha.tmall.pojo.Product;
import cn.yoha.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemDAO extends JpaRepository<OrderItem,Integer>{
    List<OrderItem> findByOrderOrderByIdDesc(Order order);
    List<OrderItem> getByProduct(Product product);
    List<OrderItem> findByUserAndOrderIsNull(User user);
}
