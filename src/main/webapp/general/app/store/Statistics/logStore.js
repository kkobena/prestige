
/* global Ext */

Ext.define('testextjs.store.Statistics.logStore', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.logModel'
    ],
    model: 'testextjs.model.statistics.logModel',
    pageSize:6,
   
    autoLoad: false,
    proxy: {
        type: 'ajax',
        url: '../webservices/configmanagement/logfile/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        }
        
    }
});