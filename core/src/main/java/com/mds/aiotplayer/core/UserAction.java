package com.mds.aiotplayer.core;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

/// <summary>
/// Specifies one or more user-related actions within MDS System. A user may or may not have authorization to
/// perform each user action. A user's authorization is determined by the role or roles to which he or she
/// belongs. This enumeration is defined with the Flags attribute, so one can combine multiple user actions by
/// performing a bitwise OR.
/// </summary>
public enum UserAction {
	/// <summary>
	/// No user action has been specified.
	/// </summary>
	notspecified(0, "useraction.notspecified"),
	/**
     * Action of adding something to a container. For example, to add an content to
     * a album, a user must have <code>ADD</code> permission on the
     * collection.
     */
	add(1, "useraction.add"),
	
	/** Action of modifying something */
	edit(2, "useraction.edit"),
		
	/** Action of reading, viewing something */
	view(4, "useraction.view"),
	
	/** Action of delete something */
	delete(8, "useraction.delete"),
		
	/** Action of downloading something */
	download(16, "useraction.download"),
	
	/** Action of abort */
	abort(32, "useraction.abort"),
	
	/** Action of print */
	print(64, "useraction.print"),
	
	/** Action of add children */
	addchild(128, "useraction.addchild"),
	
	removechild(256, "useraction.removechild"),
	
	/** Action of import */
	data_import(512, "useraction.import"),
	
	/** Action of export */
	data_export(1024, "useraction.export"),
	
	/** Action of calculate */
	calculate(2048, "useraction.calculate"),
	
	/// <summary>
	/// Represents the ability to synchronize content objects on the hard drive with records in the data store.
	/// </summary>
	synchronize(4096, "useraction.synchronize"),
	
	/** Action of approve */
    approve(8192, "useraction.approve"),
    
    vieworiginalmedia(16384, "useraction.vieworiginalmedia"),
    
    /**
     * Action of removing something from a container. Different from deletion.
     * 
     */
	remove(32768, "useraction.remove"),
	
	/**
     * Action of operate player(s).
     * 
     */
	operate(65536, "useraction.operate"),
	
	/**
     * Action of send message to player(s).
     * 
     */
	send(131072, "useraction.send"),
	
	/// <summary>
  	/// Represents all possible permissions. Note: This enum value is defined to contain ALL POSSIBLE enum values to ensure
  	/// the <see cref="SecurityActionEnumHelper.IsValidSecurityAction(SecurityActions)" /> method properly works. If a developer adds or removes
  	/// items from this enum, this item must be updated to reflect the ORed list of all possible values.
  	/// </summary>
    All(add.getValue() | edit.getValue() | view.getValue() | delete.getValue() | remove.getValue() | download.getValue() | abort.getValue() 
    		| print.getValue() | data_import.getValue() | data_export.getValue() | calculate.getValue() | synchronize.getValue()
    		| approve.getValue()| addchild.getValue()| removechild.getValue()| vieworiginalmedia.getValue() | operate.getValue() | send.getValue(), "useraction.all");
	
	private final int userActions;
	private final String label;
    
    private UserAction(int userActions, String label) {
        this.userActions = userActions;
        this.label = label;
    }
    
    public int getValue(){
    	return this.userActions;
    }
    
    public String getLabel(){
    	return this.label;
    }

	
	/// <summary>
	/// Determines if the userActions parameter is one of the defined enumerations or a valid combination of valid enumeration
	/// values (since <see cref="UserAction" /> is defined with the Flags attribute). <see cref="Enum.IsDefined" /> cannot be used since it does not return
	/// true when the enumeration contains more than one enum value. This method requires the <see cref="UserAction" /> enum to have a member
	/// All that contains every enum value ORed together.
	/// </summary>
	/// <param name="userActions">A <see cref="UserAction" />. It may be a single value or some
	/// combination of valid enumeration values.</param>
	/// <returns>Returns true if userActions is one of the defined items in the enumeration or is a valid combination of
	/// enumeration values; otherwise returns false.</returns>
	public static boolean isValidSecurityAction(UserAction userActions)	{
		switch (userActions){
			case add:
			case edit:
			case view:
			case delete:
			case remove:
			case download:
			case abort:
			case print:
			case data_import:
			case data_export:
			case calculate:
			case synchronize:
			case approve:
			case addchild:
			case removechild:
			case vieworiginalmedia:
			case operate:
			case send:
				break;

			default:
				return false;
		}
		
		return true;
	}
	
	/// <summary>
	/// Determines if the userActions parameter is one of the defined enumerations or a valid combination of valid enumeration
	/// values (since <see cref="UserAction" /> is defined with the Flags attribute). <see cref="Enum.IsDefined" /> cannot be used since it does not return
	/// true when the enumeration contains more than one enum value. This method requires the <see cref="UserAction" /> enum to have a member
	/// All that contains every enum value ORed together.
	/// </summary>
	/// <param name="userActions">A <see cref="UserAction" />. It may be a single value or some
	/// combination of valid enumeration values.</param>
	/// <returns>Returns true if userActions is one of the defined items in the enumeration or is a valid combination of
	/// enumeration values; otherwise returns false.</returns>
	public static boolean isValidUserAction(UserAction userActions){
		if ((userActions.getValue() != 0) && ((userActions.getValue() & All.getValue()) == userActions.getValue()))	{
			return true;
		}else{
			return false;
		}
	}

