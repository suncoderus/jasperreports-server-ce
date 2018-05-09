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

package com.jaspersoft.jasperserver.war.control;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mondrian.olap.Util.PropertyList;
import mondrian.rolap.RolapConnectionProperties;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.apache.log4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.logging.audit.domain.AuditEvent;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceLookupImpl;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapClientConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapManagementService;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.war.common.JasperServerUtil;
import com.jaspersoft.jasperserver.war.util.OlapSessionState;
import com.tonbeller.jpivot.core.Model;
import com.tonbeller.jpivot.mondrian.JPivotPackageAccess;
import com.tonbeller.jpivot.mondrian.MondrianDrillThroughTableModel;
import com.tonbeller.jpivot.mondrian.MondrianModel;
import com.tonbeller.jpivot.olap.model.OlapModel;
import com.tonbeller.jpivot.olap.model.OlapModelDecorator;
import com.tonbeller.jpivot.tags.OlapModelProxy;
import com.tonbeller.jpivot.xmla.XMLA_Model;
import com.tonbeller.tbutils.res.Resources;
import com.tonbeller.wcf.bookmarks.BookmarkManager;
import com.tonbeller.wcf.controller.RequestContext;
import com.tonbeller.wcf.table.EditableTableComponent;


/**
 * The OlapModelController handles the request to load a specified OLAP model
 * A session 'sess' contains can have multiple models stored in
 * the 'olapModels' map. An entry in the 'olapModels' map contains the
 * 'olapSessionState' of a given olap model.
 *
 * @author jshih
 * @revision $Id$
 *
 */
public class OlapModelController extends JRBaseMultiActionController {

    private static final Logger log = Logger.getLogger(OlapModelController.class);
    private Resources resources = Resources.instance(OlapModelController.class);

    private OlapConnectionService olapConnectionService;
    private OlapManagementService olapManagementService;
    private AuditContext auditContext;

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

    public OlapManagementService getOlapManagementService() {
        return olapManagementService;
    }

    public void setOlapManagementService(OlapManagementService olapManagementService) {
        this.olapManagementService = olapManagementService;
    }


    public void setAuditContext(AuditContext auditContext) {
        this.auditContext = auditContext;
    }

    private void createAuditEvent(final String olapUnitUri) {
        auditContext.doInAuditContext(new AuditContext.AuditContextCallback() {
            public void execute() {
                AuditEvent auditEvent = auditContext.getOpenedAuditEvent("olapView");
                if (auditEvent == null) {
                    auditEvent = auditContext.createAuditEvent("olapView", olapUnitUri, null);
                } else {
                    auditEvent.setResourceUri(olapUnitUri);
                }
                auditContext.setResourceTypeToAuditEvent(OlapUnit.class.getName(), auditEvent);
            }
        });
    }

    private void updateAuditEvent(final String olapUnitUri) {
        auditContext.doInAuditContext("olapView", new AuditContext.AuditContextCallbackWithEvent() {
            public void execute(AuditEvent auditEvent) {
                auditEvent.setResourceUri(olapUnitUri);
            }
        });
    }

