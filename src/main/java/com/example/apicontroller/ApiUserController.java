package com.example.apicontroller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
import com.example.bean.UserBean;
import com.example.dao.UserDao;
import com.example.utils.ResultUtils;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/api/user")
public class ApiUserController {

	@Autowired
	private UserDao userDao;

	@RequestMapping(value = "/{limit}", method = RequestMethod.POST)
	public BaseBean<UserBean> addUser(@PathVariable String limit,
			HttpServletRequest request) {
		UserBean userBean = new UserBean();
		userBean.setUserName(request.getParameter("name"));
		userBean.setPwd(request.getParameter("pwd"));
		int limitInt = Integer.parseInt(limit);
		userBean.setRole(limitInt);
		if (userDao.findUserByUserName(userBean.getUserName()) == null) {
			return ResultUtils.resultSucceed(userDao.save(userBean));
		} else {
			return ResultUtils.resultError("该账号已存在！");
		}
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public BaseBean<UserBean> userLogin(HttpServletRequest request) {
		UserBean userBean = new UserBean();
		userBean.setUserName(request.getParameter("name"));
		userBean.setPwd(request.getParameter("pwd"));
		int limit = Integer.parseInt(request.getParameter("limit"));
		UserBean select = userDao.findUserByUserNameAndPwd(userBean.getUserName(), userBean.getPwd(),limit);
		if (select == null) {
			return ResultUtils.resultError("账号或密码错误");
		} else {
			return ResultUtils.resultSucceed(select);
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public BaseBean<UserBean> getUser(@PathVariable String id) {
		UserBean user = userDao.findOne(Long.parseLong(id));
		return ResultUtils.resultSucceed(user);
	}
	
	@RequestMapping(value = "/change/{id}", method = RequestMethod.POST)
	public BaseBean<UserBean> changeUser(@PathVariable String id,
			HttpServletRequest request) {
		UserBean user = userDao.findOne(Long.parseLong(id));
		String name = request.getParameter("name");
		String addr = request.getParameter("addr");
		String tel = request.getParameter("tel");
		if (!StringUtils.isEmpty(name)) {
			user.setUserName(name);
		}
		if (!StringUtils.isEmpty(addr)) {
			user.setAddr(addr);
		}
		if (!StringUtils.isEmpty(tel)) {
			user.setTel(tel);
		}
		return ResultUtils.resultSucceed(userDao.save(user));
	}
}
