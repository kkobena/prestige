/* global Ext */

Ext.define('testextjs.store.Statistics.QtyAchatsStore', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.QtyAchats'
    ],
    model: 'testextjs.model.statistics.QtyAchats',
    pageSize: 20,
    storeId: 'orderqty',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '../webservices/Report/statsAchats/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        }
        
    }
});


