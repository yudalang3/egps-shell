package egps2.builtin.modules.largetextedi.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import egps2.builtin.modules.largetextedi.TextEditorMain;

/**
 * Copyright (c) 2019 Chinese Academy of Sciences. All rights reserved.
 * 
 * TEXTAREA 行号显示插件
 * 
 * @ClassName LineNumberHeaderView
 * 
 * @author mhl
 * 
 * @Date Created on:2019-09-06 14:27
 * 
 */
public class LineNumberHeaderView extends javax.swing.JComponent {

	private static final long serialVersionUID = 1L;
	public final Color DEFAULT_BACKGROUD = new Color(228, 228, 228);
	public final Color DEFAULT_FOREGROUD = Color.BLACK;
	public final int nHEIGHT = Integer.MAX_VALUE - 1000000;

	public final int MARGIN = 5;
	private int currentRowWidth;
	private FontMetrics fontMetrics;

	private TextEditorViewPort textEditorViewPort;
	private TextEditorMain textEditorMain;

	private int maxLength;

	public LineNumberHeaderView(TextEditorMain textEditorMain) {
		this.textEditorMain = textEditorMain;
		this.textEditorViewPort = textEditorMain.getTextEditorViewPort();
		setFont(textEditorViewPort.getFont());
		setForeground(DEFAULT_FOREGROUD);
		setBackground(DEFAULT_BACKGROUD);
		setPreferredSize(9999);
	}

	public void setPreferredSize(int row) {
		int width = fontMetrics.stringWidth(String.valueOf(row));
		if (currentRowWidth <= width) {
			currentRowWidth = width;
			setPreferredSize(new Dimension(2 * MARGIN + width + 1, nHEIGHT));
		}

	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		fontMetrics = getFontMetrics(getFont());
	}

	public int getStartOffset() {
		return 2;
	}

	@Override
	protected void paintComponent(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;

		setFont(textEditorViewPort.getFont());

		textEditorMain.getEgpsTextPane().setFont(textEditorViewPort.getFont());

		int startLineNum = textEditorViewPort.getStartPosition();

		int endLineNum = textEditorViewPort.getEndPosition();

		int nlineHeight = textEditorViewPort.getLineHeight();

		int startOffset = getStartOffset();

		g2.setFont(textEditorViewPort.getFont());

		Rectangle drawHere = g2.getClipBounds();

		// g2.setColor(getBackground());

		// g2.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);

		g2.setColor(getForeground());

		int start = (drawHere.y / nlineHeight) * nlineHeight + nlineHeight - startOffset;
		// int start = nlineHeight-2;

		for (int i = startLineNum; i < endLineNum; ++i) {

			String lineNum = String.valueOf(i);

			int width = fontMetrics.stringWidth(lineNum);

			g2.drawString(lineNum + " ", MARGIN + currentRowWidth - width - 1, start);

			start += nlineHeight;
		}

		maxLength = textEditorMain.getEditorDataManager().getMaxLength();

		setPreferredSize(maxLength);

	}

}
