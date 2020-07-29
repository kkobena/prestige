/* global Ext */

Ext.define('testextjs.store.Statistics.TPStore', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.VenteTP'
    ],
    model: 'testextjs.model.VenteTP',
    
    autoLoad: true,
    pageSize:25,
    proxy: {
        type: 'ajax',
        url:  "../webservices/configmanagement/tierspayant/ws_vente.jsp",
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        }
       
       
    }
});