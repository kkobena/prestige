/* global Ext */

Ext.define('testextjs.model.Vente', {
    extend: 'Ext.data.Model',
    idProperty: 'lg_PREENREGISTREMENT_ID',
    fields: [
        {name: 'lg_PREENREGISTREMENT_ID', type: 'string'},
        {name: 'str_REF', type: 'string'},
        {name: 'lg_USER_CAISSIER_ID', type: 'string'},
        {name: 'lg_USER_VENDEUR_ID', type: 'string'},
        {name: 'int_PRICE', type: 'string'},
        {name: 'dt_CREATED', type: 'string'},
        {name: 'str_hour', type: 'string'},
        {name: 'str_STATUT', type: 'string'},
        {name: 'str_FAMILLE_ITEM', type: 'string'},
        {name: 'str_TYPE_VENTE', type: 'string'},
        {name: 'b_IS_CANCEL', type: 'string'},
        {name: 'str_TYPE_VENTE', type: 'string'},
        {name: 'str_FIRST_LAST_NAME_CLIENT', type: 'string'},
        {name: 'int_PRICE_FORMAT', type: 'string'},
        {name: 'etat', type: 'string'},
        {name: 'int_SENDTOSUGGESTION', type: 'string'},
        {name: 'BTN_ANNULATION', type: 'string'},
        {name: 'lg_EMPLACEMENT_ID', type: 'string'},
        {name: 'lg_TYPE_VENTE_ID', type: 'string'}
        

    ]
});



