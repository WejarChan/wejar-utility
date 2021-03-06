package org.wejar.qrcode;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liuxf
 * @date 2018/10/22 14:28
 * @param
 * @return
 */
public class QrCodeDetectCapture {

	static Logger logger = LoggerFactory.getLogger(QrCodeDetectCapture.class);

	static boolean hasLoadOpencv = false;

	private boolean debugMode = false;

	private String captureOutputPath = "/home/wejarchan/Desktop/CVOUT/";

	private String debugOutputPath = "/home/wejarchan/Desktop/CVDEBUG/";

	// 彩色列表，用于画图时候使用
	static List<Scalar> scalarList = new ArrayList<>();
	static {
		scalarList.add(new Scalar(255, 192, 203));
		scalarList.add(new Scalar(220, 20, 60));
		scalarList.add(new Scalar(123, 104, 238));
		scalarList.add(new Scalar(100, 149, 237));
		scalarList.add(new Scalar(0, 255, 255));
		scalarList.add(new Scalar(0, 250, 154));
		scalarList.add(new Scalar(0, 255, 0));
		scalarList.add(new Scalar(255, 255, 0));
		scalarList.add(new Scalar(218, 165, 32));
		scalarList.add(new Scalar(139, 69, 19));
		scalarList.add(new Scalar(0, 128, 0));
	}

	static {
		try {
			System.out.println(System.getProperty("java.library.path"));
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			hasLoadOpencv = true;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.info("加载opencv_341失败，找不到库文件。");
		}
	}

	public static void main(String[] args) {
		String filePath = "/home/wejarchan/Desktop/0cf82301c6e1467d91b6cd04bdda08692.jpg";
		QrCodeDetectCapture qdc = new QrCodeDetectCapture();
		long startTime = System.currentTimeMillis();
		String path = qdc.qrCodeCorrection(filePath);
		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
		System.out.println(path);
		
//		File dir = new File("/home/wejarchan/Downloads/权益ui");
//		for(File file : dir.listFiles()) {
//			String path = file.getAbsolutePath();
//			
//			Mat src = Imgcodecs.imread(path, 1);
//			
//			// 图像太大，压缩至短边小于1500
//			if (Math.min(src.width(), src.height()) > 700) {
//				int width = src.width();
//				int height = src.height();
//				do {
//					width /= 2;
//					height /= 2;
//				} while (Math.min(width, height) > 1500);
//				
//				// 短边小于1500，重设图片大小
//				Imgproc.resize(src, src, new Size(width, height));
//			}
//			
//			Imgcodecs.imwrite(path,  src);
//
//		}

		
		
		
		
		
		
	}

	public String qrCodeCorrection(String qrcodePicPath) {
		String apiName = "[二维码矫正]";

		if (debugMode) {
			if (debugOutputPath != null && debugOutputPath.length() > 0) {
				if (!debugOutputPath.endsWith(File.separatorChar + "")) {
					debugOutputPath += (File.separatorChar + "");
				}
				File outputDir = new File(debugOutputPath);
				if (!outputDir.exists()) {
					logger.info("debug输出目录不存在，开始创建目录。path:{}", debugOutputPath);
					outputDir.mkdirs();
				}
			}
		}

		File file = new File(qrcodePicPath);

		if (!file.exists()) {
			logger.info("{}--二维码矫正失败，文件不存在。path:{}", apiName, qrcodePicPath);
			return null;
		}
		Mat src = Imgcodecs.imread(qrcodePicPath, 1);
		// 使用100作为二值化的阀值进行三角形检测
		Triangle triangle = detectQrCodeTrangle(src, 100);
		if (triangle == null) {
			logger.info("{}--100阀值检测不到三角形，使用80作为阀值检测三角形。");
			triangle = detectQrCodeTrangle(src, 80);
		}
		if (triangle == null) {
			logger.info("{}--100阀值检测不到三角形，使用80作为阀值检测三角形。");
			triangle = detectQrCodeTrangle(src, 60);
		}

		if (triangle == null) {
			logger.info("{}--矫正失败，找不到定位二维码的三角形。", apiName);
			return null;
		}
		logger.info("{}--找到定位二维码的三角形。triangle:{}", apiName, triangle.toString());
		String outputFilePath = capture(triangle, src, captureOutputPath);
		logger.info("{}--二维码矫正成功，输出文件路径:{}", apiName, outputFilePath);
		return outputFilePath;
	}

