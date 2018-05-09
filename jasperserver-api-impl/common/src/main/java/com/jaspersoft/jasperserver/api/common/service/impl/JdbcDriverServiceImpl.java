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
package com.jaspersoft.jasperserver.api.common.service.impl;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.common.properties.PropertiesManagementService;
import com.jaspersoft.jasperserver.api.common.properties.PropertiesManagementServiceImpl;
import com.jaspersoft.jasperserver.api.common.service.JdbcDriverService;
import com.jaspersoft.jasperserver.api.common.util.JdbcDriverServicePropertyChanger;
import com.jaspersoft.jasperserver.api.common.util.JdbcDriverShim;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.FileResourceData;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FileResourceImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation of {@link JdbcDriverService}
 * It uses shim driver {@link com.jaspersoft.jasperserver.api.common.util.JdbcDriverShim} to register real jdbc driver
 * <p/>
 * Also it holds registry of already registered with shim class jdbc drivers
 * so they will not be registered twice.
 *
 * @author Sergey Prilukin (sprilukin@jaspersoft.com)
 * @version $Id$
 */
public class JdbcDriverServiceImpl implements JdbcDriverService, ApplicationContextAware {
    protected final Log logger = LogFactory.getLog(getClass());

    public static final String DEFAULT_JDBC_REPO_FOLDER = "/jdbc";
    public static final String DEFAULT_JDBC_TEMP_FOLDER = System.getProperty("java.io.tmpdir");
    public static final String URL_CLASSLOADER_FORMAT = "jar:file:%s!/";
    public static final String TEMP_FILE_PATH_FORMAT = "%s/jdbc-%s-%s.jar";
    public static final String SYSTEM_PROPERTIES_LIST_KEY_PREFIX = JdbcDriverServicePropertyChanger.PROPERTY_PREFIX;
    public static final String SYSTEM_PROPERTIES_LIST_KEY_FORMAT = SYSTEM_PROPERTIES_LIST_KEY_PREFIX + "%s";
    public static final String SYSTEM_PROPERTIES_PRESERVED_KEY_SUFFIX = "_backup";
    public static final String SYSTEM_CLASSLOADER_PATH = "[SYSTEM]";

    private Map<String, Driver> driverClassToDriverMap = new HashMap<String, Driver>();
    private Map<Driver, ClassLoader> driverToClassLoaderMap = new HashMap<Driver, ClassLoader>();
    private Map<ClassLoader, String> classLoaderToUrlMap = new HashMap<ClassLoader, String>();
    private Map<String, String> driverClassToUrlMap = new HashMap<String, String>();

    private RepositoryService repository;
    private PropertiesManagementService propertiesManagementService;
    private ApplicationContext applicationContext;

    private String jdbcDriversFolder = DEFAULT_JDBC_REPO_FOLDER;
    private Boolean systemClassLoaderFirst = false;

    public void setRepository(RepositoryService repository) {
        this.repository = repository;
    }

    public void setSystemClassLoaderFirst(Boolean systemClassLoaderFirst) {
        this.systemClassLoaderFirst = systemClassLoaderFirst;
    }

    public void setJdbcDriversFolder(String jdbcDriversFolder) {
        this.jdbcDriversFolder = jdbcDriversFolder;
    }

    public String getJdbcDriversFolder() {
        return jdbcDriversFolder;
    }

    @Override
    public synchronized void register(String driverClass) throws Exception {
        if (driverClassToDriverMap.containsKey(driverClass)) {
            //Class already loaded
            return;
        }

        //String url = getUrlByDriverClassName(driverClass);
        //Trying to load driver from repository
        //loadJdbcDriverFromRepository(url);

        loadDriverFromExistingClassLoaders(driverClass);
    }

    @Override
    public synchronized boolean isRegistered(String driverClass) {

        boolean registered = true;

        try {
            this.register(driverClass);
        } catch (Exception e) {
            registered = false;
        }

        return registered;
    }

