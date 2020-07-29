Ext.define('testextjs.model.RuptureFournisseur', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'lg_RUPTURE_HISTORY_ID',
            type: 'string'
        }, {
            name: 'lg_FAMILLE_ID',
            type: 'string'
        },
        {
            name: 'lg_FAMILLE_LIBELLE',
            type: 'string'
        },
        //lg_FAMILLE_CIP
        {
            name: 'lg_FAMILLE_CIP',
            type: 'string'
        },
        {
            name: 'lg_GROSSISTE_ID',
            type: 'string'
        },
        {
            name: 'str_GROSSISTE_LIBELLE',
            type: 'string'
        },
        {
            name: 'dt_CREATED',
            type: 'string'
        }
    ]
});
