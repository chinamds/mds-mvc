package com.mds.core;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

/// <summary>
/// Specifies one or more security-related actions within MDS System. A user may or may not have authorization to
/// perform each security action. A user's authorization is determined by the role or roles to which he or she
/// belongs. This enumeration is defined with the Flags attribute, so one can combine multiple security actions by
/// performing a bitwise OR.
/// </summary>
public enum SecurityActions{
	/// <summary>
	/// No security action has been specified.
	/// </summary>
	NotSpecified(0),
	/// <summary>
	/// Represents the ability to view an album or content object. Does not include the ability to view high resolution
	/// versions of images. Includes the ability to download the content object and view a slide show.
	/// </summary>
	ViewAlbumOrContentObject(1),
	/// <summary>
	/// Represents the ability to create a new album within the current album. This includes the ability to move or
	/// copy an album into the current album.
	/// </summary>
	AddChildAlbum(2),
	/// <summary>
	/// Represents the ability to add a new content object to the current album. This includes the ability to move or
	/// copy a content object into the current album.
	/// </summary>
	AddContentObject(4),
	/// <summary>
	/// Represents the ability to edit an album's title, summary, and begin and end dates. Also includes rearranging the
	/// order of objects within the album and assigning the album's thumbnail image. Does not include the ability to
	/// add or delete child albums or content objects.
	/// </summary>
	EditAlbum(8),
	/// <summary>
	/// Represents the ability to edit a content object's caption, rotate it, and delete the high resolution version of
	/// an image.
	/// </summary>
	EditContentObject(16),
	/// <summary>
	/// Represents the ability to delete the current album. This permission is required to move 
	/// albums to another album, since it is effectively deleting it from the current album's parent.
	/// </summary>
	DeleteAlbum(32),
	/// <summary>
	/// Represents the ability to delete child albums within the current album.
	/// </summary>
	DeleteChildAlbum(64),
	/// <summary>
	/// Represents the ability to delete content objects within the current album. This permission is required to move 
	/// content objects to another album, since it is effectively deleting it from the current album.
	/// </summary>
	DeleteContentObject(128),
	/// <summary>
	/// Represents the ability to synchronize content objects on the hard drive with records in the data store.
	/// </summary>
	Synchronize(256),
	/// <summary>
	/// Represents the ability to administer a particular gallery. Automatically includes all other permissions except
	/// AdministerSite.
	/// </summary>
	AdministerGallery(512),
	/// <summary>
	/// Represents the ability to administer all aspects of MDS System. Automatically includes all other permissions.
	/// </summary>
	AdministerSite(1024),
	/// <summary>
	/// Represents the ability to not render a watermark over content objects.
	/// </summary>
	HideWatermark(2048),
	/// <summary>
	/// Represents the ability to view the original version of content objects.
	/// </summary>
	ViewOriginalContentObject(4096),
    /// <summary>
    /// Represents the ability to approve content objects within the current album. This permission is required to move 
    /// content objects to another album, since it is effectively approving it from the current album.
    /// </summary>
    ApproveContentObject(8192),
	/// <summary>
	/// Represents all possible permissions. Note: This enum value is defined to contain ALL POSSIBLE enum values to ensure
	/// the <see cref="SecurityActionEnumHelper.IsValidSecurityAction(SecurityActions)" /> method properly works. If a developer adds or removes
	/// items from this enum, this item must be updated to reflect the ORed list of all possible values.
	/// </summary>
    All(ViewAlbumOrContentObject.value() | AddChildAlbum.value() | AddContentObject.value() | EditAlbum.value() 
    		| EditContentObject.value() | DeleteAlbum.value() | DeleteChildAlbum.value() | DeleteContentObject.value() 
    		| Synchronize.value() | AdministerGallery.value() | AdministerSite.value() | HideWatermark.value() 
    		| ViewOriginalContentObject.value() | ApproveContentObject.value());
	
	private final int securityActions;
    
    private SecurityActions(int securityActions) {
        this.securityActions = securityActions;
    }
    
    public int value(){
    	return this.securityActions;
    }

	
	/// <summary>
	/// Determines if the securityActions parameter is one of the defined enumerations or a valid combination of valid enumeration
	/// values (since <see cref="SecurityActions" /> is defined with the Flags attribute). <see cref="Enum.IsDefined" /> cannot be used since it does not return
	/// true when the enumeration contains more than one enum value. This method requires the <see cref="SecurityActions" /> enum to have a member
	/// All that contains every enum value ORed together.
	/// </summary>
	/// <param name="securityActions">A <see cref="SecurityActions" />. It may be a single value or some
	/// combination of valid enumeration values.</param>
	/// <returns>Returns true if securityActions is one of the defined items in the enumeration or is a valid combination of
	/// enumeration values; otherwise returns false.</returns>
	public static boolean isValidSecurityAction(SecurityActions securityActions){
		if ((securityActions.value() != 0) && ((securityActions.value() & All.value()) == securityActions.value()))	{
			return true;
		}else{
			return false;
		}
	}

