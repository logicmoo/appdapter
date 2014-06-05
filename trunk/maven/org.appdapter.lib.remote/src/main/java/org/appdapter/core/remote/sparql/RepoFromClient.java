/*
 *  Copyright 2013 by The Appdapter Project (www.appdapter.org).
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
package org.appdapter.core.remote.sparql;

import org.appdapter.core.repo.DirectRepo;
import org.appdapter.help.repo.RepoClient;
import org.appdapter.core.repo.*;
import org.appdapter.core.matdat.*;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;

public class RepoFromClient extends DirectRepo {

	private RepoClient rc;
	private String dirModelName;

	public RepoFromClient(RepoClient rc, String dirModelName) {
		super(null);
		this.rc = rc;
		this.dirModelName = dirModelName;
	}

	@Override public Model getDirectoryModel() {
		return getMainQueryDataset().getNamedModel(this.dirModelName);
	}

	@Override public Dataset makeMainQueryDataset() {
		return rc.getRepo().getMainQueryDataset();
	}

	@Override public Dataset getMainQueryDataset() {
		return rc.getRepo().getMainQueryDataset();
	}
}
