/*
 * Copyright (C) 2005 - 2018 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @author: Zakhar.Tomchenko
 * @version: $Id$
 */

define(function(require, exports, module) {
    "use strict";

    require("underscore.string");

    var tv4 = require("tv4"),
        $ = require("jquery"),
        Backbone = require("backbone"),
        _ = require("underscore"),
        log = require("logger").register(module),
        biComponentErrorFactory = require("../../error/biComponentErrorFactory");

    function checkAlreadyDestroyed(stateModel) {
        if (stateModel && stateModel instanceof Backbone.Model && stateModel.get("_destroyed")) {
            throw biComponentErrorFactory.alreadyDestroyedError();
        }
    }

    var biComponentUtil = {
        // shortcut function to not deep clone jquery objects
        cloneDeep: function(obj) {
            return _.cloneDeep(obj, function(o) {
                if (o instanceof $) {
                    return o;
                }
                if (_.isArray(o) && o.parameters) {
                    var array = _.cloneDeep(o);
                    array.parameters = _.cloneDeep(o.parameters);
                    return array;
                }
            });
        },

        createField: function(object, propertyName, instanceData, readOnly, stateModel) {
            object[propertyName] = function(value) {
                checkAlreadyDestroyed(stateModel);

                if (!arguments.length) {
                    return biComponentUtil.ES5Object(biComponentUtil.cloneDeep(instanceData[propertyName]));
                }

                var func = biComponentUtil.cloneDeep(value);
                if (readOnly && readOnly.length) {
                    for (var i = 0; i < readOnly.length; i++) {
                        delete func[readOnly[i]];
                    }
                }
                instanceData[propertyName] = _.extend({}, instanceData[propertyName], func);
                if(stateModel !== undefined && propertyName == "properties"){
                    stateModel.set(biComponentUtil.cloneDeep(instanceData[propertyName]));
                }
                return object;
            }
        },

        createReadOnlyField: function(object, fieldName, instanceData, throwError, stateModel) {
            object[fieldName] = function(value) {
                checkAlreadyDestroyed(stateModel);

                if (!arguments.length) {
                    return biComponentUtil.ES5Object(biComponentUtil.cloneDeep(instanceData[fieldName]));
                }

                if (throwError){
                    throw new Error("The field '" + fieldName + "' cannot be set at this moment.");
                }

                return object;
            }
        },

        createProperty: function(object, propertyName, instanceData, stateModel) {
            object[propertyName] = function(value) {
                checkAlreadyDestroyed(stateModel);

                if (!arguments.length) {
                    return instanceData.properties[propertyName];
                }

                instanceData.properties[propertyName] = value;

                if( stateModel && stateModel instanceof Backbone.Model) {
                    stateModel.set(propertyName, biComponentUtil.cloneDeep(instanceData.properties[propertyName]));
                }

                return object;
            }
        },

        createReadOnlyProperty: function(object, propertyName, instanceData, throwError, stateModel) {
            object[propertyName] = function(value) {
                checkAlreadyDestroyed(stateModel);

                if (!arguments.length) {
                    return instanceData.properties[propertyName];
                }

                if (throwError){
                    throw new Error("The property '" + propertyName + "' cannot be set at this moment.");
                }

                return object;
            }
        },

        createValidateAction: function(instanceData, schema, stateModel) {
            return function() {
                checkAlreadyDestroyed(stateModel);

                return biComponentUtil.validateObject(schema, instanceData.properties);
            }
        },

        createInstancePropertiesAndFields: function(context, instanceData, propertyNames, fieldNames, readOnlyFieldNames, stateModel) {
            function createField(object, propertyName){
                biComponentUtil.createField(object, propertyName, this, false, stateModel);
                return object;
            }

            function createReadOnlyField(object, propertyName){
                biComponentUtil.createReadOnlyField(object, propertyName, this, true, stateModel);
                return object;
            }

            function createProperty(object, propertyName){
                biComponentUtil.createProperty(object, propertyName, this, stateModel);
                return object;
            }

            _.reduce(propertyNames, _.bind(createProperty, instanceData), context);
            _.reduce(fieldNames, _.bind(createField, instanceData), context);
            _.reduce(readOnlyFieldNames, _.bind(createReadOnlyField, instanceData), context);
        },

        createDeferredAction: function() {
            var actionArguments = arguments;

            return function(successCallback, errorCallback, completeCallback){
                var dfd = new $.Deferred();

                successCallback && _.isFunction(successCallback) && dfd.done(successCallback);
                errorCallback && _.isFunction(errorCallback) && dfd.fail(errorCallback);
                completeCallback && _.isFunction(completeCallback) && dfd.always(completeCallback);

                try {
                    var actionFunc = actionArguments[0],
                        stateModel = actionArguments[1],
                        args = Array.prototype.slice.call(actionArguments, 2);

                    if (stateModel.get("_destroyed")) {
                        var err = biComponentErrorFactory.alreadyDestroyedError();

                        log.error(err.toString());
                        dfd.reject(err);
                    } else {
                        args.unshift(dfd);

                        actionFunc.apply(this, args);
                    }
                }
                catch (ex) {
                    var e = biComponentErrorFactory.javaScriptException(ex);

                    log.error(e.toString());
                    dfd.reject(e);
                }

                return dfd;
            }
        },

        validateObject: function(schema, obj) {
            var result = tv4.validateResult(obj, schema, false, true);

            if (!result.valid) {
                return convertValidationError(result.error);
            }
        },

        ES5Object: function(object) {
            if (_.isArray(object)) {
                return this.ES5Array(object);
            }

            if (_.isObject(object)) {
                _.each(_.keys(object), function(key) {
                    biComponentUtil.ES5Object(object[key]);
                });
            }

            return object;
        },

        ES5Array: function(array){
            if (!_.isArray(array)){
                throw new Error("Must be array!", array);
            }

            array.indexOf || (array.indexOf = _.bind(_.indexOf, array, array));
            array.lastIndexOf || (array.lastIndexOf = _.bind(_.lastIndexOf, array, array));
            array.forEach || (array.forEach = _.bind(_.each, array, array));
            array.every || (array.every = _.bind(_.every, array, array));
            array.some || (array.some = _.bind(_.some, array, array));
            array.map || (array.map = _.bind(_.map, array, array));
            array.filter || (array.filter = _.bind(_.filter, array, array));
            array.reduce || (array.reduce = _.bind(_.reduce, array, array));
            array.reduceRight || (array.reduceRight = _.bind(_.reduceRight, array, array));

            return array;
        },

        bindContextToArgument: function(context, obj) {

            if (!obj) {
                // null safety
                return obj;
            }

	        if (_.isFunction(obj.done) && _.isFunction(obj.fail) && _.isFunction(obj.progress)) {
                // it's a Deferred. Let's proxy it with another Deferred with context bound
                var dfd = $.Deferred();
                obj.progress(function(){
                    dfd.notifyWith(context, arguments);
                });
                obj.fail(function(){
                    dfd.rejectWith(context, arguments);
                });
                obj.done(function(){
                    dfd.resolveWith(context, arguments);
                });
                return dfd;
            } else if (_.isString(obj.jquery)) {
                // it's jQuery object. Don't need to proxy it. Return original object.
                return obj;
	        } else if (isElement(obj)) {
		        // it's DOM element. Don't need to proxy it. Return original object.
		        return obj;
            } else if (_.isFunction(obj)) {
                // it's a function. Bind context to it here
                return _.bind(obj, context);
            } else if (_.isArray(obj)) {
	            // it's array, iterate over it and apply this function recursively.
	            var mapped =  _.map(obj, function (element) {
		            return biComponentUtil.bindContextToArgument(context, element);
	            });
                obj.parameters && (mapped.parameters = _.cloneDeep(obj.parameters));
                return mapped;
            } else if(_.isObject(obj)){
                // it's an object, iterate over it's fields and apply this function recursively.
                return _.reduce(obj, function(memo, element, key){
	                memo[key] = biComponentUtil.bindContextToArgument(context, element);
                    return memo;
                }, {});
            }

	        return obj;
        }
    };

    return biComponentUtil;

    function convertValidationError(validationError){

        var result =  validationError;

        if (validationError.subErrors && validationError.subErrors.length){

            var subErrors = _.filter(validationError.subErrors, function(subError){
                return subError.code != tv4.errorCodes.ENUM_MISMATCH;
            });

            var nonEnumMismatchError =  subErrors.length > 0;

            if (nonEnumMismatchError){
                result =  convertValidationError(subErrors[0]);
            }else{
                //enum mismatch
                //workaround for http://jira.jaspersoft.com/browse/JRS-1668
                var noChildSubErrors = _.all(validationError.subErrors, function (err){
                    return !_.isUndefined(err.subErrors)
                });

                if (noChildSubErrors){
                     result = _.max(validationError.subErrors, function(err){
                           var dataPath = err.dataPath;
                           //find most deep data path
                           return _.count(dataPath, "/");
                     });
                }
            }
        }
        return result;
    }

	//Returns true if it is a DOM element
	function isElement(o) {
		return (
			typeof HTMLElement === "object" ? o instanceof HTMLElement : //DOM2
			o && typeof o === "object" && o !== null && o.nodeType === 1 && typeof o.nodeName==="string"
		);
	}
});