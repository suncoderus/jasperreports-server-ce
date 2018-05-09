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
 * User: Papanii (a.k.a Code Pimp)
 * Original Author: Angus Croll
 * Date: Feb 10, 2010
 * Time: 2:42:21 PM
 */
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;

import com.jaspersoft.jasperserver.core.util.XMLUtil;
import com.jaspersoft.jasperserver.core.util.RegexUtil;
import com.jaspersoft.jasperserver.api.engine.common.service.ActionModelSupport;


public class ActionModel {
    private final static Log log = LogFactory.getLog(ActionModel.class);

    private static final ActionModel _singleTon = new ActionModel();

    //ELEMENTS...
    private static final String SIMPLE_ACTION_ELEM = "simpleAction";
    private static final String INPUT_ACTION_ELEM = "inputAction";
    private static final String SELECT_ACTION_ELEM = "selectAction";
    private static final String SEPARATOR_ELEM = "separator";
    private static final String CONDITION_ELEM = "condition";
    private static final String OPTION_ELEM = "option";
    private static final String GENERATED_OPTIONS_ELEM = "generatedOptions";
    private static final String GENERATE_FROM_TEMPLATE_ELEM = "generateFromTemplate";
    private static final String LABEL_OPTION_ELEM = "labelOption";

    //ATTRIBUTES....
    //general
    private static final String NAME_ATTR = "name";
    private static final String ID_ATTR = "id";
    private static final String TEST_ATTR = "test";
    private static final String TEST_ARGS_ATTR = "testArgs";
    private static final String CLIENT_TEST_ATTR = "clientTest";
    private static final String CLIENT_TEST_ARGS_ATTR = "clientTestArgs";
    private static final String ACTION_ATTR = "action";
    private static final String ACTION_ARGS_ATTR = "actionArgs";
    private static final String SELECTION_CONSTRAINT_ATTR = "selectionConstraint";
    private static final String BUTTON_ATTR = "button";
    private static final String DISABLED_ATTR = "disabled";
    //labels
    private static final String LABEL_KEY_ATTR = "labelKey";
    private static final String LABEL_CONDITION_ATTR = "labelCondition";
    private static final String LABEL_FUNCTION_ATTR = "labelFunction";
    private static final String LABEL_FUNCTION_ARGS_ATTR = "labelFunctionArgs";
    private static final String LABEL_EXPRESSION_ATTR = "labelExpression";
    private static final String FUNCTION_RESPONSE_ATTR = "functionResponse";
    //options
    private static final String IS_SELECTED_TEST_ATTR = "isSelectedTest";
    private static final String IS_SELECTED_TEST_ARGS_ATTR = "isSelectedTestArgs";
    private static final String FUNCTION_ATTR = "function";
    private static final String FUNCTION_ARGS_ATTR = "functionArgs";
    private static final String ALLOWS_INPUT_TEST_ATTR = "allowsInputTest";
    private static final String ALLOWS_INPUT_TEST_ARGS_ATTR = "allowsInputTestArgs";
    private static final String NO_ICON_INDENT_ATTR = "noIconIndent";

    //JSON objects...
    private static final String TYPE_KEY = "type";
    private static final String ID_KEY = "id";
    private static final String TEXT_KEY = "text";
    private static final String CLIENT_TEST_KEY = "clientTest";
    private static final String CLIENT_TEST_ARGS_KEY ="clientTestArgs";
    private static final String ACTION_KEY = "action";
    private static final String ACTION_ARGS_KEY = "actionArgs";
    private static final String IS_SELECTED_TEST_KEY = "isSelectedTest";
    private static final String IS_SELECTED_TEST_ARGS_KEY = "isSelectedTestArgs";
    private static final String ALLOWS_INPUT_TEST_KEY = "allowsInputTest";
    private static final String ALLOWS_INPUT_TEST_ARGS_KEY = "allowsInputTestArgs";
    private static final String NO_ICON_INDENT_KEY = "noIndent";
    private static final String CHILDREN_KEY = "children";
    private static final String SELECTION_CONSTRAINT_KEY = "selectionConstraint";
    private static final String BUTTON_KEY = "button";
    private static final String DISABLED_KEY = "isDisabled";
    private static final String CLASS_NAME = "className";

