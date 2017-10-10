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
package bio.knowledge.server.impl;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.util.UrlPathHelper;

/**
 * Configures Spring to properly convert URLs into method calls.
 * 
 * @author Meera Godden
 *
 */
@Configuration
public class SpringConfiguration extends WebMvcConfigurerAdapter {

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		
		UrlPathHelper helper = new UrlPathHelper();
		
		// NOTE: %2F is a percent-encoded forward slash
		
		// the following prevents "/concepts/example-dot-com%2Fpath"
		// from being treated as  "/concepts/example-dot-com/{parameter}"
		// (which doesn't exist, and would result in an HTTP 404 response)
		helper.setUrlDecode(false);
		
		// the following prevents "/concepts/http-colon-%2F%2Fexample-dot-com"
		// from being treated as  "/concepts/http-colon-/{parameter}
		// (which doesn't exist, and would result in an HTTP 404 response)
		helper.setAlwaysUseFullPath(true);
		
		configurer

			.setUrlPathHelper(helper)
			
			// the following prevents "/concepts/example.com"
			// from being truncated to "/concepts/example"
			.setUseSuffixPatternMatch(false)
	
		;
	}
	
}
