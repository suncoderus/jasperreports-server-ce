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

package com.jaspersoft.jasperserver.api.engine.common.service.impl;

/**
 * Created by IntelliJ IDEA.
 * User: Papanii
 * Date: Feb 10, 2010
 * Time: 1:09:14 PM
 */
import com.jaspersoft.jasperserver.api.engine.common.service.ActionModelService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


/**
 * This class is responsible for creating the action model data for each menu type.
 * Note: This is a singleton. We only need one instance of this class since all menu data construction is done at
 * startup
 */
public class ActionModelServiceImpl implements ActionModelService, InitializingBean, ResourceLoaderAware {
    private static final Log log = LogFactory.getLog(ActionModelServiceImpl.class);
    private static String actionModelBaseURI;
    private static Map<String, Document> actionModelMap;
    private static List<String> actionModelTypes;
    private static final String XML_FILE_EXT = ".xml";
    private static ActionModelServiceImpl _singleTon;
    private ResourceLoader resourceLoader;

    //Class constructor
    private ActionModelServiceImpl(){
        actionModelMap = new HashMap<String, Document>();
        actionModelTypes = new ArrayList<String>();
        log.debug("Creating ActionModelService object.");
    }

    /**
     * Singleton getter method
     * @return singleton reference
     */
    public synchronized static ActionModelServiceImpl getInstance(){
        return ActionModelServiceImpl._singleTon;
    }


    /**
     * Method called from spring initializing bean
     * @throws Exception
     */
    public void afterPropertiesSet() throws Exception {
        _singleTon = this;
    	generateActionModelMenus();
    }


    /**
     * Getter method for getting map of action model documents
     * @return map of action model documents
     */
    public static Map<String, Document> getActionModelMap() {
        return actionModelMap;
    }


    /**
     * Map setter
     * @param actionModelMap map we are setting
     */
    public static void setActionModelMap(Map<String, Document> actionModelMap) {
        ActionModelServiceImpl.actionModelMap = actionModelMap;
    }



    /**
     * @see com.jaspersoft.jasperserver.api.engine.common.service.ActionModelService#generateActionModelMenus()
     */
    public void generateActionModelMenus(){
        log.debug("Creating action model infrastructure.");
        ActionModel singleTon = ActionModel.getInstance();

        for(String model : actionModelTypes){
            String xmlFile = getActionModelXmlURIStub() + "-" + model + XML_FILE_EXT;
            Document doc = singleTon.generateActionModelDocument(getInputStreamFromFile(xmlFile));
            actionModelMap.put(model, doc);
        }

    }


    /**
     * Helper method to get stream of bytes from xml files
     * @param fileName action model xml file
     * @return stream of bytes
     */
    private InputStream getInputStreamFromFile(String fileName){
        InputStream stream;
        try {
            Resource resource = resourceLoader.getResource(fileName);
            stream = resource.getInputStream();
        } catch (IOException e) {
            System.err.println("[FATAL]: file " + fileName + " could not be found.");
            e.printStackTrace();
            throw new RuntimeException("[FATAL]: action model initialization failed");
        }
        return stream;
    }


    /**
     * @see com.jaspersoft.jasperserver.api.engine.common.service.ActionModelService#getActionModelMenu(String)
     */
    public Document getActionModelMenu(String context){
        if(actionModelMap.size() > 0){
            return actionModelMap.get(context);
        } else{
            throw new RuntimeException("[FATAL]: action model initialization failed");
        }
    }

    /*
     * The following methods are getters and setters for the bean properties.
     */
    public void setActionModelTypes(List<String> modelTypes){
        actionModelTypes = new ArrayList<String>(modelTypes);
    }

    public List<String> getActionModelTypes(){
        return actionModelTypes;
    }


    public void setActionModelXmlURIStub(String url){
        actionModelBaseURI = url;
    }


    public String getActionModelXmlURIStub(){
        return actionModelBaseURI;
    }


    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
