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
package com.jaspersoft.jasperserver.api.engine.jasperreports.domain.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.iterators.ReverseListIterator;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class CompositeReportExecutionListener implements ReportExecutionListener {

	private static final CompositeReportExecutionListener EMPTY = 
			new CompositeReportExecutionListener(Collections.<ReportExecutionListener>emptyList());

	public static ReportExecutionListener asListener(List<ReportExecutionListener> listeners) {
		if (listeners == null || listeners.isEmpty()) {
			return EMPTY;
		}
		
		if (listeners.size() == 1) {
			return listeners.get(0);
		}
		
		return new CompositeReportExecutionListener(listeners);
	}
	
	public static ReportExecutionListener combine(
			ReportExecutionListener first, ReportExecutionListener second) {
		List<ReportExecutionListener> firstList = list(first);
		List<ReportExecutionListener> secondList = list(second);
		
		ReportExecutionListener combined;
		if (firstList.isEmpty()) {
			if (secondList.isEmpty()) {
				combined = EMPTY;
			} else {
				combined = second;
			}
		} else if (secondList.isEmpty()) {
			combined = first;
		} else {
			List<ReportExecutionListener> list = 
					new ArrayList<ReportExecutionListener>(firstList.size() + secondList.size());
			list.addAll(firstList);
			list.addAll(secondList);
			combined = new CompositeReportExecutionListener(list);
		}
		return combined;
	}
	
	private static List<ReportExecutionListener> list(ReportExecutionListener listener) {
		if (listener == null) {
			return Collections.<ReportExecutionListener>emptyList();
		}
		
		if (listener instanceof CompositeReportExecutionListener) {
			return ((CompositeReportExecutionListener) listener).listeners;
		}
		
		return Collections.singletonList(listener);
	}

	private final List<ReportExecutionListener> listeners;

	public CompositeReportExecutionListener(List<ReportExecutionListener> listeners) {
		this.listeners = listeners;
	}

	public void init() {
		for (ReportExecutionListener listener : listeners) {
			listener.init();
		}
	}

	public void start() {
		for (ReportExecutionListener listener : listeners) {
			listener.start();
		}
	}
	public void end(boolean success, long time) {
		for (Iterator<?> it = new ReverseListIterator(listeners); it.hasNext();) {
			ReportExecutionListener listener = (ReportExecutionListener) it.next();
			listener.end(success,time);
		}
	}

	public void end(boolean success) {
		end(success,0);
	}
}
