package com.mds.core;

import com.mds.util.StringUtils;

/// <summary>
/// Specifies the metering mode.
/// </summary>
public enum MeteringMode{
	///<summary>Unknown</summary>
	Unknown(0),
	///<summary>Average</summary>
	Average(1),
	///<summary>Center weighted average</summary>
	CenterWeightedAverage(2),
	///<summary>Spot</summary>
	Spot(3),
	///<summary>Multi Spot</summary>
	MultiSpot(4),
	///<summary>Pattern</summary>
	Pattern(5),
	///<summary>Partial</summary>
	Partial(6),
	///<summary>Other</summary>
	Other(255);
	
	private final int meteringMode;
    private MeteringMode(int meteringMode) {
        this.meteringMode = meteringMode;
    }
}