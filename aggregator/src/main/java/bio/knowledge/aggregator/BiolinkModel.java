/*-------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-18 STAR Informatics / Delphinai Corporation (Canada) - Dr. Richard Bruskiewich
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

package bio.knowledge.aggregator;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * 
 * @author Richard
 *
 */
public class BiolinkModel {
	
	/*
	 * TODO: need to flesh out the BiolinkModel 
	 * to store the contents of the model
	 */

	private static String BIOLINK_MODEL = "https://raw.githubusercontent.com/biolink/biolink-model/master/biolink-model.yaml";
	
	private static BiolinkModel model = null ;
	
	public Optional<BiolinkModel> load() {
		
		URL yamlSource;
		try {
			
			// Option 1: construct a standard ObjectMapper
			yamlSource = new URL(BIOLINK_MODEL);
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			model = mapper.readValue(yamlSource, BiolinkModel.class);
			
			/*
			 * Option 2: We can also just use underlying YAMLFactory and 
			 * the parser it produces, for event-based processing:
			 */

			YAMLFactory factory = new YAMLFactory();
			JsonParser parser = factory.createParser(yamlSource); 
			while (parser.nextToken() != null) {
			  // do something!
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
			return Optional.empty();
		}
		return Optional.of(model);
	}
}
