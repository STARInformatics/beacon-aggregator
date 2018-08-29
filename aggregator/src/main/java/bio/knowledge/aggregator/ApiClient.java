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

package bio.knowledge.aggregator;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bio.knowledge.client.Pair;

/**
 * Extends a regular ApiClient with the ability to
 * ask for its associated beacon ID and latest query.
 * Used to enable error-logging.
 * 
 * @author Meera Godden
 *
 */
public class ApiClient extends bio.knowledge.client.ApiClient {

	private static Logger _logger = LoggerFactory.getLogger(ApiClient.class);
	
	private Integer beaconId;
	private String  query;
	
	public ApiClient(Integer beaconId, String basePath) {
		super();
		setBasePath(basePath);
		this.beaconId = beaconId;
		
		super.getHttpClient().setReadTimeout(20, TimeUnit.SECONDS);
	}
	
	public ApiClient() {
		super();
		
		super.getHttpClient().setReadTimeout(20, TimeUnit.SECONDS);
	}

	@Override
	public ApiClient setConnectTimeout(int connectionTimeout) {
		getHttpClient().setConnectTimeout(connectionTimeout, TimeUnit.MILLISECONDS);
		getHttpClient().setReadTimeout(connectionTimeout, TimeUnit.MILLISECONDS);
		return this;
	}

	@Override
    public String buildUrl(String path, List<Pair> queryParams) {
		
		query = super.buildUrl(path, queryParams);

		_logger.debug(query);
		
		return query;
	}

	public String getQuery() {
		return query;
	}

	public Integer getBeaconId() {
		return beaconId;
	}
	
}