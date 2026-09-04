package egps2.frame.features;

import utils.string.EGPSStringUtil;
import egps2.EGPSProperties;

/**
 * EGPSVersion supports the main eGPS window, actions, or tab management.
 */
public class EGPSVersion {

	byte a;
	byte b;
	byte c;
	byte d;

	public EGPSVersion(String str) {
		String header = EGPSProperties.EGPS_VERSION_HEADER;
		String substring = str.substring(header.length());
		
		String[] split = EGPSStringUtil.split(substring, '.', 4);
		a = Byte.parseByte(split[0]);
		b = Byte.parseByte(split[1]);
		c = Byte.parseByte(split[2]);
		d = Byte.parseByte(split[3]);
	}

	public byte getA() {
		return a;
	}

	public void setA(byte a) {
		this.a = a;
	}

	public byte getB() {
		return b;
	}

	public void setB(byte b) {
		this.b = b;
	}

	public byte getC() {
		return c;
	}

	public void setC(byte c) {
		this.c = c;
	}

	public byte getD() {
		return d;
	}

	public void setD(byte d) {
		this.d = d;
	}

	/**
	 * 是否这个Bean 大于等于anther版本
	 * 
	 * @param another
	 * @return
	 */
	public boolean isNotLessThan(EGPSVersion another) {
		if (another == null) {
			throw new IllegalArgumentException("The other instance cannot be null");
		}

		return this.a >= another.a && this.b >= another.b && this.c >= another.c && this.d >= another.d;
	}
	
	@Override
	public String toString() {
		StringBuilder sBuilder = new StringBuilder();
		
		sBuilder.append("Version is:");
		sBuilder.append("\t").append(a);
		sBuilder.append("\t").append(b);
		sBuilder.append("\t").append(c);
		sBuilder.append("\t").append(d);
		
		return sBuilder.toString();
	}

}
