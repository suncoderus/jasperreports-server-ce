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

import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Constants;
import org.springframework.scheduling.quartz.JobDetailAwareTrigger;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Date;
import java.util.TimeZone;

import com.jaspersoft.jasperserver.api.JSException;

public class JSCronTriggerBean extends CronTriggerImpl
    implements JobDetailAwareTrigger, BeanNameAware, InitializingBean {

    public JSCronTriggerBean()
     {
     }

     public void setJobDataAsMap(Map jobDataAsMap)
     {
         getJobDataMap().putAll(jobDataAsMap);
     }

     public void setMisfireInstructionName(String constantName)
     {
         // 2012-01-03 thorick:  The set of Misfire Instructions are defined as public static final int
         //                      in the Quartz Trigger and CronTrigger interfaces.
         setMisfireInstruction(constants.asNumber(constantName).intValue());
     }

     public void setTriggerListenerNames(String names[])
     {
         for(int i = 0; i < names.length; i++)
         // 2012-01-23 thorick:  this method is from Quartz 1.5.1
         //            TriggerListeners are dealt with via ListenerManagers in Quartz 2.1.1
         //            To date I do not believe that any users of this Bean will attempt
         //             to set TriggerListenerNames
         //            If we find out that we need to, then this will need to be implemented.
         //             Probably this will be done by specifying the Listener in the Bean config
         //             have Spring instantiate and set the Listener instances on this instance
         //             then register them with the ListenerManager using the TriggerKey of this Trigger.
         //
             throw new JSException("Error !  unhandled setTriggerListenerNames from old Quartz 1.5.  Need to port to Quartz 2.1.2");

     }

     public void setStartDelay(long startDelay)
     {
         Assert.state(startDelay >= 0L, "Start delay cannot be negative.");
         this.startDelay = startDelay;
     }

     public void setJobDetail(JobDetail jobDetail)
     {
         this.jobDetail = jobDetail;
     }

     public JobDetail getJobDetail()
     {
         return jobDetail;
     }

     public void setBeanName(String beanName)
     {
         this.beanName = beanName;
     }

     public void afterPropertiesSet()
         throws Exception
     {
         if(startDelay > 0L)
             setStartTime(new Date(System.currentTimeMillis() + startDelay));
         if(getName() == null)
             setName(beanName);
         if(getGroup() == null)
             setGroup("DEFAULT");
         if(getStartTime() == null)
             setStartTime(new Date());
         if(getTimeZone() == null)
             setTimeZone(TimeZone.getDefault());
         if(jobDetail != null)
         {
             setJobKey(jobDetail.getKey());
             //setJobName(jobDetail.getName());
             //setJobGroup(jobDetail.getGroup());
         }
     }

     private static final Constants constants = new Constants(org.quartz.impl.triggers.CronTriggerImpl.class);
     private JobDetail jobDetail;
     private String beanName;
     private long startDelay;


    
}
