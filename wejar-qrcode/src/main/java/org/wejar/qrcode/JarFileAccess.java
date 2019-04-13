package org.wejar.qrcode;

/**
* This class implements the funcationality of reading and writing files in jar files.
*/
import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.jar.*;
import java.util.Enumeration;

/**
 * @author Leo Share
 * @since 08/09/2007
 * @version 1.0
 */
public class JarFileAccess {
	private static final String fileSeparator = System.getProperty("file.separator");

	
	
	public static void main(String[] args) throws Exception {
		JarFileAccess jfa = new JarFileAccess();
//		jfa.accessJarFile("wejar-qrcode-0.0.1-SNAPSHOT.jar", "/home/wejarchan/.m2/repository/org/wejar/wejar-qrcode/0.0.1-SNAPSHOT","/home/wejarchan/Desktop/vcout");
	
		String str = "qx+D55gXOmMny20QAd/tEYXbdILsUW563y5RnXXa3LTdImRiehcRtW+Y/HolqyWWw9S4l8NkjuR2X+lcor7KpLostCvWMvZaprPjxXDnTptv74nrLZxAsccLCRa7w2e2yKaxyHj3+vXvhRdw64Z/UM2vRzA01BA8iIVYVO4NnVc=";
	
		System.out.println(str.length());
	}
	
	public void accessJarFile(String jarFileName, String fromDir, String toDir) throws Exception {
		JarFile myJarFile = new JarFile(fromDir + fileSeparator + jarFileName);
		JarEntry entry = myJarFile.getJarEntry("org/wejar/");
		System.out.println(entry.getName());
		
		if(entry != null) {
			if(entry.isDirectory()) {
				System.out.println("是文件夹");
				File f = new File(toDir + fileSeparator + entry.getName());
			}
			
			InputStream is = myJarFile.getInputStream(entry);
			FileOutputStream fos = new FileOutputStream(toDir + fileSeparator + entry.getName());
			byte[] b = new byte[1024];
			int len;
			while ((len = is.read(b)) != -1) {
				fos.write(b, 0, len);
			}
			fos.close();
			is.close();
		}
//		Enumeration myEnum = myJarFile.entries();
//		while (myEnum.hasMoreElements()) {
//			JarEntry myJarEntry = (JarEntry) myEnum.nextElement();
//			System.out.println(myJarEntry.getName());
//			if (myJarEntry.getName().equals("libopencv_java341.so")) {
//				InputStream is = myJarFile.getInputStream(myJarEntry);
//				FileOutputStream fos = new FileOutputStream(toDir + fileSeparator + myJarEntry.getName());
//				byte[] b = new byte[1024];
//				int len;
//				while ((len = is.read(b)) != -1) {
//					fos.write(b, 0, len);
//				}
//				fos.close();
//				is.close();
//				break;
//			} else {
//				continue;
//			}
//		}
//		myJarFile.close();
	}
}