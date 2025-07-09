
Ext.define('testextjs.view.caisseManager.balance.BalanceSaleCashDepot', {
    extend: 'Ext.panel.Panel',
    xtype: 'balancesalecashdepot',
    id: 'balanceSaleCashDepotID',
    requires: [
        'Ext.chart.*',
        'Ext.layout.container.Fit',
        'Ext.window.MessageBox'
    ],
    frame: true,
    title: 'Balance des ventes par dépôt',
    width: '98%',
    height: 570,
    layout: 'vbox',
    
    initComponent: function() {
        var me = this;
        var today = new Date();

        // Store pour les dépôts
        var depotStore = Ext.create('Ext.data.Store', {
            fields: ['lgEMPLACEMENTID', 'strNAME'],
            proxy: {
                type: 'ajax',
                url: '../api/v1/magasin/find-depots',
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            autoLoad: true,
            listeners: {
                load: function(store, records, successful) {
                    if (successful) {
                        // Ajout de l'option "TOUT" au début de la liste
                        store.insert(0, [{
                            lgEMPLACEMENTID: 'ALL',
                            strNAME: 'TOUT'
                        }]);
                        // Sélection par défaut de "TOUT"
                        Ext.getCmp('depot').setValue('ALL');
                    }
                }
            }
        });

        Ext.applyIf(me, {
            items: [
                {
                    xtype: 'panel',
                    width: '100%',
                    bodyPadding: 10,
                    layout: {
                        type: 'hbox',
                        align: 'middle'
                    },
                    items: [
                        {
                            xtype: 'combobox',
                            fieldLabel: 'Choisir un dépôt',
                            id: 'depot',
                            store: depotStore,
                            displayField: 'strNAME',
                            valueField: 'lgEMPLACEMENTID',
                            queryMode: 'local',
                            labelWidth: 110,
                            width: 380,
                            allowBlank: false,
                            listConfig: {
                                getInnerTpl: function() {
                                    return '<span>{strNAME}</span>';
                                }
                            }
                        },
                        { xtype: 'splitter' },
                        {
                            xtype: 'datefield',
                            fieldLabel: 'Du',
                            id: 'dtStart',
                            name: 'dtStart',
                            value: today,
                            format: 'd/m/Y',
                            allowBlank: false,
                            submitFormat: 'Y-m-d',
                            labelWidth: 20,
                            width: 130
                        },
                        { xtype: 'splitter' },
                        {
                            xtype: 'datefield',
                            fieldLabel: 'Au',
                            id: 'dtEnd',
                            name: 'dtEnd',
                            value: today,
                            format: 'd/m/Y',
                            allowBlank: false,
                            submitFormat: 'Y-m-d',
                            labelWidth: 20,
                            width: 130
                        },
                        { xtype: 'splitter' },
                        {
                            xtype: 'button',
                            text: 'Rechercher',
                            iconCls: 'search-icon',
                            id: 'searchBtn',
                            tooltip: 'Lancer la recherche'
                        },
                        { xtype: 'splitter' },
                        {
                            xtype: 'button',
                            text: 'Imprimer',
                            iconCls: 'printable-icon',
                            id: 'printBtn',
                            tooltip: 'Imprimer la balance'
                        }
                    ]
                },
                {
                    xtype: 'gridpanel',
                    id: 'gridBalance',
                    store: Ext.create('testextjs.store.BalanceSaleCashDepotStore'),
                    height: 250,
                    width: '100%',
                    stripeRows: true,
                    columns: [
                        { text: 'Type Vente', dataIndex: 'typeVente', flex: 1, summaryType: 'count', summaryRenderer: function(value){return "<b>TOTAL</b>";} },
                        { text: 'Montant TTC', dataIndex: 'montantTTC', renderer: Ext.util.Format.numberRenderer('0,000'), align: 'right', flex: 1, summaryType: 'sum', summaryRenderer: function(value){return "<b>"+Ext.util.Format.number(value, '0,000')+"</b>";} },
                        { text: 'Montant Net', dataIndex: 'montantNet', renderer: Ext.util.Format.numberRenderer('0,000'), align: 'right', flex: 1, summaryType: 'sum', summaryRenderer: function(value){return "<b>"+Ext.util.Format.number(value, '0,000')+"</b>";} },
                        { text: 'Marge', dataIndex: 'marge', renderer: Ext.util.Format.numberRenderer('0,000'), align: 'right', flex: 1, summaryType: 'sum', summaryRenderer: function(value){return "<b>"+Ext.util.Format.number(value, '0,000')+"</b>";} },
                        { text: 'Nbre Ventes', dataIndex: 'nbreVente', align: 'center', flex: 0.5, summaryType: 'sum', summaryRenderer: function(value){return "<b>"+Ext.util.Format.number(value, '0,000')+"</b>";} },
                        { text: 'Panier Moyen', dataIndex: 'panierMoyen', renderer: Ext.util.Format.numberRenderer('0,000'), align: 'right', flex: 1 }
                    ],
                    features: [{
                        ftype: 'summary',
                        dock: 'bottom'
                    }]
                },
                {
                    xtype: 'panel',
                    title: 'Résumé Global',
                    id: 'summaryPanel',
                    width: '100%',
                    flex: 1,
                    bodyPadding: 10,
                    layout: { type: 'table', columns: 4, tableAttrs: { style: { width: '100%' } } },
                    defaults: { xtype: 'displayfield', labelWidth: 150, renderer: function(v) { return Ext.util.Format.number(v, '0,000'); } },
                    items: [
                        { fieldLabel: 'Montant TTC', id: 'montantTTC_summary' },
                        { fieldLabel: 'Montant Net', id: 'montantNet_summary' },
                        { fieldLabel: 'Marge', id: 'marge_summary' },
                        { fieldLabel: 'Nbre Vente', id: 'nbreVente_summary' },
                        { fieldLabel: 'Panier Moyen', id: 'panierMoyen_summary' },
                        { fieldLabel: 'Montant Achat', id: 'montantAchat_summary' },
                        { fieldLabel: 'Ratio VA', id: 'ratioVA_summary', renderer: function(v) { return Ext.util.Format.number(v, '0.00'); } },
                        { fieldLabel: 'Ratio AV', id: 'rationAV_summary', renderer: function(v) { return Ext.util.Format.number(v, '0.00'); } },
                        { fieldLabel: 'Montant Espèces', id: 'montantEsp_summary' },
                        { fieldLabel: 'Montant Chèque', id: 'montantCheque_summary' },
                        { fieldLabel: 'Montant CB', id: 'montantCB_summary' },
                        { fieldLabel: 'Paiement Mobile', id: 'montantMobilePayment_summary' }
                    ]
                }
            ]
        });

        me.callParent(arguments);
    }
});
