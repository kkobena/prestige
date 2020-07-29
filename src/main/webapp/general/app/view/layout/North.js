Ext.define('testextjs.view.layout.North', {
    extend: 'Ext.panel.Panel',
    // alias allows us to define a custom xtype for this component, which we can use as a shortcut
    // for adding this component as a child of another
    alias: 'widget.layout.north',
    region: 'north',
    bodyPadding: 5,
    html: '<img src="resources/images/car.png" /><h1>Car Tracker</h1>',
    cls: 'header',
    initComponent: function(){
        var me = this;
        Ext.applyIf(me,{

        });
        me.callParent( arguments );
    }
});