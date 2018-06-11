package com.quanmin.appium;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.PressesKeyCode;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.functions.ExpectedCondition;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import qmtv.email.ReportTemplate;


public class AppiumUtil {
	/*启动前的参数设置*/
	public static AppiumDriver<WebElement> driver;
	public static Logger logger = Logger.getLogger(AppiumUtil.class);
	public DesiredCapabilities capabilities;
	public static String platformName = "android";/*测试平台名字，默认认为是安卓平台*/
	
	public String serverURL = PropertiesUtil.getProperty("serverURL");/*appium server地址*/
	public String deviceName = PropertiesUtil.getProperty("deviceName");/*设备名字随便起*/
	public String appPackage = PropertiesUtil.getProperty("appPackage");/*android app的package路径*/
	public String appActivity = PropertiesUtil.getProperty("appActivity");/*android app的启动activity*/
	
	public String apkName = PropertiesUtil.getProperty("apkName");
	public String androidAppPath = PropertiesUtil.getProperty("androidAppPath");/*android app路径*/
	public String platformVersion = PropertiesUtil.getProperty("platformVersion");/*测试平台版本号*/
	public String udid = PropertiesUtil.getProperty("udid");/*设备udid*/
	
	public String ipaName = PropertiesUtil.getProperty("ipaName");
	public String iosAppPath = PropertiesUtil.getProperty("iosAppPath");/*ios app的路径*/
	public String bundleid = PropertiesUtil.getProperty("bundleid");/*ios bundleid*/
	
	public String automationName = "uiautomator2";/*测试引擎名字*/
	public boolean unicodeKeyboard = Boolean.parseBoolean(PropertiesUtil.getProperty("unicodeKeyboard"));/*安卓独有 - 是否使用unicode键盘，使用此键盘可以输入中文字符*/
	public boolean resetKeyboard = Boolean.parseBoolean(PropertiesUtil.getProperty("resetKeyboard"));/*android独有 - 是否重置键盘，如果设置了unicodeKeyboard键盘，可以将此参数设置为true，然后键盘会重置为系统默认的*/
	public boolean sessionOverride = Boolean.parseBoolean(PropertiesUtil.getProperty("sessionOverride"));/*是否覆盖已有的seesssion，这个用于多用例执行，如果不设置的话，会提示前一个session还没有结束，用例就不能继续执行了*/
	
	public int sleepTime = Integer.valueOf(PropertiesUtil.getProperty("sleepTime"));/*设置全局暂停等待时间*/
	public int elementTimeOut = Integer.valueOf(PropertiesUtil.getProperty("elementTimeOut"));/*设置全局隐式等待时间*/
	
	public int newCommandTimeout = 5000;
	public ITestContext testContext;/*声明ITestContext，用于获取testng配置文件内容*/
	public static String version = "未获取到app的版本号";
	
