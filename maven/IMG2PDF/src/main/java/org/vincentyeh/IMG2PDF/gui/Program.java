package org.vincentyeh.IMG2PDF.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JScrollPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;

public class Program {

	private JFrame frame;
	private DefaultListModel<String> listmodel;

	/**
	 * Launch the application.
	 * 
	 * @throws UnsupportedLookAndFeelException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Program window = new Program();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Program() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 483, 344);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		listmodel = new DefaultListModel();
										frame.getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
								
										JButton btnImport = new JButton("import");
										frame.getContentPane().add(btnImport);
												
														JList list = new JList(listmodel);
														frame.getContentPane().add(list);
														list.setSelectedIndex(0);
														list.setVisibleRowCount(3);
						btnImport.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								JFileChooser fileChooser = new JFileChooser();// 宣告filechooser
								fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
								fileChooser.setMultiSelectionEnabled(true);
								int returnValue = fileChooser.showOpenDialog(null);// 叫出filechooser

								if (returnValue == JFileChooser.APPROVE_OPTION) // 判斷是否選擇檔案
								{

									File[] selectedFiles = fileChooser.getSelectedFiles();// 指派給File
									for (File selectedFile : selectedFiles) {
										System.out.println(selectedFile.getAbsolutePath()); // 印出檔名
										listmodel.addElement(selectedFile.getAbsolutePath());
									}
								}
							}
						});

	}

}
