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
        url: '../webservices/sm_user/RecapOrganisme/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
        timeout: 240000
    }
});