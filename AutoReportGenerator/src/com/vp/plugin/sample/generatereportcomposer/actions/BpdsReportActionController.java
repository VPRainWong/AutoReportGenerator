package com.vp.plugin.sample.generatereportcomposer.actions;

import com.vp.plugin.*;
import com.vp.plugin.action.*;
import com.vp.plugin.sample.generatereportcomposer.*;

public class BpdsReportActionController implements VPActionController {
	
	@Override
	public void performAction(VPAction aAction) {
		
		ApplicationManager.instance().getViewManager().showDialog(new BpdReportTemplateDialog());
		
	}

	@Override
	public void update(VPAction aAction) {
	}

}