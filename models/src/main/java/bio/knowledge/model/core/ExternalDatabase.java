/*-------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-16 Scripps Institute (USA) - Dr. Benjamin Good
 *                       STAR Informatics / Delphinai Corporation (Canada) - Dr. Richard Bruskiewich
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

package bio.knowledge.model.core;

import bio.knowledge.model.DomainModelException;

/**
 * @author Richard
 *
 */
public interface ExternalDatabase extends IdentifiedEntity {

	public enum Prefix {
	   	 AF,
	   	 CAS,
	   	 CHEB,
	   	 CHID,
	   	 DRUG,
	   	 EG,
	   	 GD,
	   	 GO,
	   	 HG,
	   	 HMDB,
	   	 KEGD,
	   	 KEGG,
	   	 MESH,
	   	 MGI,
	   	 OM,
	   	 PUBC,
	   	 PUBS,
	   	 RGD,
	   	 RM,
	   	 UG,
	   	 UMLS,
	   	 UP,
	   	 WIKI  
	   	;
	   	
	       public static Prefix lookUp(String query) {
	       	for(Prefix prefix: Prefix.values()) {
	       		if(prefix.name().equals(query))
	       			return prefix ;
	       	}
	       	throw new DomainModelException("Unknown database prefix: '"+query+"'") ;
	       }
	   }
	String getUrl();

	void setUrl(String url);

	String getNameSpacePrefix();

	void setNameSpacePrefix(String prefix);
}