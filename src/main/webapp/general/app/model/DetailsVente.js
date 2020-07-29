/* global Ext */

Ext.define('testextjs.model.DetailsVente', {
    extend: 'Ext.data.Model',
    idProperty: 'lg_PREENREGISTREMENT_DETAIL_ID',
    fields: [
        {
            name: 'lg_PREENREGISTREMENT_DETAIL_ID',
            type: 'string'
        },
        {
            name: 'str_REF',
            type: 'string'
        },
        {
            name: 'lg_PREENREGISTREMENT_ID',
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
            name: 'int_FREE_PACK_NUMBER',
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
        },{
            name: 'int_AVOIR',
            type: 'int'
        },{
            name: 'b_IS_AVOIR',
            type: 'boolean'
        },{
            name: 'str_STATUT',
            type: 'string'
        },{
            name:'bl_PROMOTED',
            type: 'boolean'
        },
        {
            name: 'bool_UPDATE_PRICE',
            type: 'boolean'
        }
    ]
});
