/* global Ext */

Ext.define('testextjs.store.Statistics.AchatProduits', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.AchatProduit'
    ],
   
    model:'testextjs.model.statistics.AchatProduit',
    groupField: 'ANNEE',
    pageSize:20,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url:'../webservices/Report/achatproduits/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
         timeout: 1440000
    }
});