    //Reserved symbols on client
    private static final String RES_SELECTED_SERVER = "${selected}";
    private static final String RES_EVENT_SERVER = "${event}";
    private static final String RES_LABEL_SERVER = "${label}";
    private static final String RES_OPTION_ID_SERVER = "${optionId}";
    private static final String RES_OPTION_VALUE_SERVER = "${optionValue}";
    private static final String RES_TEMPLATE_INJECTION_INDEX = "${templateInjectionIndex}";
    private static final String RES_TEMPLATE_INJECTION_ID = "${templateInjectionId}";
    private static final String RES_TEMPLATE_INJECTION_VALUE = "${templateInjectionValue}";

    //JS constants on client (see actionModel.js for java-script ActionModel object)
    private static final String SIMPLE_VALUE = "simpleAction";
    private static final String SELECT_VALUE = "selectAction";
    private static final String INPUT_VALUE = "inputAction";
    private static final String OPTION_VALUE = "optionAction";
    private static final String SEPARATOR_VALUE = "separator";
    //reserved variables on client
    private static final String RES_LABEL_CLIENT = "$label";
    //private static final String RES_ID_CLIENT = "$id";
    private static final String RES_SELECTED_CLIENT = "$selected";
    private static final String RES_EVENT_CLIENT = "$event";

    //maps action model xml elem names to client model names
    private static Map<String,String> actionTypesMap = new HashMap<String,String>();

    //actions that fire an action directly (eg simple actions or options but not drop down selectors)
    private static List<String> directActionTypes = new ArrayList<String>();

    //actions that have child actions
    private static List<String> parentActionTypes = new ArrayList<String>();

    //special string mapping
    private static Map<String,String> specialStringMap = new HashMap<String,String>();

    //regex patterns
    private static String resourceKeyRegularExpression = "\\$R\\{[a-zA-Z0-9_.]*\\}";
    private static String arrayStringDelimiterRegularExpression = "@@";

    //values used when instantiating templates - make global so we don't have to keep passing around
    private static int templateInjectionIndex = 0; //zero based iteration index
    private static String templateInjectionId = "";
    private static String templateInjectionValue= "";

    static {
        //map element names to equivalent client constants
        actionTypesMap.put(SIMPLE_ACTION_ELEM,SIMPLE_VALUE);
        actionTypesMap.put(SELECT_ACTION_ELEM,SELECT_VALUE);
        actionTypesMap.put(INPUT_ACTION_ELEM,INPUT_VALUE);
        actionTypesMap.put(OPTION_ELEM,OPTION_VALUE);
        actionTypesMap.put(SEPARATOR_ELEM,SEPARATOR_VALUE);

        directActionTypes.add(SIMPLE_ACTION_ELEM);
        directActionTypes.add(OPTION_ELEM);

        parentActionTypes.add(SELECT_ACTION_ELEM);
        parentActionTypes.add(CONDITION_ELEM);

        specialStringMap.put(RES_LABEL_SERVER,RES_LABEL_CLIENT);
        specialStringMap.put(RES_SELECTED_SERVER,RES_SELECTED_CLIENT);
        specialStringMap.put(RES_EVENT_SERVER,RES_EVENT_CLIENT);

    }


    //private constructor
    private ActionModel(){}


    //factory method for singleton
    public synchronized static ActionModel getInstance(){
        return ActionModel._singleTon;
    }


