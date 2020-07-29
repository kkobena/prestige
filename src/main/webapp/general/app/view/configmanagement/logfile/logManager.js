/* global Ext */

Ext.define('testextjs.view.configmanagement.logfile.logManager', {
    extend: 'Ext.panel.Panel',
    xtype: 'logfile',
    id: 'logfileID',
    requires: [
        'testextjs.view.configmanagement.logfile.logGrid'

    ],
    frame: true,
    title: 'Fichier Journal',
    width: '98%',
    height: 570,
    minHeight: 570,
    maxHeight: 800,
    cls: 'custompanel',
//    autoScroll: true,
    layout: 'fit',
    items: [{
            xtype: 'logfile-grid'

        }


    ]
    
    
   

});


