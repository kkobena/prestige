Ext.define('testextjs.model.Litige', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'lg_LITIGE_ID',
            type: 'string'
        },
        {
            name: 'lg_TYPELITIGE_ID',
            type: 'string'
        },
        {
            name: 'str_TYPE_LITIGE',
            type: 'string'
        },
        {
            name: 'str_MEDECIN',
            type: 'string'
        },{
            name: 'int_total_product',
            type: 'int'
        },{
            name: 'int_total_vente',
            type: 'int'
        },
        {
            name: 'lg_TYPE_VENTE_ID',
            type: 'string'
        },
        {
            name: 'lg_PREENREGISTREMENT_ID',
            type: 'string'
        },
        {
            name: 'str_LIBELLE',
            type: 'string'
        },
        {
            name: 'str_DESCRIPTION',
            type: 'string'
        },
        {
            name: 'str_COMMENTAIRE_LITIGE',
            type: 'string'
        },
        {
            name: 'str_STATUT',
            type: 'string'
        },
        {
            name: 'etat',
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
            name: 'str_REFERENCE',
            type: 'string'
        },{
            name: 'str_REF_CREATED',
            type: 'date'
        },
        {
             name: 'str_ETAT_LITIGE',
             type: 'string'
        },
        {
            name: 'str_ORGANISME',
            type: 'string'
        },
        {
            name: 'str_FIRST_LAST_NAME',
            type: 'string'
        },
        {
            name: 'int_AMOUNT',
            type: 'int'
        },{
            name: 'str_FAMILLE_ITEM',
            type: 'string'
        },{
            name: 'int_ECART',
            type: 'int'
        },{
            name: 'int_AMOUNT_DUS',
            type: 'int'
        },{
            name: 'str_STATUT_TRAITEMENT',
            type: 'string'
        }
    ]
});
