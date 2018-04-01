package com.example.apicontroller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
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
@RequestMapping(value = "/api/product")
public class ApiProductController {

	@Autowired
	private UserDao userDao;
	@Autowired
	private ShopDao shopDao;
	@Autowired
	private ProductDao productDao;
	@Value("${bs.imagesPath}")
	private String location;
	@Autowired
	private ResourceLoader resourceLoader;
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public BaseBean<ProductBean> uploadImg(@RequestParam("file") MultipartFile file, 
			HttpServletRequest request) {
		
		String name = request.getParameter("name");
		String info = request.getParameter("info");
		Long sid = Long.parseLong(request.getParameter("sid"));
		double price = Double.parseDouble(request.getParameter("price"));
		boolean isActivity = Boolean.parseBoolean(request.getParameter("isActivity"));
		double actProce = 0;
		if (isActivity) {
			actProce = Double.parseDouble(request.getParameter("actProce"));
		}
		if (!file.isEmpty()) {
			try {
				String path = sid + "_" + System.currentTimeMillis() + "." + file.getOriginalFilename().split("\\.")[1];
				
				File root = new File(location);
		        if (!root.exists()) {
		        	root.mkdirs();
		        }
				Files.copy(file.getInputStream(), Paths.get(location, path));

				ProductBean bean = new ProductBean();
				bean.setInfo(info);
				bean.setName(name);
				bean.setSid(sid);
				bean.setImg("/img/" + path);
				bean.setPrice(price);
				bean.setActivity(isActivity);
				if (isActivity) {
					bean.setActProce(actProce);
				}
				return ResultUtils.resultSucceed(productDao.save(bean));
			} catch (IOException | RuntimeException e) {
				return ResultUtils.resultError("");
			}
		} else {
			return ResultUtils.resultError("");
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/img/{filename:.+}")
	public ResponseEntity<?> getFile(@PathVariable String filename) {
		try {
			return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(location, filename).toString()));
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public BaseBean<ShopBean> delete(HttpServletRequest request) {
		String id = request.getParameter("id");
		productDao.delete(Long.parseLong(id));
		return ResultUtils.resultSucceed("");
	}
}
