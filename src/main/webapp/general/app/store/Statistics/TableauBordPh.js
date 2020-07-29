/* global Ext */

Ext.define('testextjs.store.Statistics.TableauBordPh', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.TableauBordPharmacien'
    ],

    model: 'testextjs.model.TableauBordPharmacien',
    autoLoad: true,
    pageSize: 20,
    proxy: {
        type: 'ajax',
        url: '../webservices/Report/tableauBordPharmacien/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
//            cumul: 'cumul'
        },
        timeout: 240000
    }
});
