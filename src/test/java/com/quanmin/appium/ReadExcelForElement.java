package com.quanmin.appium;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;

/* 获取文件中所有页面元素定位方式及定位值
 * 形如VerViewerPage.xlsx，包含了VerViewerPage这个页面中所有元素的别名、定位方式、定位值
 * 解析后返回的数据如下:
 * 
 */
public class ReadExcelForElement {
	public static Logger logger = Logger.getLogger(ReadExcelForElement.class);
	private static String baseDir = "res/elements";
	private static String elementSheetName = "elements";

	public static Map<String, String[]> parseExcel(String filename){/* 只传一个name进来，元素所在的目录是写死的。VerViewerPage */
		FileInputStream filePath = null;
		XSSFWorkbook workbook = null;
//		String file = baseDir+"/"+filename+".xlsx";/* res/elements/VerViewerPage.xlsx */
		String file = baseDir+"/"+filename+"Locate.xlsx";/* res/elements/VerViewerPageLocate.xlsx */
		
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
			logger.info("读取页面元素定位的excel文件时发生IO异常");
			Assert.fail("读取页面元素定位的excel文件时发生IO异常");
		}
		
		/*通过sheet页的名字得到sheet对象*/
//		Sheet sheet = workbook.getSheetAt(0);/*这个是根据索引来获取*/
		Sheet sheet = workbook.getSheet(elementSheetName);
		
		int rows = sheet.getPhysicalNumberOfRows();/*获取行数*/
		int cells = sheet.getRow(0).getPhysicalNumberOfCells();/*获取首行列数*/
		
		/*定义容器来存放要返回的元素定位信息:元素名称、定位方式、定位值*/
		Map<String, String[]> elementLocate = new HashMap<String, String[]>();
		
		/*如果rows不大于1，认为没有页面元素及其定位方式，用例失败*/
		if(rows<=1 || cells!=3) {/* 只有列数为3的时候，认为是正确的格式 */
			logger.info("相对路径【"+file+".xlsx】下,没有维护页面元素及其定位，用例失败.");
			Assert.fail("相对路径【"+file+".xlsx】下,没有维护页面元素及其定位，用例失败.");/*没有页面元素，就让这条用例失败*/
		}
        
        /*定义一个变量来存取每个单元格的值，默认为blank，当获取到的单元格值为null时，就使用这个blank代替null，其余情况正常处理*/
		String cellvalue = "blank";
		String elementName = "blank";
		String byType = "blank";/*定义一个长度为2的一位数组，index=0存放定位方式，index存放定位值*/
		String byValue = "blank";/*定义一个长度为2的一位数组，index=0存放定位方式，index存放定位值*/
		
		/*处理每一个页面元素*/
        for (int i = 1; i < rows; i++) {/*过滤掉表头，从第二行开始作为操作步骤，中间不能有空行*/
            for (int j = 0; j < cells; j++) {/*从第一列开始取*/
            	try {/*获取单元格的值*/
            		 sheet.getRow(i).getCell(j).setCellType(Cell.CELL_TYPE_STRING);/*设置将每个单元格作为字符串来处理*/
            		 cellvalue = sheet.getRow(i).getCell(j).getStringCellValue();/*获取该单元格的字符串值*/
            		 if(cellvalue == null || "".equals(cellvalue) ) {
//            			 throw new RuntimeException("【"+file+".xlsx】文件中,定位方式及定位值均不允许为空，用例失败.");
            			 Assert.fail("【"+file+".xlsx】文件中,定位方式及定位值均不允许为空，用例失败.");
            		 }
				} catch (Exception e) {
					Assert.fail("【"+file+".xlsx】文件中,获取页面元素别名、定位方式及定位值时发生异常，用例失败.");
				}
            	
            	/*根据列号，将单元格的值赋值给相关变量*/
            	if(j==0) {
            		elementName = cellvalue;/*存放页面元素别名*/
            	}else if(j==1) {
            		byType = cellvalue;/*存放页面元素定位方式*/
            	}else if(j==2) {
            		byValue = cellvalue;/*存放页面元素定位值*/
            	}
            }
            /*一行元素信息获取后，存放在*/
            String[] by = new String[2];
            by[0] = byType;
            by[1] = byValue;
            
            elementLocate.put(elementName,by);/*将elementName作为key，将元素定位方式、元素定位值组成的字符串数组作为值，添加到map集合里*/
        }
		return elementLocate;
	}
}
