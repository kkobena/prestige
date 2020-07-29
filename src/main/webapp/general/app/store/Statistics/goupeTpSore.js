/* global Ext */

Ext.define('testextjs.store.Statistics.goupeTpSore', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.TiersPayant'
    ],
    model: 'testextjs.model.statistics.TiersPayant',
    pageSize: 10,
    storeId: 'tierspayantgp',
    autoLoad: false,
    proxy: {
        type: 'ajax',
        url: '../webservices/configmanagement/groupe/ws_tiers_payantseach.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        }
        
    }
});