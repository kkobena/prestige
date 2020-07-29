/* global Ext */

Ext.define('testextjs.model.GroupeFamille', {
    extend: 'Ext.data.Model',
    fields: [
    {
        name: 'lg_GROUPE_FAMILLE_ID',
        type: 'string'
    },
    {
        name: 'str_LIBELLE',
        type: 'string'
    },
    {
        name: 'str_COMMENTAIRE',
        type: 'string'
    },
    {
        name: 'str_CODE_GROUPE_FAMILLE',
        type: 'string'
    },
    {
        name: 'str_STATUT',
        type: 'string'
    },
    
    {
        name: 'dt_UPDATED',
        type: 'string'
    },
    
    {
        name: 'dt_CREATED',
        type: 'string'
    }

    ]
});
