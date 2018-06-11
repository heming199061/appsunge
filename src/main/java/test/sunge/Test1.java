package test.sunge;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class Test1 {

	public static void main(String[] args) {
//		test1();
		test2();
	}

	public static void test2() {
		String info = "123456|sunge.123";
		
		String[] vk = info.split("\\|");
		
		for (String string : vk) {
			System.out.println(string);
		}
	}

	public static void test1() {
		Map<String,Map<String,String>> school = new HashMap<String,Map<String,String>>();
		Map<String,String> class1 = new HashMap<String,String>();
		Map<String,String> class2 = new HashMap<String,String>();
		Map<String,String> class3 = new HashMap<String,String>();
		
		class1.put("a001", "张三");
		class1.put("a002", "李四");
		class1.put("a003", "王五");
		
		class2.put("b001", "佐助");
		class2.put("b002", "鸣人");
		class2.put("b003", "卡卡西");
		
		class3.put("c001", "娜美");
		class3.put("c002", "汉库克");
		
		school.put("cls1", class1);
		school.put("cls2", class2);
		school.put("cls3", class3);
		
		System.out.println(class2);
		System.out.println("----------------------------");
		printMap(class1,true);
		
		System.out.println("***********************************************");
		
		System.out.println(school);
		System.out.println("----------------------------");
		printMap2(school);
	}
	
	public static void printMap(Map<String,String> map,Boolean nextLineFlag) {
		String key = "";
		String value = "";
		Map.Entry<String, String> me = null;
		System.out.print("{");
		
		Set<Map.Entry<String, String>> entry = map.entrySet();
		int size = entry.size();
		
		Iterator<Map.Entry<String, String>> it = entry.iterator();
		int i=0;
		while(it.hasNext()) {
			i++;
			me = it.next();
			key = me.getKey();
			value = me.getValue();
			if(i<size) {
				System.out.print("\""+key+"\" : \""+value+"\",");
			}else {
				System.out.print("\""+key+"\" : \""+value+"\"");
			}
		}
		if(nextLineFlag) {
			System.out.println("}");
		}else {
			System.out.print("}");
		}
	}

	public static void printMap2(Map<String,Map<String,String>> map) {
		String key = "";
		Map<String,String> value = null;
		Map.Entry<String,Map<String,String>> me = null;
		System.out.println("{");
		
		Set<Map.Entry<String,Map<String,String>>> entry = map.entrySet();
		int size = entry.size();
		
		Iterator<Map.Entry<String,Map<String,String>>> it = entry.iterator();
		int i=0;
		while(it.hasNext()) {
			i++;
			me = it.next();
			key = me.getKey();
			value = me.getValue();
			if(i<size) {
				System.out.print("\t\""+key+"\" : \"");
				printMap(value,false);
				System.out.println("\",");
			}else {
				System.out.print("\t\""+key+"\" : \"");
				printMap(value,true);
			}
		}
		System.out.println("}");
	}
}
