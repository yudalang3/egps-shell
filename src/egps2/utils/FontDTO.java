package egps2.utils;

import java.awt.Font;

/**
 * FontDTO provides shared utility logic for eGPS modules and UI.
 */
public class FontDTO {
	private String name;
	private int style;
	private int size;

	// 构造函数
	public FontDTO() {
	}
	public FontDTO(Font font) {
		this.name = font.getName();
		this.style = font.getStyle();
		this.size = font.getSize();
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
	}
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

}
