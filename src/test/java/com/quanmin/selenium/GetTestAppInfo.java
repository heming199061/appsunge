package com.quanmin.selenium;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GetTestAppInfo {
	public WebDriver driver;
	private WebDriverWait wait;
	String baseUrl="http://qrcode_display.io.qmtv.com/android/QuanminLive#release";
	private static int TIMEOUT = 20;
	Logger log = Logger.getLogger(GetTestAppInfo.class);
	
	public static void main(String[] args) {
		GetTestAppInfo gtai = new GetTestAppInfo();
		gtai.getAppInfo();
	}
	
	public void getAppInfo() {
		System.setProperty("webdriver.chrome.driver", "res\\drivers\\chromedriver6466.exe");
		ChromeOptions option=new ChromeOptions();
		option.addArguments("disable-infobars");
		driver = new ChromeDriver(option);/*打开Chrome浏览器*/
		System.out.println("等待2秒，测试即将开始。。。");
		sleep(2);
		driver.manage().window().maximize();/*窗口最大化*/
		System.out.println("已将浏览器窗口最大化。");
		wait = new WebDriverWait(driver, TIMEOUT);
		
		System.out.println("Step00:打开被测试页面");
		driver.get(baseUrl);/*打开网址*/
		
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[text()='QuanminLive']")));/*等待//li[text()='QuanminLive']*/
		System.out.println("Step01:点击QuanminGame");
		driver.findElement(By.xpath("//li[text()='QuanminGame']")).click();
		
		System.out.println("Step02:点击QuanminLive");
		driver.findElement(By.xpath("//li[text()='QuanminLive']")).click();
		
		//使用name属性找到页面上name属性为fruit的下拉列表元素
		Select dropList = new Select(driver.findElement(By.xpath("//span[contains(text(),'选择分支')]/following-sibling::select")));
		System.out.println("Step03:找到[选择分支类型]下拉列表");
		
		//isMultiple表示此下拉列表是否允许多选，被测试网页是一个单选下拉列表，所以此函数的返回结果是false
		Assert.assertFalse(dropList.isMultiple());

		System.out.println("Step04:选择[release]分支");
		dropList.selectByVisibleText("release");/*选择release下拉列表*/
		Assert.assertEquals(dropList.getFirstSelectedOption().getText(), "release");
		
		String appVersion = driver.findElement(By.xpath("//tbody/tr[1]/td[1]")).getText().trim();/*获取第一个分支名作为待测试的安卓app版本*/
//		appVersion = "4.2.0";/*调试代码请忽略*/
		System.out.println("Step05:获得需要测试的安卓APP的版本");
		System.out.println("\t需要测试的安卓app版本是: "+appVersion);
		
		System.out.println("Step06:点击这个需要被测试的安卓APP的版本号");
		driver.findElement(By.xpath("//tbody/tr[1]/td[1]")).click();/*点击这个版本号，展开所有该版本的build版本信息*/
//		driver.findElement(By.xpath("//tbody/tr[3]/td[1]")).click();/*点击这个版本号，展开所有该版本的build版本信息*//*调试代码请忽略*/
		
		System.out.println("\t等待5秒，等待页面加载完毕");
		sleep(5);
		System.out.println("Step07:获取该版本的build的次数，即小版本数量");
		List<WebElement> appInfo = driver.findElements(By.xpath("//td[normalize-space(text())=normalize-space(//tbody/tr[1]/td[1]/text())]"));
//		List<WebElement> appInfo = driver.findElements(By.xpath("//td[normalize-space(text())='"+appVersion+"']"));/*调试代码请忽略*/
		System.out.println("\t安卓【"+appVersion+"】，这个版本的build包的数量: "+appInfo.size());

		for (int i = 0; i < 10; i++) {
			System.out.println("即将关闭由Selenium WebDriver打开的Chrome浏览器，正在等待第"+(i+1)+"秒......");
			sleep(1);
		}
		driver.quit();
		System.out.println("浏览器已关闭，本测试程序结束。");
	}
	
	public void sleep(int sec) {
		try {
			Thread.sleep(sec * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//将当前的浏览器窗口截屏
	@Test(enabled=true)
	public void captureScreenInCurrentWindow() throws InterruptedException{
		baseUrl="http://yuedu.baidu.com/ebook/59ebbf3ff90f76c660371a0f.html?f=read";
		driver.get(baseUrl);
		
		driver.findElement(By.xpath("//div[@id='bd']")).click();
		
		Actions action = new Actions(driver);
		action.sendKeys(Keys.F11).perform();
		Thread.sleep(10000);
//		action.keyDown(Keys.CONTROL).sendKeys("0").keyUp(Keys.CONTROL).perform();
//		Thread.sleep(3000);
		
		for(int x=1;x<=20;x++){
//			WebElement next = driver.findElement(By.xpath("//a[@class='ic-page ic-page-next']"));
//			next.click();
	
			action.sendKeys(Keys.RIGHT).build().perform();
			Thread.sleep(1500);
			String filename="num"+Integer.toString(x)+".png";
			System.out.println("filename:"+filename);
			
			String filepath="E:\\tmp\\"+filename;
			System.out.println("filepath:"+filepath);
			capture(filepath);
		}
		//Thread.sleep(1000);
	}
	
	public void capture(String filepath){
		//调用getScreenshotAs方法把当前浏览器打开的页面进行截图，保存到一个file对象中
		File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		
		try {
			//把file对象转换为一个保存在某目录中名为test.png的图片文件
			FileUtils.copyFile(scrFile, new File(filepath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}