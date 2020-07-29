/* global Ext */

Ext.define('testextjs.model.statistics.Vente', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields: [
        {name: 'id', type: 'int'},
         {name: 'num', type: 'int'},
         {name: 'year', type: 'int'},
        {name: 'AMOUT_VO', type: 'float'},
        {name: 'AMOUT_VNO', type: 'float'},
        {name: 'BRUT_TTC', type: 'float'},
        {name: 'NET_TTC', type: 'float'},
        {name: 'MONTANT_BRUTCUMUL', type: 'float'},
        {name: 'MONTANT_VNOCUMUL', type: 'float'},
        {name: 'MONTANT_VOCUMUL', type: 'float'},
        {name: 'REMISE', type: 'float'},
        {name: 'PANIER_MOYEN_M_VNO', type: 'float'},
        {name: 'PANIER_MOYEN_M_VO', type: 'float'},
        {name: 'MONTANT_NETCUMUL', type: 'float'},
        {name: 'MONTANT_REMISECUMUL', type: 'float'},
        {name: 'NB_CLIENTCUMUL', type: 'int'},
        {name: 'NB_CLIENT', type: 'int'},
        {name: 'month', type: 'string'},
        {name: 'PANIER_MOYEN_M_VNO_CUMUL', type: 'float'},
        {name: 'PANIER_MOYEN_M_VO_CUMUL', type: 'float'},
        {name: 'vno_month_percent', type: 'float'},
        {name: 'vo_month_percent', type: 'float'},
        {name: 'vo_cumul_percent', type: 'float'},
        {name: 'vno_cumul_percent', type: 'float'}

    ]
});

