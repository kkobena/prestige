Ext.define('testextjs.store.Statistics.TVAS', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.TVA'
    ],

    model:'testextjs.model.statistics.TVA',
   pageSize:20,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url:'../webservices/Report/statistictva/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
         timeout: 240000
    }
});
