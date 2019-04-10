package org.wejar.encryption;

import java.util.Arrays;

import net.vrallev.java.ecc.Ecc25519Helper;
import net.vrallev.java.ecc.KeyHolder;

public class App {

	
	
	
	public static void main(String args[]) {
		String seed = "12341222222524324";
		byte[] privateKey = KeyHolder.createPrivateKey(seed.getBytes());
		Ecc25519Helper helper = new Ecc25519Helper(privateKey);
		
		
		long time = System.currentTimeMillis();
		
		String msg = "abcdefg3333333";
		for(int i=0;i<1000;++i) {
			byte[] some = helper.sign(msg.getBytes());
		}
		
		long usingTime = System.currentTimeMillis() - time;
		
		System.out.println("using:"+usingTime);
		
		
		
		
	}
	
}
