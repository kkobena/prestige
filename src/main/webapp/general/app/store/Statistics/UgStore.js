/* global Ext */

Ext.define('testextjs.store.Statistics.UgStore', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.UG'
    ],
    model: 'testextjs.model.UG',
    groupField: 'GROSSISTE',
    autoLoad: true,
    pageSize:10,
    proxy: {
        type: 'ajax',
        url:  "../webservices/Report/uniteGratuite/ws_data.jsp",
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        }
       
    }
});