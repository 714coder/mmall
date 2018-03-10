package com.mmall.dao;

import com.mmall.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    int updateStatusByProductId(@Param("status") Integer status, @Param("productId") Integer productId);

    List<Product> selectList();  //mybatis pagehelper 使用aop传入startpage()中的pageNum,pageSize增强sql语句所以order by语句没有;结尾
}