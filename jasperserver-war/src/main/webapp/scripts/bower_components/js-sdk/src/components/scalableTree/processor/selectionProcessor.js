define(function() {
    return {
        processItem: function(viewState, controller, item) {

            if (item.addToSelection) {
                controller.select(item.id);
            } else {
                item.addToSelection = viewState.isSelected(item.id)
            }

            return item;
        }
    }
});
