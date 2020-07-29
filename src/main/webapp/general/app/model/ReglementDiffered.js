/* global Ext */

Ext.define('testextjs.model.ReglementDiffered', {
    extend: 'Ext.data.Model',
  
    fields: [
        {name: 'lg_DEFFERED_ID', type: 'string'},
        {name: 'ORGANISME', type: 'string'},
        {name: 'MODEREGLEMENT', type: 'string'},
        {name: 'MONTANTREGL', type: 'float'},
        {name: 'DATEREGL', type: 'string'},
        {name: 'HEUREREGL', type: 'string'},
        {name: 'MONTANTATT', type: 'float'},
        {name: 'OPPERATEUR', type: 'string'}
    ]
});
