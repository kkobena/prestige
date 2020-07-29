Ext.define('testextjs.store.Clients', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.Client'
    ],

    model:'testextjs.model.Client',
   
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url:'../webservices/configmanagement/client/ws_standartclient_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
         timeout: 240000
    }
});
