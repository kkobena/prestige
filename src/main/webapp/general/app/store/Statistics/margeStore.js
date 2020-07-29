
/* global Ext */

Ext.define('testextjs.store.Statistics.margeStore', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.margeModel'
    ],
    model: 'testextjs.model.statistics.margeModel',
    pageSize: 20,
   
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '../webservices/Report/marge/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        }
        
    }
});