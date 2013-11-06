/**
 * PartnershipHoverbox is a class for all the UI elements and graphics surrounding a Partnership node and
 * its labels. This includes the box that appears around the node when it's hovered by a mouse, as
 * well as the handles used for creating connections and creating new nodes.
 *
 * @class PartnershipHoverbox
 * @extends AbstractHoverbox
 * @constructor
 * @param {Partnership} partnership The Partnership for which the hoverbox is drawn
 * @param {Number} junctionX The x coordinate around which the partnership bubble is centered
 * @param {Number} junctionY The y coordinate around which the partnership bubble is centered
 * @param {Raphael.st} shapes Raphaël set containing the graphical elements that make up the node
 */

var PartnershipHoverbox = Class.create(AbstractHoverbox, {

    initialize: function($super, partnership, junctionX, junctionY, shapes) {
        var radius = PedigreeEditor.attributes.radius;        
        $super(partnership, -radius/1.5, -radius/1.5, radius*(4/3), radius*2.1, junctionX, junctionY, shapes);
        this._isMenuToggled = false;
    },

    /**
     * Creates the handles used in this hoverbox
     *
     * @method generateHandles
     * @return {Raphael.st} A set of handles
     */
    generateHandles: function($super) {
        this._downHandle = this.generateHandle('child', this.getNodeX(), this.getNodeY() + (PedigreeEditor.attributes.radius *.9));
        return $super().push(this._downHandle);
    },

    /**
     * Creates the buttons used in this hoverbox
     *
     * @method generateButtons
     * @return {Raphael.st} A set of buttons
     */
    generateButtons: function($super) {
        var deleteButton = this.generateDeleteBtn();
        var menuButton   = this.generateMenuBtn();
        return $super().push(deleteButton, menuButton);
    },

    /**
     * Hides the child handle
     *
     * @method hideChildHandle
     */
    hideChildHandle: function() {
        this.getCurrentHandles().exclude(this._downHandle.hide());
    },

    /**
     * Unhides the child handle
     *
     * @method unhideChildHandle
     */
    unhideChildHandle: function() {
        if(this.isHovered() || this.isMenuToggled()) {
            this._downHandle.show();
        }
        (!this.getCurrentHandles().contains(this._downHandle)) && this.getCurrentHandles().push(this._downHandle);
    },

    /**
     * Returns true if the menu is toggled for this partnership node
     *
     * @method isMenuToggled
     * @return {Boolean}
     */
    isMenuToggled: function() {
        return this._isMenuToggled;
    },

    /**
     * Shows/hides the menu for this partnership node
     *
     * @method toggleMenu
     * @param {Boolean} isMenuToggled Set to True to make the menu visible
     */
    toggleMenu: function(isMenuToggled) {
        this._isMenuToggled = isMenuToggled;
        if(isMenuToggled) {
            var optBBox = this.getBoxOnHover().getBBox();
            var x = optBBox.x2;
            var y = optBBox.y;
            var position = editor.getWorkspace().canvasToDiv(x+5, y);
            editor.getPartnershipMenu().show(this.getNode(), position.x, position.y);
        }
        else {
            editor.getPartnershipMenu().hide();
        }
    },
    
    /**
     * Hides the hoverbox with a fade out animation
     *
     * @method animateHideHoverZone
     */
    animateHideHoverZone: function($super) {
        if(!this.isMenuToggled()){
            $super();
        }
    },    

    /**
     * Performs the appropriate action for clicking on the handle of type handleType
     *
     * @method handleAction
     * @param {String} handleType Can be either "child", "partner" or "parent"
     * @param {Boolean} isDrag Set to True if the handle is being dragged at the time of the action
     */
    handleAction : function(handleType, isDrag, curHoveredId) {
        if(isDrag && curHoveredId) {            
            if(handleType == "child") { 
                var event = { "personID": curHoveredId, "parentID": this.getNode().getID() };
                document.fire("pedigree:person:drag:newparent", event);
            }
        }
        else if (!isDrag && handleType == "child") {
            var position = editor.getWorkspace().canvasToDiv(this.getNodeX(), (this.getNodeY() + PedigreeEditor.attributes.radius * 2.3));
            editor.getNodetypeSelectionBubble().show(this.getNode(), position.x, position.y);
            // if user selects anything the bubble will fire an even on its own
        }
        this.animateHideHoverZone();        
    }
});