    public ModelAndView viewOlap(HttpServletRequest req,
                 HttpServletResponse res) throws ServletException {

        if (req.getParameter("_eventId_backFromErrorPage") != null) {
            String redirect = "redirect:/flow.html?_flowId=" + req.getParameter("_flowId") + "&folder=" + req.getParameter("folderPath");
            return new ModelAndView(redirect);
        }

        if (!isAnalysisFeatureSupported()) {
            /* Will never go there for ce. */
            return new ModelAndView("redirect:/licenseAnalysisFeatureUnavailable.html");
        }

        // set model attributes
        String viewUri = req.getParameter("name");
        //fix for issue #13034. TODO: Better solution is to find why we has such broken url here.
        if (viewUri != null && viewUri.startsWith("//")) {
            viewUri = viewUri.substring(1);
        }

        createAuditEvent(viewUri);

        HttpSession sess = req.getSession();

        if ((viewUri == null) || ("".equals(viewUri))) {
            viewUri = (String) sess.getAttribute("currentView");
        } else if ("true".equals(req.getParameter("new"))) {
            try {
                clearOlapSession(sess, viewUri);
            } catch (Exception ex) {
                throw new ServletException(ex);
            }
        }

        if (viewUri == null || viewUri.length() == 0) {
            log.warn(resources.getString("jsexception.session.timeout.occurred"));
            // try to recover viewUri from servlet context
            if ((viewUri = (String) sess.getServletContext().getAttribute("name")) == null) {
                log.warn(resources.getString("jsexception.no.olap.model.name"));
                // display analysis views
                String redirect = "redirect:/" + resources.getString("jasperserver.OlapModelController.olapViewList"); // i.e., flow.html?_flowId=olapViewListFlow&curlnk=2;
                return new ModelAndView(redirect);
            }
        }

        updateAuditEvent(viewUri);

        String currentView = (String) sess.getAttribute("currentView");
        String currentUserName = (String) sess.getAttribute("currentUserName");
        Locale browserLocale = req.getLocale();
        Locale defaultLocale = RequestContext.instance().getLocale();
        Locale locale = LocaleContextHolder.getLocale(); // login locale

        // set locale
        if (locale == null) {
            // default
            locale = defaultLocale;
        }
        log.debug("Request locale set to: " + locale.toString());
        req.setAttribute("locale", locale); // for fmt:
        sess.setAttribute("locale", locale); // for drill through
        sess.getServletContext().setAttribute("locale", browserLocale); // for mondrian query, see MondrianModel

        boolean wasInDrillThrough = sess.getAttribute("inDrillThrough") != null &&
                ((String) sess.getAttribute("inDrillThrough")).equals("true");
        // hook for ja-pro drill-through
        String drillThrough = getDrillThrough(req, sess);

        // If we are no longer in drill through, kill the connection
        if (wasInDrillThrough && drillThrough == null) {
            try {
                clearDrillThroughConnectionFromSession(sess, currentView);
            } catch (Exception ex) {
                throw new ServletException(ex);
            }
        }

        sess.setAttribute("drillthrough", drillThrough);

        req.setAttribute("name", viewUri);
        sess.getServletContext().setAttribute("name", viewUri); // initialize and be used in case of session timeout

        // set flag to reload session state when switching view FIXME synchronize?
        boolean isSameView = false;
        if (currentView != null &&
                viewUri.equals(currentView)) {
            isSameView = true;
        } else {
            if (currentView != null) {
                try {
                    clearOlapSession(sess, currentView);
                } catch (Exception ex) {
                    throw new ServletException(ex);
                }
            }
            sess.setAttribute("currentView", viewUri);
        }

        boolean isSameUser = setCurrentUser(sess, currentUserName);

        if (!isSameUser) {
            // force the re-creation of the saveas form component
            removeAttributesEndingWith(sess, "saveas");
        }
        log.debug("Viewing OLAP Model: " + viewUri + ", isSameView: " + isSameView + ", isSameUser: " + isSameUser);

        Map olapModels = (Map) sess.getAttribute("olapModels");

        if (olapModels == null) {
            olapModels = new HashMap();
            sess.setAttribute("olapModels", olapModels);
        }

        sess.setAttribute("drillThroughTableModel", new MondrianDrillThroughTableModel(null));
        OlapSessionState sessionState = (OlapSessionState) olapModels.get(viewUri);

        // reload sessionState if:
        // * no previous state
        // * we restored state but the OlapModel in it was null: return from server restart while logged in
        // * MDX is passed as a parameter
        // * user is switched

        if (sessionState == null || sessionState.getOlapModel() == null ||
                req.getParameter("mdx") != null || !isSameUser) {
            log.debug("Swapping view");
            sessionState = getOlapSession(viewUri, sess); // load olap model
            olapModels.put(viewUri, sessionState);
            // need to set the attribute for the first time, so we can leverage it for retrieving the
            // "save view as" functionality
            sess.setAttribute("drillthrough", drillThrough);
            sess.setAttribute("olapUnit", sessionState.getOlapUnit()); // put current OLAP Unit to session for save purpose

            // Always clear out the old session values, because the new ones will be additive.
            // We don't want to see the previous settings

            BookmarkManager.instance(sess).restoreSessionState(null);

            // restore previously bookmark to the current session
            Object state = null;
            OlapUnit olapUnit = sessionState.getOlapUnit();

            if ((olapUnit != null) && (olapUnit.getOlapViewOptions() != null)) {
                state = olapUnit.getOlapViewOptions();
                BookmarkManager.instance(sess).restoreSessionState(state);
            }

            // get drillthroughSQL

            String curView = (String) sess.getAttribute("currentView");
            EditableTableComponent drillthru = (EditableTableComponent) sess.getAttribute(curView + ".drillthroughtable");
            if (drillthru == null) {
                Model mdl = ((OlapModelProxy) sessionState.getOlapModel()).getRootModel();
                if (mdl instanceof MondrianModel) {
                    PropertyList connectInfo = ((MondrianModel) mdl).getConnectProperties();
                    if (connectInfo != null) {
                        MondrianDrillThroughTableModel dtm = new MondrianDrillThroughTableModel(null);
                        String jdbcUrl = connectInfo.get(RolapConnectionProperties.Jdbc.name());
                        String jdbcUser = connectInfo.get(RolapConnectionProperties.JdbcUser.name());
                        String jdbcPassword = connectInfo.get(RolapConnectionProperties.JdbcPassword.name());
                        String dataSourceName = connectInfo.get(RolapConnectionProperties.DataSource.name());
                        dtm.setJdbcUrl(jdbcUrl);
                        dtm.setJdbcUser(jdbcUser);
                        dtm.setJdbcPassword(jdbcPassword);
                        dtm.setDataSourceName(dataSourceName);
                        // restore sql
                        HashMap hMap = (HashMap) state;
                        if (hMap != null) {
                            String curSql = (String) hMap.get("drillThruSQL");
                            dtm.setSql(curSql);
                            sess.setAttribute("drillThroughTableModel", dtm);
                            // set drill-throug switches
                            sess.removeAttribute("belowCube");
                            if (curSql != null && curSql.length() > 0) {
                                log.debug("Drillthrough SQL: " + curSql);
                                // if curSql contains a sql statement, activate drill-through table (for belowCube option only)
                                log.debug("Display drill-through table.");
                                if (state != null && viewUri != null) {
                                    // set showTableBelowCube in olapModel
                                    HashMap stateMap = (HashMap) ((HashMap) state).get(viewUri + "/displayform");
                                    if (stateMap != null) {
                                        Boolean showTableBelowCube = (Boolean) stateMap.get("extensions(drillThrough).showTableBelowCube");
                                        sess.setAttribute("belowCube", showTableBelowCube.booleanValue() ? "true" : "false");
                                    } else {
                                        log.error("Invalid displayform state.");
                                    }
                                } else {
                                    log.error("Invalid view URI, or view state.");
                                }
                                sess.setAttribute("drillthrough", "y"); // FIXME: check (((DrillThroughUI) listener).isShowTableBelowCube())
                                sess.setAttribute("inDrillThrough", "true");
                            } else {
                                log.debug("Drill-through table not displayed");
                            }
                        } else {
                            dtm.setSql("");
                        }
                    }
                }
            }
        } // ...blank-view-the-second-time-load-fix: load the view if switched
        else if (!isSameView) {
            log.debug("setting session state as !isSameView");
            sessionState = getOlapSession(viewUri, sess); // load olap model
        }

        ExecutionContextImpl executionContext = new ExecutionContextImpl();

        getOlapManagementService().notifySchemaUse(executionContext, sessionState.getOlapClientConnection());
        
        setJPivotParams(req, sessionState.getOlapModel());

        req.setAttribute("olapModel", sessionState.getOlapModel());
        req.setAttribute("olapSession", sessionState);
        req.setAttribute("drillthrough", drillThrough); // hook for pro (deprecated)


        // Because WCF is so behind the times, we have to also set the
        // session attribute by name

        // if (sessionState.getOlapModel() instanceof MondrianModel) {
            // MondrianModel tmpModel = (MondrianModel) sessionState.getOlapModel();
            //log.warn("#TMP OlapModel.connection is " + tmpModel.getConnection());
            // the connect string may not even be relevant since we use connect props instead
            //log.warn("#TMP OlapModel.connectString is " + tmpModel.getConnectString());
        // }

        // put olapModel into session initially and subsequently when view is changed
        if ((sess != null && sess.getAttribute("olapModel") == null) || // initial session
                (sessionState != null && sessionState.getOlapModel() != null && // subsequent session
                sess.getAttribute("olapModel") != sessionState.getOlapModel())) {// may be refactor to replace isSameView)
            sess.setAttribute("olapModel", sessionState.getOlapModel());
        }

        return new ModelAndView("/modules/olap/viewOlap");
    }

