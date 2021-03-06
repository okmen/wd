package com.okwei.myportal.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.okwei.web.base.BaseController;

@Controller
public class IndexController extends BaseController {

	private static final Log logger = LogFactory.getLog(IndexController.class);

	@RequestMapping(value = "/index")
	public String index(Model model) {

		logger.info("IndexController index method starting.......");
		return "index";
	}

}
