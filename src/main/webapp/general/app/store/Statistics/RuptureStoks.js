/* global Ext */

Ext.define('testextjs.store.Statistics.RuptureStoks', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.RuptureStock'
    ],
   
    model:'testextjs.model.statistics.RuptureStock',
    pageSize:20,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url:'../webservices/Report/RuptureStock/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
         timeout: 240000
    }
});