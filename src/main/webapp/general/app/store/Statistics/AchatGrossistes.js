Ext.define('testextjs.store.Statistics.AchatGrossistes', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.AchatGrossiste'
    ],

    model:'testextjs.model.statistics.AchatGrossiste',
   pageSize:20,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url:'../webservices/Report/achatfournisseurs/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
         timeout: 240000
    }
});
