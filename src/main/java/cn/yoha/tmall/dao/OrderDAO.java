package cn.yoha.tmall.dao;


import cn.yoha.tmall.pojo.Order;
import cn.yoha.tmall.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDAO extends JpaRepository<Order,Integer> {
    List<Order> findByUserAndStatusNotOrderByIdDesc(User user,String status);
}
