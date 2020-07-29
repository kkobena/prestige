Ext.define('testextjs.model.FamilleGrossiste', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'lg_FAMILLE_GROSSISTE_ID',
            type: 'string'
        },
        {
            name: 'lg_GROSSISTE_ID',
            type: 'string'
        },
        //lg_GROSSISTE_LIBELLE
        {
            name: 'lg_GROSSISTE_LIBELLE',
            type: 'string'
        },
        {
            name: 'str_CODE_FAMILLE',
            type: 'string'
        },
        {
            name: 'lg_FAMILLE_ID',
            type: 'string'
        },
        //lg_FAMILLE_LIBELLE
        {
            name: 'lg_FAMILLE_LIBELLE',
            type: 'string'
        },
        {
            name: 'str_CODE_ARTICLE',
            type: 'string'
        },
        {
            name: 'str_STATUT',
            type: 'string'
        },
        {
            name: 'dt_CREATED',
            type: 'string'
        },
        {
            name: 'dt_UPDATED',
            type: 'string'
        },
        {
            name: 'int_PRICE',
            type: 'int'
        },
        {
            name: 'int_PAF',
            type: 'int'
        }
    ]
});
