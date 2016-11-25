package com.okwei.bean.vo.agent;

import java.io.Serializable;
import java.util.List;

public class HeadInfo implements Serializable{

	private static final long serialVersionUID = 1L;
    public long userCount;
    
    public List<LeftMenu> leftMenu;

    public long getUserCount() {
        return userCount;
    }

    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }

    public List<LeftMenu> getLeftMenu() {
        return leftMenu;
    }

    public void setLeftMenu(List<LeftMenu> leftMenu) {
        this.leftMenu = leftMenu;
    }
    
    
}
