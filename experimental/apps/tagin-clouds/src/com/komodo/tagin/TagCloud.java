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

import android.graphics.Color;

public class TagCloud implements Iterable<Tag> {

	private static final int DEFAULT_RADIUS = 3;
	private static final int TEXT_SIZE_MAX = 30 , TEXT_SIZE_MIN = 4;
	private static final int DEFAULT_COLOR1 = Color.argb(1, 226, 185, 48);
	private static final int DEFAULT_COLOR2 = Color.argb(1, 76, 76,  76);
	
	private List<Tag> mTags;
	private int mRadius;
	private int tagColor1;
	private int tagColor2;
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

	public TagCloud(List<Tag> tags, int radius, int tagColor1, int tagColor2, 
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
			int tempTextSize = getTextSizeGradient(percentage);
			tempTag.setColor(getColorFromGradient(percentage));
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
		int tempTextSize = getTextSizeGradient(percentage);
		newTag.setColor(getColorFromGradient(percentage));
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
				int tempTextSize = getTextSizeGradient(percentage);
				mTags.get(i).setColor(getColorFromGradient(percentage));
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
		int tempTextSize = getTextSizeGradient(percentage);
		tagToBeUpdated.setColor(getColorFromGradient(percentage));
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
		newTag.setX((int) (mRadius * Math.cos(theta) * Math.sin(phi)));
		newTag.setY((int) (mRadius * Math.sin(theta) * Math.sin(phi)));
		newTag.setZ((int) (mRadius * Math.cos(phi)));
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
			mTags.get(i-1).setX((int) ((mRadius * Math.cos(theta) * Math.sin(phi))));
			mTags.get(i-1).setY((int) (mRadius * Math.sin(theta) * Math.sin(phi)));
			mTags.get(i-1).setZ((int) (mRadius * Math.cos(phi)));
		}		
	}	
	
	private void updateAll() {
		// update transparency/scale for all tags:
		int max = mTags.size();
		for (int j = 0; j < max; j++) {
			// There exists two options for this part:
			// multiply positions by a x-rotation matrix
			float rx1 = (mTags.get(j).getX());
			float ry1 = (mTags.get(j).getY()) * cos_mAngleX +
						 mTags.get(j).getZ() * -sin_mAngleX;
			float rz1 = (mTags.get(j).getY()) * sin_mAngleX +
						 mTags.get(j).getZ() * cos_mAngleX;						
			// multiply new positions by a y-rotation matrix
			float rx2 = rx1 * cos_mAngleY + rz1 * sin_mAngleY;
			float ry2 = ry1;
			float rz2 = rx1 * -sin_mAngleY + rz1 * cos_mAngleY;
			// multiply new positions by a z-rotation matrix
			float rx3 = rx2 * cos_mAngleZ + ry2 * -sin_mAngleZ;
			float ry3 = rx2 * sin_mAngleZ + ry2 * cos_mAngleZ;
			float rz3 = rz2;
			// set arrays to new positions
			mTags.get(j).setX(rx3);
			mTags.get(j).setY(ry3);
			mTags.get(j).setZ(rz3);

			// add perspective
			int diameter = 2 * mRadius;
			float per = diameter / (diameter + rz3);
			// let's set position, scale, alpha for the tag;
			mTags.get(j).setX2D((int) (rx3 * per));
			mTags.get(j).setY2D((int) (ry3 * per));
			mTags.get(j).setScale(per);
			
			int color = mTags.get(j).getColor();
			int a = (int) ((per/2) * 255);
			int r = Color.red(color);
			int g = Color.green(color);
			int b = Color.blue(color);
			mTags.get(j).setColor(Color.argb(a, r, g, b));
		}	
		depthSort();
	}	
	
	// now let's sort all tags in the tagCloud based on their z coordinate
	// this way, when they are finally drawn, upper tags will be drawn on top of lower tags
	private void depthSort() {
		Collections.sort(mTags);		
	}
	
	private int getColorFromGradient(float perc) {
		int r = (int) ((perc * Color.red(tagColor1)) + ((1-perc) * Color.red(tagColor2)));
		int g = (int) ((perc * Color.green(tagColor1)) + ((1-perc) * Color.green(tagColor2)));
		int b = (int) ((perc * Color.blue(tagColor1)) + ((1-perc) * Color.blue(tagColor2)));
 		return Color.rgb(r, g, b);
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
	
	public int getTagColor1() {
		return tagColor1;
	}
	
	public void setTagColor1(int tagColor) {
		this.tagColor1 = tagColor;
	}
	
	public int getTagColor2() {
		return tagColor2;
	}
	
	public void setTagColor2(int tagColor2) {
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
