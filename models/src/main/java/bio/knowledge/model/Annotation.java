/*-------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-17 STAR Informatics / Delphinai Corporation (Canada) - Dr. Richard Bruskiewich
 * Copyright (c) 2017    NIH National Center for Advancing Translational Sciences (NCATS)
 * Copyright (c) 2015-16 Scripps Institute (USA) - Dr. Benjamin Good
 *                       
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *-------------------------------------------------------------------------------
 */
package bio.knowledge.model;

public class Annotation {
	private String value;
	private String tag;
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
}

//import bio.knowledge.model.core.IdentifiedEntity;
//
//public interface Annotation extends IdentifiedEntity {
//
//	/**
//	 * Type of Annotation
//	 */
//	public enum Type {
//		
//		Remark("remark"), 
//		Title("ti"), 
//		Abstract("ab");
//		
//		private String abbreviation ;
//		
//		Type(String abbreviation){
//			this.abbreviation = abbreviation ;
//		}
//		
//	    public static Type lookUp(String abbreviation) {
//	    	for(Type type: Type.values()) {
//	    		if(type.abbreviation.toLowerCase().equals(abbreviation))
//	    			return type ;
//	    	}
//	    	throw new DomainModelException("Invalid Sentence type abbreviation: "+abbreviation) ;
//	    }
//	    
//	    public String toString() { return name() ; }
//		
//	}
//	
//	void setType(Type type);
//
//	/**
//	 * @param reference the reference to set
//	 */
//	void setReference(Reference reference);
//
//	/**
//	 * @return the reference
//	 */
//	Reference getReference();
//
//	void setPublicationDate(String date);
//
//	String getPublicationDate();
//
//	void setSupportingText(String text);
//
//	String getSupportingText();
//
//	/**
//	 * 
//	 */
//	String toString();
//
//	/**
//	 * 
//	 * @return
//	 */
//	EvidenceCode getEvidenceCode();
//
//	/**
//	 * 
//	 * @param evidenceCode
//	 */
//	void setEvidenceCode(EvidenceCode evidenceCode);
//
//	String getUserId();
//
//	void setUserId(String userId);
//
//	boolean isVisible();
//
//	void setVisible(boolean visible);
//	
//	public String getUrl();
//	public void setUrl(String url);
//
//}