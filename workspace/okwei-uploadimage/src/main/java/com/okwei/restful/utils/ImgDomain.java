package com.okwei.restful.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ImgDomain {
	private static Map<Integer, String> imgDomains = AppSettingUtil.getImgDomains();

	public static String ReplitImgDoMain(String imgUrl) {
		if (!"".equals(imgUrl)) {
			for (String str : imgDomains.values()) {
				imgUrl = imgUrl.replace(str + "/", "");
			}
		} else {
			imgUrl = "";
		}
		return imgUrl;
	}

	public static String GetFullImgUrl(String imgUrl) {
		if (!"".equals(imgUrl) && imgUrl != null) {

			imgUrl = ReplitImgDoMain(imgUrl);

			// 先拿所有的键：
			Integer[] keys = imgDomains.keySet().toArray(new Integer[0]);
			// 然後随机一个键，找出该值：
			Random random = new Random();
			Integer randomKey = keys[random.nextInt(keys.length)];
			// String randomValue = imgDomains.get(;);
			String randomValue = imgDomains.get(randomKey);
			if (imgUrl.indexOf("/images/logo") >= 0) {
				if (imgUrl.indexOf("http://") >= 0)
					return imgUrl;
				return "http://base3.okimgs.com" + imgUrl;
			}
			if (imgUrl.indexOf("http://") >= 0) {
				return imgUrl;
			} else {
				return randomValue + "/" + imgUrl;
			}
		} else {
			imgUrl = "";
		}
		return imgUrl;
	}

	/**
	 * 获取指定大小的图片
	 * 
	 * @param imgSrc
	 * @param size
	 *            （1：原图，75表示750*750； 24为:240*240 依次类推）
	 * @return
	 */
	public static String GetFullImgUrl(String imgSrc, int size) {
		if ("".equals(imgSrc))
			return imgSrc;
		imgSrc = GetFullImgUrl(imgSrc);
		int i = imgSrc.lastIndexOf("_");
		int j = imgSrc.lastIndexOf(".");
		int s = j - i;
		if (s > 0 && s <= 3) {
			if (size <= 0)
				return imgSrc.substring(0, i) + imgSrc.substring(j, imgSrc.length());
			return imgSrc.substring(0, i + 1) + size + imgSrc.substring(j, imgSrc.length());
		}

		return imgSrc;
	}

	/**
	 * 产品图片 大小替换（）
	 * 
	 * @param imgSrc
	 * @param size
	 * @return
	 */
	public static String GetFullImgUrl_product(String imgSrc, int size) {
		if (null == imgSrc || "".equals(imgSrc))
			return imgSrc;
		imgSrc = GetFullImgUrl(imgSrc);
		int i = imgSrc.lastIndexOf("_");
		int j = imgSrc.lastIndexOf(".");
		int s = j - i;
		if (s > 0 && s <= 3) {
			if (size <= 0)
				return imgSrc.substring(0, i) + imgSrc.substring(j, imgSrc.length());
			return imgSrc.substring(0, i + 1) + size + imgSrc.substring(j, imgSrc.length());
		} else {
			return imgSrc.substring(0, j) + "_" + size + imgSrc.substring(j, imgSrc.length());
		}
	}

	/**
	 * baseImg 给全路径 （基础图片）
	 * 
	 * @param imgSrc
	 * @return
	 */
	public static String GetFullImgUrl_base(String imgSrc) {
		List<Map<String, String>> baseList = AppSettingUtil.getMaplist("imgBaseDomains");
		if (baseList == null && baseList.size() <= 0) {
			baseList = new ArrayList<Map<String, String>>();
			Map<String, String> map = new HashMap<String, String>();
			map.put("value", "http://base3.okimgs.com/");
			baseList.add(map);
		}
		Random random = new Random();
		int i = random.nextInt(baseList.size());
		Map<String, String> map = null;
		if (i >= baseList.size())
			map = baseList.get(0);
		else {
			map = baseList.get(i);
		}
		if (imgSrc != null && !"".equals(imgSrc) && imgSrc.indexOf("http://") > 0) {
			for (Map<String, String> map1 : baseList) {
				String base=map1.get("value");
				if(null!=base&&!"".equals(base))
					imgSrc = imgSrc.replace(base, "");
			}
		}
		return map.get("value") + imgSrc;
	}
}
