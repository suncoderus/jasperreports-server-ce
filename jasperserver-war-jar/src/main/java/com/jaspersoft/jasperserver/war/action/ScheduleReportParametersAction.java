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

import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJob;
import org.json.JSONObject;
import org.springframework.webflow.core.collection.AttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ScheduleReportParametersAction extends ReportParametersAction {


    private String jobFormObjectName;
    private Class jobFormObjectClass;
    private ScopeType jobFormObjectScope;



    private List<String> doNotOverWriteList = null;
  {
    doNotOverWriteList = new java.util.ArrayList<String>(1);
    doNotOverWriteList.add(net.sf.jasperreports.engine.JRParameter.REPORT_TIME_ZONE);
  }

	public Class getJobFormObjectClass() {
		return jobFormObjectClass;
	}

	public void setJobFormObjectClass(Class jobFormObjectClass) {
		this.jobFormObjectClass = jobFormObjectClass;
	}

	public String getJobFormObjectName() {
		return jobFormObjectName;
	}

	public void setJobFormObjectName(String jobFormObjectName) {
		this.jobFormObjectName = jobFormObjectName;
	}

	public ScopeType getJobFormObjectScope() {
		return jobFormObjectScope;
	}

	public void setJobFormObjectScope(ScopeType jobFormObjectScope) {
		this.jobFormObjectScope = jobFormObjectScope;
	}

	public Event checkForParameters(RequestContext context) {
        super.checkForParams(context);

        // the report only has hidden controls
        if (hasInputControls(context) && !hasVisibleInputControls(context)) {
            // set the default values
            Map parameterValues = getReportParameterValuesFromRequest(context);
            ReportJob reportJob = getReportJob(context);

            // 2012-05-17  thorick chow
            //             http://bugzilla.jaspersoft.com/show_bug.cgi?id=26640
            //             Certain existing Parameters set in the ReportJob
            //             take precedence over InputControls.
            //             As of 4.7  REPORT_TIME_ZONE is one of them
            //             as part of the 'set Report TimeZone' Scheduled Report feature.
            //             Users can set the TimeZone of the executing Report (not the executing Server)
            //             directly via a ReportJob's REPORT_TIME_ZONE
            //             Thus we don't let InputControls overwrite this value if it is already set

            setParametersDoNotOverwrite(reportJob, parameterValues, doNotOverWriteList);
        }

        // Setting thread repository context to be able resolve ICs labels.
        setupThreadRepositoryContext(getExecutionContext(context));

        return success();
	}

	public Event setParameterValues(RequestContext context) {
        Map<String, Object> typedValues = getReportParameterValuesFromRequest(context);
        ReportJob reportJob = getReportJob(context);
		reportJob.getSource().setParameters(typedValues);

        return success();
	}

    public Event prepareParameterValues(RequestContext context) {
        ReportJob reportJob = getReportJob(context);
        Map<String, Object> jobParameters = reportJob.getSource().getParameters();
        JSONObject jsonObject = new JSONObject(prepareParameterValues(context, jobParameters));
        context.getFlashScope().put(REPORT_PARAMETER_VALUES, jsonObject);
        return success();
    }

    protected Map<String, String[]> prepareParameterValues(RequestContext context, Map<String, Object> parameters) {
        return formatReportParameterValues(getReportURI(context),parameters);
    }

/*	protected InputValueProvider initialValueProvider(RequestContext context) {
		InputValueProvider provider = baseJobValueProvider(context);
		
		ReportJob reportJob = getReportJob(context);
		Map paramValues = reportJob.getSource().getParametersMap();
		if (paramValues != null && !paramValues.isEmpty()) {
			provider = new MapValueProvider(paramValues, provider);
		}

		return provider;
	}*/

/*	protected InputValueProvider baseJobValueProvider(RequestContext context) {
		return defaultValuesProvider(context);
	}*/
	
	protected ReportJob getReportJob(RequestContext context) {
		AttributeMap scope = getJobFormObjectScope().getScope(context);
		return (ReportJob) scope.getRequired(getJobFormObjectName(), getJobFormObjectClass());
	}

  /**
   *
   * In some cases  we don't want Input List ReportJob Parameters members to overwrite
   * SOME specified existing members.
   *
   * This method takes care of that.
   *
   * @param reportJob
   * @param sourceParams
   */
  private void setParametersDoNotOverwrite(ReportJob reportJob, Map<String, Object> sourceParams,
                                           List<String> doNotOverWrite) {
    if (sourceParams == null) return;
    if (reportJob == null)  return;
    Map<String, Object> existingParams = reportJob.getSource().getParameters();
    if (existingParams == null){
        existingParams = new java.util.HashMap<String, Object>();
        reportJob.getSource().setParameters(existingParams);
    }
    Iterator<String> it = sourceParams.keySet().iterator();
    while (it.hasNext()) {
      String key = it.next();
      if (doNotOverWrite.contains(key)) {
        if (existingParams.containsKey(key)) continue;
      }
      existingParams.put(key, sourceParams.get(key));
    }
  }

}
