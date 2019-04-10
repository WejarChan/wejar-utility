package org.wejar.commons.utils.my;

import java.util.Arrays;
import java.util.Random;

public class MyRandomUtils {

	public static int nextInt(int bound){
		Random rand = new Random(System.currentTimeMillis()-100L);
		return rand.nextInt(bound);
	}
	
	public static String randomString(int length){
		Random rand = new Random(System.currentTimeMillis()-100L);
		char[] arr = new char[length];
		while(length -- > 0){
			int low = rand.nextInt(2);
			if(low == 1){
				arr[length] = (char)(97+rand.nextInt(26));
			}else{
				arr[length] = (char)(65+rand.nextInt(26));
			}
		}
		return String.valueOf(arr);
	}
	
	public static void main(String args[]) throws InterruptedException{
		
		Random rand = new Random(System.currentTimeMillis()-100L);
		int count = 0;
		for(int i=0;i<1000;++i){
//			int result = rand.nextInt(110);
			int result = nextInt(10);
			Thread.sleep(150L);
			if(result ==0){
				++count;
				System.err.println(result);
			}else{
				System.out.println(result);
			}
		}
		System.out.println("count:"+count);
	}
	
	
	
}
