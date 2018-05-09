define(function() {
    return {
        processItem: function(getLevelNesting, paddingValue, item) {
            item.padding = getLevelNesting(item.id) * paddingValue;
            return item;
        }
    }
});
