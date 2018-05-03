package com.vp.plugin.sample.generatereportcomposer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;

import javax.imageio.*;
import javax.swing.*;

import com.vp.plugin.*;
import com.vp.plugin.diagram.*;
import com.vp.plugin.model.*;
import com.vp.plugin.view.*;

public class BpdReportTemplateDialog implements IDialogHandler {
	
	private final JPanel _panel;
	private final JTextField _templateXmlField;
	private final JTextField _outputDirField;
	private final JProgressBar _progressBar;
	private final JButton _closeButton;
	
	private IDialog _dialog;
	
	private static String _outptuDir;
	
	public BpdReportTemplateDialog() {
		
		JLabel lTemplateXmlLabel = new JLabel("Template URI:");
		JTextField lTemplateXmlField = new JTextField();
		JButton lTemplateXmlHelp = new JButton("?");
		{
			lTemplateXmlHelp.setToolTipText("Help");
			lTemplateXmlHelp.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(Color.white), 
					BorderFactory.createEmptyBorder(1, 5, 1, 5)
			));
			lTemplateXmlHelp.setBackground(new Color(0x077EBE));
			lTemplateXmlHelp.setForeground(Color.white);
		}
		
		JLabel lBpdCountLabel = new JLabel("Number of BPDs:");
		JTextField lBpdCountField = new JTextField();
		lBpdCountField.setEditable(false);
		
		JLabel lOutputDirLabel = new JLabel("Output Folder:");
		JTextField lOutputDirField = new JTextField();
		JButton lOutputDirButton = new JButton("...");
		
		JButton lGenerateReportsButton = new JButton("Generate Report(s)");
		JProgressBar lProgressBar = new JProgressBar();
		
		JButton lCloseButton = new JButton("Close");
		
		JPanel lPanel = new JPanel();
		{
			GridBagLayout lLayout = new GridBagLayout();
			lPanel.setLayout(lLayout);
			
			GridBagConstraints lCons = new GridBagConstraints();
			lCons.anchor = GridBagConstraints.WEST;
			lCons.insets.bottom = 5;
			lCons.insets.right = 2;
			lLayout.setConstraints(lTemplateXmlLabel, lCons);
			lLayout.setConstraints(lBpdCountLabel, lCons);
			lLayout.setConstraints(lOutputDirLabel, lCons);
			lCons.insets.right = 0;
			
			lCons.gridwidth = GridBagConstraints.REMAINDER;
			lLayout.setConstraints(lTemplateXmlHelp, lCons);
			lLayout.setConstraints(lOutputDirButton, lCons);
			lCons.gridwidth = 1;
			lCons.weightx = 1;
			lCons.fill = GridBagConstraints.HORIZONTAL;
			lLayout.setConstraints(lTemplateXmlField, lCons);
			lLayout.setConstraints(lOutputDirField, lCons);
			lCons.gridwidth = GridBagConstraints.REMAINDER;
			lLayout.setConstraints(lBpdCountField, lCons);
			lLayout.setConstraints(lProgressBar, lCons);
			lCons.fill = GridBagConstraints.NONE;
			lLayout.setConstraints(lGenerateReportsButton, lCons);
			lCons.anchor = GridBagConstraints.EAST;
			lCons.insets.bottom = 0;
			lCons.weighty = 1;
			lCons.anchor = GridBagConstraints.SOUTHEAST;
			lLayout.setConstraints(lCloseButton, lCons);
			
		}
		lPanel.add(lTemplateXmlLabel);
		lPanel.add(lTemplateXmlField);
		lPanel.add(lTemplateXmlHelp);
		lPanel.add(lBpdCountLabel);
		lPanel.add(lBpdCountField);
		lPanel.add(lOutputDirLabel);
		lPanel.add(lOutputDirField);
		lPanel.add(lOutputDirButton);
		lPanel.add(lGenerateReportsButton);
		lPanel.add(lProgressBar);
		lPanel.add(lCloseButton);
		
		_panel = lPanel;
		_templateXmlField = lTemplateXmlField;
		_outputDirField = lOutputDirField;
		_progressBar = lProgressBar;
		_closeButton = lCloseButton;
		
		
		{	
			// Collect all business process diagram in project
			IBusinessProcessDiagramUIModel[] lBpds = BpdsReportGenerator.collectBpds();
			// Create Doc. Composer and retrieve template model.
			IRDOOTemplate lTemplate = BpdsReportGenerator.getBpdReportTemplate();
			
			if (lTemplate != null) {
				lTemplateXmlField.setText(lTemplate.getTemplateURI());
			}
			// List out the number of business process in project.
			if (lBpds.length == 0) {
				lBpdCountField.setText("BPD not found. Please open the project contains your BPD(s).");
			}
			else {
				lBpdCountField.setText(String.valueOf(lBpds.length));
			}
			if (_outptuDir != null) {
				lOutputDirField.setText(_outptuDir);
			}
						
			_progressBar.setVisible(false);
		}
		
		
		lTemplateXmlHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent aE) {
				File lImageFile = new File(BpdsReportGenerator.getPluginDir(), "images/TemplateXmlURI.png");
				
				JLabel lMessageLabel = new JLabel("Open the dialog to copy the Template URI: Tools > Doc. Composer > Manage Template XMLs...");
				JLabel lImageLabel = new JLabel();
				try {
					lImageLabel.setIcon(new ImageIcon(ImageIO.read(lImageFile)));
					
				} catch (Exception lE) {
					lE.printStackTrace();
					System.out.println(lImageFile.exists());
					System.out.println(lImageFile);
				}
				
				JPanel lMessagePanel = new JPanel(new BorderLayout());
				lMessagePanel.add(lMessageLabel, BorderLayout.NORTH);
				lMessagePanel.add(lImageLabel, BorderLayout.SOUTH);
				
				ApplicationManager.instance().getViewManager().showMessageDialog(
						BpdReportTemplateDialog.this.getComponent(), 
						lMessagePanel, 
						"Help", 
						JOptionPane.INFORMATION_MESSAGE
				);
			}
		});
		// Let user specify output folder for the generated documents.
		lOutputDirButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent aE) {
				JFileChooser lFileChooser = ApplicationManager.instance().getViewManager().createJFileChooser();
				lFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				lFileChooser.setDialogTitle("Output Folder");
				{
					String lDir = _outputDirField.getText();
					if (lDir != null && new File(lDir).exists()) {
						lFileChooser.setCurrentDirectory(new File(lDir));
					}
				}
				int lResult = lFileChooser.showSaveDialog(BpdReportTemplateDialog.this.getComponent());
				if (lResult == JFileChooser.APPROVE_OPTION) {
					File lOutputDir = lFileChooser.getSelectedFile();
					_outputDirField.setText(lOutputDir.getAbsolutePath());
					_outptuDir = lOutputDir.getAbsolutePath();
				}
			}
		});
		// Output Doc. Composer content into physical document file.
		lGenerateReportsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent aE) {
				generateReports();
			}
		});
		lCloseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent aE) {
				_dialog.close();
			}
		});
	}
	
	// Create content block
	private boolean prepareReportTemplate() {

		IBusinessProcessDiagramUIModel[] lBpds = BpdsReportGenerator.collectBpds();
		if (lBpds.length == 0) {
			ApplicationManager.instance().getViewManager().showMessageDialog(
					BpdReportTemplateDialog.this.getComponent(), 
					"Please open the project contains your BPD(s).", 
					"BPD not found", 
					JOptionPane.ERROR_MESSAGE
			);
			return false;
		}
		
		// Create document template model form user specified template XML
		BpdsReportGenerator.createBpdReportTemplate(_templateXmlField.getText(), lBpds, BpdReportTemplateDialog.this.getComponent());
		
		IRDOOTemplate lTemplate = BpdsReportGenerator.getBpdReportTemplate();
		{
			if (lTemplate != null) {
				_templateXmlField.setText(lTemplate.getTemplateURI());
			}
			
		}
		
		return lTemplate != null;
	}
	
	// Generate doc. composer for BPD and output to document
	private void generateReports() {
		
		{	
			// Retrieve default content block
			IRDOOTemplate lTemplate = BpdsReportGenerator.getBpdReportTemplate();
			if (lTemplate == null) {
				// Create content block
				boolean lIsTemplatePrepared = prepareReportTemplate();
				if (! lIsTemplatePrepared) {
					return;
				}
				
				lTemplate = BpdsReportGenerator.getBpdReportTemplate();
			}
			
			try {
				//Use user specified template in content block
				lTemplate.setTemplateURI(_templateXmlField.getText());
				
			} catch (IllegalArgumentException lE) {
				ApplicationManager.instance().getViewManager().showMessageDialog(
						BpdReportTemplateDialog.this.getComponent(), 
						"Please make sure it is an available TemplateXML for BPD.", 
						"Invalid TemplateXML", 
						JOptionPane.ERROR_MESSAGE
				);
				_templateXmlField.requestFocus();
				return;
			}
		}
		
		// Retrieve Doc. Composer
		final IReportDiagramUIModel lReportDiagram = BpdsReportGenerator.getBpdReport();
		
		final String lOutputDir = _outputDirField.getText();
		if (lOutputDir == null || lOutputDir.length() == 0) {
			ApplicationManager.instance().getViewManager().showMessageDialog(
					BpdReportTemplateDialog.this.getComponent(), 
					"Please specify output dir.", 
					"Invalid Output Directory", 
					JOptionPane.ERROR_MESSAGE
			);
			_outputDirField.requestFocus();
			return;
		}
		_outptuDir = lOutputDir;
		
		File outputDir = new File(_outptuDir);
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}

		final String[] lBpdIds;
		final String[] lBpdNames;
		{
			IBusinessProcessDiagramUIModel[] lBpds = BpdsReportGenerator.collectBpds();
			lBpdIds = new String[lBpds.length];
			lBpdNames = new String[lBpds.length];
			int lIndex = -1;
			for (IBusinessProcessDiagramUIModel lBpd : lBpds) {
				lIndex++;
				
				lBpdIds[lIndex] = lBpd.getId();
				lBpdNames[lIndex] = lBpd.getName();
			}
		}
		_progressBar.setValue(0);
		_progressBar.setMaximum(lBpdIds.length);
		_progressBar.setStringPainted(true);
		_progressBar.setString("Generating... (0 of "+lBpdIds.length+")");
		_progressBar.setVisible(true);
		
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				_closeButton.setEnabled(false);
				try {
					DocumentationManager lDocumentationManager = ApplicationManager.instance().getDocumentationManager();
					
					int lIndex = -1;
					for (String lBpdId : lBpdIds) {
						lIndex++;
						
						_progressBar.setString("Generating... ("+lIndex+" of "+lBpdIds.length+")");
						{
							// Retrieve the template model user specified, and specify the diagram ID into template
							IRDOOTemplate lTemplate = BpdsReportGenerator.getBpdReportTemplate();
							lTemplate.setSourceId(lBpdId);
						}
						
						
						// Generate document to Word file.
						File lOutputFile = new File(lOutputDir, lBpdNames[lIndex]+".docx");
						lDocumentationManager.generateDocComposerWord(lReportDiagram, lOutputFile, BpdReportTemplateDialog.this._panel);
						ApplicationManager.instance().getViewManager().showMessage(
								"["+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+"] " + 
										"Generated: " + lOutputFile.getAbsolutePath(), 
								"com.vp.plugin.sample.generatereportcomposer"
						);
						
						_progressBar.setValue(lIndex+1);
						_progressBar.setString("Generating... ("+(lIndex+1)+" of "+lBpdIds.length+")");
						
					}
					_progressBar.setString("Done");
					
				}
				finally {
					_closeButton.setEnabled(true);
				}
			}
		}).start();
		
	}
	
	
	@Override
	public void prepare(IDialog aDialog) {
		aDialog.setTitle("Generate BPD Report(s)");
		aDialog.setSize(600, 300);
		aDialog.setModal(true);
		
		_dialog = aDialog;
	}
	
	@Override
	public Component getComponent() {
		return _panel;
	}
	
	@Override
	public void shown() {
	}
	@Override
	public boolean canClosed() {
		return true;
	}
	
}