    @Override
    public synchronized void setDriverMappings(Map<String, String> driverMappings) throws Exception {
        for (Map.Entry<String, String> entry : driverMappings.entrySet()) {
            setDriverMapping(entry.getKey(), entry.getValue());
        }

        Set<String> values = new HashSet<String>(classLoaderToUrlMap.values());
        for (String path : values) {
            if (!driverMappings.values().contains(path)) {
                unRegisterByUrl(path);
            }
        }
    }

    public synchronized void setDriverMapping(String driverClass, String path) throws Exception {
        if (driverClass == null) {
            throw new IllegalArgumentException("Driver class could not be null");
        }

        if (driverClass.endsWith(SYSTEM_PROPERTIES_PRESERVED_KEY_SUFFIX)) {
            //Do not process backups
            return;
        }

        String registeredUrl = getUrlByDriverClassName(driverClass);
        if (!path.equals(registeredUrl)) {
            unRegisterByUrl(registeredUrl);
            driverClassToUrlMap.put(driverClass, path);
            loadJdbcDriverFromRepository(path);
        }
    }

    public synchronized void removeDriverMapping(String driverClass) throws Exception {
        if (driverClass == null) {
            throw new IllegalArgumentException("Driver class could not be null");
        }

        if (driverClass.endsWith(SYSTEM_PROPERTIES_PRESERVED_KEY_SUFFIX)) {
            //Do not process backups
            return;
        }

        String registeredUrl = getUrlByDriverClassName(driverClass);
        if (registeredUrl != null) {
            if (SYSTEM_CLASSLOADER_PATH.equals(registeredUrl)) {
                unRegisterByClassName(driverClass);
            } else {
                unRegisterByUrl(registeredUrl);
                driverClassToUrlMap.remove(driverClass);
            }
        }
    }

    @Override
    public Map<String, String> getDriverMappings() {
        return Collections.unmodifiableMap(driverClassToUrlMap);
    }

    @Override
    public Set<String> getRegisteredDriverClassNames() {
        Set<String> driverNames = new HashSet<String>(driverClassToUrlMap.size());

        for (String className : driverClassToUrlMap.keySet()) {
            if (isRegistered(className)) {
                driverNames.add(className);
            }
        }

        return driverNames;
    }

    private ClassLoader getClassLoaderAndVerifyDriverClassName(String driverClassName, String path) throws Exception {
        List<FileResource> driverResources = findJdbcDriversInRepo(path);
        if (driverResources.isEmpty()) {
            throw new IllegalStateException("No drivers found in folder");
        }

        List<String> pathToDriverFile = new ArrayList<String>(driverResources.size());
        for (FileResource driverResource : driverResources) {
            //Copy jdbc driver from repository to temp folder since for now we only can load jar from file only
            pathToDriverFile.add(copyJdbcDriverToTempFolder(driverResource));
        }

        //Trying to load driver class for jdbc driver from temp folder
        ClassLoader classLoader = getClassLoader(pathToDriverFile.toArray(new String[pathToDriverFile.size()]));

        //check that driver could be loaded from classloader
        Class.forName(driverClassName, true, classLoader);

        return classLoader;
    }

