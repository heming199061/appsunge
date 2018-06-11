package com.quanmin.appium;

import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.testng.Assert;

import com.quanmin.selenium.OpenWeb;


public class SuperAction {
	/* 获取日志记录器对象 */
	public static Logger logger = Logger.getLogger(SuperAction.class);	
	/* 定义一个字符串数组，用来存放页面，及定位字符串 */
	//public static String[] localization = {"blank","blank"};/* VerViewerPage.[Android]星光 */
	public static Map<String, String[]> elementData;	/* 获取某个页面所有元素的别名、定位方式、定位值 */
	public static Map<Integer,TreeMap<String,String>> data;/* 获取该用例的所有操作步骤及数据 */
	public static String serialNumber;/* 存step序号 */
	public static String action;/* 存step动作 */
	public static String locator;/* 存step元素定位 */
	
	public static String page="blank";
	public static String keyword="blank";
	public static String information="blank";/* 存step操作的数据 */

	/* SuperAction.parseCase("com.quanmin.testcase.myPage.MyPage_001", "android");
	 * 解析每个testcase(excel里的sheet页)
	 */
	public static void parseCase(String function,String platformName){
		/* function=com.quanmin.testcase.myPage.MyPage_001 
		 * 解析，获得测试功能名称(MyPage.xlsx中的MyPage)，及当前sheet页的名字，如001
		 */
		String functionSplit[] = function.split("\\_");	/*[com.quanmin.testcase.myPage.MyPage,001]*/	
		String caseSplit[] = functionSplit[0].split("\\.");/*[com,quanmin,testcase,myPage,MyPage]*/
		String caseFile = caseSplit[4];/*MyPage*/
		String sheetName = functionSplit[1];/*001*/
		
		/*根据文件名和sheet页名，去获取该case的所有操作步骤，放在一个Map集合里*/
		data = ReadExcelForCase.getCase(caseFile, sheetName, platformName);	/*ReadExcelForCase.getCase("MyPage", "001", "android")*/
		
		System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
//		System.out.println("Step"+"\t"+"Action"+"\t"+"Locator"+"\t\t\t\t"+"TestData");
		System.out.printf("%-4s\t%-6s\t%-50s\t%-30s\n","Step","Action","Locator","TestData");
		for(Map.Entry<Integer,TreeMap<String,String>> entry : data.entrySet()){
			TreeMap<String,String> tm = entry.getValue();
			String step = tm.get("step_num");
			String action = tm.get("action");
			String locator = tm.get("locator");
//			int len = locator.length();
//			if(len<60) {
//				locator+=getBlank(60-len);
//			}
			String data = tm.get("test_data");
//			System.out.println(step+"\t"+action+"\t"+locator+"\t"+data);
			System.out.printf("%-4s\t%-6s\t%-50s\t%-30s\n",step,action,locator,data);
        }
		System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
		/*以下是调试代码
			data = ReadExcelForCase.getCase("MyPage", "002", "android");
			for(Map.Entry<Integer,TreeMap<String,String>> entry : data.entrySet()){
	            System.out.println("key:"+entry.getKey()+", value:"+entry.getValue());          
	        }
	        
			得到的结果:
			key:1, value:{step_nu m=1, action=登录, locator=blank, test_data=2147865099/123456789}
			key:2, value:{step_num=2, action=进入房间, locator=blank, test_data=1150670}
			key:3, value:{step_num=3, action=点击, locator=VerViewerPage.[Android]星光, test_data=blank}
			......
		*/

		for(int i = 1;i <= data.size();i++){/*data里的操作步骤序号从1开始*/
//			serialNumber = data.get(i).get("step_num");/* 获取第i+1操作步骤的序号 */
			serialNumber = String.valueOf(i);
			action = data.get(i).get("action");/* 获取第i+1操作步骤的动作，即关键字 */
			locator = data.get(i).get("locator");/* VerViewerPage.[Android]星光 */
			System.out.println("Debug:"+serialNumber+"-----"+action+"-----"+locator);

			if(!(locator == null || locator.equals("blank") || locator.equals(""))){
				page = locator.split("\\.")[0];/* VerViewerPage */
				keyword = locator.split("\\.")[1];/* [Android]星光 */
			}else {
				page = "blank";
				keyword = "blank";
			}
			
			information = data.get(i).get("test_data");
			System.out.println("Debug:"+page+"-----"+keyword+"-----"+information);
					
			logger.info("正在解析excel--【"+caseFile+".xlsx】中的sheet（用例）--【"+sheetName+"】的第【"+serialNumber+"】个步骤");
			
			/* 使用分支语句来处理每个操作步骤中的动作 */
			
			switch (action) {
			case "点击":
				elementData = getElementsOfAPage(page);
				AppiumUtil.click(elementData.get(keyword)[0], elementData.get(keyword)[1]);//AppiumUtil.click("ID","com.maimiao.live.tv:id/master_layout")
				break;
			case "输入":				
				elementData = getElementsOfAPage(page);
				AppiumUtil.input(elementData.get(keyword)[0], elementData.get(keyword)[1],information);
				break;
			case "暂停":
				AppiumUtil.pause(information);
				break;
			case "清除":
				elementData = getElementsOfAPage(page);
				AppiumUtil.clear(elementData.get(keyword)[0], elementData.get(keyword)[1]);
				break;
			case "QQ登录":
				AppiumUtil.loginByQQ(information);
			 	break;
			case "退出QQ登录":
				AppiumUtil.logOut();
			 	break;
			case "返回":
				AppiumUtil.returnButton();
				break;
			case "进入房间":
				AppiumUtil.openLive(information);
			 	break;
			case"滑动":
				elementData = getElementsOfAPage(page);
				AppiumUtil.swipe(elementData.get(keyword)[0], elementData.get(keyword)[1]);
				break;
			case"长按":
				elementData = getElementsOfAPage(page);
				AppiumUtil.touch(elementData.get(keyword)[0], elementData.get(keyword)[1],information);
				break;
			case "检查元素":
				elementData = getElementsOfAPage(page);
				AppiumUtil.isElementCorrect(elementData.get(keyword)[0], elementData.get(keyword)[1], information);
				break;
			case "正则匹配":
				elementData = getElementsOfAPage(page);
				AppiumUtil.regExpression(elementData.get(keyword)[0], elementData.get(keyword)[1], information);
				break;
			case "检查文本":
				elementData = getElementsOfAPage(page);
				AppiumUtil.isTextCorrect(elementData.get(keyword)[0], elementData.get(keyword)[1], information);
				break;
			case "返回首页":
				//AppiumUtil.returnHome();
				AppiumUtil.returnHomeByToast("再按一次退出全民直播");
			 	break;
			case "pc发送弹幕":
				OpenWeb.sendBarrage(information);
				break;
			default:
				logger.error("你输入的操作：["+action+"]暂未适配，敬请期待!");
				Assert.fail("你输入的操作：["+action+"]暂未适配，敬请期待!");
			}
			
			AppiumUtil.pause(300);
			
		}		  
	}
	
	private static String getBlank(int num) {
		String result="";
		for(int i=0;i<num;i++) {
			result+=" ";
		}
		
		return result;
	}
	
	public static Map<String, String[]> getElementsOfAPage(String pageName){
		//System.out.println("pageName---"+pageName);
		Map<String, String[]> elementData = ReadExcelForElement.parseExcel(pageName);
//		System.out.println(elementData);
		return elementData;
	}
}
