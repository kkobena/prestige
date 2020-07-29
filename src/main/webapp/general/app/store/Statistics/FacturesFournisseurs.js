/* global Ext */

Ext.define('testextjs.store.Statistics.FactureFournisseurs', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.FactureFournisseurs'
    ],
   
    model:'testextjs.model.statistics.FactureFournisseurs',
    pageSize:20,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url:'../webservices/Report/facturefournisseurs/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
         timeout: 240000
    }
});