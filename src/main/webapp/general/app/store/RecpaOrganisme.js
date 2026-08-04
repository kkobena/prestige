/* global Ext */

Ext.define('testextjs.store.RecpaOrganisme', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.RecapOrganisme'
    ],
    model: 'testextjs.model.RecapOrganisme',
    autoLoad: true,
   // groupField: 'FULNAME',
    pageSize:20,  
    proxy: {
        type: 'ajax',
        url: '../api/v1/recap-organisme/list',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
        timeout: 240000
    }
});