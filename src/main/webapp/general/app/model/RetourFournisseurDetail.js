Ext.define('testextjs.model.RetourFournisseurDetail', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'lg_RETOUR_FRS_DETAIL',
            type: 'string'
        },
        {
            name: 'lg_FAMILLE_ID',
            type: 'string'
        },
        {
            name: 'lg_FAMILLE_NAME',
            type: 'string'
        },
        //int_DIFF
        {
            name: 'int_DIFF',
            type: 'string'
        },
        // dt_PEREMPTION
        {
            name: 'dt_PEREMPTION',
            type: 'string'
        },
        //int_STOCK
        {
            name: 'int_STOCK',
            type: 'string'
        },
        // lg_FAMILLE_CIP
        {
            name: 'lg_FAMILLE_CIP',
            type: 'string'
        },
        // lg_RETOUR_FRS_ID
        {
            name: 'lg_RETOUR_FRS_ID',
            type: 'string'
        },
        {
            name: 'lg_MOTIF_RETOUR',
            type: 'string'
        },
        {
            name: 'int_NUMBER_RETURN',
            type: 'string'
        },
        {
            name: 'int_NUMBER_ANSWER',
            type: 'int'
        },
        {
            name: 'str_STATUT',
            type: 'string'
        }, {
            name: 'dt_CREATED',
            type: 'string'
        },
        {
            name: 'dt_UPDATED',
            type: 'string'
        }
        ,
        // str_CODE_ARTICLE
        {
            name: 'str_CODE_ARTICLE',
            type: 'string'
        }

    ]
});
