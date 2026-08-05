/* global Ext */

Ext.define('testextjs.store.RecpaOrganisme', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.RecapOrganisme'
    ],
    model: 'testextjs.model.RecapOrganisme',
    autoLoad: true,
   // groupField: 'FULNAME',
    pageSize:20,  
    proxy: {
        type: 'ajax',
        // endpoint reloge sous reglement-facture (la ressource autonome n'etait pas enregistree sur certains serveurs -> 404)
        url: '../api/v1/reglement-facture/recap-organisme/list',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
        timeout: 240000
    }
});