	/*@Parameters({ "platform" })  调试的时候注释掉 */
	@BeforeSuite(/* groups = { "test" }, */enabled = true)
	public void beforeSuite(ITestContext context/*, String platform*/) throws MalformedURLException {
		logger.info("-----beforeSuite is running-----------------------------------");
		logger.info("即将测试的app平台是:" + platformName);

		LogConfiguration.initLog(this.getClass().getSimpleName());

		capabilities = new DesiredCapabilities();
		String fileAbsPath = "";
		
//		if("ios".equals(platform.trim().toLowerCase())) {
//			platformName = "ios";
//			platformVersion = PropertiesUtil.getProperty("iosPlatformVersion");/*ios测试平台版本号*/
//		}
		
		/*设置公共capability配置*/
		capabilities.setCapability("platformName", platformName);
		capabilities.setCapability("platformVersion", platformVersion);
		capabilities.setCapability("deviceName", deviceName);
		capabilities.setCapability("sessionOverride", sessionOverride);
		capabilities.setCapability("udid", udid);
		capabilities.setCapability("noSign", true);/* 不要重签名 */
//		capabilities.setCapability("noReset", false);/* false就是要重置app */
		capabilities.setCapability("fullReset", true);/* 每次都重新安装app，测试结束后卸载app */
		capabilities.setCapability("newCommandTimeout", newCommandTimeout);/* false就是要重置app */
		// capabilities.setCapability(MobileCapabilityType.NO_RESET,"flase");
		
		if("android".equals(platformName.trim().toLowerCase())) {/*android平台配置*/
			if(androidAppPath==null || "".equals(androidAppPath)) {/*使用默认apk路径，即项目名\res\app\android\XX.apk*/
				fileAbsPath = System.getProperty("user.dir") + File.separator + "res" + File.separator + "app" + File.separator + platformName + File.separator + apkName;
			}else {
				fileAbsPath = androidAppPath + File.separator + apkName;/*D:\aa\bb\cc\XX.apk*/
			}
			logger.info("安装文件的路径是:"+fileAbsPath);
			
			/*设置android特有的capability，并实例化driver对象*/
			capabilities.setCapability("app", fileAbsPath);
			capabilities.setCapability("unicodeKeyboard", unicodeKeyboard);
			capabilities.setCapability("resetKeyboard", resetKeyboard);
			capabilities.setCapability("automationName", automationName);
			capabilities.setCapability("appPackage", appPackage);
			capabilities.setCapability("appActivity", appActivity);
			
			driver = new AndroidDriver<WebElement>(new URL(serverURL), capabilities);
			logger.info("全民直播APP(Android)---已经启动");
			driver.manage().timeouts().implicitlyWait(elementTimeOut, TimeUnit.SECONDS);
			
			
		}else if("ios".equals(platformName.trim().toLowerCase())) {/*ios平台配置*/
			if(iosAppPath==null || "".equals(iosAppPath)) {/*使用默认apk路径，即项目名\res\app\ios\XX.ipa*/
				fileAbsPath = System.getProperty("user.dir") + File.separator + "res" + File.separator + "app" + File.separator + platformName + File.separator + ipaName;
			}else {
				fileAbsPath = iosAppPath + File.separator + ipaName;/*D:\aa\bb\cc\XX.ipa*/
			}
			
			/*设置ios特有的capability并实例化driver对象*/
			capabilities.setCapability("app", iosAppPath);
			// ios设置自动接收系统alert，注意IOS弹出的alert，APPIUM可以自动处理掉，支持ios8以上系统
			capabilities.setCapability("autoAcceptAlerts", true);
			capabilities.setCapability("appium-version", "1.6.5");
			capabilities.setCapability("bundleid", bundleid);
			capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "XCUITest");
			
			driver = new IOSDriver<WebElement>(new URL(serverURL), capabilities);
			logger.info("全民直播APP(IOS)---已经启动");
			pause(3000);
			driver.manage().timeouts().implicitlyWait(elementTimeOut, TimeUnit.SECONDS);
		}else {
			logger.info("初始化driver失败");
			Assert.fail("初始化driver失败");
		}
		
		ReportTemplate.startMsTime = System.currentTimeMillis();
		ReportTemplate.startTime = getCurrTimeStr(ReportTemplate.startMsTime);
		
		/*关闭开机屏页面*/
		closeWelcomePage();
		/*点击允许*/
		permission();
		/*关闭小窗播放功能授权*/
		openQuanxian();
		/*处理引导页面*/
		closeGuide();

//		boolean flag = returnHomeByToast("再按一次退出全民");
//		System.out.println("flag="+flag);
		
		/*获取环境信息和版本信息*/
		version = getBasicInfo();
		
		webViewTest();
		
