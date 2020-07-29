Ext.define('testextjs.model.DetailsAjustement', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'lg_AJUSTEMENTDETAIL_ID',
            type: 'string'
        },
        {
            name: 'str_REF',
            type: 'string'
        },
        {
            name: 'lg_AJUSTEMENT_ID',
            type: 'string'
        },
        {
            name: 'lg_FAMILLE_ID',
            type: 'string'
        },
        {
            name: 'int_QUANTITY',
            type: 'int'
        },
        {
            name: 'int_QUANTITY_SERVED',
            type: 'int'
        },
        {
            name: 'int_FAMILLE_PRICE',
            type: 'int'
        },
        {
            name: 'int_CIP',
            type: 'string'
        },
        {
            name: 'int_EAN13',
            type: 'string'
        }, {
            name: 'str_FAMILLE_NAME',
            type: 'string'
        },{
            name: 'int_PRICE_DETAIL',
            type: 'int'
        },{
            name: 'int_S',
            type: 'int'
        },{
            name: 'int_T',
            type: 'int'
        }
        ,{
            name: 'str_MEDECIN',
            type: 'string'
        }
        ,{
            name: 'lg_TYPE_VENTE_ID',
            type: 'string'
        },{
            name: 'int_total_vente',
            type: 'int'
        },{
            name: 'int_total_product',
            type: 'int'
        }
    ]
});
