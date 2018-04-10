package Window;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class NTrainer {
	public static JTabbedPane NTrainer_tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
	
	private static JScrollPane createTD_scrollPane = new JScrollPane();
	public static JTextField createTD_sourceFile_textField = new JTextField();
	public static JTextField createTD_wt_textField = new JTextField("30");
	public static JTextField createTD_wp_textField = new JTextField("1");
	public static JTextField createTD_pdiff_textField = new JTextField("1.0025");
	public static JTextField createTD_noftws_textField = new JTextField("12");
	public static JTextField createTD_nofitems_textField = new JTextField("17520");
	public static JTextField createTD_destFile_textField = new JTextField("data/TrainData/TrainData.jdat");
	private static JTextField createTD_result_textField = new JTextField();
	
	private static JScrollPane trainSNet_scrollPane = new JScrollPane();
	private static Network.TrainMLSTM2 trainSNet_trainer;
	public static JTextField trainSNet_sourceFile_textField = new JTextField("data/TrainData/TrainData.jdat");
	public static JTextField trainSNet_destFile_textField = new JTextField("data/Networks/SingleNet.jdat");
	public static XYSeriesCollection trainSNet_ChartDataset = new XYSeriesCollection( );
	public static JFreeChart trainSNet_Chart = ChartFactory.createXYLineChart("Trade history", "Time", "Price", trainSNet_ChartDataset);
	public static JTextArea trainSNet_LogTextArea = new JTextArea();
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public static void createNTrainerTab() {		
		init_createTD_JPanel();
		NTrainer_tabbedPane.addTab("CreateTD", null, createTD_scrollPane , null);
		
		init_trainSNet_JPanel();
		NTrainer_tabbedPane.addTab("TrainSNet", null, trainSNet_scrollPane, null);
	}
	
	public static void init_createTD_JPanel() {
		JPanel createTD_JPanel = new JPanel();
		createTD_JPanel.setLayout(new BoxLayout(createTD_JPanel, BoxLayout.Y_AXIS));
		createTD_scrollPane.setViewportView(createTD_JPanel);
		
		Box sourceFile_Box = Box.createHorizontalBox();
		JLabel lblSourceFile = new JLabel("Source File: ");
		sourceFile_Box.add(lblSourceFile);		
		sourceFile_Box.add(createTD_sourceFile_textField);
		
		JButton sourceFile_browseButton = new JButton("Browse");
		sourceFile_browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new java.io.File("data/HistData") );
				int result = fc.showOpenDialog(createTD_JPanel);
				if (result==0) createTD_sourceFile_textField.setText(fc.getSelectedFile().getAbsolutePath() );
			}
		});	
		
		sourceFile_Box.add(sourceFile_browseButton);
		sourceFile_Box.setMaximumSize(new Dimension(500, 22));
		createTD_JPanel.add(sourceFile_Box);
		
		JPanel settings_JPanel = new JPanel();
		settings_JPanel.setMaximumSize(new Dimension(250, 110) );
		createTD_JPanel.add(settings_JPanel);
		settings_JPanel.setLayout(new GridLayout(3, 2, 0, 0));
		
		Box verticalBox_0 = Box.createVerticalBox();
		JLabel lbl_0 = new JLabel("window time [m]");
		verticalBox_0.add(lbl_0);
		createTD_wt_textField.setMaximumSize(new Dimension(100, 20) );
		verticalBox_0.add(createTD_wt_textField);
		settings_JPanel.add(verticalBox_0);
		
		Box verticalBox_1 = Box.createVerticalBox();
		JLabel lbl_1 = new JLabel("window phase [m]");
		verticalBox_1.add(lbl_1);
		createTD_wp_textField.setMaximumSize(new Dimension(100, 20) );
		verticalBox_1.add(createTD_wp_textField);
		settings_JPanel.add(verticalBox_1);
		
		Box verticalBox_2 = Box.createVerticalBox();
		JLabel lbl_2 = new JLabel("price diff [-]");
		verticalBox_2.add(lbl_2);
		createTD_pdiff_textField.setMaximumSize(new Dimension(100, 20) );
		verticalBox_2.add(createTD_pdiff_textField);
		settings_JPanel.add(verticalBox_2);
		
		Box verticalBox_3 = Box.createVerticalBox();
		JLabel lbl_3 = new JLabel("f. t. windows [-]");
		verticalBox_3.add(lbl_3);
		createTD_noftws_textField.setMaximumSize(new Dimension(100, 20) );
		verticalBox_3.add(createTD_noftws_textField);
		settings_JPanel.add(verticalBox_3);
		
		Box verticalBox_4 = Box.createVerticalBox();
		JLabel lbl_4 = new JLabel("Max items");
		verticalBox_4.add(lbl_4);
		createTD_nofitems_textField.setMaximumSize(new Dimension(100, 20) );
		verticalBox_4.add(createTD_nofitems_textField);
		settings_JPanel.add(verticalBox_4);
		
		Box destFile_Box = Box.createHorizontalBox();
		JLabel lblDestFile = new JLabel("Dest. File: ");
		destFile_Box.add(lblDestFile);		
		destFile_Box.add(createTD_destFile_textField);
		
		JButton destFile_browseButton = new JButton("Browse");
		destFile_browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new java.io.File("data/TrainData") );
				int result = fc.showOpenDialog(createTD_JPanel);
				if (result==0) createTD_destFile_textField.setText(fc.getSelectedFile().getAbsolutePath() );
			}
		});	
		
		destFile_Box.add(destFile_browseButton);
		destFile_Box.setMaximumSize(new Dimension(500, 22));
		createTD_JPanel.add(destFile_Box);
		
		JButton MakeIt_JButton = new JButton("Make It!");
		MakeIt_JButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String result = "";
				try {
					java.io.File srcFile = new java.io.File(createTD_sourceFile_textField.getText() );
					java.io.File destFile = new java.io.File(createTD_destFile_textField.getText() );
					int wt = Integer.parseInt(createTD_wt_textField.getText() ) * 60;
					int wp = Integer.parseInt(createTD_wp_textField.getText() ) * 60;
					double pdiff = Double.parseDouble(createTD_pdiff_textField.getText() );
					int noftws = Integer.parseInt(createTD_noftws_textField.getText() );
					int nofitems = Integer.parseInt(createTD_nofitems_textField.getText() );
					
					result = Network.TDcreator.createNetInp_histdata(srcFile, destFile, wt, wp, pdiff, noftws, nofitems);
				} catch (Exception exc) {
					result = "Invalid Parameters!";
					exc.printStackTrace();
				}
				
				createTD_result_textField.setText(result);
			}
		});
		MakeIt_JButton.setAlignmentX(Component.CENTER_ALIGNMENT);	
		createTD_JPanel.add(MakeIt_JButton);
		
		Box resultBox = Box.createHorizontalBox();
		resultBox.setMaximumSize(new Dimension(500, 22));
		createTD_JPanel.add(resultBox);
		JLabel lblresult = new JLabel("Result: ");
		resultBox.add(lblresult);
		createTD_result_textField.setEditable(false);
		resultBox.add(createTD_result_textField);
	}
	
	public static void init_trainSNet_JPanel() {
		JPanel trainSNet_JPanel = new JPanel();
		trainSNet_scrollPane.setViewportView(trainSNet_JPanel);
		trainSNet_JPanel.setLayout(new BoxLayout(trainSNet_JPanel, BoxLayout.Y_AXIS));
		
		Box sourceFile_Box = Box.createHorizontalBox();
		JLabel lblSourceFile = new JLabel("Source File: ");
		sourceFile_Box.add(lblSourceFile);
		sourceFile_Box.add(trainSNet_sourceFile_textField);
		
		JButton sourceFile_browseButton = new JButton("Browse");
		sourceFile_browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new java.io.File("data/HistData") );
				int result = fc.showOpenDialog(trainSNet_JPanel);
				if (result==0) trainSNet_sourceFile_textField.setText(fc.getSelectedFile().getAbsolutePath() );
			}
		});
		
		sourceFile_Box.add(sourceFile_browseButton);
		sourceFile_Box.setMaximumSize(new Dimension(500, 22));
		trainSNet_JPanel.add(sourceFile_Box);
		
		Box destFile_Box = Box.createHorizontalBox();
		JLabel lblDestFile = new JLabel("Ws Dest File: ");
		destFile_Box.add(lblDestFile);
		destFile_Box.add(trainSNet_destFile_textField);
		
		JButton destFile_browseButton = new JButton("Browse");
		destFile_browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new java.io.File("data/Networks") );
				int result = fc.showOpenDialog(trainSNet_JPanel);
				if (result==0) trainSNet_destFile_textField.setText(fc.getSelectedFile().getAbsolutePath() );
			}
		});
		
		destFile_Box.add(destFile_browseButton);
		destFile_Box.setMaximumSize(new Dimension(500, 22));
		trainSNet_JPanel.add(destFile_Box);
		
		Box button_Box = Box.createHorizontalBox();
		
		trainSNet_JPanel.add(button_Box);
		
		JButton start_JButton = new JButton("Start!");
		JButton stop_JButton = new JButton("Stop!");
		JButton saveNet_JButton = new JButton("SaveNet");
		
		start_JButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					start_JButton.setEnabled(false);
					stop_JButton.setEnabled(true);
					saveNet_JButton.setEnabled(true);
					
					java.io.File trainDataFile = new java.io.File(trainSNet_sourceFile_textField.getText() );
					java.io.File saveDestFile = new java.io.File(trainSNet_destFile_textField.getText() );
					
					if (trainSNet_trainer == null)
						trainSNet_trainer = new Network.TrainMLSTM2(trainDataFile, saveDestFile);
					else {
						trainSNet_trainer.trainDataFile = trainDataFile;
						trainSNet_trainer.saveDestFile = saveDestFile;
					}
					
					trainSNet_trainer.going = true;
					trainSNet_trainer.start();
				} catch (Exception exc) {
					trainSNet_LogTextArea.append("Invalid Files!\n");
					exc.printStackTrace();
				}
			}
		});
		button_Box.add(start_JButton);		
		
		stop_JButton.setEnabled(false);
		stop_JButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				start_JButton.setEnabled(true);
				stop_JButton.setEnabled(false);
				
				trainSNet_trainer.going = false;
			}
		});
		button_Box.add(stop_JButton);	
		
		saveNet_JButton.setEnabled(false);
		saveNet_JButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				java.io.File saveDestFile = new java.io.File(trainSNet_destFile_textField.getText() );
				trainSNet_trainer.saveDestFile = saveDestFile;
				trainSNet_trainer.saveWStoFile();
			}
		});
		button_Box.add(saveNet_JButton);
		
		NumberAxis yAxis = (NumberAxis) trainSNet_Chart.getXYPlot().getRangeAxis();
	    yAxis.setAutoRangeIncludesZero(false);
	    yAxis.setNumberFormatOverride(new DecimalFormat("#0.00000000"));
		ChartPanel NeuralTrainer_ChartPanel = new ChartPanel(trainSNet_Chart);
		trainSNet_JPanel.add(NeuralTrainer_ChartPanel);
		
		JScrollPane reults_scrollPane = new JScrollPane();
		trainSNet_JPanel.add(reults_scrollPane);
		trainSNet_LogTextArea.setRows(5);
		trainSNet_LogTextArea.setEditable(false);
		reults_scrollPane.setViewportView(trainSNet_LogTextArea);
	}
	
	public static void updateTrainSNetChartDataset(double[][][] data) {
		trainSNet_ChartDataset.removeAllSeries();
		int scount=0;
		for (double[][] sdata : data) {
			final XYSeries series = new XYSeries("Series " + scount);
			
			for (double[] sde : sdata) {
				series.add(sde[0], sde[1]);
			}
			
			trainSNet_ChartDataset.addSeries(series);
			scount++;
		}
	}
}
