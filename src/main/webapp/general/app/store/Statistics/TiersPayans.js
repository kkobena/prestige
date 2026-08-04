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
        // API REST existante (remplace ws_data_search.jsp) : memes cles data/total,
        // champs mappes dans testextjs.model.statistics.TiersPayant
        url: '../api/v1/client/tiers-payants',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
        timeout: 240000
    }
});