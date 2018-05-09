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

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.matchers.KeyMatcher;
import org.springframework.beans.*;
import org.springframework.beans.factory.*;
import org.springframework.beans.support.ArgumentConvertingMethodInvoker;
import org.springframework.util.*;

public class JSMethodInvokingJobDetailFactoryBean extends ArgumentConvertingMethodInvoker
    implements FactoryBean, BeanNameAware, BeanClassLoaderAware, BeanFactoryAware, InitializingBean
{

    static
    {
        try
        {
            jobDetailImplClass = Class.forName("org.quartz.impl.JobDetailImpl");
        }
        catch(ClassNotFoundException _ex)
        {
            jobDetailImplClass = null;
        }
    }

    private Scheduler scheduler;

    private static Class jobDetailImplClass;
    private String name;
    private String group;
    private boolean concurrent;
    private String targetBeanName;
    private String jobListenerNames[];
    private String beanName;
    private ClassLoader beanClassLoader;
    private BeanFactory beanFactory;
    private JobDetail jobDetail;

    

	public Scheduler getScheduler()
      throws JobExecutionException
    {
        if (scheduler == null) {
            throw new JobExecutionException("Fatal Error:  bean '"+this.getClass().getName()+"' does not have its QuartzScheduler bean set and it is required to be.");
        }
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}


    public JSMethodInvokingJobDetailFactoryBean()
    {
        group = "DEFAULT";
        concurrent = true;
        beanClassLoader = ClassUtils.getDefaultClassLoader();
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setGroup(String group)
    {
        this.group = group;
    }

    public void setConcurrent(boolean concurrent)
    {
        this.concurrent = concurrent;
    }

    public void setTargetBeanName(String targetBeanName)
    {
        this.targetBeanName = targetBeanName;
    }

    public void setJobListenerNames(String names[])
    {
        jobListenerNames = names;
    }

    public void setBeanName(String beanName)
    {
        this.beanName = beanName;
    }

    public void setBeanClassLoader(ClassLoader classLoader)
    {
        beanClassLoader = classLoader;
    }

    public void setBeanFactory(BeanFactory beanFactory)
    {
        this.beanFactory = beanFactory;
    }

    protected Class resolveClassName(String className)
        throws ClassNotFoundException
    {
        return ClassUtils.forName(className, beanClassLoader);
    }

    public void afterPropertiesSet()
        throws ClassNotFoundException, NoSuchMethodException
    {
        prepare();
        String name = this.name == null ? beanName : this.name;
        Class jobClass = concurrent ? com.jaspersoft.jasperserver.api.engine.scheduling.quartz.JSMethodInvokingJobDetailFactoryBean.MethodInvokingJob.class : com.jaspersoft.jasperserver.api.engine.scheduling.quartz.JSMethodInvokingJobDetailFactoryBean.StatefulMethodInvokingJob.class;
        if(jobDetailImplClass != null)
        {
            jobDetail = (JobDetail)BeanUtils.instantiate(jobDetailImplClass);
            BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(jobDetail);
            bw.setPropertyValue("name", name);
            bw.setPropertyValue("group", group);
            bw.setPropertyValue("jobClass", jobClass);
            // 2012-01-25   thorick:  no more durability in Quartz 2.1.2 ?
            bw.setPropertyValue("durability", Boolean.valueOf(true));
            ((JobDataMap)bw.getPropertyValue("jobDataMap")).put("methodInvoker", this);
        }
        else {
            jobDetail = new JobDetailImpl(name, group, jobClass);
            // 2012-01-25  thorick:  there is no more volatility in Quartz 2.1.2
            //jobDetail.setVolatility(true);

            if (!(jobDetail instanceof JobDetailImpl))
                throw new RuntimeException("Expected JobDetail to be an instance of '"+JobDetailImpl.class+
                        "' but instead we got '"+jobDetail.getClass().getName()+"'");
            ((JobDetailImpl)jobDetail).setDurability(true);
            jobDetail.getJobDataMap().put("methodInvoker", this);
        }
        if(jobListenerNames != null)
        {
            String as[];
            int j = (as = jobListenerNames).length;
            for(int i = 0; i < j; i++)
            {
                String jobListenerName = as[i];
                if(jobDetailImplClass != null)
                    throw new IllegalStateException("Non-global JobListeners not supported on Quartz 2 - manually register a Matcher against the Quartz ListenerManager instead");

                JobKey jk = jobDetail.getKey();
                Matcher<JobKey> matcher = KeyMatcher.keyEquals(jk);
                try {
                  getScheduler().getListenerManager().addJobListenerMatcher(jobListenerName, matcher);
                } catch (org.quartz.SchedulerException e) {
                  throw new RuntimeException("Error adding Quartz Trigger Listener: "+e.getMessage());
                }

                //jobDetail.addJobListener(jobListenerName);
            }

        }
        postProcessJobDetail(jobDetail);
    }

    protected void postProcessJobDetail(JobDetail jobdetail)
    {
    }

    public Class getTargetClass()
    {
        Class targetClass = super.getTargetClass();
        if(targetClass == null && targetBeanName != null)
        {
            Assert.state(beanFactory != null, "BeanFactory must be set when using 'targetBeanName'");
            targetClass = beanFactory.getType(targetBeanName);
        }
        return targetClass;
    }

    public Object getTargetObject()
    {
        Object targetObject = super.getTargetObject();
        if(targetObject == null && targetBeanName != null)
        {
            Assert.state(beanFactory != null, "BeanFactory must be set when using 'targetBeanName'");
            targetObject = beanFactory.getBean(targetBeanName);
        }
        return targetObject;
    }

    public JobDetail getObject()
    {
        return jobDetail;
    }

    public Class getObjectType()
    {
        return jobDetail == null ? org.quartz.impl.JobDetailImpl.class : jobDetail.getClass();
    }

    public boolean isSingleton()
    {
        return true;
    }

    /*   todo:  remove
    public Object getObject()
        throws Exception
    {
        return getObject();
    }
    */


    public static class MethodInvokingJob extends JSQuartzJobBean
    {
        public void setMethodInvoker(MethodInvoker methodInvoker)
        {
            this.methodInvoker = methodInvoker;
        }

        protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException
        {
            try
            {
                context.setResult(methodInvoker.invoke());
            }
            catch(InvocationTargetException ex)
            {
                if(ex.getTargetException() instanceof JobExecutionException)
                    throw (JobExecutionException)ex.getTargetException();
                else
                    throw new JSJobMethodInvocationFailedException(methodInvoker, ex.getTargetException());
            }
            catch(Exception ex)
            {
                throw new JSJobMethodInvocationFailedException(methodInvoker, ex);
            }
        }

        protected static final Log logger = LogFactory.getLog(JSMethodInvokingJobDetailFactoryBean.MethodInvokingJob.class);
        private MethodInvoker methodInvoker;


        public MethodInvokingJob()
        {
        }
    }

    public static class StatefulMethodInvokingJob extends MethodInvokingJob
        implements StatefulJob
    {

        public StatefulMethodInvokingJob()
        {
        }
    }


}