    public Document generateActionModelDocument(InputStream stream) {
        try {
            return XMLUtil.toDocument(stream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String generateClientActionModel(ActionModelSupport actionModelInterface, Document actionModel) throws Exception {
        StringBuffer sb = new StringBuffer();
        sb.append("{");

        Element root = actionModel.getRootElement();
        Iterator contextIt = root.getChildren().iterator();
        while (contextIt.hasNext()) {
            Element context = (Element)contextIt.next();
            //context test
            if(context.getAttributeValue(TEST_ATTR) !=  null){
                if(!shouldInclude(actionModelInterface, context)){
                    continue;
                }
            }

            appendContext(context,sb);
            sb.append("[");
            generateChildActions(context, actionModelInterface, sb);
            sb.append("]");
            if (contextIt.hasNext()) {
                sb.append(",");
            }
        }
        int lastCharIndex = sb.length() - 1;
        if (sb.charAt(lastCharIndex) == ',') {
            sb.deleteCharAt(lastCharIndex);
        }
        sb.append("}");
        return sb.toString();
    }

    /*
     * Create an JSON object for each child action
     */
    private static void generateChildActions(Element parentNode, ActionModelSupport actionModelInterface, StringBuffer sb) throws Exception {
        Iterator actionIt = parentNode.getChildren().iterator();
        while (actionIt.hasNext()) {
            Element action = (Element)actionIt.next();
            if (action.getName().equals(CONDITION_ELEM)) {
                //Action is contingent on view model (server) condition
                if (shouldInclude(actionModelInterface, action)) {
                    //condition passed the test - the real action is the condition's child
                    //action = (Element)action.getChildren().get(0);
                    generateChildActions(action, actionModelInterface, sb);
                }
            } else {
                //special cases - generated selections
                if (action.getName().equals(GENERATED_OPTIONS_ELEM)) {
                    generateOptions(actionModelInterface, sb, action);
                } else if (action.getName().equals(GENERATE_FROM_TEMPLATE_ELEM)) {
                    generateFromTemplate(actionModelInterface, sb, action);
                } else {
                    //OK now we have an action for sure - render as a client action
                    generateAction(actionModelInterface, sb, action);
                }
                if(actionIt.hasNext()) {
                    //sb.append(",");
                }
            }
        }
    }

    private static void generateAction(ActionModelSupport actionModelInterface, StringBuffer sb, Element action)
            throws Exception {
        String actionType = action.getName();
        //Opener
        Character latestChar = sb.charAt(sb.length()-1);
        if (latestChar==']'||latestChar=='}') {
            sb.append(",");
        }
        sb.append("{");

        //attributes
        appendType(actionType,sb);
        appendStandardProperty(ID_KEY,action.getAttributeValue(ID_ATTR),sb);
        appendStandardProperty(CLIENT_TEST_KEY,action.getAttributeValue(CLIENT_TEST_ATTR),sb);
        appendStandardArgs(CLIENT_TEST_ARGS_KEY,action.getAttributeValue(CLIENT_TEST_ARGS_ATTR),sb);
        appendStandardProperty(SELECTION_CONSTRAINT_KEY,action.getAttributeValue(SELECTION_CONSTRAINT_ATTR),sb);
        appendStandardProperty(ALLOWS_INPUT_TEST_KEY,action.getAttributeValue(ALLOWS_INPUT_TEST_ATTR),sb);
        appendStandardArgs(ALLOWS_INPUT_TEST_ARGS_KEY,action.getAttributeValue(ALLOWS_INPUT_TEST_ARGS_ATTR),sb);
        appendStandardProperty(NO_ICON_INDENT_KEY,action.getAttributeValue(NO_ICON_INDENT_ATTR),sb);
        appendStandardProperty(BUTTON_KEY,action.getAttributeValue(BUTTON_ATTR),sb);
        appendStandardProperty(DISABLED_KEY,action.getAttributeValue(DISABLED_ATTR),sb);

        if (action.getAttributeValue(CLASS_NAME) != null) {
            appendStandardProperty(CLASS_NAME,action.getAttributeValue(CLASS_NAME),sb);
        }
        if (hasText(actionType)) {
            appendText(action, actionModelInterface, sb);
        }
        if (firesAction(actionType)) {
            appendStandardProperty(ACTION_KEY,action.getAttributeValue(ACTION_ATTR),sb);
            appendStandardArgs(ACTION_ARGS_KEY,action.getAttributeValue(ACTION_ARGS_ATTR),sb);
        }
        if (indicatesSelection(action)) {
            appendStandardProperty(IS_SELECTED_TEST_KEY,action.getAttributeValue(IS_SELECTED_TEST_ATTR),sb);
            appendStandardArgs(IS_SELECTED_TEST_ARGS_KEY,action.getAttributeValue(IS_SELECTED_TEST_ARGS_ATTR),sb);
        }
        if (hasChildren(actionType)) {
            appendChildren(action, actionModelInterface, sb);
        }

        //Final closure
        sb.append("}");
    }




    private static void appendContext(Element context, StringBuffer sb) {
        sb.append("\"");
        sb.append(context.getAttributeValue(NAME_ATTR));
        sb.append("\"");
        sb.append(":");
    }

    private static void appendType(String actionType, StringBuffer sb) {
        //key
        sb.append("\"");
        sb.append(TYPE_KEY);
        sb.append("\": ");
        //value
        sb.append("\"");
        sb.append(actionTypesMap.get(actionType));
        sb.append("\"");
    }

    private static void appendStandardProperty(String key, String value, StringBuffer sb) {
        if (value!=null) {
            sb.append(",");
            //key
            sb.append("\"");
            sb.append(key);
            sb.append("\": ");
            //value
            sb.append("\"");
            sb.append(value);
            sb.append("\"");
        }
    }

    private static void appendStandardArgs(String key, String args, StringBuffer sb) {
        if (args == null) {
            return;
        }
        sb.append(",");
        //key
        sb.append("\"");
        sb.append(key);
        sb.append("\": ");
        //value
        sb.append(formatDelimitedStringAsArrayString(args));
    }

    private static void appendText(Element action, ActionModelSupport actionModelContext, StringBuffer sb) throws Exception {
        sb.append(",");
        //key
        sb.append("\"");
        sb.append(TEXT_KEY);
        sb.append("\": ");
        //get actual text
        String labelKey = getLabelKey(action, actionModelContext);
        //value
        sb.append("\"");
        sb.append(getLocalizedValue(labelKey, actionModelContext));
        sb.append("\"");
    }

    private static String getLabelKey(Element action, ActionModelSupport actionModelContext) throws Exception {
        //simple i18n key or hard coded value?
        String labelKey = action.getAttributeValue(LABEL_KEY_ATTR);
        if (labelKey != null) {
            return swapOutSpecialStrings(labelKey);
        }
        //conditional?
        String labelCondition = action.getAttributeValue(LABEL_CONDITION_ATTR);
        if (labelCondition != null) {
            return getLabelForCondition(action, actionModelContext, labelCondition);
        }
        //function based?
        String labelFunction = action.getAttributeValue(LABEL_FUNCTION_ATTR);
        if (labelFunction != null) {
            return getLabelForFunction(action, actionModelContext, labelFunction, action.getAttributeValue(LABEL_FUNCTION_ARGS_ATTR));
        }

        throw new Exception("could not derive action text for action " + action);
    }

    private static String getLabelForCondition(Element elem, ActionModelSupport actionModelContext, String labelCondition) throws Exception {
        Method labelMethod = actionModelContext.getClass().getMethod(labelCondition, new Class[0]);
        String methodResult = (labelMethod.invoke(actionModelContext, new Object[0])).toString();

        Iterator labelOptionsIt = elem.getChildren(LABEL_OPTION_ELEM).iterator();
        while (labelOptionsIt.hasNext()) {
            Element thisLabelOption = (Element)labelOptionsIt.next();
            if (thisLabelOption.getAttributeValue(FUNCTION_RESPONSE_ATTR).equals(methodResult)) {
                return getLabelKey(thisLabelOption, actionModelContext);
            }
        }
        return null;
    }

    private static String getLabelForFunction(Element elem, ActionModelSupport actionModelContext, String labelFunction, String labelFunctionArgsString) throws Exception {
        Object[] args = labelFunctionArgsString == null ? new Object[0] : RegexUtil.getResourceKeyPattern(arrayStringDelimiterRegularExpression).split(labelFunctionArgsString);
        Class[] argTypes = RegexUtil.getArgTypesFromArgs(args);
        Method thisMethod = actionModelContext.getClass().getMethod(labelFunction, argTypes);
        return thisMethod.invoke(actionModelContext, args).toString();
    }

    private static void appendChildren(Element action, ActionModelSupport actionModelContext, StringBuffer sb) throws Exception{
        sb.append(",");
        //key
        sb.append("\"");
        sb.append(CHILDREN_KEY);
        sb.append("\": ");
        //value
        sb.append("[");
        generateChildActions(action, actionModelContext, sb);
        sb.append("]");
    }

    /*
     * Generate Option Elements Programmatically
     * The generator function can return either a Map or a Collection
     */
    private static void generateOptions(ActionModelSupport actionModelContext, StringBuffer sb, Element action) throws  Exception {
        //The generator function can return either a Map or a Collection
        //if it returns a map then use key and value for optionId and optionValue respectively
        //if it returns a Collection then use each element (toString) as both optionId and optionValue
        //Use optionValues as defaults for labels
        //Use optionIds as defaults for tests, functions etc.
        Object options = getGenerationSource(actionModelContext,action.getAttributeValue(FUNCTION_ATTR),action.getAttributeValue(FUNCTION_ARGS_ATTR));
        boolean usingMap;
        Iterator optionsIt;
        if (options instanceof Map) {
            optionsIt = ((Map)options).keySet().iterator();
            usingMap = true;
        } else {
            optionsIt = ((Collection)options).iterator();
            usingMap = false;
        }
        while (optionsIt.hasNext()) {
            Object thisOption = optionsIt.next();
            String optionId = thisOption.toString();
            String optionValue;
            if (usingMap) {
                optionValue = ((Map)options).get(thisOption).toString();
            } else {
                optionValue = optionId;
            }
            Element optionAction = new Element(OPTION_ELEM);
            String labelExpression = action.getAttributeValue(LABEL_EXPRESSION_ATTR);
            if (labelExpression != null) {
                optionAction.setAttribute(LABEL_KEY_ATTR,generateOptionLabel(actionModelContext, labelExpression, optionValue, optionId));
            } else {
                //default label is the option value
                optionAction.setAttribute(LABEL_KEY_ATTR,optionValue);
            }
            optionAction.setAttribute(ACTION_ATTR,action.getAttributeValue(ACTION_ATTR));
            optionAction.setAttribute(ACTION_ARGS_ATTR,getAsOptionArgs(action.getAttributeValue(ACTION_ARGS_ATTR),optionValue, optionId));
            
            if (action.getAttributeValue(IS_SELECTED_TEST_ATTR) != null) {
                optionAction.setAttribute(IS_SELECTED_TEST_ATTR,action.getAttributeValue(IS_SELECTED_TEST_ATTR));
                optionAction.setAttribute(IS_SELECTED_TEST_ARGS_ATTR,getAsOptionArgs(action.getAttributeValue(IS_SELECTED_TEST_ARGS_ATTR),optionValue, optionId));
            }
            if (action.getAttributeValue(CLIENT_TEST_ATTR) != null) {
                optionAction.setAttribute(CLIENT_TEST_ATTR,action.getAttributeValue(CLIENT_TEST_ATTR));
                optionAction.setAttribute(CLIENT_TEST_ARGS_ATTR,getAsOptionArgs(action.getAttributeValue(CLIENT_TEST_ARGS_ATTR),optionValue, optionId));
            }
            if (action.getAttributeValue(ALLOWS_INPUT_TEST_ATTR) != null) {
                optionAction.setAttribute(ALLOWS_INPUT_TEST_ATTR,action.getAttributeValue(ALLOWS_INPUT_TEST_ATTR));
                optionAction.setAttribute(ALLOWS_INPUT_TEST_ARGS_ATTR,getAsOptionArgs(action.getAttributeValue(ALLOWS_INPUT_TEST_ARGS_ATTR),optionValue, optionId));
            }

            if(action.getAttributeValue(CLASS_NAME) != null){
                optionAction.setAttribute(CLASS_NAME,action.getAttributeValue(CLASS_NAME));
            }
            if (action.getAttributeValue(ID_ATTR) != null) {
                optionAction.setAttribute(ID_ATTR,getAsOptionArgs(action.getAttributeValue(ID_ATTR),optionValue, optionId));
            } else {
                //default id is the option id
                optionAction.setAttribute(ID_ATTR,optionId);
            }

            generateAction(actionModelContext, sb, optionAction);
            if (optionsIt.hasNext()) {
                sb.append(",");
            }
        }
    }

    private static String getAsOptionArgs(String argsString, String optionValue, String optionId) {
        if (argsString==null) {
            //if no explicit arg string supplied, use the optionId itself as an arg
            return optionId;
        }
        argsString = argsString.replace(RES_OPTION_VALUE_SERVER, optionValue);
        argsString = argsString.replace(RES_OPTION_ID_SERVER, optionId);

        return argsString;
    }

    private static String generateOptionLabel( ActionModelSupport actionModelContext, String labelExpression, String optionValue, String optionId) {
        //first substitute option value (assumes just one of these)
        labelExpression = labelExpression.replace(RES_OPTION_VALUE_SERVER, optionValue);
        labelExpression = labelExpression.replace(RES_OPTION_ID_SERVER, optionId);
        // now look for $R{...} pattern and resolve i18n value (assumes just one of these)
        Matcher matcher = RegexUtil.getResourceKeyPattern(resourceKeyRegularExpression).matcher(labelExpression);
        if (matcher.find()) {
            String resourceKeyPattern = labelExpression.substring(matcher.start(), matcher.end());
            String resourceKey = resourceKeyPattern.substring(3, resourceKeyPattern.length()-1);
            String translation = getLocalizedValue(resourceKey, actionModelContext);
            return labelExpression.replace(resourceKeyPattern, translation);
        } else {
            return labelExpression;
        }
    }

    /*
     * Generate Multiple Actions Programatically, based on the enclosed template structure
     * Enclosed template can be of any form, and can include multiple actions and nested actions
     * The generator function can return either a Map or a Collection
     */
    private static void generateFromTemplate(ActionModelSupport actionModelContext, StringBuffer sb, Element action) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, Exception {
        //The generator function can return either a Map or a Collection
        //if it returns a map then use key and value for templateInjectId and templateInjectValue respectively
        //if it returns a Collection then use each element (toString) as both templateInjectId and templateInjectValue
        //
        Object templateInjectors = getGenerationSource(actionModelContext,action.getAttributeValue(FUNCTION_ATTR),action.getAttributeValue(FUNCTION_ARGS_ATTR));
        boolean usingMap;
        Iterator templateInjectorsIt;
        if (templateInjectors instanceof Map) {
            templateInjectorsIt = ((Map)templateInjectors).keySet().iterator();
            usingMap = true;
        } else {
            templateInjectorsIt = ((Collection)templateInjectors).iterator();
            usingMap = false;
        }
        templateInjectionIndex = -1;
        while (templateInjectorsIt.hasNext()) {
            templateInjectionIndex++;
            Object thisTemplateInjector = templateInjectorsIt.next();
            templateInjectionId = thisTemplateInjector.toString();
            if (usingMap) {
                templateInjectionValue = ((Map)templateInjectors).get(thisTemplateInjector).toString();
            } else {
                templateInjectionValue = templateInjectionId;
            }
            Iterator<Element> childActionIt = action.getChildren().iterator();
            while (childActionIt.hasNext()) {
                Element childAction = childActionIt.next();
                generateAction(actionModelContext, sb, childAction);
            }
            if (templateInjectorsIt.hasNext()) {
                sb.append(",");
            }
        }
    }

    private static String getLocalizedValue(String labelKey, ActionModelSupport actionModelContext) {
        return actionModelContext.getMessage(labelKey);
    }

    private static boolean firesAction(String actionType) {
        return directActionTypes.contains(actionType);
    }


    private static boolean hasText(String actionType) {
        return !actionType.equals(SEPARATOR_ELEM);
    }

    private static boolean indicatesSelection(Element action) {
        return action.getAttribute(IS_SELECTED_TEST_ATTR) != null;
    }

    private static boolean hasChildren(String actionType) {
        return parentActionTypes.contains(actionType);
    }

    private static boolean shouldInclude(ActionModelSupport actionModelContext, Element action) throws Exception {
        boolean passingResult = true;
        String test = action.getAttributeValue(TEST_ATTR);
        if (test.indexOf("!")==0) {
            //leading ! means test for false
            test = test.substring(1);
            passingResult = false;
        }
        String testArgsString = action.getAttributeValue(TEST_ARGS_ATTR);
        Object result = false;
        Method testMethod = null;
        Object[] testArgs = testArgsString == null ? new Object[0] : RegexUtil.getResourceKeyPattern(arrayStringDelimiterRegularExpression).split(testArgsString);
        Class[] testArgTypes = RegexUtil.getArgTypesFromArgs(testArgs);
        try {
            testMethod = actionModelContext.getClass().getMethod(test, testArgTypes);
            result = testMethod.invoke(actionModelContext, testArgs);
        } catch (NoSuchMethodException ex) {
            log.error("Test method '" + test + "' not found. Please correct the action model xml. " +
                    "All underlying elements are skipped.");
            return false; //no method - default to  don't include
        }
        catch (IllegalAccessException ex) {
            return false; //can't access - default to  don't include
        }
        catch (InvocationTargetException ex) {
            return false; //can't invoke - default to  don't include
        }
        if (result instanceof Boolean) {
            return ((Boolean)result).booleanValue() == passingResult;
        } else {
            throw new Exception("Test " + testMethod + "should return a boolean");
        }
    }


    /*
     * Returns either a Map or a Collection to be injected into option or template
     */
    private static Object getGenerationSource(ActionModelSupport actionModelContext, String function, String functionArgsString) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, Exception {
        Object[] args = functionArgsString == null ? new Object[0] : RegexUtil.getResourceKeyPattern(arrayStringDelimiterRegularExpression).split(functionArgsString);
        Class[] argTypes = RegexUtil.getArgTypesFromArgs(args);
        Method optionsMethod = actionModelContext.getClass().getMethod(function, argTypes);
        Object options = optionsMethod.invoke(actionModelContext, args);
        if (options instanceof Collection || options instanceof Map) {
            return options;
        } else {
            throw new Exception("function " + optionsMethod + "should return a Collection or a Map");
        }
    }

    /*
     * e.g. ${selected} is for currently selected objects on client
     */
    private static String swapOutSpecialStrings(String input) {
        Iterator keysIt = specialStringMap.keySet().iterator();
        while (keysIt.hasNext()) {
            String thisKey = (String) keysIt.next();
            input = input.replace(thisKey, specialStringMap.get(thisKey));
        }
        //global template var injection
        input = input.replace(RES_TEMPLATE_INJECTION_INDEX, templateInjectionIndex + "");
        input = input.replace(RES_TEMPLATE_INJECTION_ID, templateInjectionId);
        input = input.replace(RES_TEMPLATE_INJECTION_VALUE, templateInjectionValue);
        return input;
    }

    /*
     * "Elephant@@${label}@@Polar Bear@@Zebra" -> "['Elephant','$label','Polar Bear','Zebra']"
     * Swaps out reserved strings too
     */
    private static StringBuffer formatDelimitedStringAsArrayString(String delimitedString) {
        StringTokenizer tokenizer = new StringTokenizer(delimitedString,"@@");
        StringBuffer valueBuffer = new StringBuffer();
        valueBuffer.append("[");
        while (tokenizer.hasMoreTokens()) {
            valueBuffer.append("\"");
            valueBuffer.append(swapOutSpecialStrings(tokenizer.nextToken()));
            valueBuffer.append("\"");
            if (tokenizer.hasMoreTokens()) {
                valueBuffer.append(",");
            }
        }
        valueBuffer.append("]");
        return valueBuffer;
    }



}
