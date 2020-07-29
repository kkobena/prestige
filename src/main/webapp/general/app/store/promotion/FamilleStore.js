/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/* global Ext */

Ext.define('testextjs.store.promotion.FamilleStore', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.Famille'
    ],
    model: 'testextjs.model.Famille',
    pageSize: 20,
    storeId: 'familleStoreID',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '../webservices/sm_user/famille/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'results',
            totalProperty: 'total'
        },
        timeout: 240000
    }
});

