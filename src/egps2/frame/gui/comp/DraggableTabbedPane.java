package egps2.frame.gui.comp;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JFrame;

import com.jidesoft.swing.JideTabbedPane;

import egps2.utils.common.util.EGPSShellIcons;

/**
 *
 * Copyright (c) 2018 Chinese Academy of Sciences. All rights reserved.
 *
 * @ClassName: DraggableTabbedPane.java
 *
 * @Package: egps.module.simulator4Js
 *
 * @author mhl
 *
 * @version V1.0
 *
 * @Date Created on: 2018-08-23 11:38
 *
 */
public class DraggableTabbedPane extends JFrame{

	private static final long serialVersionUID = 1L;
	
	private Component superPane;

	public DraggableTabbedPane(String tabTitle, Component component, JideTabbedPane tabbedPane) {
		super(tabTitle);
		List<java.awt.Image> listOfIcons = new ArrayList<>(2);
		listOfIcons.add(EGPSShellIcons.get("eGPS_logo16x16.png").getImage());
		listOfIcons.add(EGPSShellIcons.get("eGPS_logo32x32.png").getImage());

		// eGPS_logo20x20
		super.setIconImages(listOfIcons);
		superPane = component;
		initComponents(tabbedPane);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
//				BioMainFrame.getInstance().removeDragTabbedPane(tabTitle);
//				tabbedPane.add(tabTitle, superPane);
//				tabbedPane.setSelectedComponent(superPane);
				// BioMainFrame.getInstance().updateMenuItems();
			}
		});

	}

	private void initComponents(JideTabbedPane tabbedPane) {
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		// setPreferredSize(new java.awt.Dimension(640, 480));

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);

		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(superPane,
				GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(superPane,
				GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE));
		pack();
	}


}
