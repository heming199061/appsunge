package com.quanmin.appium;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;

/* excel每个case的sheet页里的表头是固定的:
 *	序号    动作    元素定位    测试数据 
 */
public class ReadExcelForCase {
	public static Logger logger = Logger.getLogger(ReadExcelForCase.class);
	private static String basicCasePath = "res/testcases";

	/*ReadExcelForCase.getCase("MyPage", "001", "android")
	* 每一个step封装成一个HashMap，所有step封装在一个HashMap类型的数组里，数组打印如下:
	* key:1, value:{step_num=1, action=登录, locator=blank, test_data=2147865099/123456789}
	* key:2, value:{step_num=2, action=进入房间, locator=blank, test_data=1150670}
	* key:3, value:{step_num=3, action=点击, locator=VerViewerPage.[Android]星光, test_data=blank}
	* key:4, value:{step_num=4, action=点击, locator=VerViewerPage.[Android]守护榜, test_data=blank}
	* key:5, value:{step_num=5, action=点击, locator=VerViewerPage.[Android]第一名, test_data=blank}
	* key:6, value:{step_num=6, action=检查元素, locator=UserInfoPage.[Android]送出的牛币, test_data=true}
	* key:7, value:{step_num=7, action=返回按钮, locator=blank, test_data=blank}
	* key:8, value:{step_num=8, action=点击, locator=VerViewerPage.[Android]周榜, test_data=blank}
	* key:9, value:{step_num=9, action=点击, locator=VerViewerPage.[Android]第一名, test_data=blank}
	* key:10, value:{step_num=10, action=检查元素, locator=UserInfoPage.[Android]送出的牛币, test_data=true}
	* key:11, value:{step_num=11, action=返回按钮, locator=blank, test_data=blank}
	* key:12, value:{step_num=12, action=点击, locator=VerViewerPage.[Android]总榜, test_data=blank}
	* key:13, value:{step_num=13, action=点击, locator=VerViewerPage.[Android]第一名, test_data=blank}
	* key:14, value:{step_num=14, action=检查元素, locator=UserInfoPage.[Android]送出的牛币, test_data=true}
	* key:15, value:{step_num=15, action=返回按钮, locator=blank, test_data=blank}
	* key:16, value:{step_num=16, action=点击, locator=VerViewerPage.[Android]粉丝榜, test_data=blank}
	* key:17, value:{step_num=17, action=点击, locator=VerViewerPage.[Android]第一名, test_data=blank}
	* key:18, value:{step_num=18, action=检查元素, locator=UserInfoPage.[Android]送出的牛币, test_data=true}
	 */
	public static Map<Integer,TreeMap<String,String>> getCase(String caseFile,String sheetName,String platformName){
		FileInputStream filePath = null;
		XSSFWorkbook workbook = null;

		/*拼接testcase的excel文件路径*/
		String file = null;
		if(platformName.toLowerCase().equals("android")){
			file = basicCasePath + "/android/" + caseFile + ".xlsx";
		}else if(platformName.toLowerCase().equals("ios")){
			file = basicCasePath+ "/ios/" + caseFile + ".xlsx";
		}else{
			logger.info("设备只能是Android或ios");
			throw new RuntimeException("设备只能是Android或ios");/*抛出异常，让程序停下来*/
		}
		
		/*获取字节输入流对象，用来读取文件*/
		try {
			filePath = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			logger.info("文件"+file+"不存在");
			Assert.fail("文件"+file+"不存在");
		}
		
		/*获取能够处理xlsx文件的对象*/
		try {
			workbook = new XSSFWorkbook(filePath);
		} catch (IOException e) {
			logger.info("读取测试用例的excel文件时发生IO异常");
			Assert.fail("读取测试用例的excel文件时发生IO异常");
		}
		
		/*通过sheet页的名字得到sheet对象*/
		Sheet sheet = workbook.getSheet(sheetName);
		
		int rows = sheet.getPhysicalNumberOfRows();/*获取行数*/
		int cells = sheet.getRow(0).getPhysicalNumberOfCells();/*获取首行列数*/
		
		//System.out.println("row_num="+rows+", col_num="+cells);
		
		Map<Integer,TreeMap<String,String>> steps = new TreeMap<Integer,TreeMap<String,String>>(
				new Comparator<Integer>(){
		            public int compare(Integer o1,Integer o2){
		                return  o1-o2; //用正负表示大小值,外层用序号正序排列
		            }
		        });/*用来存放测试用例所有的步骤*/
		
		/*如果rows大于1，认为存在测试用例操作步骤，为每一个step创建一个HashMap对象，存到数组里*/
		if(rows > 1){
			for(int i=0;i < rows-1; i++){
				steps.put(i+1, new TreeMap<String,String>(
					new Comparator<String>(){
			            public int compare(String str1,String str2){//用正负表示大小值,内层用表头顺序来排列
			            	//表头:  序号    动作    元素定位    测试数据 
			            	int s1 = 0;
			        		int s2 = 0;
			        		
			        		if("step_num".equals(str1)) {
			        			s1 = 1;
			        		}else if("action".equals(str1)) {
			        			s1 = 2;
			        		}else if("locator".equals(str1)) {
			        			s1 = 3;
			        		}else if("test_data".equals(str1)) {
			        			s1 = 4;
			        		}

			        		if("step_num".equals(str2)) {
			        			s2 = 1;
			        		}else if("action".equals(str2)) {
			        			s2 = 2;
			        		}else if("locator".equals(str2)) {
			        			s2 = 3;
			        		}else if("test_data".equals(str2)) {
			        			s2 = 4;
			        		}
			        		
			        		return s1 - s2;//用正负表示大小值,外层用序号正序排列
			            }
					}));/*为每一个step创建一个TreeMap，来存放每一个step里的相关信息*/
			}
		}else{
			logger.info(caseFile+".xlsx文件中的"+sheetName+"标签页，没有用例操作步骤信息.");
			Assert.fail(caseFile+".xlsx文件中的"+sheetName+"标签页，没有用例操作步骤信息.");/*没有操作步骤，就让这条用例失败*/
		}
		
		/*定义一个变量来存取每个单元格的值，默认为blank，当获取到的单元格值为null时，就使用这个blank代替null，其余情况正常处理*/
		String cellvalue = "blank";
		/*处理每一个操作步骤*/
        for (int i = 1; i < rows; i++) {/*过滤掉表头，从第二行开始作为操作步骤，中间不能有空行*/
            for (int j = 0; j < cells; j++) {/*从第一列开始取*/
            	try {
            		 sheet.getRow(i).getCell(j).setCellType(Cell.CELL_TYPE_STRING);/*设置将每个单元格作为字符串来处理*/
            		 cellvalue = sheet.getRow(i).getCell(j).getStringCellValue();/*获取该单元格的字符串值*/
            		 if(cellvalue == null || "".equals(cellvalue) ) {
            			 cellvalue = "blank";
            		 }
				} catch (Exception e) {
					cellvalue = "blank";
					//Assert.fail("读取"+sheetName+"标签，处理step时，发生异常，用例failed.");/*处理每一个step时，发生异常就让这条用例失败，比如这个时候手动关闭了excel。*/
				}
            	steps.get(i).put(changeToEng(sheet.getRow(0).getCell(j).getStringCellValue()), cellvalue);/*将表头作为key，将该单元格的字符串作为值，添加到map集合里*/
            }
        }
		return steps;
	}
	
	/*将表头字段改为英文来传递*/
	private static String changeToEng(String title) {
		if("序号".equals(title)) {
			return "step_num";
		}else if("动作".equals(title)) {
			return "action";
		}else if("元素定位".equals(title)) {
			return "locator";
		}else if("测试数据".equals(title)) {
			return "test_data";
		}
		return "blank";
	}
}
