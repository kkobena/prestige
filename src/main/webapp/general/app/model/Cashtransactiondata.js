Ext.define('testextjs.model.Cashtransactiondata', {
    extend: 'Ext.data.Model',
//    idProperty: 'str_ref',
    fields: [
        {
            name: 'str_ref',
            type: 'string'
        },
        {
            name: 'str_client_infos',
            type: 'string'
        },
        {
            name: 'str_REF_BON',
            type: 'string'
        },
        {
            name: 'str_type_vente',
            type: 'string'
        },
        {
            name: 'str_type_reglement',
            type: 'string'
        },
        {
            name: 'str_date',
            type: 'string'
        },
        {
            name: 'str_hour',
            type: 'string'
        },
        {
            name: 'str_vendeur',
            type: 'string'
        },
        {
            name: 'str_client',
            type: 'string'
        },
        {
            name: 'str_mt_vente',
            type: 'string'
        },
        {
            name: 'str_mt_tp',
            type: 'string'
        },
        {
            name: 'str_mt_rem',
            type: 'string'
        },
        {
            name: 'str_mt_clt',
            type: 'string'
        },
        {
            name: 'amount_tp_ro',
            type: 'int'
        },
        {
            name: 'amount_tp_rc2',
            type: 'int'
        },
        {
            name: 'amount_tp_rc1',
            type: 'int'
        },
        {
            name: 'lg_COMPTE_CLIENT_TIERS_PAYANT_ID',
            type: 'string'
        },
        {
            name: 'lg_PREENREGISTREMENT_ID',
            type: 'string'
        },
        {
            name: 'str_TIERS_PAYANT',
            type: 'string'
        }, {
            name: 'int_PRICE',
            type: 'int'
        },
        {
            name: 'int_PERCENT',
            type: 'int'
        },
        {
            name: 'info_tierspayant',
            type: 'string'
        },
        {
            name: 'str_FAMILLE_ITEM',
            type: 'string'
        },
        {
            name: 'str_TRANSACTION_REF',
            type: 'string'
        },
        {
            name: 'int_PRICE_TOTAL',
            type: 'string'
        },
        //code ajouté
        {
            name: 'lg_MVT_CAISSE_ID',
            type: 'string'
        },
        {
            name: 'lg_TYPE_MVT_CAISSE_ID',
            type: 'string'
        },
        {
            name: 'str_NUM_COMPTE',
            type: 'string'
        },
        {
            name: 'int_AMOUNT',
            type: 'int'
        },
        {
            name: 'str_NUM_PIECE_COMPTABLE',
            type: 'string'
        },
        {
            name: 'lg_MODE_REGLEMENT_ID',
            type: 'string'
        },
        {
            name: 'str_BANQUE',
            type: 'string'
        },
        {
            name: 'str_LIEU',
            type: 'string'
        },
        {
            name: 'str_CODE_MONNAIE',
            type: 'string'
        },
        {
            name: 'str_COMMENTAIRE',
            type: 'string'
        },
        {
            name: 'int_TAUX',
            type: 'string'
        },
        {
            name: 'dt_DATE_MVT',
            type: 'string'
        },
        {
            name: 'lg_TYPE_REGLEMENT_ID',
            type: 'string'
        }
        
        //fin code ajouté
    ]
});
