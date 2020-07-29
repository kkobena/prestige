/* global Ext */

Ext.define('testextjs.store.Statistics.Articles', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.Articles'
    ],

    model:'testextjs.model.statistics.Articles',
    pageSize: 20,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url:'../webservices/Report/statistiqueunitevente/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
         timeout: 240000
    }
});
