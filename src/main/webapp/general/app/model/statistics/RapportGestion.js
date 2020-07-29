/* global Ext */

Ext.define('testextjs.model.statistics.RapportGestion', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields: [
        {name: 'id'},
        
        {name: 'LIBELLE'},
         {name: 'TYPEMVT'},
        {name: 'AMOUNT',type:'float'}, 
        {name: 'DISPLAY',type:'int'},
        
         {name: 'STATUS',type:'int'}
        
        
    ]
});

