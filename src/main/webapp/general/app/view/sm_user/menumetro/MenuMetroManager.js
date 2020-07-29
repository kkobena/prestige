Ext.define('testextjs.view.sm_user.menumetro.MenuMetroManager', {
    extend: 'Ext.window.Window',
    xtype: 'menumetromanager',
    id: 'menumetromanagerID',
    requires: [
    'Ext.ux.IFrame'
    ],
    layout: 'fit',
    title: 'Menu Principal',
    width: '98%',
    height: valheight,
    border: false,
    
    
    initComponent: function() {

     
        this.items = [{
            xtype: "component",
            autoEl: {
                tag: "iframe",
                src: 'www.google.com'
            }
        }],
        this.callParent();

    }
})



