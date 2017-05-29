package bio.knowledge.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

/**
 * @author Richard
 *
 */
@Service
public class ConfigLoader implements ResourceLoaderAware {
	
	private ResourceLoader loader = null ;
	
	public ConfigLoader() {	}
	
	/* (non-Javadoc)
	 * @see org.springframework.context.ResourceLoaderAware#setResourceLoader(org.springframework.core.io.ResourceLoader)
	 */
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		loader = resourceLoader ;
	}
	
	private ResourceLoader getResourceLoader() {
		if(loader!=null)
			return loader ;
		else
			return new DefaultResourceLoader() ;
	}
	
	public static InputStream getResourceStream(String filename) throws IOException {
		ConfigLoader cl   = new ConfigLoader() ;
		ResourceLoader rl = cl.getResourceLoader();
		Resource resource = rl.getResource("classpath:"+filename) ;
		File file         = resource.getFile() ;
		InputStream is    = new FileInputStream(file) ;
		return is ;
	} 

}