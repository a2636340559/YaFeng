package com.poet.yafeng.DAO;

import com.poet.yafeng.POJO.Collect;
import com.poet.yafeng.POJO.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface CollectDAO  extends JpaRepository<Collect,Integer> {

    List<Collect> findAllByUserId(BigInteger user_id);

    @Query(value = "select * from collect where user_id=?1 and poetry_name=?2 and author=?3",nativeQuery = true)
    public Collect isStoraged(BigInteger userId,String poetryName,String author);
}
