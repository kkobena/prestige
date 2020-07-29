/* global Ext */

Ext.define('testextjs.store.Statistics.Ventes', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.Vente'
    ],

    model: 'testextjs.model.statistics.Vente',
    pageSize: 20,
    autoLoad: true,
    sorters: [
    {
            property: 'year',
            direction: 'ASC'
        },
        {
            property: 'num',
            direction: 'ASC'
        }
    ],
   
    proxy: {
        type: 'ajax',
        url: '../webservices/Report/statistiquevente/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
        timeout: 240000
    }
});
