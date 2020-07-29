
/* global Ext */

Ext.define('testextjs.store.SearchStore', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.Search'
    ],
    model: 'testextjs.model.Search',
    pageSize:20,
    storeId: 'SearchStore', 
   autoLoad: false,
    sorters: [
    
        {
            property: 'str_DESCRIPTION',
            direction: 'ASC'
        }
    ],
    proxy: {
        type: 'ajax',
        url: '../webservices/sm_user/famille/ws_search_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        }
        
    }
});