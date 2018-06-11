package com.quanmin.selenium;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


public class OpenWeb {

	public static void main(String[] args) {
		//demo();
	}
	public static void sendBarrage(String information){
		String info[] = information.split("\\.");
		String liveNum = info[1];
		String userInfo[] = info[0].split("\\/");
		String user = userInfo[0];
		String pwd = userInfo[1];
		
		
		//System.setProperty("webdriver.firefox.marionette","D:\\Program Files\\selenium\\geckodriver.exe");
		System.setProperty("webdriver.chrome.driver","driver\\chromedriver.exe");
		
		WebDriver webDriver = new ChromeDriver();
		webDriver.get("https://test-www.quanmin.tv/"+liveNum);
		webDriver.manage().window().maximize();
		
		webDriver.findElement(By.cssSelector("span.js-login")).click();
		webDriver.findElement(By.id("bdlog_tj_qq")).click();
		webDriver.switchTo().frame("ptlogin_iframe");
		webDriver.findElement(By.id("switcher_plogin")).click();
		webDriver.findElement(By.id("u")).sendKeys(user);
		webDriver.findElement(By.id("p")).sendKeys(pwd);
		webDriver.findElement(By.id("login_button")).click();
		
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		webDriver.findElement(By.cssSelector("textarea.room_w-sender_textarea")).sendKeys(info[2]);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		webDriver.findElement(By.cssSelector("a.room_w-sender_submit-btn")).click();
		try {
			Thread.sleep(8000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		webDriver.close();
	}
	private static By ByCssSelector(String string) {
		// TODO Auto-generated method stub
		return null;
	}
}
