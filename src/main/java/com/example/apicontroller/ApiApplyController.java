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

import com.example.bean.ApplyBean;
import com.example.bean.BaseBean;
import com.example.bean.OrderBean;
import com.example.bean.ProductBean;
import com.example.bean.ShopBean;
import com.example.bean.UserBean;
import com.example.dao.ApplyDao;
import com.example.dao.OrderDao;
import com.example.dao.ProductDao;
import com.example.dao.ShopDao;
import com.example.dao.UserDao;
import com.example.utils.ResultUtils;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/api/apply")
public class ApiApplyController {

	@Autowired
	private ShopDao shopDao;
	@Autowired
	private ProductDao productDao;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private ApplyDao applyDao;
	@Autowired
	private UserDao userDao;

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public BaseBean<ApplyBean> add(HttpServletRequest request) {
		String uid = request.getParameter("uid");
		String addr = request.getParameter("addr");
		String tel = request.getParameter("tel");
		String name = request.getParameter("name");
		ApplyBean bean = new ApplyBean();
		bean.setUid(Long.parseLong(uid));
		bean.setAddr(addr);
		bean.setTel(tel);
		bean.setName(name);
		bean.setTime(new Date().getTime());
		return ResultUtils.resultSucceed(applyDao.save(bean));
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public BaseBean<List<ApplyBean>> get(HttpServletRequest request) {
		List<ApplyBean> list = applyDao.findAll();
		for (ApplyBean applyBean : list) {
			UserBean userBean = userDao.findOne(applyBean.getUid());
			applyBean.setUname(userBean.getUserName());
			applyBean.setOldAddr(userBean.getAddr());
			applyBean.setOldName(userBean.getName());
			applyBean.setOldTel(userBean.getTel());
		}
		return ResultUtils.resultSucceed(list);
	}

	@RequestMapping(value = "/change", method = RequestMethod.POST)
	public BaseBean<ApplyBean> change(HttpServletRequest request) {
		String id = request.getParameter("id");
		String action = request.getParameter("action");
		ApplyBean bean = applyDao.findOne(Long.parseLong(id));
		if (action.equals("0")) { // 同意
			bean.setStatus(1);
			UserBean userBean = userDao.findOne(bean.getUid());
			ShopBean shopBean = shopDao.findByIdOrUid(bean.getUid());
			if (!StringUtils.isEmpty(bean.getName())) {
				userBean.setName(bean.getName());
				shopBean.setName(bean.getName());
			}
			if (!StringUtils.isEmpty(bean.getAddr())) {
				userBean.setAddr(bean.getAddr());
				shopBean.setAddr(bean.getAddr());
			}
			if (!StringUtils.isEmpty(bean.getTel())) {
				userBean.setTel(bean.getTel());
				shopBean.setTel(bean.getTel());
			}
			userDao.save(userBean);
			shopDao.save(shopBean);
		} else {
			bean.setStatus(2);
		}
		return ResultUtils.resultSucceed(applyDao.save(bean));
	}
}
