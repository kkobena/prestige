Ext.define('testextjs.store.Statistics.VenteCumuls', {
    extend: 'Ext.data.Store',
     requires: [
        'testextjs.model.statistics.VenteCumul'
    ],

    model:'testextjs.model.statistics.VenteCumul',
   pageSize:20,
    autoLoad: true,
     
    proxy: {
        type: 'ajax',
        url: '../webservices/Report/statistiquevente/ws_cumul_data.jsp',
        reader: {
            type: 'json',
            root: 'data'
        },
         timeout: 240000
    }
});
