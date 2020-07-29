Ext.define('testextjs.store.Statistics.FamillesCA', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.FamilleCA'
    ],

    model:'testextjs.model.statistics.FamilleCA',
    storeId: 'FamilleCAStore',
    autoLoad: true,
    pageSize:20,
    proxy: {
        type: 'ajax',
        url:'../webservices/Report/comparaisonChiffreAffaire/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
         timeout: 240000
    }
});