    public static void clearOlapSession(HttpSession sess, String uriToRemove) throws Exception {
        Map olapModels = (Map) sess.getAttribute("olapModels");
        if (olapModels == null)
            return;

        log.debug("clearing OLAP session of " + uriToRemove);

        OlapSessionState existingModel = (OlapSessionState) olapModels.get(uriToRemove);

        if (existingModel != null) {
            ((OlapModelProxy) existingModel.getOlapModel()).destroyAll();
        }

        olapModels.remove(uriToRemove);
        sess.setAttribute("olapUnit", null);

        // Get rid of all the forms etc
        for ( Enumeration coll = sess.getAttributeNames(); coll.hasMoreElements(); ) {
            String attName = (String) coll.nextElement();
            if (attName.startsWith(uriToRemove)) {
                log.debug("Removing " + attName + ", " + sess.getAttribute(attName).getClass().getName());

                Object att = sess.getAttribute(attName);
                clearDrillThroughConnection(attName, att);

                // removing a ComponentSuport from the session destroys it
                sess.removeAttribute(attName);
            }
        }

        // Always clear out the old session values, because the new ones will be additive.
        // We don't want to see the previous settings

        BookmarkManager.instance(sess).restoreSessionState(null);
    }

    public static void clearDrillThroughConnectionFromSession(HttpSession sess, String uriToRemove) throws Exception {

        // destroy the drill through JDBC connections
        for ( Enumeration coll = sess.getAttributeNames(); coll.hasMoreElements(); ) {
            String attName = (String) coll.nextElement();
            Object att = sess.getAttribute(attName);
            if (attName.startsWith(uriToRemove)) {
                clearDrillThroughConnection(attName, att);
            }
        }
    }

