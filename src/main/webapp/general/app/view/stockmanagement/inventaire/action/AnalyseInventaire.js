/* global Ext */

Ext.define('testextjs.view.stockmanagement.inventaire.action.AnalyseInventaire', {
    extend: 'Ext.window.Window',
    xtype: 'analyseinventaire',

    autoShow: true,
    modal: true,
    layout: 'fit',
    width: '85%',
    height: 450,
    maximizable: true,
    closable: false,

    requires: [
        'Ext.form.*',
        'Ext.window.Window'
    ],
    config: {
        odatasource: '',
        parentview: '',
        mode: '',
        titre: ''
    },
    url_api_analyse_inventaire: '/laborex/api/v1/analyse-inventaire',
    url_api_pdf_inventaire: '/laborex/api/v1/analyse-inventaire-pdf',
    url_api_excel_inventaire: '/laborex/api/v1/analyse-inventaire-excel',

    initComponent: function() {
        var me = this;

        var dataStore = Ext.create('Ext.data.Store', {
            fields: ['emplacement', 'prixAchat', 'prixVente', 'qteSaisie', 'qteInitiale'],
            proxy: {
                type: 'ajax',
                url: me.url_api_analyse_inventaire,
                reader: {
                    type: 'json',
                    root: ''
                }
            },
            autoLoad: false
        });

        var summaryStore = Ext.create('Ext.data.Store', {
            fields: [
                'emplacement', 'valeurAchatMachine', 'valeurAchatRayon', 'ecartValeurAchat', 'pourcentageEcartAchat',
                'valeurVenteMachine', 'valeurVenteRayon', 'ecartValeurVente', 'pourcentageEcartVente'
            ]
        });

        var moneyRenderer = function(val) {
            if (val === null || val === undefined) {
                return '0 F';
            }
            var formattedVal = val.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ");
            return formattedVal + ' F';
        };
        
        var ecartRenderer = function(val) {
            var color = val > 0 ? 'green' : (val < 0 ? 'red' : 'black');
            return '<span style="color:' + color + ';">' + moneyRenderer(val) + '</span>';
        };

        var percentRenderer = function(val) {
            if (val === 0) return '(0,00 %)';
            var color = val > 0 ? 'green' : (val < 0 ? 'red' : 'black');
            var text = '(' + Ext.util.Format.number(val, '0.00').replace('.', ',') + ' %)';
            return '<span style="color:' + color + ';">' + text + '</span>';
        };

        var form = Ext.create('Ext.form.Panel', {
            bodyPadding: 10,
            layout: 'fit',
            items: [{
                xtype: 'gridpanel',
                itemId: 'analyseGridID',
                store: summaryStore,
                // --- AJOUT D'UNE BARRE D'OUTILS POUR LE RAPPORT DE CONFORMITÉ ---
                tbar: [{
                    xtype: 'displayfield',
                    itemId: 'complianceReport',
                    fieldStyle: "font-size: 14px; font-weight: bold; color: #00529C;",
                    value: 'Calcul en cours...'
                }],
                features: [{ ftype: 'summary', dock: 'bottom' }],
                columns: [
                    { text: 'Emplacement', dataIndex: 'emplacement', flex: 1.5, summaryType: 'count', summaryRenderer: function(value){ return '<b>TOTAL GÉNÉRAL</b>'; } },
                    { text: 'Valeur Achat Machine', dataIndex: 'valeurAchatMachine', renderer: moneyRenderer, flex: 1, align: 'right', summaryType: 'sum', summaryRenderer: function(val) { return '<b>' + moneyRenderer(val) + '</b>'; } },
                    { text: 'Valeur Achat Rayon', dataIndex: 'valeurAchatRayon', renderer: moneyRenderer, flex: 1, align: 'right', summaryType: 'sum', summaryRenderer: function(val) { return '<b>' + moneyRenderer(val) + '</b>'; } },
                    { text: 'Écart V.Achat', dataIndex: 'ecartValeurAchat', renderer: ecartRenderer, flex: 1, align: 'right', summaryType: 'sum', summaryRenderer: function(val) { return '<b>' + ecartRenderer(val) + '</b>'; } },
                    { text: '(%) Écart Achat', dataIndex: 'pourcentageEcartAchat', renderer: percentRenderer, flex: 1, align: 'center' },
                    { text: 'Valeur Vente Machine', dataIndex: 'valeurVenteMachine', renderer: moneyRenderer, flex: 1, align: 'right', summaryType: 'sum', summaryRenderer: function(val) { return '<b>' + moneyRenderer(val) + '</b>'; } },
                    { text: 'Valeur Vente Rayon', dataIndex: 'valeurVenteRayon', renderer: moneyRenderer, flex: 1, align: 'right', summaryType: 'sum', summaryRenderer: function(val) { return '<b>' + moneyRenderer(val) + '</b>'; } },
                    { text: 'Écart V.Vente', dataIndex: 'ecartValeurVente', renderer: ecartRenderer, flex: 1, align: 'right', summaryType: 'sum', summaryRenderer: function(val) { return '<b>' + ecartRenderer(val) + '</b>'; } },
                    { text: '(%) Écart Vente', dataIndex: 'pourcentageEcartVente', renderer: percentRenderer, flex: 1, align: 'center' }
                ]
            }]
        });
        
        this.items = form;

        this.dockedItems = [{
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            items: ['->', 
            {
                text: 'Imprimer (PDF)',
                icon: 'resources/images/icons/fam/page_white_acrobat.png',
                handler: function() {
                    me.onPrintClick();
                }
            },
            {
                text: 'Exporter (Excel)',
                icon: 'resources/images/icons/fam/page_white_excel.png',
                handler: function() {
                    me.onExcelExportClick();
                }
            },
            {
                text: 'Retour',
                icon: 'resources/images/icons/fam/door_out.png',
                handler: function() {
                    me.onbtncancel();
                }
            }]
        }];

        this.callParent();
        this.setTitle(this.getTitre());

        dataStore.load({
            params: {
                inventaireId: me.getOdatasource().lg_INVENTAIRE_ID
            },
            callback: function(records, operation, success) {
                if (!success) {
                    Ext.Msg.alert('Erreur', 'Impossible de charger les données d\'analyse.');
                    return;
                }

                var emplacementTotals = {};
                var modifiedProducts = 0;
                var totalProducts = records.length;

                Ext.each(records, function(rec) {
                    if (rec.get('qteInitiale') !== rec.get('qteSaisie')) {
                        modifiedProducts++;
                    }

                    var loc = rec.get('emplacement');
                    if (!emplacementTotals[loc]) {
                        emplacementTotals[loc] = {
                            valeurAchatMachine: 0, valeurAchatRayon: 0,
                            valeurVenteMachine: 0, valeurVenteRayon: 0
                        };
                    }
                    emplacementTotals[loc].valeurAchatMachine += rec.get('qteInitiale') * rec.get('prixAchat');
                    emplacementTotals[loc].valeurAchatRayon += rec.get('qteSaisie') * rec.get('prixAchat');
                    emplacementTotals[loc].valeurVenteMachine += rec.get('qteInitiale') * rec.get('prixVente');
                    emplacementTotals[loc].valeurVenteRayon += rec.get('qteSaisie') * rec.get('prixVente');
                });
                
                // --- MISE À JOUR DU RAPPORT DE CONFORMITÉ ---
                var complianceField = me.down('#complianceReport');
                complianceField.setValue('Rapport de conformité : ' + modifiedProducts + ' produit(s) modifié(s) sur ' + totalProducts + ' au total.');

                var summaryData = [];
                for (var locName in emplacementTotals) {
                    var totals = emplacementTotals[locName];
                    var ecartAchat = totals.valeurAchatRayon - totals.valeurAchatMachine;
                    var pourcentageEcartAchat = (totals.valeurAchatMachine !== 0) ? (ecartAchat / totals.valeurAchatMachine) * 100 : 0;
                    var ecartVente = totals.valeurVenteRayon - totals.valeurVenteMachine;
                    var pourcentageEcartVente = (totals.valeurVenteMachine !== 0) ? (ecartVente / totals.valeurVenteMachine) * 100 : 0;

                    summaryData.push({
                        emplacement: locName,
                        valeurAchatMachine: totals.valeurAchatMachine,
                        valeurAchatRayon: totals.valeurAchatRayon,
                        ecartValeurAchat: ecartAchat,
                        pourcentageEcartAchat: pourcentageEcartAchat,
                        valeurVenteMachine: totals.valeurVenteMachine,
                        valeurVenteRayon: totals.valeurVenteRayon,
                        ecartValeurVente: ecartVente,
                        pourcentageEcartVente: pourcentageEcartVente
                    });
                }
                
                summaryStore.loadData(summaryData);
            }
        });
    },

    onPrintClick: function() {
        var me = this;
        var form = Ext.create('Ext.form.Panel', {
            standardSubmit: true,
            url: me.url_api_pdf_inventaire,
            method: 'POST'
        });

        form.submit({
            target: '_blank',
            params: {
                inventaireId: me.getOdatasource().lg_INVENTAIRE_ID,
                inventaireName: me.getOdatasource().str_NAME
            }
        });
    },

    onExcelExportClick: function() {
        var me = this;
        var form = Ext.create('Ext.form.Panel', {
            standardSubmit: true,
            url: me.url_api_excel_inventaire,
            method: 'POST'
        });

        form.submit({
            target: '_blank',
            params: {
                inventaireId: me.getOdatasource().lg_INVENTAIRE_ID,
                inventaireName: me.getOdatasource().str_NAME
            }
        });
    },

    onbtncancel: function() {
        this.close();
    }
});
