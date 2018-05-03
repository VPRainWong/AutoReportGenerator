package com.vp.plugin.sample.generatereportcomposer;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import com.vp.plugin.*;
import com.vp.plugin.diagram.*;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.*;

public class BpdsReportGenerator {
	
	private static File _pluginDir;
	static void setPluginDir(File aPluginDir) {
		_pluginDir = aPluginDir;
	}
	public static File getPluginDir() {
		return _pluginDir;
	}

	// Collect all business process diagram in project
	public static IBusinessProcessDiagramUIModel[] collectBpds() {
		Collection<IBusinessProcessDiagramUIModel> lCollection = new ArrayList<IBusinessProcessDiagramUIModel>();
		
		IProject lProject = ApplicationManager.instance().getProjectManager().getProject();
		IDiagramUIModel[] lDiagrams = lProject.toDiagramArray();
		if (lDiagrams != null) {
			
			for (IDiagramUIModel lDiagram : lDiagrams) {
				if (lDiagram instanceof IBusinessProcessDiagramUIModel) {
					lCollection.add((IBusinessProcessDiagramUIModel) lDiagram);
				}
			}
		}
		
		return lCollection.toArray(new IBusinessProcessDiagramUIModel[lCollection.size()]);
	}
	
	public static String 
		REPORT_NAME = "BPD Report Template", 
		TEMPLATE_NAME = "Template";
	
	// Retrieve Doc. Composer in project
	public static IReportDiagramUIModel getBpdReport() {
		IProject lProject = ApplicationManager.instance().getProjectManager().getProject();
		IDiagramUIModel[] lDiagrams = lProject.toDiagramArray();
		if (lDiagrams != null) {
			for (IDiagramUIModel lDiagram : lDiagrams) {
				if (lDiagram instanceof IReportDiagramUIModel) {
					
					if (REPORT_NAME.equals(lDiagram.getName())) {
						// found
						return (IReportDiagramUIModel) lDiagram;
					}
				}
			}
		}
		
		return null;
	}
	
	// Retrieve default content block
	public static IRDOOTemplate getBpdReportTemplate() {
		// Retrieve Doc. Composer in project
		IReportDiagramUIModel lReportDiagram = getBpdReport();
		
		if (lReportDiagram == null) {
			return null;
		}
		
		IProject lProject = ApplicationManager.instance().getProjectManager().getProject();
		IReportDiagramDetails lDetails = (IReportDiagramDetails) lProject.getModelElementByAddress(lReportDiagram.getGenericXMLElementAddress());
		return lDetails.getRDOOTemplateByName(TEMPLATE_NAME);
	}
	
	public static void createBpdReportTemplate(String aTemplateXmlURI, IBusinessProcessDiagramUIModel[] aBpds, Component aInvoker) {
		IProject lProject = ApplicationManager.instance().getProjectManager().getProject();
		
		IReportDiagramDetails lReportDiagramDetails;
		{
			// all ReportDiagramDetails should be contained in a ReportDiagramDetailsContainer. (one project have one ReportDiagramDetailsContainer only)
			IReportDiagramDetailsContainer lContainer;
			{
				Iterator lIter = lProject.modelElementIterator(IModelElementFactory.MODEL_TYPE_REPORT_DIAGRAM_DETAILS_CONTAINER);
				if (lIter.hasNext()) {
					lContainer = (IReportDiagramDetailsContainer) lIter.next();
				}
				else {
					lContainer = IModelElementFactory.instance().createReportDiagramDetailsContainer();
				}
			}
			
			lReportDiagramDetails = lContainer.createReportDiagramDetails();
		}
		
		
		// default
		{
			IRDOOTemplate lTemplate = lReportDiagramDetails.createRDOOTemplate();
			lTemplate.setName(TEMPLATE_NAME);
			try {
				lTemplate.setTemplateURI(aTemplateXmlURI);
				
				
			} catch (IllegalArgumentException lE) {
				
				lReportDiagramDetails.delete(); // creation failed.
				
				ApplicationManager.instance().getViewManager().showMessageDialog(
						aInvoker, 
						"Please make sure it is an available TemplateXML for BPD.", 
						"Invalid TemplateXML", 
						JOptionPane.ERROR_MESSAGE
				);
				return;
			}
			lTemplate.setSourceType(1); // 1 for Diagram, 2 for DiagramElement, 3 for ModelElement
			lTemplate.setSourceId(aBpds[0].getId()); // switch this SourceId for different BPDs.
			
			lReportDiagramDetails.addTemplate(lTemplate);
		}
		
		{
			// setting
			ReportManager lReportManager = ApplicationManager.instance().getReportManager();
			
			{
				// numbering
				IRDNumberingSetting lNumberingSetting = lReportDiagramDetails.createRDNumberingSetting();
				lReportManager.initDefaultRDNumberingSetting(lNumberingSetting);
			}
		}
		
		DiagramManager lDiagramManager = ApplicationManager.instance().getDiagramManager();
		IReportDiagramUIModel lReportDiagram = (IReportDiagramUIModel) lDiagramManager.createDiagram(IDiagramTypeConstants.DIAGRAM_TYPE_REPORT_DIAGRAM);
		lReportDiagram.setName(REPORT_NAME);
		lReportDiagram.setGenericXMLElementAddress(lReportDiagramDetails.getAddress());
		
	}
	
}