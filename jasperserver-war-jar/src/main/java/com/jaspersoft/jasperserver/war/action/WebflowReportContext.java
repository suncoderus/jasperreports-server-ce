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

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.ReportContext;

import org.springframework.binding.collection.MapAdaptable;
import org.springframework.webflow.execution.RequestContext;

/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id: BaseHttpServlet.java 4336 2011-05-24 13:30:34Z teodord $
 */
public class WebflowReportContext implements ReportContext
{
	
	/**
	 *
	 */
	//private ThreadLocal<RequestContext> threadLocalRequest = new ThreadLocal<RequestContext>();
	private Map<String, Object> requestParameters;
	private Map<String, Object> requestAttributes;
	private Map<String, Object> flowAttributes;
	
	private Map<String, Object> parameterValues;
	private Map flowValues;
	private String id;

	/**
	 *
	 */
	protected WebflowReportContext()
	{
		parameterValues = new HashMap<String, Object>();
		parameterValues.put(JRParameter.REPORT_CONTEXT, this);
	}

	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 *
	 */
	public String getId()
	{
		return id;
	}

	/**
	 *
	 */
	public void setRequestContext(RequestContext request)
	{
		//threadLocalRequest.set(request);
		requestParameters = copyContextMap(request.getRequestParameters());
		requestAttributes = copyContextMap(request.getRequestScope());
		//FIXME we have flowValues as well, are both required?
		flowAttributes = copyContextMap(request.getFlowScope());
	}

	protected Map<String, Object> copyContextMap(MapAdaptable contextMapAdaptable)
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (contextMapAdaptable != null)
		{
			Map<?, ?> contextMap = contextMapAdaptable.asMap();
			if (contextMap != null)
			{
				for (Map.Entry<?, ?> entry : contextMap.entrySet())
				{
					// doing this to match org.springframework.binding.collection.StringKeyedMapAdapter
					map.put(String.valueOf(entry.getKey()), entry.getValue());
				}
			}
		}
		return map;
	}
	
	/**
	 *
	 */
	public Map getFlowValues()
	{
		//return threadLocalRequest.get();
		return flowValues;
	}

	/**
	 *
	 */
	public void setFlowValues(Map flowValues)
	{
		//threadLocalRequest.set(request);
		this.flowValues = flowValues;
	}

	/**
	 *
	 */
	public Object getParameterValue(String parameterName)
	{
		Object requestParameterValue = null;

		if (requestParameters != null)
		{
			requestParameterValue = requestParameters.get(parameterName);
			if (requestParameterValue != null)
			{
				return requestParameterValue;
			}
		}
		
		if (requestAttributes != null)
		{
			requestParameterValue = requestAttributes.get(parameterName);
			if (requestParameterValue != null)
			{
				return requestParameterValue;
			}
		}
		
		if (flowAttributes != null)
		{
			requestParameterValue = flowAttributes.get(parameterName);
			if (requestParameterValue != null)
			{
				return requestParameterValue;
			}
		}
		
//		Map jiveMap = (Map)requestContext.getFlowScope().get("jive");
//		if (jiveMap != null)
//		{
//			requestParameterValue = jiveMap.get(parameterName);
//			if (requestParameterValue != null)
//			{
//				return requestParameterValue;
//			}
//		}

		if (flowValues != null)
		{
			requestParameterValue = flowValues.get(parameterName);
			if (requestParameterValue != null)
			{
				return requestParameterValue;
			}
		}
		
		return parameterValues.get(parameterName);
	}

	/**
	 *
	 */
	public boolean containsParameter(String parameterName)
	{
		boolean contains = requestParameters != null && requestParameters.containsKey(parameterName);
		return contains ? contains : parameterValues.containsKey(parameterName);
	}

	/**
	 *
	 */
	public void setParameterValue(String parameterName, Object value)
	{
		parameterValues.put(parameterName, value);
	}

	/**
	 *
	 */
	public void setParameterValues(Map<String, Object> newValues)
	{
		parameterValues.putAll(newValues);
	}

	/**
	 *
	 */
	public Map<String, Object> getParameterValues()
	{
		return parameterValues;
	}

	@Override
	public Object removeParameterValue(String parameterName)
	{
		return parameterValues.remove(parameterName);
	}

	@Override
	public void clearParameterValues()
	{
		parameterValues.clear();
		requestParameters.clear();
		requestAttributes.clear();
		flowAttributes.clear();
	}

}
