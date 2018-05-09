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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.util.CharacterEncodingProvider;
import com.jaspersoft.jasperserver.api.common.util.TimeZoneContextHolder;

import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameter;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameters;
import net.sf.jasperreports.engine.export.JRHyperlinkProducer;
import net.sf.jasperreports.engine.util.Pair;
import net.sf.jasperreports.types.date.DateRange;
import net.sf.jasperreports.types.date.DateRangeExpression;
import net.sf.jasperreports.types.date.TimestampRange;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public abstract class BaseReportExecutionHyperlinkProducerFactory implements Serializable {

	protected final static Log log = LogFactory.getLog(BaseReportExecutionHyperlinkProducerFactory.class);

	private HyperlinkParameterFormatter dateFormatter;
	private CharacterEncodingProvider encodingProvider;
    private DateFormatProvider dateFormatProvider;
	private String flowControllerMapping;
	private String reportExecutionFlowId;
	private String hyperlinkParameterReportUnit;
	private String urlParameterReportUnit;
	private String defaultOutputChannel;
	private String hyperlinkParameterOutputChannel;
	private String urlParameterOutputChannel;
    private String hyperlinkParameterPageIndex;
    private String urlParameterPageIndex;
    private String hyperlinkParameterAnchor;
    private String urlParameterAnchor;

	public abstract class BaseHyperlinkProducer implements JRHyperlinkProducer, Serializable {
		
		public BaseHyperlinkProducer() {
		}

		public String getHyperlink(JRPrintHyperlink hyperlink) {
			StringBuffer sb = new StringBuffer();
			appendHyperlinkStart(hyperlink, sb);
			sb.append(getFlowControllerMapping());
			sb.append("?_flowId=");
			sb.append(getReportExecutionFlowId());

			URLParameters urlParams = new URLParameters();
			JRPrintHyperlinkParameters parameters = hyperlink.getHyperlinkParameters();
			if (parameters != null) {
				appendParameters(urlParams, parameters);
			}
			
			appendAdditionalParameters(hyperlink, urlParams);
			urlParams.write(sb);

			return sb.toString();
		}

		protected void appendParameters(URLParameters urlParams, JRPrintHyperlinkParameters parameters) {
			boolean outputChannelSet = false;
			for (Iterator it = parameters.getParameters().iterator(); it.hasNext();) {
				JRPrintHyperlinkParameter parameter = (JRPrintHyperlinkParameter) it.next();
				if (parameter.getName().equals(getHyperlinkParameterReportUnit())) {
					String paramReportURI = getParameterReportURI((String) 
							parameter.getValue());
					urlParams.appendParameter(getUrlParameterReportUnit(), paramReportURI);
				} else if (parameter.getName().equals(getHyperlinkParameterOutputChannel())) {
					urlParams.appendParameter(getUrlParameterOutputChannel(), (String) parameter.getValue());
					outputChannelSet = true;
                } else if (parameter.getName().equals(getHyperlinkParameterPageIndex())) {
                    appendParameter(urlParams, getUrlParameterPageIndex(), parameter);
                } else if (parameter.getName().equals(getHyperlinkParameterAnchor())) {
                	urlParams.appendParameter(getUrlParameterAnchor(), (String) parameter.getValue());
				} else {
					appendParameter(urlParams, parameter);
				}
			}
			if (!outputChannelSet && getDefaultOutputChannel() != null)
			{
				urlParams.appendParameter(getUrlParameterOutputChannel(), getDefaultOutputChannel());
			}
		}
		
		protected String getParameterReportURI(String reportURI) {
			// by default, just return the URI
			return reportURI;
		}
		
		protected String encode(String text) {
			try {
				String encoding = getEncodingProvider().getCharacterEncoding();
				return URLEncoder.encode(text, encoding);
			} catch (UnsupportedEncodingException e) {
				throw new JSExceptionWrapper(e);
			}
		}

        protected void appendParameter(URLParameters urlParams, JRPrintHyperlinkParameter parameter) {
            appendParameter(urlParams, parameter.getName(), parameter);
        }

        protected void appendParameter(URLParameters urlParams, String paramName,
                JRPrintHyperlinkParameter parameter) {
            String valueClassName = parameter.getValueClass();
            Class<?> valueClass = loadClass(valueClassName);
            Object value = parameter.getValue();
            if (valueClass.isArray()) {
            	if (value != null) {
                	appendMultiParameter(urlParams, paramName, new ArrayIterator(value));
            	}
            } else if (Collection.class.isAssignableFrom(valueClass)) {
            	if (value != null) {
                	appendMultiParameter(urlParams, paramName, ((Collection<?>) value).iterator());
            	}
            } else {
            	appendSingleParameter(urlParams, paramName, valueClass, value);
            }
        }

        protected void appendMultiParameter(URLParameters urlParams, String paramName, Iterator<?> values) {
        	while (values.hasNext()) {
        		Object value = values.next();
        		Class<?> valueClass = value == null ? String.class : value.getClass();
        		appendSingleParameter(urlParams, paramName, valueClass, value);
        	}
        }
        
        protected void appendSingleParameter(URLParameters params, String paramName, 
        		Class<?> valueClass, Object value) {
            if (valueClass.equals(String.class)) {
            	params.appendParameter(paramName, (String) value);
            } else if (valueClass.equals(Boolean.class)) {
                if (value != null && ((Boolean) value).booleanValue()) {
                	params.appendParameter(paramName, "true");
                }
            } else if (Number.class.isAssignableFrom(valueClass)) {
                if (value != null) {
                	params.appendParameter(paramName, value.toString());
                }
            } else if (Date.class.isAssignableFrom(valueClass)) {
                if (value != null) {
                    String formattedValue = getDateFormatter().format(value);
                    params.appendParameter(paramName, formattedValue);
                }
            } else if (DateRangeExpression.class.isAssignableFrom(valueClass)) {
                if (value != null) {
                    String expression = ((DateRangeExpression) value).getExpression();
                    params.appendParameter(paramName, expression);
                }
            } else if (TimestampRange.class.isAssignableFrom(valueClass)) {
                if (value != null) {
                    //copied from TimestampRangeDataConverter
                    DateFormat datetimeFormat = dateFormatProvider.getDatetimeFormat();
                    datetimeFormat.setTimeZone(TimeZoneContextHolder.getTimeZone());
                    
                    Timestamp startDate = ((TimestampRange) value).getStart();
                    String formattedValue = datetimeFormat.format(startDate);
                    params.appendParameter(paramName, formattedValue);
                }
            } else if (DateRange.class.isAssignableFrom(valueClass)) {
                if (value != null) {
                    //copied from DateRangeDataConverter
                    Date startDate = ((DateRange) value).getStart();
                    String formattedValue = dateFormatProvider.getDateFormat().format(startDate);
                    params.appendParameter(paramName, formattedValue);
                }
            } else {
            	log.warn("Unknown hyperlink parameter type " + valueClass);
            }
        }
        
		protected Class loadClass(String valueClassName) {
			try {
				return Class.forName(valueClassName);
			} catch (ClassNotFoundException e) {
				log.error("Parameter class \"" + valueClassName + "\" not found", e);
				throw new JSExceptionWrapper(e);
			}
		}
		
		protected abstract void appendHyperlinkStart(JRPrintHyperlink hyperlink, StringBuffer sb);
		
		protected abstract void appendAdditionalParameters(JRPrintHyperlink hyperlink, URLParameters urlParams);
		
		protected class URLParameters {
			private final LinkedHashSet<Pair<String, String>> parameters = 
					new LinkedHashSet<Pair<String,String>>();
			
			public void appendParameter(String name, String value) {
				Pair<String, String> parameter = new Pair<String, String>(name, value);
				// checking if we already have the same param with the same value in order to preserve the fix for bug 24723.
				// note however that this prevents passing collections with repeating values, which might be a valid requirement 
				// in some (uncommon) scenarios.
				if (!parameters.add(parameter)) {
					if (log.isDebugEnabled()) {
						log.debug("parameter " + name + " with value " + value + " already present");
					}
				}
			}
			
			public void write(StringBuffer sb) {
				for (Pair<String, String> param : parameters) {
	                sb.append('&');
	                sb.append(encode(param.first()));

	                if (param.second() != null) {
	                    sb.append('=');
	                    sb.append(encode(param.second()));
	                }
				}
			}
			
			public boolean hasParameter(String name) {
				for (Pair<String, String> param : parameters) {
					if (param.first().equals(name)) {
						return true;
					}
				}
				return false;
			}
		}
	}

	public BaseReportExecutionHyperlinkProducerFactory() {
	}

	public HyperlinkParameterFormatter getDateFormatter() {
		return dateFormatter;
	}

	public void setDateFormatter(HyperlinkParameterFormatter dateFormatter) {
		this.dateFormatter = dateFormatter;
	}

	public String getFlowControllerMapping() {
		return flowControllerMapping;
	}

	public void setFlowControllerMapping(String flowControllerMapping) {
		this.flowControllerMapping = flowControllerMapping;
	}

	public String getReportExecutionFlowId() {
		return reportExecutionFlowId;
	}

	public void setReportExecutionFlowId(String reportExecutionFlowId) {
		this.reportExecutionFlowId = reportExecutionFlowId;
	}

	public String getHyperlinkParameterReportUnit() {
		return hyperlinkParameterReportUnit;
	}

	public void setHyperlinkParameterReportUnit(
			String hyperlinkParameterReportUnitURI) {
		this.hyperlinkParameterReportUnit = hyperlinkParameterReportUnitURI;
	}

	public String getUrlParameterReportUnit() {
		return urlParameterReportUnit;
	}

	public void setUrlParameterReportUnit(String urlParameterReportUnitURI) {
		this.urlParameterReportUnit = urlParameterReportUnitURI;
	}

	public String getDefaultOutputChannel() {
		return defaultOutputChannel;
	}

	public void setDefaultOutputChannel(
			String defaultOutputChannel) {
		this.defaultOutputChannel = defaultOutputChannel;
	}

	public String getHyperlinkParameterOutputChannel() {
		return hyperlinkParameterOutputChannel;
	}

	public void setHyperlinkParameterOutputChannel(
			String hyperlinkParameterOutputChannel) {
		this.hyperlinkParameterOutputChannel = hyperlinkParameterOutputChannel;
	}

	public String getUrlParameterOutputChannel() {
		return urlParameterOutputChannel;
	}

	public void setUrlParameterOutputChannel(String urlParameterOutputChannel) {
		this.urlParameterOutputChannel = urlParameterOutputChannel;
	}

	public CharacterEncodingProvider getEncodingProvider() {
		return encodingProvider;
	}

	public void setEncodingProvider(CharacterEncodingProvider encodingProvider) {
		this.encodingProvider = encodingProvider;
	}

    public String getHyperlinkParameterPageIndex() {
        return hyperlinkParameterPageIndex;
    }

	public DateFormatProvider getDateFormatProvider() {
		return dateFormatProvider;
	}

	public void setDateFormatProvider(DateFormatProvider dateFormatProvider) {
		this.dateFormatProvider = dateFormatProvider;
	}

    public void setHyperlinkParameterPageIndex(String hyperlinkParameterPageIndex) {
        this.hyperlinkParameterPageIndex = hyperlinkParameterPageIndex;
    }

    public String getUrlParameterPageIndex() {
        return urlParameterPageIndex;
    }

    public void setUrlParameterPageIndex(String urlParameterPageIndex) {
        this.urlParameterPageIndex = urlParameterPageIndex;
    }

    public String getHyperlinkParameterAnchor() {
        return hyperlinkParameterAnchor;
    }

    public void setHyperlinkParameterAnchor(String hyperlinkParameterAnchor) {
        this.hyperlinkParameterAnchor = hyperlinkParameterAnchor;
    }

    public String getUrlParameterAnchor() {
        return urlParameterAnchor;
    }

    public void setUrlParameterAnchor(String urlParameterAnchor) {
        this.urlParameterAnchor = urlParameterAnchor;
    }

}
