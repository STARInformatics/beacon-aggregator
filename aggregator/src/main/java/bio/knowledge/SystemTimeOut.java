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
package bio.knowledge;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author richard
 *
 */
public interface SystemTimeOut {
	
	final long     BEACON_TIMEOUT_DURATION = 1;
	final TimeUnit BEACON_TIMEOUT_UNIT = TimeUnit.MINUTES;
	
	/**
	 * Classes which implement this method should generally
	 * call the Knowledge Beacon Registry implementation of the method.
	 * 
	 * @return total number of registered beacons?
	 */
	int countAllBeacons();
	
	/**
	 * Dynamically compute adjustment to query timeouts proportionately to 
	 * the number of beacons and pageSize
	 * @param beacons
	 * @param pageSize
	 * @return
	 */
	default long weightedTimeout( List<Integer> beacons, Integer pageSize ) {
		long timescale;
		if(!(beacons==null || beacons.isEmpty())) 
			timescale = beacons.size();
		else
			
			timescale = countAllBeacons();
		
		timescale *= Math.max(1,pageSize/10) ;
		
		return timescale*BEACON_TIMEOUT_DURATION;
	}
	
	/**
	 * Timeout simply weighted by total number of beacons and pagesize
	 * @return
	 */
	default long weightedTimeout(Integer pageSize) {
		return weightedTimeout(null, pageSize); // 
	}
	
	/**
	 * Timeout simply weighted by number of beacons
	 * @return
	 */
	default long weightedTimeout() {
		return weightedTimeout(null, 0); // 
	}
}
