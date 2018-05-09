define(function() {

    return function(id) {
        var regexString = "^" + id;

        if (id !== "/") {
            regexString += "/";
        }

        return new RegExp(regexString);
    }
});