	/// <summary>
	/// Determines if the userActions parameter is one of the defined enumerations or a valid combination of valid enumeration
	/// values (since <see cref="UserAction" /> is defined with the Flags attribute). <see cref="Enum.IsDefined" /> cannot be used since it does not return
	/// true when the enumeration contains more than one enum value. This method requires the <see cref="UserAction" /> enum to have a member
	/// All that contains every enum value ORed together.
	/// </summary>
	/// <param name="userActions">An integer representing a <see cref="UserAction" />.</param>
	/// <returns>Returns true if userAction is one of the defined items in the enumeration or is a valid combination of
	/// enumeration values; otherwise returns false.</returns>
	public static boolean isValidUserAction(int userActions){
		if ((userActions != 0) && ((userActions & All.getValue()) == userActions))	{
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isValidUserAction(String userActions){
		for(UserAction value : UserAction.values()) {
			if (value.toString() == userActions)
				return true;
		}
		
		return false;
	}
	
	public static UserAction getUserAction(int userActions){
		for(UserAction value : UserAction.values()) {
			if (value.getValue() == userActions)
				return value;
		}
		
		return UserAction.notspecified;
	}
	
	public static UserAction getUserAction(String userActions){
		for(UserAction value : UserAction.values()) {
			if (value.toString().equalsIgnoreCase(userActions))
				return value;
		}
		
		return UserAction.notspecified;
	}
	
	public static int getUserAction(UserAction[] userActions){
		int userActionMix = UserAction.notspecified.getValue();
		for(UserAction value : userActions) {
			userActionMix = (userActionMix | value.getValue());
		}
		
		return userActionMix;
	}
	
	public static boolean containUserAction(int userActions, UserAction userActionsTo){
		if ((userActions != 0) && ((userActions & userActionsTo.getValue()) == userActionsTo.getValue()))	{
			return true;
		}else{
			return false;
		}
	}

	/// <summary>
	/// Determines if the specified value is a single, valid enumeration value. Since the <see cref="UserAction" /> enum has the 
	/// Flags attribute and may contain a bitwise combination of more than one value, this function is useful in
	/// helping the developer decide if the enum value is just one value or it must be parsed into its constituent
	/// parts with the MDS.Business.SecurityService.ParseUserAction method.
	/// </summary>
	/// <param name="userActions">A <see cref="UserAction" />. It may be a single value or some
	/// combination of valid enumeration values.</param>
	/// <returns>Returns true if userAction is a valid, single bit flag; otherwise return false.</returns>
	public static boolean isSingleUserAction(int userActions){
		if (isValidUserAction(userActions) && (userActions == UserAction.notspecified.getValue())
				|| (userActions == UserAction.add.getValue())
				|| (userActions == UserAction.edit.getValue()) || (userActions == UserAction.view.getValue())
				|| (userActions == UserAction.remove.getValue()) || (userActions == UserAction.download.getValue())
				|| (userActions == UserAction.abort.getValue()) || (userActions == UserAction.print.getValue())
				|| (userActions == UserAction.data_import.getValue()) || (userActions == UserAction.data_export.getValue())
				|| (userActions == UserAction.calculate.getValue()) || (userActions == UserAction.synchronize.getValue())
				|| (userActions == UserAction.addchild.getValue())|| (userActions == UserAction.removechild.getValue()) 
				|| (userActions == UserAction.vieworiginalmedia.getValue())	|| (userActions == UserAction.approve.getValue())
				|| (userActions == UserAction.operate.getValue())	|| (userActions == UserAction.send.getValue())){
			return true;
		}else{
			return false;
		}
	}

	/// <summary> addchild.getValue()| removechild.getValue()| vieworiginalmedia.getValue()
	/// Parses the security action into one or more <see cref="UserAction"/>. Since the <see cref="UserAction" /> 
	/// enum has the Flags attribute and may contain a bitwise combination of more than one value, this function is useful
	/// in creating a list of the values that can be enumerated.
	/// </summary>
	/// <param name="userActionsToParse">A <see cref="UserAction" />. It may be a single value or some
	/// combination of valid enumeration values.</param>
	/// <returns>Returns a list of <see cref="UserAction"/> that can be enumerated.</returns>
	public static Collection<UserAction> parseUserAction(int userActionsToParse)	{
		List<UserAction> userActions = Lists.newArrayList();

		for(UserAction userActionIterator : UserAction.values()){
			if (userActionIterator == UserAction.notspecified)
				continue; // Skip notspecified, since it falsely matches the test below

			if ((userActionsToParse & userActionIterator.getValue()) == userActionIterator.getValue()){
				userActions.add(userActionIterator);
			}
		}

		return userActions;
	}
}