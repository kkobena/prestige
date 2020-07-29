/* global Ext */

Ext.define('testextjs.model.statistics.logModel', {
    extend: 'Ext.data.Model',
    idProperty: 'lgEVENTLOGID',
    fields: [
        {name: 'lgEVENTLOGID', type: 'string'},
        {name: 'dtCREATED', type: 'string'},
        {name: 'dtHEURE', type: 'string'},
        {name: 'strDESCRIPTION', type: 'string'},
        {name: 'strCREATEDBY', type: 'string'},
        {name: 'strTYPELOG', type: 'string'}
    ]
});
