package com.example.apicontroller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.persistence.Transient;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.bean.BaseBean;
import com.example.bean.ProductBean;
import com.example.bean.ShopBean;
import com.example.bean.UserBean;
import com.example.dao.ProductDao;
import com.example.dao.ShopDao;
import com.example.dao.UserDao;
import com.example.utils.ResultUtils;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/api/shop")
public class ApiShopController {

	@Autowired
	private ShopDao shopDao;
	@Autowired
	private ProductDao productDao;
	@Autowired
	private UserDao userDao;

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public BaseBean<ShopBean> add(HttpServletRequest request) {
		String name = request.getParameter("name");
		String info = request.getParameter("info");
		String addr = request.getParameter("addr");
		String tel = request.getParameter("tel");
		String uid = request.getParameter("uid");
		ShopBean bean = new ShopBean();
		bean.setName(name);
		bean.setInfo(info);
		bean.setTel(tel);
		bean.setAddr(addr);
		bean.setUid(Long.parseLong(uid));
		UserBean userBean = userDao.findOne(Long.parseLong(uid));
		userBean.setName(name);
		userBean.setTel(tel);
		userBean.setAddr(addr);
		userDao.save(userBean);
		return ResultUtils.resultSucceed(shopDao.save(bean));
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public BaseBean<List<ShopBean>> list(HttpServletRequest request) {
		List<ShopBean> list = shopDao.findAll();
		for (ShopBean shopBean : list) {
			List<ProductBean> productBeans = productDao.findBySid(shopBean.getId());
			int sum = 0;
			for (ProductBean productBean : productBeans) {
				sum += productBean.getSum();
			}
			shopBean.setSum(sum);
			shopBean.setProductBeans(productBeans);
		}
		return ResultUtils.resultSucceed(list);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public BaseBean<ShopBean> get(@PathVariable String id) {
		long idL = Long.parseLong(id);
		ShopBean shopBean = shopDao.findByIdOrUid(idL);
		if (shopBean == null) {
			return ResultUtils.resultSucceed(shopBean);
		} else {
			List<ProductBean> productBeans = productDao.findBySid(shopBean.getId());
			int sum = 0;
			for (ProductBean productBean : productBeans) {
				sum += productBean.getSum();
			}
			shopBean.setSum(sum);
			shopBean.setProductBeans(productBeans);
			return ResultUtils.resultSucceed(shopBean);
		}
	}
}
