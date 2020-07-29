Ext.define('testextjs.view.layout.West', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.layout.west',
    requires: [
    'testextjs.view.layout.Menu'
    ],
    region: 'west',
    title: 'Menu',
    split: true,
    width: 200,
    initComponent: function(){
        var me = this;
        Ext.applyIf(me,{
            items: [
            {
                xtype: 'layout.menu'
            }
            ]
        });
        me.callParent( arguments );
    }
});