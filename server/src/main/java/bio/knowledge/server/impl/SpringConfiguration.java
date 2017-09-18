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
