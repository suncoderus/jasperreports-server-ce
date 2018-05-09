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
package com.jaspersoft.jasperserver.api.engine.scheduling.quartz;

import java.lang.reflect.Method;
import java.util.Map;
import org.quartz.*;
import org.springframework.beans.*;
import org.springframework.util.ReflectionUtils;

public abstract class JSQuartzJobBean implements Job {
    public JSQuartzJobBean()
    {
    }

    public final void execute(JobExecutionContext context)
        throws JobExecutionException
    {
        try
        {
            Scheduler scheduler = (Scheduler)ReflectionUtils.invokeMethod(getSchedulerMethod, context);
            Map mergedJobDataMap = (Map)ReflectionUtils.invokeMethod(getMergedJobDataMapMethod, context);
            BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
            MutablePropertyValues pvs = new MutablePropertyValues();
            pvs.addPropertyValues(scheduler.getContext());
            pvs.addPropertyValues(mergedJobDataMap);
            bw.setPropertyValues(pvs, true);
        }
        catch(SchedulerException ex)
        {
            throw new JobExecutionException(ex);
        }
        executeInternal(context);
    }

    protected abstract void executeInternal(JobExecutionContext jobexecutioncontext)
        throws JobExecutionException;

    private static final Method getSchedulerMethod;
    private static final Method getMergedJobDataMapMethod;

    static
    {
        try
        {
            getSchedulerMethod = org.quartz.impl.JobExecutionContextImpl.class.getMethod("getScheduler", new Class[0]);
            getMergedJobDataMapMethod = org.quartz.impl.JobExecutionContextImpl.class.getMethod("getMergedJobDataMap", new Class[0]);
        }
        catch(NoSuchMethodException ex)
        {
            throw new IllegalStateException((new StringBuilder("Incompatible Quartz API: ")).append(ex).toString());
        }
    }

}
