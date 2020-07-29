/* global Ext */

Ext.define('testextjs.model.statistics.QtyAchats', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'CIP', type: 'string'},
        {name: 'DESC', type: 'string'},
        {name: 'ANNEE', type: 'number'},

        {name: 'JANUARY', type: 'number', defaultValue: 0},
        {name: 'FEBRUARY', type: 'number', defaultValue: 0},
        {name: 'MARCH', type: 'number', defaultValue: 0},
        {name: 'APRIL', type: 'number', defaultValue: 0},
        {name: 'MAY', type: 'number', defaultValue: 0},
        {name: 'JUNE', type: 'number', defaultValue: 0},
        {name: 'JULY', type: 'number', defaultValue: 0},
        {name: 'AUGUST', type: 'number', defaultValue: 0},
        {name: 'SEPTEMBER', type: 'number', defaultValue: 0},
        {name: 'OCTOBER', type: 'number', defaultValue: 0},
        {name: 'NOVEMBER', type: 'number', defaultValue: 0},
        {name: 'DECEMBER', type: 'number', defaultValue: 0},
        
        
        {name: 'JANUARYQTY', type: 'number', defaultValue: 0},
        {name: 'FEBRUARYQTY', type: 'number', defaultValue: 0},
        {name: 'MARCHQTY', type: 'number', defaultValue: 0},
        {name: 'APRILQTY', type: 'number', defaultValue: 0},
        {name: 'MAYQTY', type: 'number', defaultValue: 0},
        {name: 'JUNEQTY', type: 'number', defaultValue: 0},
        {name: 'JULYQTY', type: 'number', defaultValue: 0},
        {name: 'AUGUSTQTY', type: 'number', defaultValue: 0},
        {name: 'SEPTEMBERQTY', type: 'number', defaultValue: 0},
        {name: 'OCTOBERQTY', type: 'number', defaultValue: 0},
        {name: 'NOVEMBERQTY', type: 'number', defaultValue: 0},
        {name: 'DECEMBERQTY', type: 'number', defaultValue: 0},
        
        
        {name: 'JANUARYUG', type: 'number', defaultValue: 0},
        {name: 'FEBRUARYUG', type: 'number', defaultValue: 0},
        {name: 'MARCHUG', type: 'number', defaultValue: 0},
        {name: 'APRILUG', type: 'number', defaultValue: 0},
        {name: 'MAYUG', type: 'number', defaultValue: 0},
        {name: 'JUNEUG', type: 'number', defaultValue: 0},
        {name: 'JULYUG', type: 'number', defaultValue: 0},
        {name: 'AUGUSTUG', type: 'number', defaultValue: 0},
        {name: 'SEPTEMBERUG', type: 'number', defaultValue: 0},
        {name: 'OCTOBERUG', type: 'number', defaultValue: 0},
        {name: 'NOVEMBERUG', type: 'number', defaultValue: 0},
        {name: 'DECEMBERUG', type: 'number', defaultValue: 0}
    ]

});