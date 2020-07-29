Ext.define('testextjs.view.ContentPanel', {
    extend: 'Ext.panel.Panel',
    xtype: 'contentPanel',
    id: 'content-panel',
    border: false,
    bodyStyle: "background-color:red;",
    title: '&nbsp;',
   
    // autoScroll: true,
    requires:[
    'testextjs.view.dashboard'
    ],
    items: [
        {

            /* xtype: 'mainmenumanager'*/ //appel du metro
          //  xtype: 'dashboard'
          xtype: xtypeload

        /*    
            xtype: 'mainmenumanager' 
 xtype: 'testmenu'*/

            
 


        }
    ]

});