/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.core;

//
// Summary:
//     Implements a class that is used to describe the Size of an
//     object.
public class Size {
    /**
     * The width of the instance of Size.
     */
	public Double Width;

    /**
     * The height of the instance of Size.
     */
	public Double Height;

    /**
     * Construct a blank name
     */
    public Size() {
        this.Width = null;
        this.Height = null;
    }
    
    public boolean isEmpty() {
        return (this.Width == null &&  this.Height == null);
    }
    
    public static Size Empty = new Size();

    /**
     * Construct a name from a last name and first name
     *
     * @param lastNameIn   the last name
     * @param firstNamesIn the first names
     */
    public Size(double width, double height) {
        this.Width = width;
        this.Height = height;
    }

    /**
     * Return a string for writing the name to the database
     *
     * @return the name, suitable for putting in the database
     */
    public String toString() {
        StringBuffer out = new StringBuffer();

        if (Width != null) {
            out.append(Width);

            if ((Height != null) && !Height.equals("")) {
                out.append(", ").append(Height);
            }
        }

        return (out.toString());
    }

    /**
     * Get the first name(s). Guaranteed non-null.
     *
     * @return the first name(s), or an empty string if none
     */
    public double getWidth() {
        return Width == null ? 0 : Width;
    }

    /**
     * Get the last name. Guaranteed non-null.
     *
     * @return the last name, or an empty string if none
     */
    public double getHeight() {
        return Height == null ? 0 : Height;
    }
}
