package com.mds.cm.content.nullobjects;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.mds.cm.content.ContentTemplateBo;
import com.mds.cm.content.ContentTemplateBoCollection;
import com.mds.cm.content.MimeTypeBo;
import com.mds.core.MimeTypeCategory;

/// <summary>
/// Represents a <see cref="MimeTypeBo" /> that is equivalent to null. This class is used instead of null to prevent 
/// <see cref="NullReferenceException" /> errors if the calling code accesses a property or executes a method.
/// </summary>
public class NullMimeType extends MimeTypeBo {
	public NullMimeType() {
		super(MimeTypeCategory.NotSet);
	}

	@Override
	public long getMimeTypeId(){
		return Long.MIN_VALUE;
	}
	
	@Override
	public void setMimeTypeId(long mimeTypeId)	{
	}

	@Override
	public long getMimeTypeGalleryId(){
		return Long.MIN_VALUE;
	}
	
	@Override
	public void setMimeTypeGalleryId(long mimeTypeGalleryId){
	}

	@Override
	public long getGalleryId(){
		return Long.MIN_VALUE;
	}
	
	@Override
	public void setGalleryId(long galleryId){
	}

	@Override
	public String getExtension(){
	  return StringUtils.EMPTY;
	}

	@Override
	public String getFullType(){
	  return StringUtils.EMPTY;
	}

	@Override
	public String getMajorType(){
		return StringUtils.EMPTY;
	}

	@Override
	public String getSubtype(){
	  return StringUtils.EMPTY;
	}

	@Override
	public MimeTypeCategory getTypeCategory(){
	  return MimeTypeCategory.NotSet;
	}

	@Override
	public String getBrowserMimeType(){
		return StringUtils.EMPTY;
	}

	@Override
	public boolean getAllowAddToGallery(){
		return false;
	}
	
	@Override
	public void setAllowAddToGallery(boolean allowAddToGallery)	{
	}

	@Override
	public ContentTemplateBoCollection getContentTemplates()	{
		return new ContentTemplateBoCollection();
	}

	@Override
	public MimeTypeBo Copy(){
		return new NullMimeType();
	}

	@Override
	public ContentTemplateBo getContentTemplate(List browserIds){
		return null;
	}
}
