package com.quanmin.create;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CreateTestClass {
	static String casePath = "res/testcases/android";
	/* 这里写死了安卓测试用例的类的路径 */
	static String caseFolder = "src/test/java/com/quanmin/testcase/android/";
	
	public static void main(String[] args) {
		createTestcase();
	}

	/* 获取res/testcases/android路径下有多少个文件及文件夹 */
	public static int getFunctionNum() {
		/*
		//String casePath = "res/testcases/android";
		File[] file = new File(casePath).listFiles();
//		for (File f : file) {
//			System.out.println(f.getName());
//		}
		return file.length;
		*/
		return new File(casePath).listFiles().length;
	}

	/* 获取所要测试功能的文件的名称 */
	public static String getFunctionName(int index) {
		//String casePath = "res/testcases/android";
		
		String pathName = "";
		String functionName = "";
		// 获取文件路径
		File file = new File(casePath);

		// 截取功能名称
		File[] array = file.listFiles();
		if (index<array.length && array[index].isFile()) {
			pathName = array[index].getName();
//			pathName = "MyPageCase.xlsx";
//			System.out.println("先打印一下文件name:"+pathName);
			functionName = pathName.substring(0, pathName.lastIndexOf("."));
		}else {
			functionName = "blank";
		}
		return functionName;
	}

	/* 获取excelcase相对路径 */
	public static String getExcelRelativePath(int index) {
//		String casePath = "res/testcases/android";
		String testcaseRelPath = "";
		File[] array = new File(casePath).listFiles();
		if(index < array.length) {
			testcaseRelPath = array[index].getPath();
		}
		return testcaseRelPath;
	}

	/* 获取excel中的sheet数量 */
	public static int getSheetNum(String path) {
		FileInputStream filePath = null;
		XSSFWorkbook workbook = null;
		int sheetNum = 0;
		
		try {
			filePath = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			System.out.println(path + "文件不存在");
		}

		try {
			workbook = new XSSFWorkbook(filePath);
		} catch (IOException e) {
			System.out.println("未找到excel文件");
		}
		sheetNum = workbook.getNumberOfSheets();
		return sheetNum;
	}

	/* 获取excel中sheet名称 */
	public static String getSheetName(String path, int index) {
		FileInputStream filePath = null;
		XSSFWorkbook workbook = null;
		String sheetName = "";
		try {
			filePath = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			System.out.println(path + "文件不存在");
		}
		try {
			workbook = new XSSFWorkbook(filePath);
		} catch (IOException e) {
			System.out.println("未找到excel文件");
		}
		if(index < workbook.getNumberOfSheets()) {
			sheetName = workbook.getSheetName(index);
		}else {
			sheetName = "blank";
		}
		
		return sheetName;
	}

	public static void createTestcase() {
		//File sourceFile = null;
		File testClass = null;
		FileWriter writer = null;
		String functionName = "";/*用于存放获取到的case的文件名: MyPage*/
		String funcName = "";/*用作包名:myPage*/
		File functionPackage = null;/*包路径*/
		String testCaseName = "";/*用于存放测试用例名，即类名:MyPage_001*/
		String sheetName = "";/*用于存放sheet页名*/
		
		/*获取case个数后，循环处理每个xlsx，但是这个getFunctionNum()没做异常处理，比如没有去掉文件夹等*/
		int fileNum = getFunctionNum();/*定义返回文件的数量*/
		for (int i = 0; i < fileNum; i++) {
			functionName = getFunctionName(i);/*获取每一个文件的名字*/
			if("blank".equals(functionName)) {
				continue;/*如果是目录，就继续下一次循环*/
			}

			// 先过滤当excel文件打开的时候，生成的~$Homecase.xlsx文件，避免报错。
			if (functionName.startsWith("~$")) {
				continue;/*如果文件是以~$开头，或者不是以.xlsx结尾，就继续下一次循环*/
			}
			
			// 将functionName的首字母转换为小写
			funcName = functionName.replaceFirst(functionName.substring(0, 1),functionName.substring(0, 1).toLowerCase());
			testCaseName = functionName;
			
			// 如果包名不存在，就创建
			functionPackage = new File(caseFolder + "/" + funcName);
			
			if (functionPackage.exists()) {
				System.out.println(funcName + "包已存在，自动跳过");
			} else {
				functionPackage.mkdir();
				System.out.println(funcName + "包已创建");
			}
			System.out.println("正在生成用例到" + funcName + "包下，请稍等...");
			
			// 循环遍历每个excel的sheet页，过滤
			for (int j = 0; j < getSheetNum(getExcelRelativePath(i)); j++) {
				sheetName = getSheetName(getExcelRelativePath(i), j);
				/*根据名字判断是否是第一个sheet页，如果是，则跳过*/
				if(sheetName.toLowerCase().contains("action")) {
					continue;
				}
				
				testClass = new File(caseFolder + "/" + funcName + "/" + testCaseName + "_"+ sheetName + ".java");/*构造测试用例的文件名，即类名*/
				
				if (testClass.exists()) {
					System.out.println(funcName + "_" + sheetName + ".java测试类已存在，自动跳过");

				} else {
//					sourceFile = new File(caseFolder + "/" + funcName + "/",testCaseName + "_" + getSheetName(getExcelRelativePath(i), j) + ".java");
					try {
						testClass.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}

					try {
						writer = new FileWriter(testClass);
						writer.write("package com.quanmin.testcase.android." + funcName + ";\n\n");
						writer.write("import org.testng.annotations.Test;\n");
						writer.write("import com.quanmin.appium.SuperAction;\n");
						writer.write("import com.quanmin.appium.AppiumUtil;\n\n\n");
						writer.write("public class " + testCaseName + "_" + sheetName+ " extends AppiumUtil {\n");
						writer.write("\t@Test\n");
						writer.write("\tpublic void " + funcName + "_"+ sheetName + "(){\n");
						writer.write("\t\tSuperAction.parseCase(\"com.quanmin.testcase." + funcName + "."+ testCaseName + "_" + sheetName+ "\", \"android\");\n");
						writer.write("\t}\n");
						writer.write("}");
						writer.flush();
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.out.println("自动化测试用例---"+testCaseName + "_" + sheetName + ",已创建");
				}
			}
		}
		System.out.println("所有用例已创建完毕! ");
	}
}
