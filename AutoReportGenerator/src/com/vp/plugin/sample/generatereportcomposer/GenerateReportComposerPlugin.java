package com.vp.plugin.sample.generatereportcomposer;

import com.vp.plugin.*;

public class GenerateReportComposerPlugin implements VPPlugin {

	@Override
	public void loaded(VPPluginInfo aPluginInfo) {
		BpdsReportGenerator.setPluginDir(aPluginInfo.getPluginDir());
	}

	@Override
	public void unloaded() {
	}

}