	/// <summary>
	/// Determines if the securityActions parameter is one of the defined enumerations or a valid combination of valid enumeration
	/// values (since <see cref="SecurityActions" /> is defined with the Flags attribute). <see cref="Enum.IsDefined" /> cannot be used since it does not return
	/// true when the enumeration contains more than one enum value. This method requires the <see cref="SecurityActions" /> enum to have a member
	/// All that contains every enum value ORed together.
	/// </summary>
	/// <param name="securityActions">An integer representing a <see cref="SecurityActions" />.</param>
	/// <returns>Returns true if securityAction is one of the defined items in the enumeration or is a valid combination of
	/// enumeration values; otherwise returns false.</returns>
	public static boolean isValidSecurityAction(int securityActions){
		if ((securityActions != 0) && ((securityActions & All.value()) == securityActions))	{
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isValidSecurityAction(String securityActions){
		for(SecurityActions value : SecurityActions.values()) {
			if (value.toString() == securityActions)
				return true;
		}
		
		return false;
	}
	
	public static SecurityActions getSecurityAction(int securityActions){
		for(SecurityActions value : SecurityActions.values()) {
			if (value.value() == securityActions)
				return value;
		}
		
		return SecurityActions.NotSpecified;
	}
	
	public static int getSecurityAction(SecurityActions[] securityActions){
		int securityActionMix = SecurityActions.NotSpecified.value();
		for(SecurityActions value : securityActions) {
			securityActionMix = (securityActionMix | value.value());
		}
		
		return securityActionMix;
	}
	
	public static boolean containSecurityAction(int securityActions, SecurityActions securityActionsTo){
		if ((securityActions != 0) && ((securityActions & securityActionsTo.value()) == securityActionsTo.value()))	{
			return true;
		}else{
			return false;
		}
	}

	/// <summary>
	/// Determines if the specified value is a single, valid enumeration value. Since the <see cref="SecurityActions" /> enum has the 
	/// Flags attribute and may contain a bitwise combination of more than one value, this function is useful in
	/// helping the developer decide if the enum value is just one value or it must be parsed into its constituent
	/// parts with the MDS.Business.SecurityService.ParseSecurityAction method.
	/// </summary>
	/// <param name="securityActions">A <see cref="SecurityActions" />. It may be a single value or some
	/// combination of valid enumeration values.</param>
	/// <returns>Returns true if securityAction is a valid, single bit flag; otherwise return false.</returns>
	public static boolean isSingleSecurityAction(int securityActions){
		if (isValidSecurityAction(securityActions) && (securityActions == SecurityActions.NotSpecified.value())
				|| (securityActions == SecurityActions.ViewAlbumOrContentObject.value())
				|| (securityActions == SecurityActions.ViewOriginalContentObject.value()) || (securityActions == SecurityActions.AddContentObject.value())
				|| (securityActions == SecurityActions.AdministerSite.value()) || (securityActions == SecurityActions.DeleteAlbum.value())
				|| (securityActions == SecurityActions.DeleteChildAlbum.value()) || (securityActions == SecurityActions.DeleteContentObject.value())
				|| (securityActions == SecurityActions.EditAlbum.value()) || (securityActions == SecurityActions.EditContentObject.value())
				|| (securityActions == SecurityActions.HideWatermark.value()) || (securityActions == SecurityActions.Synchronize.value())
				|| (securityActions == SecurityActions.AddChildAlbum.value()) || (securityActions == SecurityActions.AdministerGallery.value())
                || (securityActions == SecurityActions.ApproveContentObject.value())){
			return true;
		}else{
			return false;
		}
	}

	/// <summary>
	/// Parses the security action into one or more <see cref="SecurityActions"/>. Since the <see cref="SecurityActions" /> 
	/// enum has the Flags attribute and may contain a bitwise combination of more than one value, this function is useful
	/// in creating a list of the values that can be enumerated.
	/// </summary>
	/// <param name="securityActionsToParse">A <see cref="SecurityActions" />. It may be a single value or some
	/// combination of valid enumeration values.</param>
	/// <returns>Returns a list of <see cref="SecurityActions"/> that can be enumerated.</returns>
	public static Collection<SecurityActions> parseSecurityAction(int securityActionsToParse)	{
		List<SecurityActions> securityActions = Lists.newArrayList();

		for(SecurityActions securityActionIterator : SecurityActions.values()){
			if (securityActionIterator == SecurityActions.NotSpecified)
				continue; // Skip NotSpecified, since it falsely matches the test below

			if ((securityActionsToParse & securityActionIterator.value()) == securityActionIterator.value()){
				securityActions.add(securityActionIterator);
			}
		}

		return securityActions;
	}
}