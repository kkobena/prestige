
/* global Ext */

Ext.define('testextjs.store.companyStore', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.Company'
    ],
    model:'testextjs.model.Company',
    pageSize:20,
   
   autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '../webservices/configmanagement/comapnies/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        }
        
    }
});