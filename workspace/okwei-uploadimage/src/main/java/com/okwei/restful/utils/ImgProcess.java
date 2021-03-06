package com.okwei.restful.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.context.annotation.Configuration;

import com.okwei.restful.enums.ImgTypeEnum;
import com.okwei.restful.fastdfs.ClientGlobal;
import com.okwei.restful.fastdfs.StorageClient1;
import com.okwei.restful.vo.BackImgModle;

public class ImgProcess {

	
	/**
	 * 上传图片到本地（临时目录）
	 * 
	 * @return
	 */
	public BackImgModle uploadImgToLocal(String data, int type) {
		BackImgModle result = new BackImgModle();
		try {
			String imgsrc = ImageHelper.saveToStack(data, type);
//			File file = new File(imgsrc); // 读入文件
//			if((file.length()/1024)>1024){
//				result.setStatus(-1);
//				result.setStatusReason("图片大小不超过1M"); 
//			}
			String resultString = uploadImgToFastFDS(imgsrc,type);
			String tailer=resultString.substring(resultString.lastIndexOf(".")+1);
			if(type==Integer.parseInt(ImgTypeEnum.Photo.toString())){
				resultString=resultString.substring(0, resultString.lastIndexOf("."))+"_12."+tailer;
			}else if (type==Integer.parseInt(ImgTypeEnum.Product.toString())) {
				resultString=resultString.substring(0, resultString.lastIndexOf("."))+"_12."+tailer;
			}
			else {
				resultString=resultString.substring(0, resultString.lastIndexOf("."))+"_75."+tailer;
//				resultString=ImgDomain.GetFullImgUrl(resultString);
			}
			result.setStatus(1);
			result.setImgUrl(resultString);
			result.setBaseModel(imgsrc);
		} catch (Exception e) {
			result.setStatus(-1);
			result.setStatusReason(e.getMessage());
			
		}
		return result;
	}



	/**
	 * 将图片上传到 FDS 并删除临时文件
	 * @param imgPath 图片路径
	 * @return
	 */
	public String uploadImgToFastFDS(String imgPath) {

		try {
			String filename = "fdfs_client.conf";
			URL url = Configuration.class.getClassLoader().getResource(filename);
			String str = url.getFile();
			// 转换编码
			str = URLDecoder.decode(str, "utf-8");
			ClientGlobal.init(str);

			StorageClient1 sc1 = new StorageClient1();
			NameValuePair[] meta_list = null; // new NameValuePair[0];
			// String imgFullPATH = imgPath;
			String tailer = imgPath.substring(imgPath.lastIndexOf(".") + 1);
			String fileid =  sc1.upload_file1(imgPath, tailer, meta_list);
			List<String> imglist = ImageHelper.imglistPath(imgPath, 0);
			if (imglist != null && imglist.size() > 0) {
				for (String img : imglist) {
					if (img.lastIndexOf("_") > 0) {
						String prefix_name = img.substring(img.lastIndexOf("_"));
						prefix_name = prefix_name.replace("." + tailer, "");
						sc1.upload_file1(fileid, prefix_name, img, tailer, null);
					}
				}
			}
			imglist.add(imgPath);
			for (String string : imglist) {
				File f = new File(string);
				if (f.exists()) {
					f.delete();
				}
			}
			return "/"+fileid;
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}
	
	public String uploadImgToFastFDS(String imgPath,int type) {

		try {
			String filename = "fdfs_client.conf";
			URL url = Configuration.class.getClassLoader().getResource(filename);
			String str = url.getFile();
			// 转换编码
			str = URLDecoder.decode(str, "utf-8");
			ClientGlobal.init(str);

			StorageClient1 sc1 = new StorageClient1();
			NameValuePair[] meta_list = null;
			String tailer = imgPath.substring(imgPath.lastIndexOf(".") + 1);
			String fileid =  sc1.upload_file1(imgPath, tailer, meta_list);
			List<String> imglist = ImageHelper.imglistPath(imgPath, type);
			if (imglist != null && imglist.size() > 0) {
				for (String img : imglist) {
					if (img.lastIndexOf("_") > 0) {
						String prefix_name = img.substring(img.lastIndexOf("_"));
						prefix_name = prefix_name.replace("." + tailer, "");
						sc1.upload_file1(fileid, prefix_name, img, tailer, null);
					}
				}
			}
			imglist.add(imgPath);
			for (String string : imglist) {
				File f = new File(string);
				if (f.exists()) {
					f.delete();
				}
			}
			return "/"+fileid;
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}
}
