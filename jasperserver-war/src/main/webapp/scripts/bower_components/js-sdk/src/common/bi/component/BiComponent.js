define(function () {

    /**
     * @param {Object} properties - bi component properties
     * @constructor
     */

    function BiComponent(properties){}

    //Setters/Getters

    /**
     * Get/Set server settings
     * @param {String} server  - url to server
     * @returns this if 'arguments' send to the method,
     *          otherwise returns server settings
     */

    BiComponent.prototype.server = function(server){};

    /**
     * Get/Set bi component properties
     * @param {Object} properties  - url to server
     * @returns this if 'arguments' send to the method,
     *          otherwise returns bi properties
     */

    BiComponent.prototype.properties = function(properties){};

    /**
     * Get any result after invoking run action, null by default
     * @returns any data which supported by this bi component
     */
    BiComponent.prototype.data = function(){};

    //Actions

    /**
     * Perform main action for bi component
     * Callbacks will be attached to  deferred object.
     *
     * @param {Function} [callback] - optional, invoked in case of successful run
     * @param {Function} [errorback] - optional, invoked in case of failed run
     * @param {Function} [always] - optional, invoked always
     * @return {Deferred} dfd
     */
    BiComponent.prototype.run = function(callback, errorback, always){};

    /**
     *  Validate bi component properties,
     *  in generally, should covers client-side validation issues
     *  @return {Error} err - if properties is not valid state, null otherwise
     */

    BiComponent.prototype.validate = function(){};



    return BiComponent;
});