    @Override
    public synchronized void setDriverMappingAndRegister(String driverClass, String path) throws Exception {
        ClassLoader classLoader = getClassLoaderAndVerifyDriverClassName(driverClass, path);

        //Unregister any existing driver
        String registeredUrl = getUrlByDriverClassName(driverClass);
        if (SYSTEM_CLASSLOADER_PATH.equals(registeredUrl)) {
            unRegisterByClassName(driverClass);
        } else {
            unRegisterByUrl(registeredUrl);
        }

        //Set new mappings
        driverClassToUrlMap.put(driverClass, path);
        classLoaderToUrlMap.put(classLoader, path);

        //Register new driver
        register(driverClass);

        //Save mapping to global proeprties list
        if (getPropertiesManagementService() != null) {
            String driverNameKey = String.format(SYSTEM_PROPERTIES_LIST_KEY_FORMAT, driverClass);
            getPropertiesManagementService().setProperty(driverNameKey, path);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void setDriver(String driverClassName, Map<String, byte[]> driverFiles) throws Exception {
        ClassLoader classLoader = getClassLoader(driverFiles);

        // Throw ClassNotFoundException if fails.
        verifyDriverClass(driverClassName, classLoader);

        String concreteDriverFolderUri = saveDriverFiles(driverClassName, driverFiles);

        //Unregister any existing driver
        String registeredUrl = getUrlByDriverClassName(driverClassName);
        if (SYSTEM_CLASSLOADER_PATH.equals(registeredUrl)) {
            unRegisterByClassName(driverClassName);
        } else {
            unRegisterByUrl(registeredUrl);
        }

        //Set new mappings
        driverClassToUrlMap.put(driverClassName, concreteDriverFolderUri);
        classLoaderToUrlMap.put(classLoader, concreteDriverFolderUri);

        try {
            //Register new driver
            register(driverClassName);
        } catch (NoClassDefFoundError e) {
            // Rollback mapping in order to avoid illegal state of the bean.
            driverClassToUrlMap.remove(driverClassName);
            classLoaderToUrlMap.remove(classLoader);
            throw e;
        }

        //Save mapping to global proeprties list
        if (getPropertiesManagementService() != null) {
            String driverNameKey = String.format(SYSTEM_PROPERTIES_LIST_KEY_FORMAT, driverClassName);
            getPropertiesManagementService().setProperty(driverNameKey, concreteDriverFolderUri);
        }
    }

    private ClassLoader getClassLoader(Map<String, byte[]> driverFilesData) throws Exception {
        Set<String> tempFilesPathName = new HashSet<String>();
        for (byte[] driverFileData : driverFilesData.values()) {
            tempFilesPathName.add(createTempFile(driverFileData));
        }
        ClassLoader classLoader = getClassLoader(tempFilesPathName.toArray(new String[tempFilesPathName.size()]));
        return classLoader;
    }

    void verifyDriverClass(String driverClassName, ClassLoader classLoader) throws ClassNotFoundException {
        Class.forName(driverClassName, true, classLoader);
    }

    /**
     * Save each driver file into corresponding folder,
     * check if all needed folders exist, backup or create new as needed.
     * @param driverClassName concrete jdbc driver class name.
     * @param driverFilesData a map where the key is original file name and the value is file data.
     * @return Uri of concrete driver folder.
     */
    private String saveDriverFiles(String driverClassName, Map<String, byte[]> driverFilesData) {
        ExecutionContext exContext = ExecutionContextImpl.getRuntimeExecutionContext();

        String concreteDriverFolderUri = checkFoldersForDriver(exContext, driverClassName);
        Set<FileResource> jarFileResources = createJarFileResources(concreteDriverFolderUri, driverFilesData);

        saveResourceList(exContext, jarFileResources);

        return concreteDriverFolderUri.substring(jdbcDriversFolder.length() + 1);
    }

    private void saveResourceList(ExecutionContext exContext, Set<FileResource> fileResources) {
        for (FileResource fileResource: fileResources) {
            repository.saveResource(exContext, fileResource);
        }
    }

    private Set<FileResource> createJarFileResources(String parentUri, Map<String, byte[]> driverFilesData) {
        Set<FileResource> fileResources = new HashSet<FileResource>();
        for (Map.Entry<String, byte[]> entry: driverFilesData.entrySet()) {
            fileResources.add(createJarFileResource(parentUri, entry.getKey(), entry.getValue()));
        }
        return fileResources;
    }

    private FileResource createJarFileResource(String parentUri, String originalFileName, byte[] fileData) {
        FileResource result = new FileResourceImpl();
        result.setFileType(FileResource.TYPE_JAR);
        result.setData(fileData);
        result.setName(originalFileName);
        result.setLabel(originalFileName);
        result.setParentFolder(parentUri);
        return result;
    }

    /**
     * Make sure all needed folders exists, create new or backup old ones,
     * return concrete driver folder uri.
     * @param exContext
     * @param driverClassName
     * @return Uri of concrete driver folder.
     */
    private String checkFoldersForDriver(ExecutionContext exContext, String driverClassName) {
        checkJdbcFolder(exContext);
        return checkConcreteDriverFolder(exContext, driverClassName);
    }

    /**
     * Create Jdbc folder if not exists.
     * @param exContext runtime ExecutionContext
     */
    private void checkJdbcFolder(ExecutionContext exContext) {
        if (!repository.folderExists(exContext, jdbcDriversFolder)) {
            Folder folder = (Folder) repository.newResource(exContext, Folder.class);
            String[] pathAndName = getNameFromPath(jdbcDriversFolder);
            folder.setName(pathAndName[1]);
            folder.setLabel(pathAndName[1]);
            folder.setParentFolder(pathAndName[0]);
            repository.saveFolder(exContext, folder);
        }
    }

    /**
     * Create new folder for concrete driver if one doesn't exist,
     * create backup and move there all resources if one already exists.
     * @param exContext runtime ExecutionContext
     * @param driverClassName concrete jdbc driver class name.
     * @return Uri of concrete driver folder.
     */
    private String checkConcreteDriverFolder(ExecutionContext exContext, String driverClassName) {
        String concreteDriverFolderResourceName = driverClassName.replaceAll("\\W", "_");
        String concreteDriverFolderUri = jdbcDriversFolder + "/" + concreteDriverFolderResourceName;
        if (!repository.folderExists(exContext, concreteDriverFolderUri)) {
            Folder folder = (Folder) repository.newResource(exContext, Folder.class);
            folder.setName(concreteDriverFolderResourceName);
            folder.setLabel(driverClassName);
            folder.setParentFolder(jdbcDriversFolder);
            repository.saveFolder(exContext, folder);
        } else {
            Folder bakFolder = (Folder) repository.newResource(exContext, Folder.class);
            long currentTimeMillis = System.currentTimeMillis();
            String resourceNameSuffix = "_" + String.valueOf(currentTimeMillis);
            String labelSuffix = " " + new SimpleDateFormat().format(currentTimeMillis);
            String bakResourceName = concreteDriverFolderResourceName + resourceNameSuffix;
            bakFolder.setName(bakResourceName);
            bakFolder.setLabel(driverClassName + labelSuffix);
            bakFolder.setParentFolder(jdbcDriversFolder);
            repository.saveFolder(exContext, bakFolder);

            FilterCriteria criteria = FilterCriteria.createFilter(Resource.class);
            criteria.addFilterElement(FilterCriteria.createParentFolderFilter(concreteDriverFolderUri));

            String bakConcreteDriverFolderUri = jdbcDriversFolder + "/" + bakResourceName;
            @SuppressWarnings("unchecked") //cast is safe since repository.loadResourcesList always should return List<ResourceLookup>
            List<ResourceLookup> resources = repository.loadResourcesList(exContext, criteria);
            for (ResourceLookup resourceLookup : resources) {
                repository.moveResource(exContext, resourceLookup.getURIString(), bakConcreteDriverFolderUri);
            }
        }
        return concreteDriverFolderUri;
    }

    private String[] getNameFromPath(String path) {
        Matcher m = Pattern.compile("(.*/)(.+)").matcher(path);
        String[] pathAndName = null;
        if (m.matches()) {
            pathAndName = new String[] {m.group(1), m.group(2)};
        }
        return pathAndName;
    }

    private String getUrlByDriverClassName(String driverClassName) {
        return driverClassToUrlMap.get(driverClassName);
    }

    private ClassLoader getClassLoaderByClassName(String driverClassName) {
        String url = driverClassToUrlMap.get(driverClassName);
        if (url != null && !SYSTEM_CLASSLOADER_PATH.equals(url)) {
            for (Map.Entry<ClassLoader, String> entry : classLoaderToUrlMap.entrySet()) {
                if (entry.getValue().equals(url)) {
                    return entry.getKey();
                }
            }
        }

        return null;
    }

    private List<ClassLoader> getAllNonSystemClassLoaders() {
        List<ClassLoader> classLoaders = new ArrayList<ClassLoader>(classLoaderToUrlMap.size());

        for (Map.Entry<ClassLoader, String> entry : classLoaderToUrlMap.entrySet()) {
            if (!entry.getValue().equals(SYSTEM_CLASSLOADER_PATH)) {
                classLoaders.add(entry.getKey());
            }
        }

        return classLoaders;
    }

    private void unRegisterByClassName(String className) throws Exception {
        if (className == null) {
            return;
        }

        Driver driver = driverClassToDriverMap.get(className);

        if (driver != null) {
            unRegisterDriver(driver);
        }
    }

    private void unRegisterByUrl(String url) throws Exception {
        if (url == null) {
            return;
        }

        Iterator<Map.Entry<ClassLoader, String>> classLoaderToUrlMapIterator = classLoaderToUrlMap.entrySet().iterator();
        while (classLoaderToUrlMapIterator.hasNext()) {
            Map.Entry<ClassLoader, String> entry = classLoaderToUrlMapIterator.next();
            if (entry.getValue().equals(url)) {
                ClassLoader classLoader = entry.getKey();
                unRegisterDriversByClassLoader(classLoader);

                classLoaderToUrlMapIterator.remove();
                break;
            }
        }

        Iterator<Map.Entry<String, String>> driverClassToUrlMapIterator = driverClassToUrlMap.entrySet().iterator();
        while (driverClassToUrlMapIterator.hasNext()) {
            Map.Entry<String, String> entry = driverClassToUrlMapIterator.next();
            if (entry.getValue().equals(url)) {
                driverClassToUrlMapIterator.remove();
            }
        }
    }

    private void unRegisterDriversByClassLoader(ClassLoader classLoader) throws Exception {
        Iterator<Map.Entry<Driver, ClassLoader>> driverToClassLoaderMapIterator =
                driverToClassLoaderMap.entrySet().iterator();
        while (driverToClassLoaderMapIterator.hasNext()) {
            Map.Entry<Driver, ClassLoader> driverToClassLoaderMapEntry = driverToClassLoaderMapIterator.next();
            if (driverToClassLoaderMapEntry.getValue().equals(classLoader)) {
                unRegisterDriver(driverToClassLoaderMapEntry.getKey());
                driverToClassLoaderMapIterator.remove();
            }
        }
    }

    private void unRegisterDriver(Driver driver) throws Exception {
        unRegisterDriverFromJVM(driver);

        Iterator<Map.Entry<String, Driver>> driverClassToDriverMapIterator =
                driverClassToDriverMap.entrySet().iterator();
        while (driverClassToDriverMapIterator.hasNext()) {
            Map.Entry<String, Driver> driverClassToDriverMapEntry = driverClassToDriverMapIterator.next();
            if (driverClassToDriverMapEntry.getValue().equals(driver)) {
                driverClassToDriverMapIterator.remove();
                driverClassToUrlMap.remove(driverClassToDriverMapEntry.getKey());
                break;
            }
        }
    }

    private void loadDriverFromExistingClassLoaders(String driverClassName) throws Exception {
        boolean driverLoaded = false;
        Exception classLoadingException = null;

        for (ClassLoader classLoader : getAvailableClassLoaders(driverClassName)) {
            try {
                Class driverClass = Class.forName(driverClassName, true, classLoader);
                registerDriver(driverClassName, driverClass, classLoader);
                driverLoaded = true;
                break;
            } catch (ClassNotFoundException e) {
                if (classLoadingException == null) {
                    classLoadingException = e;
                }
            }
        }

        if (!driverLoaded) {
            throw classLoadingException;
        }
    }

    private void loadJdbcDriverFromRepository(String path) throws Exception {
        if (path == null || SYSTEM_CLASSLOADER_PATH.equals(path)) {
            return;
        }

        if (classLoaderToUrlMap.containsValue(path)) {
            return;
        }

        List<FileResource> driverResources = findJdbcDriversInRepo(path);

        List<String> pathToDriverFile = new ArrayList<String>(driverResources.size());
        for (FileResource driverResource : driverResources) {
            //Copy jdbc driver from repository to temp folder since for now we only can load jar from file only
            pathToDriverFile.add(copyJdbcDriverToTempFolder(driverResource));
        }

        if (pathToDriverFile.size() > 0) {
            //Trying to load driver class for jdbc driver from temp folder
            ClassLoader classLoader = getClassLoader(pathToDriverFile.toArray(new String[pathToDriverFile.size()]));
            classLoaderToUrlMap.put(classLoader, path);
        }
    }

    private void registerDriver(String driverClassName, Class driverClass, ClassLoader driverClassLoader) throws Exception {
        Driver driver = null;
        String oldUrl = driverClassToUrlMap.get(driverClassName);

        if (driverClassLoader != getSystemClassLoader()) {
            //Need to register driver shim, since it was loaded not by system class loader
            Driver driverFromRepository = (Driver) driverClass.newInstance();

            //Create and register shim for jdbc driver
            driver = new JdbcDriverShim(driverFromRepository);
            registerDriverInJVM(driver);

            if (getUrlByDriverClassName(driverClassName) == null) {
                String url = classLoaderToUrlMap.get(driverClassLoader);
                driverClassToUrlMap.put(driverClassName, url);
                mapDriverToClassLoaderInGlobalPropertiesList(driverClassName, url, oldUrl);
            }
        } else {
            Enumeration<Driver> driverEnumeration = getDriversRegisteredInJVM();
            while (driverEnumeration.hasMoreElements()) {
                Driver nextDriver = driverEnumeration.nextElement();
                if (nextDriver.getClass().equals(driverClass)) {
                    driver = nextDriver;
                    break;
                }
            }
            if (driver == null) {
                driver = (Driver) driverClass.newInstance();
                registerDriverInJVM(driver);
            }
            driverClassToUrlMap.put(driverClassName, SYSTEM_CLASSLOADER_PATH);
            classLoaderToUrlMap.put(driverClassLoader, SYSTEM_CLASSLOADER_PATH);
            mapDriverToClassLoaderInGlobalPropertiesList(driverClassName, SYSTEM_CLASSLOADER_PATH, oldUrl);
        }

        if (driver != null) {
            driverClassToDriverMap.put(driverClassName, driver);
            driverToClassLoaderMap.put(driver, driverClassLoader);
        } else {
            throw new IllegalStateException(String.format("Driver was loaded by System class loader but not registered in DriverManager: [%s]", driverClassName));
        }
    }

    private void mapDriverToClassLoaderInGlobalPropertiesList(String driverClassName, String url, String oldUrl) {
        if (getPropertiesManagementService() != null) {
            // PropertiesManagementService not always properly initialed on start up phase
            // Next hack used to initialize properties list when it's null
            try {
                getPropertiesManagementService().size();
            } catch (Exception e) {
                getPropertiesManagementService().reloadProperties();
            }
            String driverNameKey = String.format(SYSTEM_PROPERTIES_LIST_KEY_FORMAT, driverClassName);

            if (!url.equals(oldUrl)) {
                if (oldUrl != null && !oldUrl.equals(SYSTEM_CLASSLOADER_PATH)) {
                    String preservedDriverNameKey =
                            String.format("%s%s", driverNameKey, SYSTEM_PROPERTIES_PRESERVED_KEY_SUFFIX);
                    logger.info(String.format("Existing mapping for [%s] will be saved as [%s]",
                            driverClassName, preservedDriverNameKey));
                    getPropertiesManagementService().setProperty(preservedDriverNameKey, oldUrl);
                }

                if (url.equals(SYSTEM_CLASSLOADER_PATH)) {
                    if (getPropertiesManagementService().getProperty(driverNameKey) != null) {
                        getPropertiesManagementService().remove(driverNameKey);
                    }
                } else {
                    getPropertiesManagementService().setProperty(driverNameKey, url);
                }
            }
        }
    }

   /**
   *  2013-06-21 thorick
   *             This not a good name for this method.
   *             The Context ClassLoader associated with the current thread
   *             is specifically NOT related to the System ClassLoader.
   *
   */
    private ClassLoader getSystemClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private List<ClassLoader> getAvailableClassLoaders(String driverClass) {
        LinkedList<ClassLoader> classLoaders = new LinkedList<ClassLoader>();

        ClassLoader repositoryClassLoader = getClassLoaderByClassName(driverClass);
        if (repositoryClassLoader != null) {
            classLoaders.add(repositoryClassLoader);
        }

        //Adding system classloader to the start of to the end of list
        if (systemClassLoaderFirst) {
            classLoaders.addFirst(getSystemClassLoader());
        } else {
            classLoaders.addLast(getSystemClassLoader());
        }

        return classLoaders;
    }

    private List<FileResource> findJdbcDriversInRepo(String path) {
        String uri = String.format("%s/%s", jdbcDriversFolder, path);
        List<FileResource> driverResources = new ArrayList<FileResource>();
        ExecutionContext runtimeExecutionContext = ExecutionContextImpl.getRuntimeExecutionContext();

        Folder driversFolder = repository.getFolder(runtimeExecutionContext, uri);
        if (driversFolder == null) {
            return driverResources;
        }

        FilterCriteria criteria = FilterCriteria.createFilter(FileResource.class);
        criteria.addFilterElement(FilterCriteria.createParentFolderFilter(uri));

        @SuppressWarnings("unchecked") //cast is safe since repository.loadResourcesList always should return List<ResourceLookup>
                List<ResourceLookup> resources =
                repository.loadResourcesList(runtimeExecutionContext, criteria);

        for (ResourceLookup lookup : resources) {
            FileResource fileResource = (FileResource) repository.getResource(
                    runtimeExecutionContext, lookup.getURIString());
            if (FileResource.TYPE_JAR.equals(fileResource.getFileType())) {
                driverResources.add(fileResource);
            }
        }

        return driverResources;
    }

    private ClassLoader getClassLoader(String[] pathToDriverFile) throws Exception {
        URL[] urls = new URL[pathToDriverFile.length];

        for (int i = 0; i < pathToDriverFile.length; i++) {
            urls[i] = new URL(String.format(URL_CLASSLOADER_FORMAT, pathToDriverFile[i]));
        }

        return new URLClassLoader(urls, null);
    }

    private String copyJdbcDriverToTempFolder(FileResource driverResource) throws IOException {
        String pathToJdbcDriverFile = getUniqueFilePath();
        File file = new File(pathToJdbcDriverFile);
        FileUtils.writeByteArrayToFile(file, getFileResourceData(ExecutionContextImpl.getRuntimeExecutionContext(), driverResource));
        file.deleteOnExit();

        return pathToJdbcDriverFile;
    }

    private String createTempFile(byte[] fileData) throws IOException {
        String tempFilePath = getUniqueFilePath();
        File file = new File(tempFilePath);
        FileUtils.writeByteArrayToFile(file, fileData);
        file.deleteOnExit();
        return tempFilePath;
    }

    private String getUniqueFilePath() {
        return String.format(TEMP_FILE_PATH_FORMAT, DEFAULT_JDBC_TEMP_FOLDER, System.currentTimeMillis(), ((long) (Math.random() * 1000000)));
    }

    private byte[] getFileResourceData(ExecutionContext context, FileResource fileResource) {
        byte[] data;

        if (fileResource.hasData()) {
            data = fileResource.getData();
        } else {
            FileResourceData resourceData = repository.getResourceData(context, fileResource.getURIString());
            data = resourceData.getData();
        }

        return data;
    }

    protected void registerDriverInJVM(Driver driver) throws SQLException {
        DriverManager.registerDriver(driver);
    }

    protected void unRegisterDriverFromJVM(Driver driver) throws SQLException {
        String url = driverClassToUrlMap.get(driver.getClass().getName());
        if (!SYSTEM_CLASSLOADER_PATH.equals(url)) {
            DriverManager.deregisterDriver(driver);
        }
    }

    protected Enumeration<Driver> getDriversRegisteredInJVM() throws SQLException {
        return DriverManager.getDrivers();
    }

    private PropertiesManagementService getPropertiesManagementService() {
        if (propertiesManagementService == null) {
            try {
                propertiesManagementService = applicationContext.getBean("propertiesManagementService", PropertiesManagementServiceImpl.class);
            } catch(NoSuchBeanDefinitionException e) {
                // Just return null
            }
        }

        return propertiesManagementService;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
