/**
 * @class testextjs.controller.BalanceSaleCashDepotController
 * @extends Ext.app.Controller
 * @description Contrôleur pour la vue BalanceSaleCashDepot.
 */
Ext.define('testextjs.controller.BalanceSaleCashDepotController', {
    extend: 'Ext.app.Controller',
    views: ['caisseManager.balance.BalanceSaleCashDepot'],
    stores: ['BalanceSaleCashDepotStore'],
    models: ['BalanceSaleCashDepotModel'],

    refs: [{
        ref: 'balanceView',
        selector: 'balancesalecashdepot'
    }, {
        ref: 'grid',
        selector: '#gridBalance'
    }, {
        ref: 'summaryPanel',
        selector: '#summaryPanel'
    }, {
        ref: 'depotCombo',
        selector: '#depot'
    }, {
        ref: 'dtStart',
        selector: '#dtStart'
    }, {
        ref: 'dtEnd',
        selector: '#dtEnd'
    }],

    init: function() {
        this.control({
            '#searchBtn': {
                click: this.onSearchClick
            },
            '#printBtn': {
                click: this.onPrintClick
            },
            '#depot': {
                select: this.onSearchClick
            },
            'balancesalecashdepot': {
                afterrender: this.onViewAfterRender
            }
        });
    },
    
    onViewAfterRender: function() {
        // Charge les données initiales une fois la vue rendue et le combobox initialisé
        var depotStore = this.getDepotCombo().getStore();
        if (depotStore.isLoading() || depotStore.getCount() === 0) {
            depotStore.on('load', this.onSearchClick, this, { single: true });
        } else {
            this.onSearchClick();
        }
    },

    onSearchClick: function() {
        var me = this;
        var grid = me.getGrid();
        var store = grid.getStore();
        
        var depotId = me.getDepotCombo().getValue();
        var dtStart = me.getDtStart().getSubmitValue();
        var dtEnd = me.getDtEnd().getSubmitValue();

        if (!depotId) {
            // Ne fait rien si le combo n'a pas encore de valeur
            return;
        }

        grid.setLoading(true);

        store.load({
            params: {
                emplacementId: depotId,
                dtStart: dtStart,
                dtEnd: dtEnd
            },
            callback: function(records, operation, success) {
                grid.setLoading(false);
                if (success) {
                    var rawData = store.getProxy().getReader().rawData;
                    if (rawData && rawData.metaData) {
                        me.updateSummary(rawData.metaData);
                    }
                } else {
                    Ext.MessageBox.alert('Erreur', 'Impossible de charger les données.');
                }
            }
        });
    },

    updateSummary: function(metaData) {
        var summaryPanel = this.getSummaryPanel();
        summaryPanel.down('#montantTTC_summary').setValue(metaData.montantTTC);
        summaryPanel.down('#montantNet_summary').setValue(metaData.montantNet);
        summaryPanel.down('#marge_summary').setValue(metaData.marge);
        summaryPanel.down('#nbreVente_summary').setValue(metaData.nbreVente);
        summaryPanel.down('#panierMoyen_summary').setValue(metaData.panierMoyen);
        summaryPanel.down('#montantAchat_summary').setValue(metaData.montantAchat);
        summaryPanel.down('#ratioVA_summary').setValue(metaData.ratioVA);
        summaryPanel.down('#rationAV_summary').setValue(metaData.rationAV);
        summaryPanel.down('#montantEsp_summary').setValue(metaData.montantEsp);
        summaryPanel.down('#montantCheque_summary').setValue(metaData.montantCheque);
        summaryPanel.down('#montantCB_summary').setValue(metaData.montantCB);
        summaryPanel.down('#montantMobilePayment_summary').setValue(metaData.montantMobilePayment);
    },

    onPrintClick: function() {
        var depotId = this.getDepotCombo().getValue();
        var dtStart = this.getDtStart().getSubmitValue();
        var dtEnd = this.getEnd().getSubmitValue();
        
        if (!depotId) {
            Ext.MessageBox.alert('Erreur', 'Veuillez sélectionner un dépôt.');
            return;
        }

        var link = '../api/v1/balance/print-balancesalecashdepot?emplacementId=' + depotId +
                   '&dtStart=' + dtStart + '&dtEnd=' + dtEnd;
                   
        // Ouvre le lien dans un nouvel onglet pour télécharger le PDF
        window.open(link);
    }
});
