package com.okwei.common;

import java.text.DecimalFormat;

import org.springframework.stereotype.Component;

@Component
public class CommonMethod {
	
   private static CommonMethod instance=null;
   private CommonMethod(){}
   
   private static synchronized void syncInit() {  
       if (instance == null) {  
           instance = new CommonMethod();  
       }  
   }  
 
   public static CommonMethod getInstance() {  
       if (instance == null) {  
           syncInit();  
       }  
       return instance;  
   }
   
   public Double getDisplayPrice(Double defaultPrice,Double originalPrice, Double percent){
	   Double displayPrice =0.0;
	   if(null!=originalPrice){
		   displayPrice=originalPrice;
	    }else{
	    	double tpercent=1.5d;
	    	if(null!=percent && percent>0)
	    		tpercent= percent;
	    	displayPrice = defaultPrice*tpercent;
	    	DecimalFormat df = new DecimalFormat("#.00");
	    	displayPrice =Double.parseDouble(df.format(displayPrice));
	    }
	   return displayPrice;
   }
}
