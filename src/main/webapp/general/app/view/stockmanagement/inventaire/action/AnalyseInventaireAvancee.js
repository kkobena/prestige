/* global Ext */

Ext.define('testextjs.view.stockmanagement.inventaire.action.AnalyseInventaireAvancee', {
    extend: 'Ext.window.Window',
    xtype: 'analyseinventaireavancee',

    autoShow: true,
    modal: true,
    layout: 'fit',
    width: '95%',
    height: '85%',
    maximizable: true,
    closable: false,

    requires: [
        'Ext.form.*',
        'Ext.window.Window',
        'Ext.tab.Panel',
        'Ext.form.field.Number'
    ],
    config: {
        odatasource: '',
        parentview: '',
        mode: '',
        titre: ''
    },
    
    url_api_analyse_avancee: '/prestige/api/v1/analyse-avancee',
    url_api_pdf_avancee: '/prestige/api/v1/analyse-avancee-pdf',
    url_api_excel_avancee: '/prestige/api/v1/analyse-avancee-excel',

    allSyntheseData: [],
    allDetailData: [],

    initComponent: function() {
        var me = this;

        me.url_api_analyse_avancee = '/prestige/api/v1/analyse-avancee';
        me.url_api_pdf_avancee = '/prestige/api/v1/analyse-avancee-pdf';
        me.url_api_excel_avancee = '/prestige/api/v1/analyse-avancee-excel';

        var storeSynthese = Ext.create('Ext.data.Store', {
            fields: ['emplacement', 'valeurAchatMachine', 'valeurAchatRayon', 'valeurVenteMachine', 'valeurVenteRayon', 'ecartValeurAchat', 'tauxDemarque', 'contributionEcart', 'ratioVA']
        });
        var storeAnalyseABC = Ext.create('Ext.data.Store', {
            fields: ['nom', 'ecartValeurAchat', 'pourcentageEcartTotal', 'pourcentageCumule', 'categorie']
        });
        // --- CORRECTION : Le store n'a plus besoin de la fonction 'calculate' ---
        var storeDetailProduits = Ext.create('Ext.data.Store', {
            fields: ['nom', 'emplacement', 'qteInitiale', 'qteSaisie', 'ecartQuantite', 'ecartValeurAchat', 'prixAchat', 'prixVente', 'ratioVA']
        });
        
        var moneyRenderer = function(val) { 
            if (val === null || val === undefined) return '0 F';
            return val.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ") + ' F';
        };
        var percentRenderer = function(val) { 
            if (val === null || val === undefined) return '0,00 %';
            return Ext.util.Format.number(val, '0.00').replace('.', ',') + ' %';
        };
        
        var ratioRenderer = function(value) {
            if (!value || value === 0) return 'N/A';
            var color = 'black';
            if (value >= 1.51) color = 'green';
            if (value < 1.51) color = 'red';
            if (value < 1.2) color = 'orange';
            if (value > 10) color = 'blue';

            var text = Ext.util.Format.number(value, '0.00').replace('.', ',');
            return '<span style="color:' + color + '; font-weight: bold;">' + text + '</span>';
        };

        var tabPanel = Ext.create('Ext.tab.Panel', {
            plain: true,
            itemId: 'advancedAnalyseTabPanel',
            items: [{
                title: 'Synthèse par Emplacement',
                xtype: 'gridpanel',
                itemId: 'syntheseGrid',
                store: storeSynthese,
                features: [{ ftype: 'summary', dock: 'bottom' }],
                columns: [
                    { text: 'Emplacement', dataIndex: 'emplacement', flex: 1.5, summaryRenderer: function(){ return '<b>TOTAL</b>'; } },
                    { text: 'V.Achat Machine', dataIndex: 'valeurAchatMachine', renderer: moneyRenderer, flex: 1, align: 'right', summaryType: 'sum', summaryRenderer: function(val) { return '<b>' + moneyRenderer(val) + '</b>'; }},
                    { text: 'V.Achat Inventaire', dataIndex: 'valeurAchatRayon', renderer: moneyRenderer, flex: 1, align: 'right', summaryType: 'sum', summaryRenderer: function(val) { return '<b>' + moneyRenderer(val) + '</b>'; }},
                    { text: 'V.Vente Machine', dataIndex: 'valeurVenteMachine', renderer: moneyRenderer, flex: 1, align: 'right', summaryType: 'sum', summaryRenderer: function(val) { return '<b>' + moneyRenderer(val) + '</b>'; }},
                    { text: 'V.Vente Inventaire', dataIndex: 'valeurVenteRayon', renderer: moneyRenderer, flex: 1, align: 'right', summaryType: 'sum', summaryRenderer: function(val) { return '<b>' + moneyRenderer(val) + '</b>'; }},
                    { text: 'Écart V.Achat', dataIndex: 'ecartValeurAchat', renderer: moneyRenderer, flex: 1, align: 'right', summaryType: 'sum', summaryRenderer: function(val) { return '<b>' + moneyRenderer(val) + '</b>'; }},
                    { text: 'Taux de Démarque', dataIndex: 'tauxDemarque', renderer: percentRenderer, flex: 1, align: 'center' },
                    { text: 'Contribution à l\'Écart', dataIndex: 'contributionEcart', renderer: percentRenderer, flex: 1, align: 'center' },
                    { text: 'Ratio V/A', dataIndex: 'ratioVA', renderer: ratioRenderer, flex: 1, align: 'center' }
                ]
            }, {
                title: 'Analyse des Écarts (ABC)',
                xtype: 'gridpanel',
                itemId: 'abcGrid',
                store: storeAnalyseABC,
                columns: [
                    { text: 'Produit', dataIndex: 'nom', flex: 2 },
                    { text: 'Écart Valeur Achat', dataIndex: 'ecartValeurAchat', renderer: moneyRenderer, flex: 1, align: 'right' },
                    { text: '% Écart Total', dataIndex: 'pourcentageEcartTotal', renderer: percentRenderer, flex: 1, align: 'center' },
                    { text: '% Cumulé', dataIndex: 'pourcentageCumule', renderer: percentRenderer, flex: 1, align: 'center' },
                    { text: 'Catégorie', dataIndex: 'categorie', flex: 0.5, align: 'center' }
                ]
            }, {
                title: 'Détail Complet des Produits',
                xtype: 'gridpanel',
                itemId: 'detailGrid',
                store: storeDetailProduits,
                tbar: [
                    'Filtres Ratios:',
                    { xtype: 'tbspacer' },
                    { xtype: 'numberfield', fieldLabel: 'Ratio >', itemId: 'ratioMinFilter', labelWidth: 60, width: 140, hideTrigger: true, keyNavEnabled: false, mouseWheelEnabled: false },
                    { xtype: 'tbspacer' },
                    { xtype: 'numberfield', fieldLabel: 'Ratio <', itemId: 'ratioMaxFilter', labelWidth: 60, width: 140, hideTrigger: true, keyNavEnabled: false, mouseWheelEnabled: false },
                    { xtype: 'tbspacer' },
                    { text: 'Filtrer', iconCls: 'filter', handler: function() { me.filterDetailData(); } },
                    { text: 'Réinitialiser', iconCls: 'reset', handler: function() { 
                        me.down('#ratioMinFilter').reset();
                        me.down('#ratioMaxFilter').reset();
                        me.filterDetailData();
                    } }
                ],
                columns: [
                    { text: 'Produit', dataIndex: 'nom', flex: 2 },
                    { text: 'Emplacement', dataIndex: 'emplacement', flex: 1 },
                    { text: 'Qté Machine', dataIndex: 'qteInitiale', align: 'center' },
                    { text: 'Qté Rayon', dataIndex: 'qteSaisie', align: 'center' },
                    { text: 'Écart Qté', dataIndex: 'ecartQuantite', align: 'center' },
                    { text: 'Prix Achat', dataIndex: 'prixAchat', renderer: moneyRenderer, align: 'right' },
                    { text: 'Prix Vente', dataIndex: 'prixVente', renderer: moneyRenderer, align: 'right' },
                    { text: 'Ratio V/A', dataIndex: 'ratioVA', flex: 1, align: 'center', renderer: ratioRenderer, sortable: true }
                ]
            }, {
                title: 'Synthèse & Recommandations',
                itemId: 'analysisTextTab',
                xtype: 'panel',
                bodyPadding: 15,
                autoScroll: true,
                html: 'Chargement de l\'analyse...'
            }]
        });
        
        this.items = [tabPanel];

        this.tbar = [{
            xtype: 'combobox',
            fieldLabel: 'Filtrer par écart',
            labelWidth: 100,
            value: 'all',
            store: Ext.create('Ext.data.Store', {
                fields: ['id', 'name'],
                data: [
                    { "id": "all", "name": "Tous" },
                    { "id": "with", "name": "Avec Écart" },
                    { "id": "without", "name": "Sans Écart" }
                ]
            }),
            queryMode: 'local',
            displayField: 'name',
            valueField: 'id',
            listeners: {
                select: function(cmp) {
                    me.filterAdvancedData(cmp.getValue());
                }
            }
        }];

        this.dockedItems = [{
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            items: ['->', 
            {
                text: 'Imprimer (PDF)',
                icon: 'resources/images/icons/fam/page_white_acrobat.png',
                handler: function() { me.onPrintClick(); }
            },
            {
                text: 'Exporter (Excel)',
                icon: 'resources/images/icons/fam/page_white_excel.png',
                handler: function() { me.onExcelExportClick(); }
            },
            {
                text: 'Retour',
                icon: 'resources/images/icons/fam/door_out.png',
                handler: function() { me.onbtncancel(); }
            }]
        }];

        this.callParent();
        this.setTitle(this.getTitre());

        Ext.Ajax.request({
            method: 'POST',
            url: me.url_api_analyse_avancee,
            params: {
                inventaireId: me.getOdatasource().lg_INVENTAIRE_ID,
                inventaireName: me.getOdatasource().str_NAME
            },
            success: function(response) {
                var responseObject = Ext.JSON.decode(response.responseText);
                
                if (responseObject.success) {
                    me.allSyntheseData = responseObject.synthese;
                    
                    // --- CORRECTION : Pré-calcul du ratio avant de stocker les données ---
                    me.allDetailData = Ext.Array.map(responseObject.detailProduits, function(item) {
                        if (item.prixAchat && item.prixAchat > 0) {
                            item.ratioVA = item.prixVente / item.prixAchat;
                        } else {
                            item.ratioVA = 0;
                        }
                        return item;
                    });
                    
                    me.down('#abcGrid').getStore().loadData(responseObject.analyseABC);
                    var analysisPanel = me.down('#analysisTextTab');
                    var formattedText = responseObject.analysisText.replace(/\n/g, '<br/>');
                    analysisPanel.update('<div style="font-family: Arial, sans-serif; line-height: 1.6;">' + formattedText + '</div>');
                    
                    me.filterAdvancedData('all');
                } else {
                    Ext.Msg.alert('Erreur', responseObject.message || 'Impossible de charger les données d\'analyse avancée.');
                }
            },
            failure: function() {
                Ext.Msg.alert('Erreur Serveur', 'Une erreur est survenue lors de la communication avec le serveur.');
            }
        });
    },

    filterAdvancedData: function(filterType) {
        var me = this;
        var storeSynthese = me.down('#syntheseGrid').getStore();
        
        var syntheseToLoad = [];
        if (filterType === 'all') {
            syntheseToLoad = me.allSyntheseData;
        } else {
            syntheseToLoad = Ext.Array.filter(me.allSyntheseData, function(item) {
                return (filterType === 'with') ? item.ecartValeurAchat !== 0 : item.ecartValeurAchat === 0;
            });
        }
        storeSynthese.loadData(syntheseToLoad);
        
        me.filterDetailData();
    },

    filterDetailData: function() {
        var me = this;
        var storeDetail = me.down('#detailGrid').getStore();
        
        var minRatio = me.down('#ratioMinFilter').getValue();
        var maxRatio = me.down('#ratioMaxFilter').getValue();
        var ecartFilter = me.down('combobox').getValue();

        var dataToLoad = Ext.Array.filter(me.allDetailData, function(item) {
            var ratio = item.ratioVA; // Utilise le ratio pré-calculé
            var ecart = item.ecartQuantite;
            
            var ecartMatch = true;
            if (ecartFilter === 'with') {
                ecartMatch = (ecart !== 0);
            } else if (ecartFilter === 'without') {
                ecartMatch = (ecart === 0);
            }
            
            var minMatch = (minRatio === null || minRatio === '') || (ratio >= minRatio);
            var maxMatch = (maxRatio === null || maxRatio === '') || (ratio <= maxRatio);
            
            return ecartMatch && minMatch && maxMatch;
        });

        storeDetail.loadData(dataToLoad);
    },

    onPrintClick: function() {
        var me = this;
        var form = Ext.create('Ext.form.Panel', {
            standardSubmit: true,
            url: me.url_api_pdf_avancee,
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
            url: me.url_api_excel_avancee,
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
