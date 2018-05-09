define(function() {
    return {
        processItem: function(item) {
            item.value = item.id;
            return item;
        }
    }
});
