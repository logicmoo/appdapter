/*
 *  Copyright 2011 by The Appdapter Project (www.appdapter.com).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.appdapter.peru.test.module.selector;

import com.appdapter.peru.binding.jena.SPARQL_Utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.appdapter.peru.test.data.TestDataConstants.SPARQL_UnitTestConstants.*;

/**  SelectorTest is our unit test for the peruser "Selector" SPARQL query interface.
 *
 * @author      Stu B. <www.texpedient.com>
 * @version     @PERUSER_VERSION@
 */
public class SelectorTest {
	private static Log 		theLog = LogFactory.getLog(SelectorTest.class);	
	public static void main(String[] args) {
		theLog.info("SPARQL Unit Test - inflating!");
		try {
			String resultXML = SPARQL_Utils.executeQueryFromFiles (SUT_queryFileURL, SUT_modelURL, SUT_modelFormat, SUT_modelBaseURI);
			theLog.debug(resultXML);		
		} catch (Throwable t) {
			theLog.error("SPARQL Unit Test caught exception: ", t);
		}
		theLog.info("SPARQL Unit Test - deflating!");
	}

}
