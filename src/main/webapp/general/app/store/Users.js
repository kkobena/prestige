
/* global Ext */

Ext.define('testextjs.store.Users', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.Utilisateur'
    ],
    model: 'testextjs.model.Utilisateur',
    pageSize:15,
    storeId: 'Users', 
   autoLoad: false,
  
    proxy: {
        type: 'ajax',
        url: '../UserCtr',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        }
        
    }
});