		logger.info("当前测试的版本是:"+version);
		logger.info("---------- 初始化环境已经完成 ----------");
		//closeWindow();
	}

	@BeforeClass(enabled = false)
	public void beforeClass(ITestContext context) throws InterruptedException {
		context.setAttribute("APPIUM_DRIVER", driver);
		int i = 1;
		while (!isElement(getBy("id", "com.maimiao.live.tv:id/ll_tab_home"))) {
			((PressesKeyCode) driver).pressKeyCode(4);
			logger.info("点击返回，直到返回到首页");
			Thread.sleep(500);
			if (isElement(getBy("id", "android:id/button1"))) {
				driver.findElement(getBy("id", "android:id/button1")).click();
			}
			if (i > 3) {
				break;
			}
			i++;
		}
		driver.findElement(getBy("id", "com.maimiao.live.tv:id/ll_tab_home")).click();
		logger.info("已返回到首页");

	}
	
	public static void pressReturnButton(int times) {
		for(int i=0;i<times;i++) {
			((PressesKeyCode) driver).pressKeyCode(4);
			logger.info("[第"+(i+1)+"次]点击返回按钮(系统返回键)。");
			pause("1");//等待1秒
		}
	}

	@AfterSuite(enabled = true)
	public void afterSuite() {
		logger.info("-----afterSuite is running-----------------------------------");
		ReportTemplate.endMsTime = System.currentTimeMillis();
		ReportTemplate.endTime = getCurrTimeStr(ReportTemplate.endMsTime);
		driver.quit();
		logger.info("全民直播APP已经关闭.\n\n");
	}

	/*
	 * 获取elementBy,并返回By
	 */
	public static By getBy(String type, String value) {
		By by = null;
		
		if (type.equalsIgnoreCase("ID")) {
			by = By.id(value);
		}else if (type.equalsIgnoreCase("xpath")) {
			by = By.xpath(value);
		}else if (type.equalsIgnoreCase("ClassName")) {
			by = By.className(value);
		}
		return by;
	}
	

	/*
	 * 判断元素是否存在
	 */
	public static boolean isElement(By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/*
	 * 点击事件
	 */
	public static void click(String type, String value) {
		if (type.equalsIgnoreCase("css")) {
			driver.findElementByAccessibilityId(value).click();
		} else {
			// 获取elementby
			By by = null;
			int index = -1;
			if(type.toLowerCase().equals("xpath-index")) {
				index = Integer.parseInt(value.split("-")[1]);
				value = value.split("-")[0];
				type = "Xpath";
			}
			
			by = getBy(type, value);

			// 判断定位元素是否存在
			if (isElement(by)) {
				if(index==-1){
					driver.findElement(by).click();
				}else {
//					System.out.println("by="+by+"---index="+index);
					driver.findElements(by).get(index).click();
				}
				logger.info("点击元素：" + type + "：" + value);
			} else {
				logger.error("未找到:" + type + ":" + value + "定位元素");
				Assert.fail("未找到:" + type + ":" + value + "定位元素");
			}
		}
	}

	/*
	 * 输入内容
	 */
	public static void input(String type, String value, String input) {
		// 获取elementby
		By by = getBy(type, value);
		// 判断定位元素是否存在
		if (isElement(by)) {
			driver.findElement(by).sendKeys(input);
			logger.info("在元素：" + type + "：" + value + "中输入内容:" + input);
		} else {
			logger.error("未找到:" + type + ":" + value + "定位元素");
			Assert.fail("未找到:" + type + ":" + value + "定位元素");
		}
	}

	/*
	 * 暂停当前用例的执行，暂停的时间为：sleepTime 秒
	 */
	public static void pause(String sleepTime) {
		int time = Integer.parseInt(sleepTime);
		if (time <= 0) {
			return;
		}
		try {
			Thread.sleep(time * 1000);
			logger.info("暂停:" + sleepTime + "秒");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void pause(int millSec) {
		try {
			Thread.sleep(millSec);
			System.out.println("debug----暂停:" + millSec + "毫秒");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void touch(String type, String value, String touchTime) {
		// 初始化
		TouchAction action = new TouchAction(driver);
		// 获取elementby
		By by = getBy(type, value);

		int time = 1000 * Integer.parseInt(touchTime);
		if (time > 0) {
			try {
				action.press(driver.findElement(by)).wait(time);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			action.perform();
			logger.info("长按:" + type + "：" + value + ":   " + touchTime + "秒");
		}

	}

	/*
	 * 清除输入框内容
	 */
	public static void clear(String type, String value) {
		// 获取elementby
		By by = getBy(type, value);
		// 判断定位元素是否存在
		if (isElement(by)) {
			driver.findElement(by).clear();
			logger.info("清除元素：" + type + "：" + value + "输入框的内容");
		} else {
			logger.error("未找到:" + type + ":" + value + "定位元素");
			Assert.fail("未找到:" + type + ":" + value + "定位元素");
		}
	}

	/*
	 * 检测文字是否正确
	 */
	public static void isTextCorrect(String type, String value, String expected) {
		// 获取elementby
		By by = getBy(type, value);
		// 判断定位元素是否存在
		if (isElement(by)) {
			try {
				Assert.assertEquals(driver.findElement(by).getText(), expected);
			} catch (Exception e) {
				logger.error("期望的文字是 [" + expected + "] 但是找到了 [" + driver.findElement(by).getText() + "]");
				Assert.fail("期望的文字是 [" + expected + "] 但是找到了 [" + driver.findElement(by).getText() + "]");
			}
			logger.info("找到了期望的文字: [" + expected + "]");
		} else {
			logger.error("未找到:" + type + ":" + value + "定位元素");
			Assert.fail("未找到:" + type + ":" + value + "定位元素");
		}
	}

	/**
	 * 正则匹配是否包含XX
	 * 
	 * @param type 定位方式
	 * @param value 定位元素值
	 * @param expected
	 */
	public static void regExpression(String type, String value, String expected) {
		// 获取elementby
		By by = getBy(type, value);
		String pattern = "[\\s\\S]*" + expected + "[\\s\\S]*";
		List<?> test = driver.findElements(by);
		boolean isMatch = true;
		for (Object e : test) {
			isMatch = Pattern.matches(pattern, ((WebElement) e).getText());
			if (isMatch) {
				break;
			}
		}
		if (isMatch) {
			logger.info("匹配到了期望值: [" + expected + "]");
		} else {
			logger.error("未匹配到期望值:" + expected);
			Assert.fail("未匹配到期望值:" + expected);
		}
	}

	/*
	 * 检查元素是不是和预期一致，预期true为元素存在，false为不存在
	 */
	public static void isElementCorrect(String type, String value, String expected) {
		// 获取elementby
		By by = getBy(type, value);
		// 强制将boolean转换为string
		String actual = String.valueOf(isElement(by));
		if (actual.equalsIgnoreCase(expected)) {
			logger.info("实际结果和预期结果一致");
		} else {
			logger.error("实际结果和预期结果不符");
			Assert.fail("实际结果和预期结果不符");
		}

	}

	/**
	 * 滑动屏幕方法
	 * 
	 * @param type 滑动方向
	 * @param value 滑动距离
	 */
	public static void swipe(String type, String value) {
		// 获得手机屏幕宽度
		int width = driver.manage().window().getSize().width;
		// 获取手机屏幕高度
		int height = driver.manage().window().getSize().height;
//		System.out.println("手机宽度="+width+"\t手机高度="+height);
		
//		Duration duration = Duration.ofMillis(100);
		TouchAction swipes = new TouchAction(driver);
		
		if (value.equals("默认")) {
			switch (type) {
			case "SwipeToUp":
				swipes.press(width / 2, height * 7 / 8).waitAction().moveTo(width / 2, height / 8).release().perform();
				logger.info("向上滑动");
				break;
			case "SwipeToDown":
				swipes.press(width / 2, height / 8).waitAction().moveTo(width / 2, height * 7 / 8).release().perform();
				logger.info("向下滑动");
				break;
			case "SwipeToLeft":
				swipes.press(width * 7 / 8, height / 2).waitAction().moveTo(width / 8, height / 2).release().perform();
				logger.info("向左滑动");
				break;
			case "SwipeToRight":
				swipes.press(width / 8, height / 2).waitAction().moveTo(width * 7 / 8, height / 2).release().perform();
				logger.info("向右滑动");
				break;
			default:
				logger.error("您输入的滑动方式不正确，请重新输入");
			}
		}else if (value.equals("half")) {
			switch (type) {
			case "SwipeToUp":
				swipes.press(width / 2, height * 7 / 8).waitAction().moveTo(width / 2, height / 2).release().perform();
				logger.info("向上滑动");
				break;
			case "SwipeToDown":
				swipes.press(width / 2, height * 5 / 8).waitAction().moveTo(width / 2, height * 7 / 8).release().perform();
				logger.info("向下滑动");
				break;
//			case "SwipeToLeft":
//				swipes.press(width * 7 / 8, height / 2).waitAction(duration).moveTo(width / 8, height / 2).release().perform();
//				logger.info("向左滑动");
//				break;
//			case "SwipeToRight":
//				swipes.press(width / 8, height / 2).waitAction(duration).moveTo(width * 7 / 8, height / 2).release().perform();
//				logger.info("向右滑动");
//				break;
			default:
				logger.error("您输入的滑动方式不正确，请重新输入");
			}
		} else {
			logger.error("此功能尚未开发，请耐心等待！");
		}

	}

	public static void swipeUntilElePresent(String type,By by) {
		WebElement element = null;
		int count = 0;
		while(true) {
			count++;
			element = findElement(by);
			if(count>6) {
				System.out.println("尝试5次滑动，均未找到元素，停止滑动:"+by);
				break;
			}else {
				if(element==null) {
					System.out.println("第"+count+"次滑动");
					swipe(type, "默认");
					pause(200);
				}else {
					System.out.println("已找到元素，停止滑动:"+by);
					break;
				}
			}
		}
	}
	
	/*
	 * 打开传入的直播间
	 */
	public static void openLive(String liveNum) {
		By by = null;
		logger.info("进入：" + liveNum + "房间");
		if (platformName.equalsIgnoreCase("ios")) {// ios的进入直播间操作
			// 点击首页搜索按钮
			by = getBy("ID", "navigationBar searchButton ico");
			driver.findElement(by).click();
			// 在搜索输入框中输入房间号
			by = getBy("xpath", "//XCUIElementTypeNavigationBar[@name=\"SearchView\"]"
					+ "/XCUIElementTypeOther/XCUIElementTypeTextField");
			driver.findElement(by).sendKeys(liveNum);
			// 点击搜索按钮
			by = getBy("ID", "Search");
			driver.findElement(by).click();

			// 进入房间
			by = getBy("xpath", "//XCUIElementTypeApplication[@name=\"全民直播Pro\"]"
					+ "/XCUIElementTypeWindow[1]/XCUIElementTypeOther/XCUIElementTypeOther"
					+ "/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther"
					+ "/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther"
					+ "/XCUIElementTypeOther/XCUIElementTypeOther/XCUIElementTypeOther"
					+ "/XCUIElementTypeScrollView/XCUIElementTypeOther/XCUIElementTypeCollectionView"
					+ "/XCUIElementTypeCell/XCUIElementTypeOther/XCUIElementTypeImage[1]/XCUIElementTypeImage[1]");
			driver.findElement(by).click();

		} else {// android的进入直播间操作
				// 点击底部首页按钮
			by = getBy("ID", "com.maimiao.live.tv:id/ll_tab_home");
			driver.findElement(by).click();
			// 点击首页搜索按钮
			by = getBy("ID", "com.maimiao.live.tv:id/ll_search");
			driver.findElement(by).click();
			// 在搜索输入框中输入房间号
			by = getBy("ID", "com.maimiao.live.tv:id/et_search_title");
			driver.findElement(by).sendKeys(liveNum);
			// 点击搜索按钮
			by = getBy("ID", "com.maimiao.live.tv:id/tv_search");
			driver.findElement(by).click();
			// 进入房间
			by = getBy("ID", "com.maimiao.live.tv:id/item_click");
			driver.findElement(by).click();
		}
	}

	public static boolean isAlreadyLogin() {
		boolean isAlreadyLoginFlag = false;
		WebElement element = null;
		//断言当前存在导航页-我的
		if(isElement(By.id("com.maimiao.live.tv:id/iv_tab_me"))) {
			//点击我的
			element = tryToFindElement(By.id("com.maimiao.live.tv:id/iv_tab_me"),3);
			tapXY(element);
			swipe("SwipeToDown", "默认");//向下滑动
		}
		
		pause(300);
		//如果存在全民号三个字，则认为已经登录，否则认为没有登录
		try {
			WebElement qmNum = tryToFindElement(By.id("com.maimiao.live.tv:id/text_my_displayid"),3);
			if(qmNum!=null) {
				isAlreadyLoginFlag = true;
				logger.info("已经登录，登录信息:"+qmNum.getText());
			}else {
				logger.info("现在没有登录");
			}
		}catch(Exception e) {
			logger.info("现在没有登录");
			//e.printStackTrace();
		}
		
		element = tryToFindElement(By.id("com.maimiao.live.tv:id/iv_tab_home"),3);
		tapXY(element);
		
		return isAlreadyLoginFlag;
	}
	
	public static void tapXY(WebElement element){
		Point p=null;
		if(element==null) {
			Assert.fail("元素为空,无法点击。");
		}
		p=element.getLocation();
		
        TouchAction ta=new TouchAction(driver);
//        System.out.println("*********************************************");
//        System.out.println("element:横坐标="+p.getX()+",纵坐标="+p.getY());
//        System.out.println("*********************************************");
        ta.tap(p.getX(), p.getY()).perform();
    }
	
	/*
	 * 使用QQ账户登录全民直播
	 */
	public static void loginByQQ(String loginInfo) {
		String[] userInfo = loginInfo.split("\\|");
		By by = null;
		WebElement element = null;
		String qqNumber="blank";
		String qqPasswd="blank";
		if(userInfo.length==2) {
			qqNumber=userInfo[0];
			qqPasswd=userInfo[1];
		}
		logger.info("Debug----要登录的QQ号是:"+qqNumber+",QQ密码是:"+qqPasswd);

		if (platformName.equalsIgnoreCase("ios")) { // ios的登录操作
			logger.error("登录失败！");
			Assert.fail("登录失败！");
		} else { // android 的登录操作
			boolean loginFlag = isAlreadyLogin();
			if(loginFlag) {//已经登录
				// 已经登录就先退出，否则直接登录
				logOut();
			}
			
			element = tryToFindElement(By.id("com.maimiao.live.tv:id/iv_tab_me"), 5);
			tapXY(element);
			
			//下滑全屏
			swipe("SwipeToDown", "默认");

			//点击【点击登录】
			element = tryToFindElement(By.id("com.maimiao.live.tv:id/tv_my_login"), 5);
			tapXY(element);
			pause(300);
			
			//点击【QQ图标】
			element = tryToFindElement(By.id("com.maimiao.live.tv:id/iv_qq_login"), 5);
			tapXY(element);
			pause(300);
			
			//断言当前QQ号是1554347923
			WebElement qqNum = tryToFindElement(By.xpath("//android.widget.TextView[@text='添加帐号']/preceding-sibling::android.widget.LinearLayout/android.widget.RelativeLayout/android.widget.TextView[2]"),3);
			if(qqNum!=null) {
				System.out.println("Debug----当前登录的QQ号码是:"+qqNum.getText());
				Assert.assertEquals(qqNum.getText(), qqNumber);
			}
			
			//点击【添加账号】
			element = tryToFindElement(By.xpath("//android.widget.TextView[@text='添加帐号']"),5);
			tapXY(element);
			pause(300);
			
			//输入用户名
			driver.findElement(By.id("com.tencent.mobileqq:id/account")).sendKeys(qqNumber);
			
			//输入密码
			driver.findElement(By.id("com.tencent.mobileqq:id/password")).sendKeys(qqPasswd);
			
			//点击登录
			element = tryToFindElement(By.xpath("//android.widget.Button[@text='登录']"),5);
			tapXY(element);
			pause(300);
		}

		// 判断是否登录成功
		by = getBy("ID", "com.maimiao.live.tv:id/text_my_username");
		if (isElement(by)) {
			logger.info("登录成功！");
			swipe("SwipeToDown", "默认");
			//点击【首页】
			//driver.findElement(By.id("com.maimiao.live.tv:id/tv_tab_home")).click();
			element = tryToFindElement(By.id("com.maimiao.live.tv:id/iv_tab_home"),3);
			tapXY(element);
		} else {
			logger.error("登录失败！");
			Assert.fail("登录失败！");
		}

	}

	/*
	 * 检查用户是否登录，已登录就退出登录，未登录就跳过
	 */
	public static void logOut() {
		WebElement element = null;
		if (platformName.equalsIgnoreCase("ios")) { // ios 退出登录操作
			logger.error("注销登录失败！");
			Assert.fail("注销登录失败！");
		} else { // android 退出登录操作
			boolean loginFlag = isAlreadyLogin();
			if(!loginFlag) {//没有登录
				logger.info("用户未登录，不需要执行退出操作！");
			}else {
				//判断【我的】是否存在
				if(isElement(By.id("com.maimiao.live.tv:id/tv_tab_me"))) {
					//点击【我的】					
//					driver.findElement(By.id("com.maimiao.live.tv:id/tv_tab_me")).click();
					element = tryToFindElement(By.id("com.maimiao.live.tv:id/iv_tab_me"), 5);
					tapXY(element);
					pause(300);
				}
				
				//上滑全屏,直到【设置】出现
				swipeUntilElePresent("SwipeToUp", By.id("com.maimiao.live.tv:id/layout_me_setting"));

				//点击设置
				driver.findElement(By.id("com.maimiao.live.tv:id/layout_me_setting")).click();
				pause(300);
				
				//上滑全屏,直到【退出登录】出现
				swipeUntilElePresent("SwipeToUp", By.xpath("//android.widget.TextView[contains(@text,'退出登录')]"));
				
				//点击【退出登录】
				driver.findElement(By.xpath("//android.widget.TextView[contains(@text,'退出登录')]")).click();
				//点击【退出当前设备】
				driver.findElement(By.xpath("//android.widget.Button[contains(@text,'退出当前设备')]")).click();
				
				logger.info("已经退出登录！");
				swipe("SwipeToDown", "默认");
				
				//点击【首页】
				//driver.findElement(By.id("com.maimiao.live.tv:id/tv_tab_home")).click();
				element = tryToFindElement(By.id("com.maimiao.live.tv:id/iv_tab_home"),3);
				tapXY(element);
			}
		}
	}
	
    public static void permission() {
    	logger.info("若出现授权弹窗，均点击允许>>>>>>");
	    for (int i=0; i <= 10; i++) {
	        if (driver.getPageSource().contains("允许") || driver.getPageSource().contains("禁止")) {// 出现权限提示
	            try {
	                findElement(By.xpath("//android.widget.Button[contains(@text,'允许')]")).click();// 点击允许
	            } catch (Exception e1) {
	            	logger.info("第"+(i+1)+"次尝试查找，没有找到授权弹窗");
	            }
	        } else {
	            break;
	        }
	    }
	    logger.info("<<<<<<授权弹窗处理完毕");
	}
		
	
	/*关闭欢迎页面,及权限相关*/
	public static void closeWelcomePage() {
		logger.info("关闭开机屏页面>>>>>>");
		/*等待欢迎页面，如果页面出现"全新启动"，就点击该按钮，否则等待*/
		WebDriverWait wait = new WebDriverWait(driver, 5);
		WebElement btn = null;
		try {
			btn = wait.until(new ExpectedCondition<WebElement>() {
				@Override
				public WebElement apply(WebDriver driver) {
					return driver.findElement(By.id("com.maimiao.live.tv:id/btn_enter"));
				};
			});
			/*找到了就点击*/
			if(btn != null) {
				btn.click();
				pause(300);
				logger.info("<<<<<<点击【XX启动】按钮跳过开机屏页面。");
			}
		}catch(WebDriverException e){/*没找到，也不要停止，继续测试*/
			logger.info("<<<<<<没有出现【XX启动】这个开机屏页面，测试继续执行。");
		}
	}
	
	public static void openQuanxian() {
		logger.info("关闭小窗播放权限>>>>>>");
		for(int i=0;i<2;i++) {
			if (driver.getPageSource().contains("开启")) {// 出现权限提示
				try {
					findElement(By.xpath("//android.widget.Button[contains(@text,'暂不')]")).click();// 点击允许
				} catch (Exception e1) {
					System.out.println("第"+(i+1)+"次尝试查找，没有找到授权元素...");
				}
			} else {
				break;
			}
		}
		logger.info("<<<<<<关闭小窗播放权限");
	}
	
	public static void closeGuide() {
		logger.info("处理引导页面>>>>>>");
		WebElement guide1 = null;
		WebElement guide2 = null;
		pause(400);
		
		guide1 = tryToFindElement(By.id("com.maimiao.live.tv:id/iv_new_homepage_guide"),3);
		if(guide1 != null) {
			guide1.click();
			pause(100);
			System.out.println("点击跳过引导页面。");
		}
		
		guide2 = tryToFindElement(By.id("com.maimiao.live.tv:id/iv_find_guide"),3);
		if(guide2 != null) {
			guide2.click();
			pause(100);
			System.out.println("再次点击跳过引导页面。");
		}
		logger.info("<<<<<<引导页面处理完毕");
	}
	
	public static WebElement tryToFindElement(By by,int count) {
		WebElement element = null;
		for(int i=0;i<count;i++) {
//			System.out.println("Debug----第"+(i+1)+"次尝试查找元素："+by);
			element = findElement(by);
			if(element!=null) {
				System.out.println("Debug----第"+(i+1)+"次【找到了】元素："+by);
				return element;
			}else {
				if(i==count-1) {
					System.out.println("Debug----尝试"+count+"次，均未找到元素。");
				}
				pause(500);
			}
		}
		return null;
	}

	
	public static WebElement findElement(By by) {
		WebElement element = null;
		try {
			WebDriverWait wait = new WebDriverWait(driver, 10);
			element = wait.until(new ExpectedCondition<WebElement>() {
				@Override
				public WebElement apply(WebDriver driver) {
					return driver.findElement(by);
				};
			});
		}catch(Exception e) {
			logger.info("没有找到元素---"+by);
		}
		
		if(element != null) {
			return element;
		}else {
			return null;
		}
	}
	/*
	 * 测试前期处理
	 */

	public static void closeWindow() {
		By by = null;
		if (platformName.equalsIgnoreCase("ios")) { // ios操作
			// 处理欢迎页面
			// by = By.id("跳过");
			// if(isElement(by)){
			// driver.findElement(by).click();
			// logger.info("点击跳过欢迎页面");
			// }else{
			// logger.info("未发现欢迎页面，不做处理。");
			// }
			// 处理升级弹窗
			by = By.id("稍后再说");
			if (isElement(by)) {
				driver.findElement(by).click();
				logger.info("关闭升级提示");
			} else {
				logger.info("未发现升级弹出提示，不做处理。");
			}

		} else { // android操作
			// 处理欢迎页面
			by = By.id("com.maimiao.live.tv:id/splash_ll_count");
			if (isElement(by)) {
				driver.findElement(by).click();
				logger.info("点击跳过欢迎页面");
			} else {
				logger.info("未发现欢迎页面，不做处理。");
			}

			// 处理升级弹窗
			by = By.id("com.maimiao.live.tv:id/btn_cancel");
			if (isElement(by)) {
				driver.findElement(by).click();
				logger.info("关闭升级提示");
			} else {
				logger.info("未发现升级弹出提示，不做处理。");
			}
		}

	}

	/* APP中的返回按钮集成 */
	public static void returnButton() {
		((PressesKeyCode) driver).pressKeyCode(4);
		logger.info("点击返回按钮，返回到上一层。");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 此方法为返回到APP主页
	 */
	public static void returnHome() {
		int i = 1;
		while (!isElement(getBy("id", "com.maimiao.live.tv:id/ll_tab_home"))) {
			((PressesKeyCode) driver).pressKeyCode(4);
			logger.info("点击返回，直到返回到首页");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (isElement(getBy("id", "android:id/button1"))) {
				driver.findElement(getBy("id", "android:id/button1")).click();
			}
			if (i > 3) {
				break;
			}
			i++;
		}
		driver.findElement(getBy("id", "com.maimiao.live.tv:id/ll_tab_home")).click();
		logger.info("已返回到首页");
	}
	
	public static void returnHomeByToast(String toastMsg) {
		boolean flag = false;//默认没有回到首页
		int count = 0;
		
		while (!flag) {
			count++;
			if(count>10) {
				break;
			}else {
				((PressesKeyCode) driver).pressKeyCode(4);//调用一次系统返回键
				flag=isToastPresence(toastMsg);
				if(flag==false) {
					System.out.println("这次按返回键，没有找到toast。");
					pause(500); 
				}
			}
		}
		if(flag) {
			driver.findElement(getBy("id", "com.maimiao.live.tv:id/ll_tab_home")).click();
			logger.info("已返回到首页");
			Assert.assertTrue(flag,"已返回到首页!");
		}else {
			logger.info("尝试多次，返回首页失败。");
			Assert.assertFalse(flag,"尝试多次，返回首页失败!");
		}
//		return flag;
	}
	
	public static boolean isToastPresence(String toastMsg) {
		boolean flag = false;
		try {
			WebDriverWait wait = new WebDriverWait(driver,3);
			WebElement t = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(@text,'"+ toastMsg + "')]")));  
			if(t!=null) {
				String toastContent = t.getText();
				System.out.println("Debug----toast全部内容:"+toastContent);
				logger.info("查找toast["+toastContent+"]成功！");  
				flag=true;
			}
		}catch(Exception e) {
			System.out.println("Debug----toast内容没有出现!");
			//e.printStackTrace();
		}
		
		return flag;
	}

	/* 根据元素来获取此元素的定位值 */
	public String getLocatorByElement(WebElement element, String expectText) {

		String text = element.toString();
		String expect = null;
		try {
			expect = text.substring(text.indexOf(expectText) + 1, text.length() - 1);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("failed to find the string [" + expectText + "]");
		}
		return expect;
	}
	
	public static String getText(By by) {
		String text = "";
//		WebElement element = null;
		if(isElement(by)) {
			text = driver.findElement(by).getText();
		}
		if("".equals(text)){
			return "blank";
		}
		
		return text;
	}
	
	/*获取版本信息*/
	public static String getBasicInfo() {
//		pause("1");
		WebElement myBtn = findElement(By.id("com.maimiao.live.tv:id/tv_tab_me"));
		for(int i=0;i<3;i++) {
			if(myBtn!=null) {
				myBtn.click();
			}
		}
//		swipe("SwipeToUp", "默认");
		swipeUntilElePresent("SwipeToUp", By.id("com.maimiao.live.tv:id/layout_me_setting"));

		findElement(By.id("com.maimiao.live.tv:id/layout_me_setting")).click();
		pause(300);
		
		WebElement env = findElement(By.id("com.maimiao.live.tv:id/change_server_domain"));
		String envInfo = env.getText();
		System.out.println("----------  " + envInfo + "  ----------");
		
		WebElement ver = findElement(By.id("com.maimiao.live.tv:id/text_mysetting_app_version"));
		String verInfo = ver.getText();
		System.out.println("----------  " + verInfo + "  ----------");
		
		pressReturnButton(1);//按一次设备的返回键
		swipe("SwipeToDown", "默认");
		findElement(By.id("com.maimiao.live.tv:id/ll_tab_home")).click();
		pause(300);
		
		if(verInfo.contains(":")) {
			return verInfo.split(":")[1];
		} 
		
		return "blank";
	}
	
	//无法识别WebView内的元素
	public static void webViewTest() {
		//在首页-推荐页，进入精彩推荐的第一个房间
		
		//点击聊天tab
		
		//若未关注，点击X
		
		//点击周排名，周排名文字可能会滚动，点击后的页面加载较慢，要在此加一个等待
		
		//点击主播人气tab，上滑半屏
		
		//点击查看更多榜单
		
		//点击王者荣耀
		
		//点击明星主播榜这个下拉列表，选择主播人气榜
		
		//下滑半屏，获取主播人气榜后面的文字
		
		//返回首页
	}
	
	public static String getCurrTimeStr(long ms) {
		//Long time = System.currentTimeMillis();
		Date d = new Date(ms);
		SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return sdf.format(d);
	}
//	public static void main(String[] args) {
//		System.out.println(getCurrTimeStr());
//	}
}
