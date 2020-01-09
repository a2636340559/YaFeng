package com.poet.yafeng.DAO;


import com.poet.yafeng.POJO.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDAO extends JpaRepository<User,Integer> {
   //判断作者是否存在
    User findUserByAccount(String account);
}
