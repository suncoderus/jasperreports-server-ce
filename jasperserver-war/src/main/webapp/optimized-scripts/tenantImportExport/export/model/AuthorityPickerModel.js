define(["require","jquery","backbone","components.templateengine","underscore"],function(e){"use strict";var t=e("jquery"),r=e("backbone"),n=e("components.templateengine"),s=e("underscore");return r.Model.extend({defaults:{items:[]},url:function(){return n.renderUrl(this.urlTemplate,this.context,!0)},parse:function(e){return{items:e?e.role?e.role:e.user:[]}},defaultErrorHandler:function(e,r){var n;try{n=t.parseJSON(r.responseText)}catch(s){n=null}this.trigger("error:server",r.status,n,e)},setContext:function(e){this.context=s.extend(this.context||{},e),this.fetch({error:s.bind(this.defaultErrorHandler,this)})}},{instance:function(e,t){var r=new this;return r.urlTemplate=e,t&&r.setContext(t),r}})});