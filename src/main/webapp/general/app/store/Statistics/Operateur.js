Ext.define('testextjs.store.Statistics.Operateur', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.Operateur'
    ],

    model:'testextjs.model.statistics.Operateur',
   pageSize:20,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url:'../webservices/Report/Operateur/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
         timeout: 240000
    }
});