	private Triangle detectQrCodeTrangle(Mat src, double thresh) {
		Mat src_gray = new Mat();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		List<MatOfPoint> markContours = new ArrayList<MatOfPoint>();

		// 图像太大，压缩至短边小于1500
		if (Math.min(src.width(), src.height()) > 1500) {
			int width = src.width();
			int height = src.height();
			do {
				width /= 2;
				height /= 2;
			} while (Math.min(width, height) > 1500);

			// 短边小于1500，重设图片大小
			Imgproc.resize(src, src, new Size(width, height));
		}

		/** 图片太小就放大 **/
		if (Math.max(src.width(), src.height()) < 500) {
			int width = src.width();
			int height = src.height();
			do {
				width *= 2;
				height *= 2;
			} while (Math.max(width, height) < 500);
			// 长边小于500，重设大小至长边大于500
			Imgproc.resize(src, src, new Size(width, height));
		}

		Mat src_all = src.clone();
		// 彩色图转灰度图
		Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_RGB2GRAY);
		debugOutput("1_灰度处理", src_gray);

		// 对图像进行平滑处理
		Imgproc.GaussianBlur(src_gray, src_gray, new Size(3, 3), 0);
		debugOutput("2_平滑处理", src_gray);

		Imgproc.threshold(src_gray, src_gray, thresh, 255, Imgproc.THRESH_BINARY_INV);
		debugOutput("3_二值化处理", src_gray);

		// 对图像进行平滑处理
		Imgproc.GaussianBlur(src_gray, src_gray, new Size(3, 3), 0);
		debugOutput("4_平滑处理", src_gray);

		Imgproc.Canny(src_gray, src_gray, 112, 255);
		debugOutput("5_边缘检测", src_gray);

		Mat hierarchy = new Mat();
		Imgproc.findContours(src_gray, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);

		for (int i = 0; i < contours.size(); i++) {
			MatOfPoint2f newMtx = new MatOfPoint2f(contours.get(i).toArray());
			RotatedRect rotRect = Imgproc.minAreaRect(newMtx);
			double w = rotRect.size.width;
			double h = rotRect.size.height;
			double rate = Math.max(w, h) / Math.min(w, h);
			/***
			 * 长短轴比小于1.3，总面积大于60
			 */
			if (rate < 1.3 && w < src_gray.cols() / 4 && h < src_gray.rows() / 4
					&& Imgproc.contourArea(contours.get(i)) > 60) {
				/***
				 * 计算层数，二维码角框有五层轮廓（有说六层），这里不计自己这一层，有4个以上子轮廓则标记这一点
				 */
				double[] ds = hierarchy.get(0, i);
				if (ds != null && ds.length > 3) {
					int count = 0;
					if (ds[3] == -1) {/** 最外层轮廓排除 */
						continue;
					}
					/***
					 * 计算所有子轮廓数量
					 */
					while ((int) ds[2] != -1) {
						++count;
						ds = hierarchy.get(0, (int) ds[2]);
					}
					if (count >= 4) {
						markContours.add(contours.get(i));
					}
				}
			}
		}

		Set<String> coordinates = new HashSet<>();
		System.out.println("----------标注点数:" + markContours.size());

		Iterator<MatOfPoint> it = markContours.iterator();
		while (it.hasNext()) {
			MatOfPoint matOfPoint = it.next();
			Point point = centerCal(matOfPoint);
			double x = Math.floor(point.x);
			double y = Math.floor(point.y);
			String key = x + "," + y;
			if (coordinates.contains(key)) {
				System.out.println("========已存在该点，抛弃它");
				it.remove();
				continue;
			}
			coordinates.add(key);
		}
		System.out.println("----------点去重后剩下数量:" + markContours.size());

		for (int i = 0; i < markContours.size(); i++) {
			int idx = (i + 1) % scalarList.size();
			Scalar scalar = scalarList.get(idx);
			Imgproc.drawContours(src_all, markContours, i, scalar, -1);
			MatOfPoint matOfPoint = markContours.get(i);
			Point point = centerCal(matOfPoint);
			System.out.println("-------------------画点坐标--x：" + point.x + "   y:" + point.y);
		}

