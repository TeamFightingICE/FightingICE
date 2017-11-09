package util;

import struct.FrameData;

public class ResourceDrawer {

	private static ResourceDrawer resourceDrawer = new ResourceDrawer();

	private ResourceDrawer(){
		System.out.println("Create instance: " + ResourceDrawer.class.getName());
	}

	public ResourceDrawer getInstance(){
		return resourceDrawer;
	}

	public void drawResource(FrameData frameData){

	}

}
