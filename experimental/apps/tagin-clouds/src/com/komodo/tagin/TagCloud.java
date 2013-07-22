package com.komodo.tagin;

/**
 * Komodo Lab: Tagin! Project: 3D Tag Cloud
 * Google Summer of Code 2011
 * @authors Reza Shiftehfar, Sara Khosravinasr and Jorge Silva
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class TagCloud implements Iterable<Tag> {

	private List<Tag> mTags;
	private int mRadius;
	private static final int DEFAULT_RADIUS = 3;
	private static final int TEXT_SIZE_MAX = 30 , TEXT_SIZE_MIN = 4;
	private static final float[] DEFAULT_COLOR1 = {0.886f, 0.725f, 0.188f, 1f};
	private static final float[] DEFAULT_COLOR2 = {0.3f, 0.3f,  0.3f, 1f};
	private float[] tagColor1;
	private float[] tagColor2;
	private int textSizeMax, textSizeMin;
	private float sin_mAngleX, cos_mAngleX, sin_mAngleY, cos_mAngleY, sin_mAngleZ, cos_mAngleZ;
	private float mAngleZ = 0;
    private float mAngleX = 0;
    private float mAngleY = 0;
    private int mSize = 0;
    private int smallest, largest; // used to find spectrum for tag colors
    private boolean distributeEvenly = true;

	public TagCloud() {
		this(new ArrayList<Tag>());
	}
	
	public TagCloud(List<Tag> tags) {
		this(tags, DEFAULT_RADIUS); 
	}
	
	//Constructor just copies the existing tags in its List
	public TagCloud(List<Tag> tags, int radius) {
		this(tags, radius, DEFAULT_COLOR1, DEFAULT_COLOR2, TEXT_SIZE_MIN, TEXT_SIZE_MAX);
	}
	
	public TagCloud(List<Tag> tags, int radius,int textSizeMin, int textSizeMax) {
		this(tags, radius, DEFAULT_COLOR1, DEFAULT_COLOR2, textSizeMin, textSizeMax);
	}

	public TagCloud(List<Tag> tags, int radius, float[] tagColor1, float[] tagColor2, 
						int textSizeMin, int textSizeMax) {
		this.mTags = tags;    // Java does the initialization and deep copying
		this.mRadius = radius;
		this.tagColor1 = tagColor1;
		this.tagColor2 = tagColor2;
		this.textSizeMax = textSizeMax;
		this.textSizeMin = textSizeMin;	
	}       
	// create method calculates the correct initial location of each tag
	public void create(boolean distrEven) {
		this.distributeEvenly = distrEven;
		// calculate and set the location of each Tag
		positionAll(distrEven);
		sineCosine( mAngleX, mAngleY, mAngleZ);
		updateAll();
		// Now, let's calculate and set the color for each tag:
		// first loop through all tags to find the smallest and largest populariteies
		// largest popularity gets tcolor2, smallest gets tcolor1, the rest in between
		smallest = 9999;
		largest = 0;
		for (int i = 0; i< mTags.size(); i++) {
			int j = mTags.get(i).getPopularity();
			largest = Math.max(largest, j);
			smallest = Math.min(smallest, j);
		}
		//figuring out and assigning the colors/ textsize
		Tag tempTag;
		for (int i = 0; i< mTags.size(); i++) {
			tempTag = mTags.get(i);
			int j = tempTag.getPopularity();
			float percentage = (smallest == largest) ? 1.0f : ((float)j-smallest) / ((float)largest-smallest);
			float[] tempColor = getColorFromGradient(percentage); //(rgb Alpha)
			int tempTextSize = getTextSizeGradient(percentage);
			tempTag.setColorR(tempColor[0]);
			tempTag.setColorG(tempColor[1]);
			tempTag.setColorB(tempColor[2]);
			tempTag.setTextSize(tempTextSize);
		}
		
		this.mSize = mTags.size();
	}
	
	public void reset() {
		create(distributeEvenly);
	}
	
	// updates the transparency/scale of all elements
	public void update() {
		// if mAngleX and mAngleY under threshold, skip motion calculations for performance
		if (Math.abs(mAngleX) > .1 || Math.abs(mAngleY) > .1 ) {
			sineCosine( mAngleX, mAngleY, mAngleZ);
			updateAll();
		}
	}
	
	// if a single tag needed to be added
	public void add(Tag newTag) {	
		int j = newTag.getPopularity();
		float percentage = (smallest == largest) ? 1.0f : ((float) j - smallest) / ((float) largest - smallest);
		float[] tempColor = getColorFromGradient(percentage); //(rgb Alpha)
		int tempTextSize = getTextSizeGradient(percentage);
		newTag.setColorR(tempColor[0]);
		newTag.setColorG(tempColor[1]);
		newTag.setColorB(tempColor[2]);
		newTag.setTextSize(tempTextSize);
		position(distributeEvenly, newTag);
		// now add the new tag to the tagCloud
		mTags.add(newTag);
		this.mSize = mTags.size();				
		updateAll();		
	}
	
	// to replace an existing tag with a new one
	// it returns the location of the replacement, if not found=> returns -1
	public int replace(Tag newTag, String oldTagText) {
		int result = -1;
		// let's go over all elements of tagCloud list and see if the oldTagText exists:
		for (int i = 0; i < mTags.size(); i++) {
			if (oldTagText.equalsIgnoreCase(mTags.get(i).getText())) {
				result = i;
				mTags.get(i).setPopularity(newTag.getPopularity());
				mTags.get(i).setText(newTag.getText());
				mTags.get(i).setUrl(newTag.getUrl());
				int j = newTag.getPopularity();
				float percentage =  (smallest == largest) ? 1.0f : ((float) j - smallest) / ((float) largest - smallest);
				float[] tempColor = getColorFromGradient(percentage); //(rgb Alpha)
				int tempTextSize = getTextSizeGradient(percentage);
				mTags.get(i).setColorR(tempColor[0]);
				mTags.get(i).setColorG(tempColor[1]);
				mTags.get(i).setColorB(tempColor[2]);
				mTags.get(i).setTextSize(tempTextSize);
				newTag = mTags.get(i);
				break;
			}
		}				
		return result;
	}

	// for a given tag, sets teh value of RGB and text size based on other existing tags
	public void setTagRGBT(Tag tagToBeUpdated, int popularity) {
		float percentage = (smallest == largest) ? 
								1.0f :
								((float)popularity-smallest) / ((float)largest-smallest);
		float[] tempColor = getColorFromGradient(percentage); //(rgb Alpha)
		int tempTextSize = getTextSizeGradient(percentage);
		tagToBeUpdated.setColorR(tempColor[0]);
		tagToBeUpdated.setColorG(tempColor[1]);
		tagToBeUpdated.setColorB(tempColor[2]);
		tagToBeUpdated.setTextSize(tempTextSize);		
	}
	
	@Override
	public Iterator<Tag> iterator() {
		return mTags.iterator();
	}	

	private void position(boolean distrEven, Tag newTag) {
		double phi = 0;
		double theta = 0;
		// when adding a new tag, just place it at some random location
		// this is in fact why adding too many elements make TagCloud ugly
		// after many add, do one reset to rearrange all tags
		phi = Math.random() * Math.PI;
		theta = Math.random() * (2 * Math.PI);
		// coordinate conversion:
		newTag.setLocX((int) (mRadius * Math.cos(theta) * Math.sin(phi)));
		newTag.setLocY((int) (mRadius * Math.sin(theta) * Math.sin(phi)));
		newTag.setLocZ((int) (mRadius * Math.cos(phi)));
	}
	
	private void positionAll(boolean distrEven) {
		double phi = 0;
		double theta = 0;
		int max = mTags.size();
		//distribute: (disrtEven is used to specify whether distribute random or even 
		for (int i = 1; i < max+1; i++) {
			if (distrEven) {
				phi = Math.acos(-1.0 + (2.0*i -1.0)/max);
				theta = Math.sqrt(max * Math.PI) * phi;
			} else {
				phi = Math.random() * Math.PI;
				theta = Math.random() * (2 * Math.PI);
			}
			
			//coordinate conversion:			
			mTags.get(i-1).setLocX((int) ((mRadius * Math.cos(theta) * Math.sin(phi))));
			mTags.get(i-1).setLocY((int) (mRadius * Math.sin(theta) * Math.sin(phi)));
			mTags.get(i-1).setLocZ((int) (mRadius * Math.cos(phi)));
		}		
	}	
	
	private void updateAll() {
		// update transparency/scale for all tags:
		int max = mTags.size();
		for (int j = 0; j < max; j++) {
			// There exists two options for this part:
			// multiply positions by a x-rotation matrix
			float rx1 = (mTags.get(j).getLocX());
			float ry1 = (mTags.get(j).getLocY()) * cos_mAngleX +
						 mTags.get(j).getLocZ() * -sin_mAngleX;
			float rz1 = (mTags.get(j).getLocY()) * sin_mAngleX +
						 mTags.get(j).getLocZ() * cos_mAngleX;						
			// multiply new positions by a y-rotation matrix
			float rx2 = rx1 * cos_mAngleY + rz1 * sin_mAngleY;
			float ry2 = ry1;
			float rz2 = rx1 * -sin_mAngleY + rz1 * cos_mAngleY;
			// multiply new positions by a z-rotation matrix
			float rx3 = rx2 * cos_mAngleZ + ry2 * -sin_mAngleZ;
			float ry3 = rx2 * sin_mAngleZ + ry2 * cos_mAngleZ;
			float rz3 = rz2;
			// set arrays to new positions
			mTags.get(j).setLocX(rx3);
			mTags.get(j).setLocY(ry3);
			mTags.get(j).setLocZ(rz3);

			// add perspective
			int diameter = 2 * mRadius;
			float per = diameter / (diameter + rz3);
			// let's set position, scale, alpha for the tag;
			mTags.get(j).setLoc2DX((int) (rx3 * per));
			mTags.get(j).setLoc2DY((int) (ry3 * per));
			mTags.get(j).setScale(per);
			mTags.get(j).setAlpha(per / 2);
		}	
		depthSort();
	}	
	
	// now let's sort all tags in the tagCloud based on their z coordinate
	// this way, when they are finally drawn, upper tags will be drawn on top of lower tags
	private void depthSort() {
		Collections.sort(mTags);		
	}
	
	private float[] getColorFromGradient(float perc) {
		float[] tempRGB = new float[4];
		tempRGB[0] = (perc * tagColor1[0]) + ((1-perc) * tagColor2[0]);
		tempRGB[1] = (perc * tagColor1[1]) + ((1-perc) * tagColor2[1]);
		tempRGB[2] = (perc * tagColor1[2]) + ((1-perc) * tagColor2[2]);
		tempRGB[3] = 1;
 		return tempRGB;
	}
	
	private int getTextSizeGradient(float perc) {
		return (int) (perc * textSizeMax + (1-perc) * textSizeMin);
	}
	
	private void sineCosine(float angleX,float angleY,float angleZ) {
		double degToRad = Math.PI / 180;
		sin_mAngleX = (float) Math.sin(angleX * degToRad);
		cos_mAngleX = (float) Math.cos(angleX * degToRad);
		sin_mAngleY = (float) Math.sin(angleY * degToRad);
		cos_mAngleY = (float) Math.cos(angleY * degToRad);
		sin_mAngleZ = (float) Math.sin(angleZ * degToRad);
		cos_mAngleZ = (float) Math.cos(angleZ * degToRad);
	}
	
	public int getRadius() {
		return mRadius;
	}
	
	public void setRadius(int radius) {
		this.mRadius = radius;
	}
	
	public float[] getTagColor1() {
		return tagColor1;
	}
	
	public void setTagColor1(float[] tagColor) {
		this.tagColor1 = tagColor;
	}
	
	public float[] getTagColor2() {
		return tagColor2;
	}
	
	public void setTagColor2(float[] tagColor2) {
		this.tagColor2 = tagColor2;
	}
	
	public float getAngleX() {
		return mAngleX;
	}
	
	public void setAngleX(float angleX) {
		this.mAngleX = angleX;
	}
	
	public float getAngleY() {
		return mAngleY;
	}
	
	public void setAngleY(float angleY) {
		this.mAngleY = angleY;
	}
	
	public int getSize() {
		return mSize;
	}
}
