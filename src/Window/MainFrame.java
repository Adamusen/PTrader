package Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame {
	public static final JFrame frame = new JFrame("PTrader");
	public static JTextArea LogTextArea = new JTextArea();
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public static void createWindow() {
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				frame.repaint();
			}
		});
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		JTabbedPane Main_tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(Main_tabbedPane, BorderLayout.CENTER);
		
		NTrainer.createNTrainerTab();
		Main_tabbedPane.addTab("NTrainer", null, NTrainer.NTrainer_tabbedPane, null);
		
		JScrollPane Log_scrollPane = new JScrollPane();
		LogTextArea.setEditable(false);
		Log_scrollPane.setViewportView(LogTextArea);
		Main_tabbedPane.addTab("Log", null, Log_scrollPane, null);
		
		frame.setSize(1000, 700);
		initMenuBar();
		frame.setVisible(true);
	}
	
	private static void initMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnMainMenu = new JMenu("Main Menu");
		menuBar.add(mnMainMenu);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		mnMainMenu.add(mntmExit);
	}
}
