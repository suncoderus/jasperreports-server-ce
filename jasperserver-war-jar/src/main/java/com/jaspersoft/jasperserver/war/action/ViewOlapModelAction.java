/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.war.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService;
import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.tags.OlapModelProxy;

/**
 * 
 * ViewOlapModelAction provides actions to display JPivot olap view
 *
 * @author jshih
 */
public class ViewOlapModelAction extends RepositoryAction {
	protected final Logger logger = Logger.getLogger(getClass());
	public static final String OLAPUNIT_ATTR = "olapUnit";
	private OlapConnectionService olapConnectionService;
	private RepositoryService repository;
	private RequestContext requestContext;
	private OlapConnectionService olapConnection;
	private String olapUnitName;
	private OlapUnit olapUnit;
	private OlapModel olapModel;
	
    /**
 	 * initAction performs the initialization for the olap model controller
     * 
     * @param context
     * @return
     */
	public Event initOlapModel(RequestContext context) 
	{
		String viewUri = (String) context.getFlowScope().get("name");
		ServletExternalContext sec = (ServletExternalContext) context.getExternalContext();
		HttpServletRequest req = (HttpServletRequest) sec.getNativeRequest();
		init(viewUri, req);
		return success();
	}

	/**
	 * From OlapModelController
	 * 
	 * @param req
	 */
	private void init(String viewUri, HttpServletRequest req) {
		if (viewUri == null || viewUri.length() == 0) {
			throw new JSException("jsexception.no.olap.model.name");
		}
		logger.debug("Viewing OLAP Model: " + viewUri);
		
		req.setAttribute("name", viewUri);
		
		HttpSession sess = req.getSession();
		Map olapModels = (Map) sess.getAttribute("olapModels");
		if (olapModels == null) {
			olapModels = new HashMap();
			sess.setAttribute("olapModels", olapModels);
		}
		
		OlapSessionState sessionState = (OlapSessionState) olapModels.get(viewUri);
		if (sessionState == null) {
			sessionState = getOlapSession(viewUri, sess);
			olapModels.put(viewUri, sessionState);
		}
		req.setAttribute("olapModel", sessionState.getOlapModel());
		req.setAttribute("olapSession", sessionState);
		
		// Because WCF is so behind the times, we have to also set the 
		// session attribute by name
		
		sess.setAttribute("olapModel", sessionState.getOlapModel());
	}

	/**
	 * From OlapModelController
	 * 
	 * @param viewUri
	 * @param sess
	 * @return
	 */
	protected OlapSessionState getOlapSession(String viewUri, HttpSession sess) {
		
		logger.debug("Setting OlapModel for " + viewUri);
		
		com.tonbeller.wcf.controller.RequestContext context = com.tonbeller.wcf.controller.RequestContext.instance();
		ExecutionContextImpl executionContext = new ExecutionContextImpl();
		
		OlapUnit olapUnit = (OlapUnit) getRepository().getResource(executionContext,
				viewUri);
		
		if (olapUnit == null) {
			throw new JSException("jsexception.no.olap.model.retrieved");
		}
		
		OlapModel model = getOlapConnectionService().createOlapModel(executionContext, olapUnit);
		
		if (model == null) {
			throw new JSException("jsexception.no.olap.model.created.for", new Object[] {viewUri});
		}
			
		model = (OlapModel) model.getTopDecorator();
		model.setLocale(context.getLocale());
		model.setServletContext(context.getSession().getServletContext());
	    model.setID(viewUri);

	    model.setServletContext(sess.getServletContext());
/*
	    ClickableExtension ext = (ClickableExtension) model.getExtension(ClickableExtension.ID);
	    if (ext == null) {
	    	ext = new ClickableExtensionImpl();
	        model.addExtension(ext);
	    }
	    ext.setClickables(clickables);
*/
	    // stackMode
	    
	    OlapModelProxy omp = OlapModelProxy.instance(viewUri, sess, false);
/*	    if (queryName != null)
	    	omp.initializeAndShow(queryName, model);
	    else
*/	    
	    try {
			omp.initializeAndShow(viewUri, model);
		} catch (Exception e) {
			throw new JSException(e);
		}

	    return new OlapSessionState(omp, olapUnit);
	}
	
	/**
	 * From OlapModelController
	 * 
	 * @author jshih
	 *
	 */
	public class OlapSessionState {
		private OlapModel olapModel;
		private OlapUnit olapUnit;
		
		public OlapSessionState(OlapModel olapModel, OlapUnit olapUnit) {
			this.olapModel = olapModel;
			this.olapUnit = olapUnit;
		}
		
		/**
		 * @return Returns the olapModel.
		 */
		public OlapModel getOlapModel() {
			return olapModel;
		}
		/**
		 * @param olapModel The olapModel to set.
		 */
		public void setOlapModel(OlapModel olapModel) {
			this.olapModel = olapModel;
		}
		/**
		 * @return Returns the olapUnit.
		 */
		public OlapUnit getOlapUnit() {
			return olapUnit;
		}
		/**
		 * @param olapUnit The olapUnit to set.
		 */
		public void setOlapUnit(OlapUnit olapUnit) {
			this.olapUnit = olapUnit;
		}
	}
	
    /**
	 * getOlapUnit retrieves the uri of specified olap unit
	 *  
	 * @param requestContext
	 * @return success or error
	 */
	public Event getOlapUnit(RequestContext requestContext) {
		Event result = success();
		ExecutionContextImpl executionContext = new ExecutionContextImpl();
		this.requestContext = requestContext;
		if ((olapUnitName = (String) requestContext.getFlowScope().get(
				OLAPUNIT_ATTR)) == null) {
			result = error();
		} else if ((olapUnit = (OlapUnit) repository.getResource(
				executionContext, olapUnitName)) == null) {
			result = error();
		}

		return result;
	}
	
	/**
	 * createOlapModel action 
	 * 
	 * @param requestContext
	 * @return success or error
	 */
    public Event createOlapModel(RequestContext requestContext) 
    {
		Event result = success();
		ExecutionContextImpl executionContext = new ExecutionContextImpl();
		olapModel = olapConnection.createOlapModel(executionContext, olapUnit);
		if (olapModel == null) {
			result = error();
		}
		return result;
    }

    /**
	 * displayOlapModel action
	 * 
	 * @param requestContext
	 * @return
	 */
    public Event displayOlapModel(RequestContext requestContext) {
		Event result = success();

		requestContext.getRequestScope().put("olapUnitName", olapUnitName);

		return result;
	}

	/**
	 * @return Returns the olapConnectionService.
	 */
	public OlapConnectionService getOlapConnectionService() {
		return olapConnectionService;
	}

	/**
	 * @param olapConnectionService The olapConnectionService to set.
	 */
	public void setOlapConnectionService(OlapConnectionService olapConnectionService) {
		this.olapConnectionService = olapConnectionService;
	}
	
	public RepositoryService getRepository() {
		return repository;
	}

	public void setRepository(RepositoryService repository) {
		this.repository = repository;
	}

    public OlapConnectionService getOlapConnection() {
        return olapConnection;
    }
                
    public void setOlapConnection(OlapConnectionService olapConnection) {
        this.olapConnection = olapConnection;
    }
    
    public OlapModel getOlapModel() {
        return olapModel;
    }
                
    public void setOlapModel(OlapModel olapModel) {
        this.olapModel = olapModel;
    }

}
