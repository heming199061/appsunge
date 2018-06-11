package com.quanmin.testcase.android.myDemo;

import org.testng.annotations.Test;
import com.quanmin.appium.SuperAction;
import com.quanmin.appium.AppiumUtil;


public class MyDemo_001 extends AppiumUtil {
	@Test
	public void myDemo_001(){
		SuperAction.parseCase("com.quanmin.testcase.myDemo.MyDemo_001", "android");
	}
}