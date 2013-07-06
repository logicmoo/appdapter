/*
 *  Copyright 2011 by The Appdapter Project (www.appdapter.org).
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

package org.appdapter.gui.rimpl;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.appdapter.api.trigger.Box;
import org.appdapter.api.trigger.BoxContext;
import org.appdapter.api.trigger.LazySlow;
import org.appdapter.api.trigger.Trigger;
import org.appdapter.bind.rdf.jena.query.JenaArqQueryFuncs;
import org.appdapter.bind.rdf.jena.query.JenaArqResultSetProcessor;
import org.appdapter.core.log.Debuggable;
import org.appdapter.core.name.FreeIdent;
import org.appdapter.core.store.BasicStoredMutableRepoImpl;
import org.appdapter.core.store.Repo;
import org.appdapter.core.store.Repo.GraphStat;
import org.appdapter.gui.api.Utility;
import org.appdapter.gui.box.ScreenBoxImpl;
import org.appdapter.gui.box.WrapperValue;
import org.appdapter.gui.repo.ScreenGraphTrigger;
import org.appdapter.gui.repo.ScreenModelBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * @author Stu B. <www.texpedient.com>
 */
@SuppressWarnings("serial")
public abstract class RepoBoxImpl<TT extends Trigger<? extends RepoBoxImpl<TT>>> extends ScreenBoxImpl<TT> //
		implements MutableRepoBox<TT>, WrapperValue {
	static Logger theLogger = LoggerFactory.getLogger(RepoBoxImpl.class);
	private LazySlow<Repo.Mutable> myRepo;

	@Override final public Object getValue() {
		return getRepo();
	}

	@Override public Box findGraphBox(String graphURI) {
		Logger logger = theLogger;

		Box fnd = null;//super.findGraphBox(graphURI);
		boolean madeAlready = false;
		if (fnd != null) {
			logger.trace("Found graphURI=" + graphURI + " on super.findGraphBox" + fnd);
			madeAlready = true;
		}

		BoxContext ctx = getBoxContext();
		List<Repo.GraphStat> graphStats = getAllGraphStats();
		Model m = getRepo().getNamedModel(new FreeIdent(graphURI));

		for (Repo.GraphStat gs : graphStats) {
			if (gs.graphURI.equals(graphURI)) {
				ScreenModelBox graphBox = new ScreenModelBox(gs.graphURI, m);
				ScreenGraphTrigger gt = new ScreenGraphTrigger(gs.graphURI);
				graphBox.attachTrigger(gt);
				if (!madeAlready) {
					ctx.contextualizeAndAttachChildBox(this, graphBox);
				}
				return (Box) graphBox;
			}
		}

		//		fnd = superfindGraphBox(graphURI);

		if (fnd != null) {
			logger.trace("Wierdly!?! Found graphURI=" + graphURI + " on super.findGraphBox " + fnd);
			return fnd;
		}

		logger.trace("NOT FOUND graphURI=" + graphURI + " on findGraphBox");
		return null;
	}

	@Override public void setObject(Object obj) {
		setRepo((Repo.Mutable) obj);
	}

	@Override public Object reallyGetValue() {
		return getRepo();
	}

	@Override public void reallySetValue(Object newObject) throws UnsupportedOperationException, ClassCastException {
		newObject = Utility.dref(newObject, false);
		myRepo.setReady((Repo.Mutable) newObject);
	}

	// Store		myStore;
	// public static String	myStoreConfigPath;	

	@Override public Repo getRepo() {
		return getMRepo();
	}

	public Repo.Mutable getMRepo() {
		if (myRepo == null) {
			theLogger.warn("Returning null (getRepo) from " + this);
			return null;
		}
		try {
			return myRepo.get();
		} catch (InterruptedException e) {
			throw Debuggable.reThrowable(e);
		} catch (ExecutionException e) {
			throw Debuggable.reThrowable(e);
		}
	}

	public void setRepo(Repo.Mutable repo) {
		myRepo.setReady(repo);
	}

	@Override public void mount(String configPath) {
		final String m_configPath = configPath;
		// This bonehead static method call does not allow us to construct a fancier subtype of BasicStoredMutableRepoImpl.
		myRepo = new LazySlow.GetSet<Repo.Mutable>() {
			@Override protected Repo.Mutable doGet() {
				return BasicStoredMutableRepoImpl.openBasicRepoFromConfigPath(m_configPath, getClass().getClassLoader());
			}
		};
	}

	@Override public void formatStoreIfNeeded() {
		Repo.Mutable myRepo = getMRepo();
		myRepo.formatRepoIfNeeded();
	}

	@Override public List<GraphStat> getAllGraphStats() {
		Repo.Mutable myRepo = getMRepo();
		return myRepo.getGraphStats();
	}

	@Override public String getUploadHomePath() {
		Repo.Mutable myRepo = getMRepo();
		return myRepo.getUploadHomePath();
	}

	@Override public void importGraphFromURL(String graphName, String sourceURL, boolean replaceTgtFlag) {
		Repo.Mutable myRepo = getMRepo();
		myRepo.importGraphFromURL(graphName, sourceURL, replaceTgtFlag);
	}

	@Override public String processQueryAtUrlAndProduceXml(String queryURL, ClassLoader optResourceCL) {
		Query parsedQuery = JenaArqQueryFuncs.parseQueryURL(queryURL, optResourceCL);
		Repo.Mutable myRepo = getMRepo();
		String xmlOut = myRepo.processQuery(parsedQuery, null, new JenaArqResultSetProcessor<String>() {
			@Override public String processResultSet(ResultSet rset) {
				return JenaArqQueryFuncs.dumpResultSetToXML(rset);
			}
		});
		return xmlOut;

	}

}