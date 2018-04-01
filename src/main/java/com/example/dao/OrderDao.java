package com.example.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bean.OrderBean;
import com.example.bean.ProductBean;
import com.example.bean.UserBean;

public interface OrderDao extends JpaRepository<OrderBean, Long> {
	@Query("from OrderBean b where b.uid=:uid")
	List<OrderBean> findByUid(@Param("uid") Long uid);
	
	@Query("from OrderBean b where b.suid=:suid")
	List<OrderBean> findBySuid(@Param("suid") Long suid);
}
