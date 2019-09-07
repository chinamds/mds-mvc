package com.mds.core;

/// <summary>
/// Specifies whether multiple <see cref="SecurityActions" /> values passed in a parameter all must pass a test for it to succeed, or
/// whether it passes if only a single item succeeds. Relevant only when the <see cref="SecurityActions" /> specified contain multiple
/// values.
/// </summary>
public enum SecurityActionsOption
{
	/// <summary>
	/// Specifies that every <see cref="SecurityActions" /> must pass the test for the method to succeed.
	/// </summary>
	RequireAll(0),
	/// <summary>
	/// Specifies that the method succeeds if only a single <see cref="SecurityActions" /> item passes.
	/// </summary>
	RequireOne(1);
	
	private final int securityActionsOption;
    
    private SecurityActionsOption(int securityActionsOption) {
        this.securityActionsOption = securityActionsOption;
    }
    
    public int value(){
    	return this.securityActionsOption;
    }
}
