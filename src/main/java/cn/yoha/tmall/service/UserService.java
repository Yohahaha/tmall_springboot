package cn.yoha.tmall.service;


import cn.yoha.tmall.dao.UserDAO;
import cn.yoha.tmall.pojo.User;
import cn.yoha.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    public Page4Navigator<User> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size,sort);
        Page pageFromJPA =userDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    public boolean isExist(String name){
        User user = userDAO.findByName(name);
        return null!=user;
    }

    public void add(User user){
        userDAO.save(user);
    }

    public User loginByNameAndPassword(String name,String password){
        return userDAO.getByNameAndPassword(name,password);
    }

    public User getUserByName(String username){
        return userDAO.findByName(username);
    }

}