		debugOutput("6_画点", src_all);

		/***
		 * 二维码有三个角轮廓，少于三个的无法定位放弃，多余三个的循环裁剪出来
		 */
		if (markContours.size() < 3) {
			return null;
		}
		// 把每3个点组成一个三角形，并存起来。
		List<Triangle> triangleList = new ArrayList<>();
		for (int i = 0; i < markContours.size() - 2; i++) {
			for (int j = i + 1; j < markContours.size() - 1; j++) {
				for (int k = j + 1; k < markContours.size(); k++) {
					Point point1 = centerCal(markContours.get(i));
					Point point2 = centerCal(markContours.get(j));
					Point point3 = centerCal(markContours.get(k));
					triangleList.add(new Triangle(point1, point2, point3));
				}
			}
		}

		// 找出里边直角三角形
		double thresholdMax = 10;
		double thresholdOther = 5;

		Double minAllSub = null;
		Triangle target = null;

		for (Triangle triangle : triangleList) {
			double maxAngle = triangle.getMaxAngle();
			if (Math.abs(90D - maxAngle) < thresholdMax) {
				// 最大的角离90度 在 5°以内
				// 另外2个角的角度差不多一样大
				double otherAngle1 = 0d;
				double otherAngle2 = 0d;
				if (triangle.getAngle1() == maxAngle) {
					otherAngle1 = triangle.getAngle2();
					otherAngle2 = triangle.getAngle3();
				} else if (triangle.getAngle2() == maxAngle) {
					otherAngle1 = triangle.getAngle1();
					otherAngle2 = triangle.getAngle3();
				} else {
					otherAngle1 = triangle.getAngle2();
					otherAngle2 = triangle.getAngle1();
				}

				double minOtherAngle = (180 - maxAngle) / 2 - thresholdOther;
				double maxOtherAngle = (180 - maxAngle) / 2 + thresholdOther;

				if (otherAngle1 >= minOtherAngle && otherAngle1 <= maxOtherAngle) {
					if (otherAngle2 >= minOtherAngle && otherAngle2 <= maxOtherAngle) {
						// 是符合要求的三角形。
						// 计算此三角形和正45°直角三角形的差异大小。。。

						double allSub = Math.abs(90D - maxAngle) + Math.abs(45D - otherAngle1)
								+ Math.abs(45D - otherAngle2);
						// 选择差异最小的三角形
						if (minAllSub == null) {
							minAllSub = allSub;
							target = triangle;
							System.out.println("获得第一个符合的三角形。A1:" + triangle.getAngle1() + "\tA2:" + triangle.getAngle2()
									+ "\tA3:" + triangle.getAngle3());
						} else {
							if (allSub < minAllSub) {
								minAllSub = allSub;
								target = triangle;
								System.out.println("找到更优的符合的三角形。A1:" + triangle.getAngle1() + "\tA2:"
										+ triangle.getAngle2() + "\tA3:" + triangle.getAngle3());
							}
						}
					}
				}
			}
		}
		return target;
	}

	/**
	 * 对图片进行矫正，裁剪
	 * 
	 * @param contours
	 * @param src
	 * @param idx
	 */
	private String capture(Triangle triangle, Mat src, String outputPath) {
		// String apiName = "[二维码矫正]";
		if (triangle == null || src == null) {
			return null;
		}
		if (outputPath == null || outputPath.length() <= 0) {
			logger.info("矫正输出路径不能为空。");
			return null;
		}

		System.out.println("进行矫正的三角形:" + triangle.toString());

		if (!outputPath.endsWith(File.separatorChar + "")) {
			outputPath += (File.separatorChar + "");
		}
		File outputDir = new File(outputPath);
		if (!outputDir.exists()) {
			logger.info("矫正输出目录不存在，开始创建目录。path:{}", outputPath);
			outputDir.mkdirs();
		}

		Point point1 = triangle.getPoint1();
		Point point2 = triangle.getPoint2();
		Point point3 = triangle.getPoint3();

		Double angle1 = triangle.getAngle1();
		Double angle2 = triangle.getAngle2();
		Double angle3 = triangle.getAngle3();

		int ccw1 = triangle.getCcw1();
		int ccw2 = triangle.getCcw2();
		int ccw3 = triangle.getCcw3();

		/** 画线 * **/
		Mat sline = src.clone();
		Imgproc.line(sline, point1, point2, new Scalar(0, 0, 255), 2);
		Imgproc.line(sline, point2, point3, new Scalar(0, 0, 255), 2);
		Imgproc.line(sline, point1, point3, new Scalar(0, 0, 255), 2);
		debugOutput("划线处理", sline);

		Point[] poly = new Point[4];
		if (angle3 > angle2 && angle3 > angle1) {
			if (ccw3 == 1) {
				poly[1] = point2;
				poly[3] = point1;
			} else {
				poly[1] = point1;
				poly[3] = point2;
			}
			poly[0] = point3;
			Point temp = new Point(point1.x + point2.x - point3.x, point1.y + point2.y - point3.y);
			poly[2] = temp;
		} else if (angle2 > angle1 && angle2 > angle3) {
			if (ccw2 == 1) {
				poly[1] = point1;
				poly[3] = point3;
			} else {
				poly[1] = point3;
				poly[3] = point1;
			}
			poly[0] = point2;
			Point temp = new Point(point1.x + point3.x - point2.x, point1.y + point3.y - point2.y);
			poly[2] = temp;
		} else if (angle1 > angle2 && angle1 > angle3) {
			if (ccw1 == 1) {
				poly[1] = point2;
				poly[3] = point3;
			} else {
				poly[1] = point3;
				poly[3] = point2;
			}
			poly[0] = point1;
			Point temp = new Point(point2.x + point3.x - point1.x, point2.y + point3.y - point1.y);
			poly[2] = temp;
		}

		Point[] trans = new Point[4];

		int temp = 50;
		trans[0] = new Point(0 + temp, 0 + temp);
		trans[1] = new Point(0 + temp, 100 + temp);
		trans[2] = new Point(100 + temp, 100 + temp);
		trans[3] = new Point(100 + temp, 0 + temp);

		Mat perspectiveMmat = Imgproc.getPerspectiveTransform(
				Converters.vector_Point_to_Mat(Arrays.asList(poly), CvType.CV_32F),
				Converters.vector_Point_to_Mat(Arrays.asList(trans), CvType.CV_32F)); // warp_mat
		Mat dst = new Mat();
		// 计算变换结果
		Imgproc.warpPerspective(src, dst, perspectiveMmat, src.size(), Imgproc.INTER_LINEAR);
		Rect roiArea = new Rect(0, 0, 200, 200);
		Mat dstRoi = new Mat(dst, roiArea);
		logger.info("矫正图像成功。");

		String fileName = System.currentTimeMillis() + ".jpg";
		String absPath = outputPath + fileName;
		Imgcodecs.imwrite(absPath, dstRoi);
		logger.info("矫正输出文件成功。filePath:" + absPath);
		return absPath;
	}

	private void debugOutput(String name, Mat src_gray) {
		if (debugMode) {
			if (debugOutputPath != null && debugOutputPath.length() > 0) {
				String fileName = name + ".jpg";
				String absPath = debugOutputPath + fileName;
				Imgcodecs.imwrite(absPath, src_gray);
			}
		}
	}

	private static BufferedImage toBufferedImage(Mat m) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);

		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);

		return image;
	}

	private static Point centerCal(MatOfPoint matOfPoint) {
		double centerx = 0, centery = 0;
		int size = matOfPoint.cols();
		MatOfPoint2f mat2f = new MatOfPoint2f(matOfPoint.toArray());
		RotatedRect rect = Imgproc.minAreaRect(mat2f);
		Point vertices[] = new Point[4];
		rect.points(vertices);
		centerx = ((vertices[0].x + vertices[1].x) / 2 + (vertices[2].x + vertices[3].x) / 2) / 2;
		centery = ((vertices[0].y + vertices[1].y) / 2 + (vertices[2].y + vertices[3].y) / 2) / 2;
		Point point = new Point(centerx, centery);
		return point;
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	public String getDebugOutputPath() {
		return debugOutputPath;
	}

	public void setDebugOutputPath(String debugOutputPath) {
		this.debugOutputPath = debugOutputPath;
	}

	public String getCaptureOutputPath() {
		return captureOutputPath;
	}

	public void setCaptureOutputPath(String captureOutputPath) {
		this.captureOutputPath = captureOutputPath;
	}
}