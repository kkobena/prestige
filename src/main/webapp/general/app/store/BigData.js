Ext.define('testextjs.store.BigData', {
    extend: 'Ext.data.Store',

    requires: [
        'testextjs.data.BigData',
        'Ext.ux.ajax.JsonSimlet',
        'Ext.ux.ajax.SimManager'
    ],

    model: 'testextjs.model.grid.Employee',

    groupField: 'department',

    proxy: {
        type: 'ajax',
        limitParam: null,
        url: '/testextjs/BigData',
        reader: {
            type: 'json'
        }
    },
    autoLoad: true
}, function() {
    Ext.ux.ajax.SimManager.init({
        defaultSimlet: null
    }).register({
        '/testextjs/BigData': {
            data: testextjs.data.BigData.data,
            stype: 'json'
        }
    });
});