package com.example.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bean.ProductBean;
import com.example.bean.ShopBean;
import com.example.bean.UserBean;

public interface ProductDao extends JpaRepository<ProductBean, Long> {

	@Query("from ProductBean b where b.sid=:sid")
	List<ProductBean> findBySid(@Param("sid") Long sid);
}
