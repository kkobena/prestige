
/* global Ext */

Ext.define('testextjs.store.Statistics.OrderQty', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.QtyModel'
    ],
    model: 'testextjs.model.statistics.QtyModel',
    pageSize: 20,
    storeId: 'orderqty',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '../webservices/Report/qtyorder/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        }
        
    }
});