/* global Ext */

Ext.define('testextjs.store.Statistics.Retrocessions', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.AchatProduit'
    ],
    model: 'testextjs.model.statistics.AchatProduit',
    autoLoad: true,
    pageSize:20,
    proxy: {
        type: 'ajax',
        url: '../webservices/Report/Retrocessions/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
        timeout: 60000
    }
});