    public static void clearDrillThroughConnection(String attName, Object att) throws Exception {
        if (attName.endsWith(".drillthroughtable") && att instanceof EditableTableComponent) {
            EditableTableComponent etc = (EditableTableComponent) att;

            log.debug("clearDrillThroughConnection for URI " + attName);
            MondrianDrillThroughTableModel dtm = (MondrianDrillThroughTableModel) etc.getModel();

            dtm.destroy();
        }
    }

    private void removeAttributesEndingWith(HttpSession sess, String component) {
        Enumeration attributeNames = sess.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attribute = (String) attributeNames.nextElement();
            if (attribute.endsWith(component)) {
                sess.removeAttribute((String) attribute);
                break;
            }
        }
    }

    /**
     * set drill-through status:
     * x = drill-through in new browser window
     * y = drill-through below cube (navigation table)
     * z = other hyperlinks while in 'y'
     * @param req
     * @param sess
     * @return
     */
    private String getDrillThrough(HttpServletRequest req, HttpSession sess) {
        String drillthrough = null;
        Map parametersMap = req.getParameterMap();
        Iterator itrMap = parametersMap.entrySet().iterator();
        sess.setAttribute("inDrillThrough", "false");
        while (itrMap.hasNext()) {
            Map.Entry mapEntry = (Map.Entry) itrMap.next();
            String requestParameter = (String) mapEntry.getKey();
            if (requestParameter.equals("d")) {
                // initialize
                drillthrough = ((String[]) mapEntry.getValue())[0];
                sess.setAttribute("inDrillThrough", "true");
                if (sess.getAttribute("changemode") != null) {
                    // clear it
                    sess.removeAttribute("changemode");
                }
                break;
            }
            else if (requestParameter.contains("drillthrough")) {
                // subsequent interactions for drill-through pop-up
                if (sess.getAttribute("changemode") != null) {
                    // remove it, don't set drillthrough as side effect.
                    sess.removeAttribute("changemode");
                }
                else {
                    drillthrough = "x"; //(String) sess.getAttribute("drillthrough");
                    sess.setAttribute("inDrillThrough", "true");
                }
                break;
            }
        }
        return drillthrough;
    }

    /** may set state in the OlapModel to reflect the supported http params */
    protected void setJPivotParams( HttpServletRequest req,
                    OlapModel model ) {
    String mdx = req.getParameter("mdx");
    if (mdx != null) {
        log.debug("mdx request param: |" + mdx + "|");
        log.debug("OlapModel class: " + model.getClass());
        // how many layers are there to this onion?
        while (model instanceof OlapModelDecorator) {
        model = ((OlapModelDecorator)model).getDelegate();
        log.debug("OlapModel class: " + model.getClass());
        }
        // JPivot is missing out on polymorphism here
        if (model.getClass() == MondrianModel.class) {
        JPivotPackageAccess.setMdx( (MondrianModel)model, mdx );
        }
        if (model.getClass() == XMLA_Model.class) {
        try {
            ((XMLA_Model)model).setUserMdx(mdx);
        } catch (com.tonbeller.jpivot.olap.model.OlapException oe) {
            log.error(oe);
        }
        }
    }
    }

    protected OlapSessionState getOlapSession(String viewUri, HttpSession sess) {

        log.debug("Setting OlapModel for " + viewUri);

        ExecutionContextImpl executionContext = new ExecutionContextImpl();

        OlapUnit olapUnit = (OlapUnit) getRepository().getResource(executionContext,
                                                                   viewUri);

        if (olapUnit == null) {
                throw new JSException("jsexception.no.olap.model.retrieved");
        }

        OlapModel model = getOlapConnectionService().initializeOlapModel(executionContext, olapUnit, sess);

        OlapClientConnection olapConnection =
                (OlapClientConnection) getOlapConnectionService().dereference(executionContext, olapUnit.getOlapClientConnection());

        return new OlapSessionState(model, olapUnit, olapConnection);
    }

    /**
     * The handle() method looks up a given OLAP model given by the command parameter.
     *
     * @param request
     * @param response
     * @param command Specifies the OLAP model to load.
     * @param errors TODO
     * @return
     */
    protected ModelAndView handle(HttpServletRequest request,
                  HttpServletResponse response, Object command, BindException errors)
    throws Exception {

    ModelAndView modelAndView = null;

    ResourceLookupImpl olapUnitCommand = (ResourceLookupImpl) command;

    String olapUnitName = olapUnitCommand.getName();

    if (olapUnitName != null)
        {
        modelAndView = new ModelAndView("modules/olap/viewOlap", "olapUnitName", olapUnitName);
        }
    else {
        // TODO resolve the double clicking problem
        ExecutionContext executionContext = JasperServerUtil.getExecutionContext(request);
        ResourceLookup[] olapUnits = repository.findResource(executionContext, FilterCriteria.createFilter(OlapUnit.class));
        modelAndView = new ModelAndView("modules/listOlapViews", "olapUnits", olapUnits);
    }

    return modelAndView;
    }

    private boolean setCurrentUser(HttpSession sess, String currentUserName) {
        boolean isSameUser = false;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() == null) {
            log.error("authenticationError");
        }

        User user = (User) authentication.getPrincipal();
        if (user == null) {
            log.error("userError");
        }

        String userName = user.getFullName();

        if (currentUserName != null &&
            userName.equals(currentUserName)) {
            isSameUser = true;
        }
        else {
            sess.setAttribute("currentUserName", userName);
        }

        return isSameUser;
    }

    protected Boolean isAnalysisFeatureSupported() {
        /* For CE analysis feature is always supported. */
        return true;
    }

}
