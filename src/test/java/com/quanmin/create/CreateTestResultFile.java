package com.quanmin.create;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class CreateTestResultFile {
	public static void main(String[] args) {
		// System.out.println(getFormatDateTime());
		/* testTry(); */
		// System.out.println(getAllVersionInfo());
		test1();
	}

	public static Map<Integer, HashMap<String, String>> getAllVersionInfo() {
		/*
		 * 			this is a demonstration for generating a test result file by android QR code,
		 * 			and it hasn't been done yet.
		 */
		Map<Integer, HashMap<String, String>> infos = new TreeMap<Integer, HashMap<String, String>>();
		// Sn,Hash_8,TestStatus,ReportDir,Author,PackageCreateTime,Msg,DownloadUrl,LocalDir

		int sn = 1;
		String hash_8 = "8cl9f82l";
		String testStatus = "true";
		String reportDir = "D:\\workspace_oxygen\\app_Project\\test-output\\excelReports";
		String author = "lijinchao@qmtv.com";
		String packageCreateTime = "2018/5/11 下午1:32:59";
		String msg = "Merge branch 'release/4.2.10' of git.quanmin.tv:Android/QuanminLive into release/4.2.10";
		String downloadUrl = "http://xxx.xx/x";
		String localDir = "D:\\workspace_oxygen\\app_Project\\res\\app\\android";

		HashMap<String, String> build1 = new HashMap<String, String>();
		build1.put("hash_8", hash_8);
		build1.put("testStatus", testStatus);
		build1.put("reportDir", reportDir);
		build1.put("author", author);
		build1.put("packageCreateTime", packageCreateTime);
		build1.put("msg", msg);
		build1.put("downloadUrl", downloadUrl);
		build1.put("localDir", localDir);

		infos.put(sn, build1);

		sn = 2;
		hash_8 = "p09f82l";
		testStatus = "blank";
		reportDir = "D:\\workspace_oxygen\\app_Project\\test-output\\excelReports";
		author = "lijinchao@qmtv.com";
		packageCreateTime = "2018/5/11 下午1:33:03";
		msg = "[dev]:{优化LiveActivity跳转问题}";
		downloadUrl = "http://xxx.xx/x";
		localDir = "D:\\workspace_oxygen\\app_Project\\res\\app\\android";

		HashMap<String, String> build2 = new HashMap<String, String>();
		build2.put("hash_8", hash_8);
		build2.put("testStatus", testStatus);
		build2.put("reportDir", reportDir);
		build2.put("author", author);
		build2.put("packageCreateTime", packageCreateTime);
		build2.put("msg", msg);
		build2.put("downloadUrl", downloadUrl);
		build2.put("localDir", localDir);

		infos.put(sn, build2);
		
		sn = 3;
		hash_8 = "poek9999";
		testStatus = "false";
		reportDir = "blank";
		author = "lijinchao@qmtv.com";
		packageCreateTime = "2018/5/12 下午1:33:03";
		msg = "[dev]:{优化LiveActivity跳转问题}";
		downloadUrl = "http://xxx.xx/x";
		localDir = "D:\\workspace_oxygen\\app_Project\\res\\app\\android";

		HashMap<String, String> build3 = new HashMap<String, String>();
		build3.put("hash_8", hash_8);
		build3.put("testStatus", testStatus);
		build3.put("reportDir", reportDir);
		build3.put("author", author);
		build3.put("packageCreateTime", packageCreateTime);
		build3.put("msg", msg);
		build3.put("downloadUrl", downloadUrl);
		build3.put("localDir", localDir);

		infos.put(sn, build3);

		return infos;
	}

	public static void test1() {
		String proPath = System.getProperty("user.dir");
		System.out.println("project path is " + proPath);

		String suffix = ".txt";/* 文件后缀名 */
		String filepath = proPath + File.separator + "res" + File.separator + "result";
		String versionNum = "4.2.0";
		String filename = "qmtv_android_" + versionNum + suffix;
		String newFilename = "tmp_qmtv_android_" + versionNum + suffix;
		File parentDir = new File(filepath);
		File file = new File(filepath + File.separator + filename);
		File newFile = new File(filepath + File.separator + newFilename);

		HashMap<String, String> hm = null;
		int sn = 0;
		String hash_8 = "blank";
		String testStatus = "blank";
		String reportDir = "blank";
		String author = "blank";
		String packageCreateTime = "blank";
		String msg = "blank";
		String downloadUrl = "blank";
		String localDir = "blank";
		
		Map<Integer, HashMap<String, String>> infos = getAllVersionInfo();

		/* 检查父目录，不存在就创建 */
		if (!parentDir.exists()) {
			System.out.println("父目录不存在，即将创建...");
			boolean createDirFlag = parentDir.mkdirs();
			if (createDirFlag) {
				System.out.println("创建父目录成功");
			} else {
				System.out.println("创建父目录失败，父目录已经存在。");
			}
		} else {
			System.out.println("父目录已存在。");
		}

		/* 检查文件是否存在，不存在就创建 */
		if (!file.exists()) {
			System.out.println("文件不存在，即将创建...");
			try {
				boolean createFileFlag = file.createNewFile();
				if (createFileFlag) {
					System.out.println("文件创建成功");
					/*
					 * 向文件里写入表头 序号|哈希值前8位|测试状态|报告路径|包作者|包创建时间|commit消息|包下载路径|本地存放包的路径
					 * Sn|Hash_8|TestStatus|ReportDir|Author|PackageCreateTime|Msg|DownloadUrl|LocalDir
					 */
					boolean flag = createNewFile(file);
					if (flag) {
						System.out.println("写入表头成功。");
					} else {
						System.out.println("写入表头失败。");
					}
					
					for (int i = infos.size(); i >0 ; i--) {
						sn = i;
						hm = infos.get(sn);
						
						hash_8 = hm.get("hash_8");
						testStatus = hm.get("testStatus");
						reportDir = hm.get("reportDir");
						author = hm.get("author");
						packageCreateTime = hm.get("packageCreateTime");
						msg = hm.get("msg");
						downloadUrl = hm.get("downloadUrl");
						localDir = hm.get("localDir");
						
						String lineinfo = sn+"|"+hash_8+"|"+testStatus+"|"+reportDir+"|"+author+"|"+packageCreateTime+"|"+msg+"|"+downloadUrl+"|"+localDir;
//						System.out.println("**************************************************************");
//						System.out.println(lineinfo);
//						System.out.println("--------------------------------------------------------------");
						flag = writeSingleVerInfoToFile(file,lineinfo);
					}
					
					if (flag) {
						System.out.println("写入版本测试信息成功。");
					} else {
						System.out.println("写入版本测试信息失败。");
					}

				} else {
					System.out.println("创建文件失败，文件已经存在。");
				}
			} catch (IOException e) {
				System.out.println("创建文件时发生IO异常。。。");
				e.printStackTrace();
			}
		/* 否则认为文件存在，存在就读文件内容，然后和之前获取到的待测试所有build版本的信息进行比较，存入到一个新的文件，再移除原文件，再重命名 */
		}else{
			System.out.println("文件存在。");
			System.out.println("比较新获取到的待测试版本信息与原始测试文件，开始生成新的文件");
			BufferedReader br = null;
			BufferedWriter bw = null;
			try {
				br = new BufferedReader(new FileReader(file));
				bw = new BufferedWriter(new FileWriter(newFile));
				String line = null;
				boolean compareDoneFlag = false;
				int lineNum = 0;
				while((line=br.readLine())!=null) {
					lineNum++;
					if(lineNum == 1 || lineNum == 2) {
						bw.write(line);
						bw.newLine();
					}else{
						sn = Integer.parseInt(line.split("|")[0]);
						if(!compareDoneFlag) {
							for (int i = infos.size(); i >0 ; i--) {
								hm = infos.get(i);
								
								hash_8 = hm.get("hash_8");
								testStatus = hm.get("testStatus");
								reportDir = hm.get("reportDir");
								author = hm.get("author");
								packageCreateTime = hm.get("packageCreateTime");
								msg = hm.get("msg");
								downloadUrl = hm.get("downloadUrl");
								localDir = hm.get("localDir");
								
								String lineinfo = i+"|"+hash_8+"|"+testStatus+"|"+reportDir+"|"+author+"|"+packageCreateTime+"|"+msg+"|"+downloadUrl+"|"+localDir;
								
								if(i>sn) {
									bw.write(lineinfo);
									bw.newLine();
								}
							}
							compareDoneFlag = true;
						}
						bw.write(line);
						bw.newLine();
					}
				}
				System.out.println("文件创建完成。");
			} catch (FileNotFoundException e) {
				System.out.println("文件没有找到。");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("读写文件发送IO异常。");
				e.printStackTrace();
			}finally {
				try {
					if (br != null) {
						br.close();
					}
				} catch (IOException e) {
					System.out.println("关闭文件时发送IO异常。");
					e.printStackTrace();
				}
				try {
					if (bw != null) {
						bw.close();
					}
				} catch (IOException e) {
					System.out.println("关闭文件时发送IO异常。");
					e.printStackTrace();
				}
			}
		}
		
		boolean flag = delAndRenameFile(file,newFile);
		if(flag) {
			System.out.println("安卓app待测试版本的所有build的信息文件已更新完成!");
		}else {
			System.out.println("更新文件时发生异常，请检查!");
		}
	}

	public static boolean delAndRenameFile(File oldfile, File newFile) {
		boolean flag = true;
		if(!oldfile.exists() && !newFile.exists()) {
			throw new RuntimeException("文件【"+oldfile+"】或文件【"+newFile+"】不存在，替换失败");
		}
		
		System.out.println("删除文件【"+oldfile.getName()+"】成功:"+oldfile.delete());
		System.out.println("将文件【"+newFile.getName()+"】重命名为【"+oldfile.getName()+"】成功:"+newFile.renameTo(oldfile));
		return flag;
	}

	public static boolean writeSingleVerInfoToFile(File file, String info) {
		boolean flag = true;
		BufferedWriter bw = null;
	
		try {
			bw = new BufferedWriter(new FileWriter(file,true));
			bw.write(info);
			bw.newLine();
			System.out.println("写入"+info.split("|")[0]+"版本测试信息完成.");
		} catch (IOException e) {
			flag = false;
			System.out.println("写入【" + file + "】文件时发生IO异常.");
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (IOException e) {
				flag = false;
				System.out.println("关闭【" + file + "】文件时发生IO异常.");
				e.printStackTrace();
			}
		}
		
		return flag;
	}

	public static boolean createNewFile(File file) {
		boolean flag = true;
//		String firstLine = "序号,哈希值前8位,测试状态,报告路径,包作者,包创建时间,commit消息,包下载路径,本地存放包的路径";
//		String secondLine = "Sn,Hash_8,TestStatus,ReportDir,Author,PackageCreateTime,Msg,DownloadUrl,LocalDir";

		String firstLine = "序号|哈希值前8位|测试状态|报告路径|包作者|包创建时间|commit消息|包下载路径|本地存放包的路径";
		String secondLine = "Sn|Hash_8|TestStatus|ReportDir|Author|PackageCreateTime|Msg|DownloadUrl|LocalDir";
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			bw.write(firstLine);
			bw.newLine();
			bw.write(secondLine);
			bw.newLine();
			System.out.println("写入表头完成.");
			// bos.flush();
		} catch (IOException e) {
			flag = false;
			System.out.println("写入【" + file + "】文件时发生IO异常.");
			e.printStackTrace();
		} finally {
			try {
				/*
				 * 为什么一定要close? A.让流对象变成垃圾，就可以被JVM垃圾回收器回收 B.通知系统去释放跟该文件相关的资源
				 */
				if (bw != null) {
					bw.close();
				}
			} catch (IOException e) {
				flag = false;
				System.out.println("关闭【" + file + "】文件时发生IO异常.");
				e.printStackTrace();
			}
		}
		return flag;
	}

	public static boolean write(File file, String str) {
		BufferedOutputStream bos = null;
		boolean flag = true;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(file));
			bos.write(str.getBytes());
			// bos.flush();
		} catch (IOException e) {
			flag = false;
			System.out.println("写入【" + file + "】文件时发生IO异常.");
			e.printStackTrace();
		} finally {
			try {
				if (bos != null) {
					bos.close();
				}
			} catch (IOException e) {
				flag = false;
				System.out.println("关闭【" + file + "】文件时发生IO异常.");
				e.printStackTrace();
			}
		}
		return flag;
	}

	public static String getFormatDateTime() {
		long time = System.currentTimeMillis();
		// System.out.println("Unix元年毫秒值:"+time);
		Date date = new Date(time);

		String pattern = "yyyy-MM-dd HH:mm:ss.SSS";
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		String datetime = format.format(date);
		System.out.println("格式化后的日期时间是:" + datetime);
		return datetime;
	}

	/*
	 * public static void testTry() { int[] arr = {1,2,3,4}; try { System.out.println(arr[5]);
	 * }catch(ArrayIndexOutOfBoundsException e){ System.out.println("发生数组角标越界异常!"); e.printStackTrace();
	 * }finally { System.out.println("关闭资源"); } System.out.println("over"); }
	 */
}
