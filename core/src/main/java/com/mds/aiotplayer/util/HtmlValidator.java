package com.mds.aiotplayer.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.mds.aiotplayer.cm.content.GallerySettings;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;

/// <summary>
/// Provides functionality for validating and cleaning HTML.
/// </summary>
public class HtmlValidator{
	//#region Private Fields

	private static Pattern jsAttributeRegex = Pattern.compile("javascript:", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
	private static Pattern scriptTag = Pattern.compile("<script[\\w\\W]*?</script>", Pattern.COMMENTS);

	private String originalHtml;
	private String dirtyHtml;
	private StringBuilder cleanHtml;
	private String[] allowedTags;
	private String[] allowedAttributes;
	private boolean allowJavascript;
	private boolean invalidJavascriptDetected;
	private List<String> invalidHtmlTags = new ArrayList<String>();
	private List<String> invalidHtmlAttributes = new ArrayList<String>();
	private boolean validateHasExecuted;

	//#endregion

	//#region Properties

	/// <summary>
	/// Gets the list of HTML tags found in the user-entered input that are not allowed. This property is set after
	/// the <see cref="Validate"/> method is invoked. Guaranteed to not be null.
	/// </summary>
	/// <value>
	/// The list of HTML tags found in the user-entered input that are not allowed.
	/// </value>
	public List<String> getInvalidHtmlTags(){
		return this.invalidHtmlTags;
	}

	/// <summary>
	/// Gets the list of HTML attributes found in the user-entered input that are not allowed. This property is set after
	/// the <see cref="Validate"/> method is invoked. Guaranteed to not be null.
	/// </summary>
	/// <value>
	/// The list of HTML attributes found in the user-entered input that are not allowed.
	/// </value>
	public List<String> getInvalidHtmlAttributes(){
		return this.invalidHtmlAttributes;
	}

	/// <summary>
	/// Gets a value indicating whether invalid javascript was detected in the HTML. This property is set after
	/// the <see cref="Validate"/> method is invoked. Returns <c>true</c> only when the configuration setting
	/// allowUserEnteredJavascript is <c>false</c> and either a script tag or the String "javascript:" is detected.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if invalid javascript is detected; otherwise, <c>false</c>.
	/// </value>
	public boolean isInvalidJavascriptDetected(){
		return this.invalidJavascriptDetected;
	}

	/// <summary>
	/// Gets a value indicating whether any invalid HTML tags, attributes, or javascript was found in the HTML.
	/// </summary>
	/// <value>
	/// 	<c>true</c> if invalid HTML tags, attributes, or javascript was found; otherwise, <c>false</c>.
	/// </value>
	public boolean isValid(){
		if (!this.validateHasExecuted)
			throw new UnsupportedOperationException("You must call the Validate method before accessing the IsValid property.");

		if (this.allowJavascript)
			return ((this.invalidHtmlTags.size() == 0) && (this.invalidHtmlAttributes.size() == 0));
		else
			return ((this.invalidHtmlTags.size() == 0) && (this.invalidHtmlAttributes.size() == 0) && !this.invalidJavascriptDetected);
	}

	//#endregion

	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="HtmlValidator"/> class.
	/// </summary>
	/// <param name="html">The text to be cleaned. May be null.</param>
	/// <param name="allowedHtmlTags">The HTML tags that are allowed in <paramref name="html"/>. May be null.</param>
	/// <param name="allowedHtmlAttributes">The HTML attributes that are allowed in <paramref name="html"/>. May be null.</param>
	/// <param name="allowJavascript">If set to <c>true</c> allow script tag and the String "javascript:". Note that
	/// if the script tag is not a member of <paramref name="allowedHtmlTags"/> it will be removed even if this
	/// parameter is <c>true</c>.</param>
	private HtmlValidator(String html, String[] allowedHtmlTags, String[] allowedHtmlAttributes, boolean allowJavascript)	{
		//#region Validation

		if (html == null)
			html = StringUtils.EMPTY;

		if (allowedHtmlTags == null)
			allowedHtmlTags = new String[0];

		if (allowedHtmlAttributes == null)
			allowedHtmlAttributes = new String[0];

		//#endregion

		this.originalHtml = html;
		this.allowedTags = allowedHtmlTags;
		this.allowedAttributes = allowedHtmlAttributes;
		this.allowJavascript = allowJavascript;

		this.cleanHtml = new StringBuilder(this.originalHtml.length());
	}

	//#endregion

	//#region Public Methods

	/// <summary>
	/// Evaluates the HTML for invalid tags, attributes, and javascript. After executing this method the <see cref="IsValid"/>
	/// property can be checked. If this property is <c>true</c>, the properties <see cref="InvalidHtmlTags"/>,
	/// <see cref="InvalidHtmlAttributes"/>, and <see cref="InvalidJavascriptDetected"/> can be inspected for details.
	/// </summary>
	public void validate(){
		this.invalidHtmlTags.clear();
		this.invalidHtmlAttributes.clear();

		clean();

		validateHasExecuted = true;
	}

	//#endregion

	//#region Public Static Methods

	/// <summary>
	/// Initializes a new instance of the <see cref="HtmlValidator"/> class with the specified parameters.
	/// </summary>
	/// <param name="html">The text to be cleaned.</param>
	/// <param name="galleryId">The gallery ID. This is used to look up the appropriate configuration values for the gallery.</param>
	/// <returns>Returns an instance of <see cref="HtmlValidator"/>.</returns>
	public static HtmlValidator Create(String html, long galleryId) throws UnsupportedContentObjectTypeException, InvalidGalleryException	{
		GallerySettings gallerySetting = CMUtils.loadGallerySetting(galleryId);

		return new HtmlValidator(html, gallerySetting.getAllowedHtmlTags(), gallerySetting.getAllowedHtmlAttributes(), gallerySetting.getAllowUserEnteredJavascript());
	}

	/// <summary>
	/// Removes potentially dangerous HTML and Javascript in <paramref name="html"/>. If the configuration
	/// setting <see cref="IGallerySettings.AllowUserEnteredHtml" /> is true, then the input is cleaned so that all 
	/// HTML tags that are not in a predefined list are HTML-encoded and invalid HTML attributes are deleted. If 
	/// <see cref="IGallerySettings.AllowUserEnteredHtml" /> is false, then all HTML tags are deleted. If the setting 
	/// <see cref="IGallerySettings.AllowUserEnteredJavascript" /> is true, then script tags and the text "javascript:"
	/// is allowed. Note that if script is not in the list of valid HTML tags defined in <see cref="IGallerySettings.AllowedHtmlTags" />,
	/// it will be deleted even when <see cref="IGallerySettings.AllowUserEnteredJavascript" /> is true. When the setting 
	/// is false, all script tags and instances of the text "javascript:" are deleted.
	/// </summary>
	/// <param name="html">The String containing the HTML tags.</param>
	/// <param name="galleryId">The gallery ID. This is used to look up the appropriate configuration values for the gallery.</param>
	/// <returns>
	/// Returns a String with potentially dangerous HTML tags deleted.
	/// </returns>
	public static String clean(String html, long galleryId) throws UnsupportedContentObjectTypeException, InvalidGalleryException{
		GallerySettings gallerySetting = CMUtils.loadGallerySetting(galleryId);

		if (gallerySetting.getAllowUserEnteredHtml()){
			HtmlValidator scrubber = new HtmlValidator(html, gallerySetting.getAllowedHtmlTags(), gallerySetting.getAllowedHtmlAttributes(), gallerySetting.getAllowUserEnteredJavascript());
			
			return scrubber.clean();
		}else{
			// HTML not allowed. Pass in empty variables for the valid tags and attributes.
			HtmlValidator scrubber = new HtmlValidator(html, null, null, gallerySetting.getAllowUserEnteredJavascript());
			
			return scrubber.clean();
		}
	}

	/// <summary>
	/// Remove all HTML tags and javascript from the specified String. If <paramref name="escapeQuotes"/> is <c>true</c>, then all 
	/// apostrophes and quotation marks are replaced with &quot; and &apos; so that the String can be specified in HTML 
	/// attributes such as title tags.
	/// </summary>
	/// <param name="html">The String containing HTML tags to remove.</param>
	/// <param name="escapeQuotes">When true, all apostrophes and quotation marks are replaced with &quot; and &apos;.</param>
	/// <returns>Returns a String with all HTML tags removed, including the brackets.</returns>
	public static String removeHtml(String html, boolean escapeQuotes)	{
		HtmlValidator scrubber = new HtmlValidator(html, null, null, false);
		String cleanHtml = scrubber.clean();

		if (escapeQuotes){
			cleanHtml = cleanHtml.replace("\"", "&quot;");
			cleanHtml = cleanHtml.replace("'", "&apos;");
		}

		return cleanHtml;
	}

	//#endregion

	//#region Private Functions

	/// <summary>
	/// Remove invalid HTML tags, attributes, and javascript from the HTML.
	/// </summary>
	/// <returns>Returns a String consisting of clean HTML.</returns>
	private String clean(){
		if (this.allowJavascript)
			this.dirtyHtml = this.originalHtml;
		else
			this.dirtyHtml = deleteScriptTags(this.originalHtml);
		
		Whitelist whitelist = Whitelist.none().addTags(this.allowedTags);
		for(String tag : this.allowedTags) {
			whitelist.addAttributes(tag, this.allowedAttributes);
		}
		this.cleanHtml.append(Jsoup.clean(this.dirtyHtml, whitelist));

		return this.cleanHtml.toString();
	}

	/// <summary>
	/// Scrub the specified <paramref name="tagMatch"/> of invalid HTML tags, attributes, and javascript. The tag will be
	/// either a starting tag (e.g. &lt;p&gt;) or a single tag (e.g. &lt;br /&gt;).
	/// </summary>
	/// <param name="tagMatch">A <see cref="Match"/> resulting from passing a String containing HTML to an instance of
	/// <see cref="TagRegex"/>.</param>
	/// <param name="index">The position within the original HTML where the <paramref name="tagMatch"/> ends.</param>
	/// <returns>The position within the original HTML after the <paramref name="tagMatch"/> and any text that occurs
	/// after it. It can be used by the calling code for looking for the next match.</returns>
	/*private int ProcessStartTag(Match tagMatch, int index)
	{
		String tagName = tagMatch.Groups["tagname"].Value.ToLowerInvariant();

		if (Array.IndexOf<String>(this.allowedTags, tagName) >= 0)
		{
			// This tag is valid. Clean the attributes and append to our output.
			cleanHtml.Append(RemoveInvalidAttributes(tagMatch, this.allowedAttributes, this.invalidHtmlAttributes));
		}
		else
		{
			// Invalid tag. Call RemoveInvalidAttributes so that we can get our list of invalid attributes updated.
			RemoveInvalidAttributes(tagMatch, this.allowedAttributes, this.invalidHtmlAttributes);

			// Add to list of invalid tags if not already there
			if (!this.invalidHtmlTags.Contains(tagName))
				this.invalidHtmlTags.Add(tagName);
		}

		// Add any text between this start tag and the beginning of the next tag.
		Match contentMatch = innerContentRegEx.Match(dirtyHtml, index);
		if (contentMatch.Success)
		{
			cleanHtml.Append(contentMatch.Value);

			// Increment our index so that when we search for the next tag we do it after the content we just found.
			index = contentMatch.Index + contentMatch.length;
		}

		return index;
	}
*/
	/// <summary>
	/// Scrub the specified <paramref name="tagMatch"/> of invalid HTML tags, attributes, and javascript. The tag will be
	/// an ending tag (e.g. &lt;/p&gt;).
	/// </summary>
	/// <param name="tagMatch">A <see cref="Match"/> resulting from passing a String containing HTML to an instance of
	/// <see cref="TagRegex"/>.</param>
	/// <param name="index">The position within the original HTML where the <paramref name="tagMatch"/> ends.</param>
	/// <returns>The position within the original HTML after the <paramref name="tagMatch"/> and any text that occurs
	/// after it. It can be used by the calling code for looking for the next match.</returns>
	/*private int ProcessEndTag(Match tagMatch, int index)
	{
		if (Array.IndexOf<String>(this.allowedTags, tagMatch.Groups["tagname"].Value.ToLowerInvariant()) >= 0)
		{
			cleanHtml.Append(tagMatch.Value);
		}

		// Add any text between this end tag and the beginning of the next tag.
		Match contentMatch = innerContentRegEx.Match(dirtyHtml, index);
		if (contentMatch.Success)
		{
			cleanHtml.Append(contentMatch.Value);

			// Increment our index so that when we search for the next tag we do it after the content we just found.
			index = contentMatch.Index + contentMatch.length;
		}

		return index;
	}*/

	/// <summary>
	/// Removes HTML attributes and their values from the HTML String in <paramref name="tagMatch"/> if they do not exist in 
	/// <paramref name="allowedAttributes"/>. Any invalid attributes are added to <paramref name="invalidHtmlAttributes"/>.
	/// </summary>
	/// <param name="tagMatch">A <see cref="Match"/> resulting from passing a String containing HTML to an instance of
	/// <see cref="TagRegex"/>.</param>
	/// <param name="allowedAttributes">The HTML attributes that are allowed to be present in the HTML String in 
	/// <paramref name="tagMatch"/>.</param>
	/// <param name="invalidHtmlAttributes">A running list of invalid HTML attributes. Any attributes found to be invalid
	/// in <paramref name="tagMatch"/> are added to this list.</param>
	/// <returns>Returns the HTML String stored in <paramref name="tagMatch"/> with invalid attributes and their values removed.</returns>
	/*private static String RemoveInvalidAttributes(Match tagMatch, String[] allowedAttributes, ICollection<String> invalidHtmlAttributes)
	{
		String cleanTag = String.Concat("<", tagMatch.Groups["tagname"].Value.ToLowerInvariant());

		Group grpAttrName = tagMatch.Groups["attrname"];
		Group grpAttrVal = tagMatch.Groups["attrval"];

		CaptureCollection attrNameCaptures = grpAttrName.Captures;
		CaptureCollection attrValCaptures = grpAttrVal.Captures;

		if (attrNameCaptures.size() == attrValCaptures.size())
		{
			for (int attValuePairIterator = 0; attValuePairIterator < attrNameCaptures.size(); attValuePairIterator++)
			{
				if (Array.IndexOf<String>(allowedAttributes, attrNameCaptures[attValuePairIterator].Value.ToLowerInvariant()) >= 0)
				{
					// Valid attribute. Append attribute/value String to our clean tag.
					cleanTag += GetAttValuePair(tagMatch, attValuePairIterator);
				}
				else
				{
					if (!invalidHtmlAttributes.Contains(attrNameCaptures[attValuePairIterator].Value.ToLowerInvariant()))
						invalidHtmlAttributes.Add(attrNameCaptures[attValuePairIterator].Value.ToLowerInvariant());
				}
			}
		}

		cleanTag += ">";

		return cleanTag;
	}*/

	/// <summary>
	/// Gets the attribute="value" String in the <see cref="tagMatch"/> at the specified <see cref="index" />. If the original
	/// value was not surrounded by quotation marks or apostrophes, add them, selectively choosing the most appropriate one so
	/// as not to interfere with the presence of one or the other in the attribute value. For example, if the attribute value
	/// contains an apostrophe, surround it with quotation marks. Includes a leading space. (Example: " class='boldtext'")
	/// </summary>
	/// <param name="tagMatch">A <see cref="Match"/> resulting from passing a String containing HTML to an instance of
	/// <see cref="TagRegex"/>.</param>
	/// <param name="index">The index of the attribute within the <see cref="CaptureCollection"/> of <paramref name="tagMatch"/>.</param>
	/// <returns>Returns an attribute="value" String with a leading space (Example: " class='boldtext'").</returns>
	/*private static String GetAttValuePair(Match tagMatch, int index)
	{
		char[] delimiters = new char[] { '\'', '"' };

		Capture attrValCapture = tagMatch.Groups["attrval"].Captures[index];
		int indexOfAttributeStart = attrValCapture.Index - tagMatch.Index;

		// Get the characters at the start and end of the attribute value. Typically this is a quote or apostrophe.
		char beginAttValue = tagMatch.Value.SubString(indexOfAttributeStart - 1, 1)[0];
		char endAttValue = tagMatch.Value.SubString(indexOfAttributeStart + attrValCapture.length, 1)[0];

		// If one or both characters are not a quote or apostrophe, specify one. If the attribute value contains one, 
		// then choose the other so as not to interfere.
		if (Array.IndexOf(delimiters, beginAttValue) < 0)
			beginAttValue = (attrValCapture.Value.Contains("'") ? '"' : '\'');

		if (Array.IndexOf(delimiters, endAttValue) < 0)
			endAttValue = (attrValCapture.Value.Contains("'") ? '"' : '\'');

		return String.Concat(" ", tagMatch.Groups["attrname"].Captures[index].Value, "=", beginAttValue, attrValCapture.Value, endAttValue);
	}*/

	///// <summary>
	///// HTML-encodes a String and returns the encoded String.
	///// </summary>
	///// <param name="text">The text String to encode. </param>
	///// <returns>The HTML-encoded text.</returns>
	//private static String HtmlEncode(String text)
	//{
	//  if (text == null)
	//    return null;

	//  StringBuilder sb = new StringBuilder(text.length);

	//  int len = text.length;
	//  for (int i = 0; i < len; i++)
	//  {
	//    switch (text[i])
	//    {
	//      case '<':
	//        sb.Append("&lt;");
	//        break;
	//      case '>':
	//        sb.Append("&gt;");
	//        break;
	//      case '"':
	//        sb.Append("&quot;");
	//        break;
	//      case '&':
	//        sb.Append("&amp;");
	//        break;
	//      default:
	//        if (text[i] > 159)
	//        {
	//          // decimal numeric entity
	//          sb.Append("&#");
	//          sb.Append(((int)text[i]).ToString(CultureInfo.InvariantCulture));
	//          sb.Append(";");
	//        }
	//        else
	//          sb.Append(text[i]);
	//        break;
	//    }
	//  }
	//  return sb.ToString();
	//}

	/// <summary>
	/// Delete any script tag and its content. Delete any instances of the String "javascript:". If javascript
	/// is detected, the member variable javascriptDetected is set to <c>true</c>.
	/// </summary>
	/// <param name="html">The String to be cleaned of script tags.</param>
	/// <returns>
	/// Returns <paramref name="html"/> cleaned of script tags.
	/// </returns>
	private String deleteScriptTags(String html){
		int originalLength = html.length();

		// Delete any <script> tags
		Matcher m = scriptTag.matcher(html);
		html = m.replaceAll(StringUtils.EMPTY);

		// Delete any instances of the String "javascript:"
		m = jsAttributeRegex.matcher(html);
		html = m.replaceAll(StringUtils.EMPTY);

		if (html.length() != originalLength){
			this.invalidJavascriptDetected = true;
		}

		return html;
	}

	//#endregion
}
