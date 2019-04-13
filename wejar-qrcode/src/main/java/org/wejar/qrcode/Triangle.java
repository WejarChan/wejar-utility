package org.wejar.qrcode;

import org.opencv.core.Point;

public class Triangle {

	private Point point1;
	private Point point2;
	private Point point3;
	
	private Double angle1;
	private Double angle2;
	private Double angle3;
	
	private int ccw1;
	private int ccw2;
	private int ccw3;
	
	public Triangle(Point point1,Point point2,Point point3) {
		this.point1 = point1;
		this.point2 = point2;
		this.point3 = point3;
		
		double[] ca = new double[2];
        double[] cb = new double[2];

        ca[0] =  point2.x - point1.x;
        ca[1] =  point2.y - point1.y;
        cb[0] =  point3.x - point1.x;
        cb[1] =  point3.y - point1.y;
      /*  if (Math.max(ca[0],cb[0])/Math.min(ca[0],cb[0]) > 1.5 || Math.max(ca[1],cb[1])/Math.min(ca[1],cb[1])>1.3){
            return;
        }*/
        angle1 = 180/Math.PI*Math.acos((ca[0]*cb[0]+ca[1]*cb[1])/(Math.sqrt(ca[0]*ca[0]+ca[1]*ca[1])*Math.sqrt(cb[0]*cb[0]+cb[1]*cb[1])));
        if(ca[0]*cb[1] - ca[1]*cb[0] > 0) {
            ccw1 = 0;
        } else {
            ccw1 = 1;
        }
        ca[0] =  point1.x - point2.x;
        ca[1] =  point1.y - point2.y;
        cb[0] =  point3.x - point2.x;
        cb[1] =  point3.y - point2.y;
        angle2 = 180/Math.PI * Math.acos((ca[0]*cb[0]+ca[1]*cb[1])/(Math.sqrt(ca[0]*ca[0]+ca[1]*ca[1])*Math.sqrt(cb[0]*cb[0]+cb[1]*cb[1])));
        if(ca[0]*cb[1] - ca[1]*cb[0] > 0) {
            ccw2 = 0;
        }else {
            ccw2 = 1;
        }

        ca[0] =  point2.x - point3.x;
        ca[1] =  point2.y - point3.y;
        cb[0] =  point1.x - point3.x;
        cb[1] =  point1.y - point3.y;
        
        angle3 = 180/Math.PI*Math.acos((ca[0]*cb[0]+ca[1]*cb[1])/(Math.sqrt(ca[0]*ca[0]+ca[1]*ca[1])*Math.sqrt(cb[0]*cb[0]+cb[1]*cb[1])));
        
        if(ca[0]*cb[1] - ca[1]*cb[0] > 0) {
            ccw3 = 0;
        }else {
            ccw3 = 1;
        }
        System.out.println("角度1:"+angle1+",角度2:"+angle2+",角度3:"+angle3);
        System.out.println("ccw1:"+ccw1+",ccw2:"+ccw2+",ccw3:"+ccw3);
	}

	
	public double getMaxAngle() {
		return Math.max(Math.max(angle1, angle2), angle3);
	}
	
	public Point getPoint1() {
		return point1;
	}

	public Point getPoint2() {
		return point2;
	}

	public Point getPoint3() {
		return point3;
	}

	public Double getAngle1() {
		return angle1;
	}

	public Double getAngle2() {
		return angle2;
	}

	public Double getAngle3() {
		return angle3;
	}

	public int getCcw1() {
		return ccw1;
	}

	public int getCcw2() {
		return ccw2;
	}

	public int getCcw3() {
		return ccw3;
	}


	@Override
	public String toString() {
		return "Triangle [point1=" + point1 + ", point2=" + point2 + ", point3=" + point3 + ", angle1=" + angle1
				+ ", angle2=" + angle2 + ", angle3=" + angle3 + "]";
	}

	
	
	
	
}
