/* global Ext */

Ext.define('testextjs.model.DefferedPayment', {
    extend: 'Ext.data.Model',
    idProperty: 'lg_PREENREGISTREMENT_COMPTE_CLIENT_ID',
    fields: [
        {name: 'lg_PREENREGISTREMENT_COMPTE_CLIENT_ID', type: 'string'},
        {name: 'int_MONTANT_REGLEMENT', type: 'float'},
        {name: 'dbl_MONTANT_RESTANT', type: 'float'},
        {name: 'DATEVENTE', type: 'string'},
        {name: 'HEUREVENTE', type: 'string'},
        {name: 'REFBON', type: 'string'},
        {name: 'MONTANTVENTE', type: 'float'},
        {name: 'MONTANTVENTE', type: 'float'},
        {name: 'TOTAL_AMOUNT', type: 'float'},
        {name: 'int_Nbr_Dossier', type: 'int'},
        
        {name: 'isChecked', type: 'boolean'}

    ]
});
