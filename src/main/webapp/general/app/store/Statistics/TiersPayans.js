/* global Ext */

Ext.define('testextjs.store.Statistics.TiersPayans', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.TiersPayant'
    ],
    model: 'testextjs.model.statistics.TiersPayant',
    pageSize: 10,
    storeId: 'tierspayant',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '../webservices/tierspayantmanagement/tierspayant/ws_data_search.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
        timeout: 240000
    }
});