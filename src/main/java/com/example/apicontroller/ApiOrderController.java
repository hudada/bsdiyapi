package com.example.apicontroller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
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
import com.example.bean.OrderBean;
import com.example.bean.ProductBean;
import com.example.bean.ShopBean;
import com.example.bean.UserBean;
import com.example.dao.OrderDao;
import com.example.dao.ProductDao;
import com.example.dao.ShopDao;
import com.example.dao.UserDao;
import com.example.utils.ResultUtils;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/api/order")
public class ApiOrderController {

	@Autowired
	private ShopDao shopDao;
	@Autowired
	private ProductDao productDao;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private UserDao userDao;

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public BaseBean<OrderBean> add(HttpServletRequest request) {
		String sname = request.getParameter("sname");
		String saddr = request.getParameter("saddr");
		String price = request.getParameter("price");
		String actPrice = request.getParameter("actPrice");
		String diyInfo = request.getParameter("diyInfo");
		String img = request.getParameter("img");
		String title = request.getParameter("title");
		String uid = request.getParameter("uid");
		String pid = request.getParameter("pid");
		String suid = request.getParameter("suid");
		OrderBean bean = new OrderBean();
		bean.setUid(Long.parseLong(uid));
		bean.setSname(sname);
		bean.setSaddr(saddr);
		bean.setPrice(Double.parseDouble(price));
		if (!StringUtils.isEmpty(actPrice)) {
			bean.setActPrice(Double.parseDouble(actPrice));
		}else {
			bean.setActPrice(-1);
		}
		bean.setDiyInfo(diyInfo);
		bean.setTitle(title);
		bean.setImg(img);
		bean.setTime(new Date().getTime());
		if (pid.equals("diy")) {
			bean.setPid((long) -1);
		}else {
			ProductBean productBean = productDao.findOne(Long.parseLong(pid));
			productBean.setSum(productBean.getSum() + 1);
			productDao.save(productBean);
			bean.setPid(Long.parseLong(pid));
		}
		bean.setSuid(Long.parseLong(suid));
		return ResultUtils.resultSucceed(orderDao.save(bean));
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public BaseBean<List<OrderBean>> get(HttpServletRequest request) {
		long uid = Long.parseLong(request.getParameter("uid"));
		int type = Integer.parseInt(request.getParameter("type"));
		if (type == 1) {
			return ResultUtils.resultSucceed(orderDao.findByUid(uid));
		}else {
			List<OrderBean> list = orderDao.findBySuid(uid);
			for (OrderBean orderBean : list) {
				UserBean userBean = userDao.findOne(orderBean.getUid());
				orderBean.setUname(userBean.getUserName());
			}
			return ResultUtils.resultSucceed(list);
		}
	}
}
