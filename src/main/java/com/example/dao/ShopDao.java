package com.example.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bean.ShopBean;
import com.example.bean.UserBean;

public interface ShopDao extends JpaRepository<ShopBean, Long> {

	@Query("from ShopBean b where b.id=:id or b.uid=:id")
	ShopBean findByIdOrUid(@Param("id") Long id);
}
