Ext.define('testextjs.store.Statistics.AnalyseVente', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.AnalyseVenteStock'
    ],

    model:'testextjs.model.statistics.AnalyseVenteStock',
   pageSize:20,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url:'../webservices/Report/analyseventestock/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
         timeout: 240000
    }
});
