/* global Ext */

var Me;
var codeFact = "";

function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.tierspayantmanagement.groupetierspayant.groupeInvoices', {
    extend: 'Ext.grid.Panel',
    xtype: 'groupeInvoices',
    id: 'groupeInvoicesID',
    title: 'Groupe Factures',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.window.Window',
        'Ext.data.*',
        'Ext.util.*',
        'Ext.form.*',
        'Ext.JSON.*',
        'testextjs.model.GroupeModel',
        'testextjs.model.groupFactureModel',
        'testextjs.view.tierspayantmanagement.groupetierspayant.action.facturegroupe',
        'testextjs.view.tierspayantmanagement.groupetierspayant.reglementGroup'
    ],
    frame: true,
    config: {
        odatasource: ''
    },

    initComponent: function () {
        var mydataSource = this.getOdatasource() || {};
        codeFact = mydataSource.CODEGROUPE || "";

        // ✅ REST URL
        var REST_LIST_URL = '../api/v1/groupe-invoices';

        var groupesInvoice = new Ext.data.Store({
            model: 'testextjs.model.groupFactureModel',
            pageSize: 20,
            autoLoad: false,
            remoteSort: true,
            proxy: {
                type: 'ajax',
                url: REST_LIST_URL,
                timeout: 240000,
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                },
                // ✅ params propres REST
                extraParams: {
                    codeGroupe: codeFact,
                    dtStart: '',
                    dtEnd: '',
                    query: '',
                    lgGroupeId: ''
                }
            }
        });

        // Store groupes (tu peux le laisser en JSP si tu n’as pas encore l’API)
        var groupesStore = new Ext.data.Store({
            model: 'testextjs.model.GroupeModel',
            pageSize: 20,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../webservices/configmanagement/groupe/ws_data.jsp',
                timeout: 240000,
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }
            }
        });

        Me = this;

        Ext.apply(this, {
            width: '98%',
            minHeight: 570,
            maxHeight: 570,
            cls: 'custompanel',
            id: 'invoiceGRID',
            store: groupesInvoice,

            columns: [
                { xtype: 'rownumberer', text: '#', width: 45 },
                { header: 'ID', dataIndex: 'lg_GROUPE_ID', flex: 1, hidden: true },
                { header: 'Libellé', dataIndex: 'str_LIB', flex: 1.5 },
                { header: 'Nombre de factures', dataIndex: 'NBFACTURES', align: 'right', renderer: amountformat, flex: 1 },
                { header: 'Montant', dataIndex: 'AMOUNT', align: 'right', renderer: amountformat, flex: 1 },
                { header: 'Montant Payé', dataIndex: 'AMOUNTPAYE', align: 'right', renderer: amountformat, flex: 1 },
                { header: 'Montant Restant', dataIndex: 'MONTANTRESTANT', align: 'right', renderer: amountformat, flex: 1 },
                { header: "Date  d'édition", dataIndex: 'DATECREATION', flex: 1 },

                {
                    xtype: 'actioncolumn',
                    hidden: false,
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                        icon: 'resources/images/icons/certication.png',
                        tooltip: 'Certification',
                        scope: this,
                        handler: this.shwoChoiceModal
                    }]
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                        iconCls: 'detailclients',
                        tooltip: 'Voir les différentes factures',
                        scope: this,
                        handler: function (grid, rowIndex) {
                            var rec = grid.getStore().getAt(rowIndex);
                            new testextjs.view.tierspayantmanagement.groupetierspayant.action.facturegroupe({
                                odatasource: rec.get('CODEFACTURE'), // code groupe
                                parentview: this,
                                titre: "Les factures du groupe [" + rec.get('str_LIB') + "]"
                            });
                        }
                    }]
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                        getClass: function (v, meta, rec) {
                            if (rec.get('STATUT') !== "paid") {
                                if (rec.get('ACTION_REGLER_FACTURE')) {
                                    return 'nonregle';
                                }
                                return 'x-hide-display';
                            }
                            return 'regle';
                        },
                        getTip: function (v, meta, rec) {
                            return (rec.get('STATUT') !== "paid") ? 'R&eacute;gler Facture' : 'Sold&eacute;e';
                        },
                        scope: this,
                        handler: this.onPaidFactureClick
                    }]
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                        getClass: function () { return 'printable'; },
                        getTip: function () { return 'Imprimer la facture'; },
                        scope: this,
                        handler: this.onPrint
                    }]
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                        getClass: function (v, meta, rec) {
                            return (rec.get('STATUT') !== "paid") ? 'unpaid' : 'paid';
                        },
                        getTip: function (v, meta, rec) {
                            return (rec.get('STATUT') !== "paid") ? 'Supprimer' : 'Sold&eacute;e';
                        },
                        scope: this,
                        handler: function (grid, rowIndex) {
                            var rec = grid.getStore().getAt(rowIndex);
                            if (rec.get('STATUT') === "paid") {
                                Ext.MessageBox.alert('INFO', 'Deja subi un reglement');
                                return;
                            }
                            testextjs.app.getController('App').ShowWaitingProcess();
                            Ext.Ajax.request({
                                url: '../webservices/configmanagement/groupe/ws_transaction.jsp',
                                params: {
                                    mode: 7,
                                    CODEFACTURE: rec.get('CODEFACTURE'),
                                    lg_GROUPE_ID: rec.get('lg_GROUPE_ID')
                                },
                                success: function (response) {
                                    testextjs.app.getController('App').StopWaitingProcess();
                                    var object = Ext.JSON.decode(response.responseText, false);
                                    if (object.status === 1) {
                                        grid.getStore().load();
                                        Ext.MessageBox.alert('INFO', 'Groupe Supprimé');
                                    } else {
                                        Ext.MessageBox.alert('ERROR', 'Erreur de suppression');
                                    }
                                },
                                failure: function () {
                                    testextjs.app.getController('App').StopWaitingProcess();
                                }
                            });
                        }
                    }]
                }
            ],

            selModel: { selType: 'cellmodel' },

            tbar: [
                {
                    xtype: 'datefield',
                    format: 'd/m/Y',
                    emptyText: 'Date début',
                    submitFormat: 'Y-m-d', // ✅ dtStart REST
                    fieldLabel: 'Du',
                    labelWidth: 20,
                    flex: 1,
                    id: 'dt_start',
                    listeners: {
                        change: function () { Ext.getCmp('dt_end').setMinValue(this.getValue()); }
                    }
                },
                { xtype: 'tbseparator' },
                {
                    xtype: 'datefield',
                    format: 'd/m/Y',
                    emptyText: 'Date fin',
                    submitFormat: 'Y-m-d', // ✅ dtEnd REST
                    fieldLabel: 'Au',
                    labelWidth: 20,
                    flex: 1,
                    id: 'dt_end',
                    listeners: {
                        change: function () { Ext.getCmp('dt_start').setMaxValue(this.getValue()); }
                    }
                },
                '-',
                {
                    xtype: 'combobox',
                    margin: '0 15 0 0',
                    id: 'cmb_fact_GROUPECOMPAGNIES',
                    store: groupesStore,
                    pageSize: 20,
                    valueField: 'lg_GROUPE_ID',
                    displayField: 'str_LIBELLE',
                    typeAhead: true,
                    flex: 2,
                    queryMode: 'remote',
                    minChars: 2,
                    emptyText: 'Selection un Groupe',
                    listConfig: {
                        loadingText: 'Recherche...',
                        emptyText: 'Pas de donn&eacute;es trouv&eacute;es.',
                        getInnerTpl: function () { return '<span>{str_LIBELLE}</span>'; }
                    },
                    listeners: {
                        keypress: function (field, e) {
                            if (e.getKey() === e.BACKSPACE || e.getKey() === 46) {
                                if (field.getValue().length <= 2) {
                                    field.getStore().load();
                                }
                            }
                        },
                        select: function (field) {
                            Me.reloadRestStore();
                        }
                    }
                },
                '-',
                {
                    xtype: 'textfield',
                    id: 'groupeSearch',
                    width: 200,
                    emptyText: 'Rechercher',
                    enableKeyEvents: true,
                    listeners: {
                        specialKey: function (field, e) {
                            if (e.getKey() === e.ENTER) {
                                Me.reloadRestStore();
                            }
                        }
                    }
                },
                { xtype: 'tbseparator' },
                {
                    text: 'rechercher',
                    tooltip: 'rechercher',
                    iconCls: 'ventesearch',
                    width: 100,
                    scope: this,
                    handler: function () { Me.reloadRestStore(); }
                }
            ],

            bbar: {
                xtype: 'pagingtoolbar',
                store: groupesInvoice,
                dock: 'bottom',
                displayInfo: true,
                listeners: {
                    beforechange: function () {
                        // ✅ on aligne params REST à chaque page
                        Me.applyRestParams();
                    }
                }
            }
        });

        this.callParent();

        this.on('afterlayout', this.loadStore, this, { delay: 1, single: true });
    },

    loadStore: function () {
        this.applyRestParams();
        this.getStore().load();
    },

    applyRestParams: function () {
        var store = Ext.getCmp('invoiceGRID').getStore();
        var proxy = store.getProxy();

        var gid = Ext.getCmp('cmb_fact_GROUPECOMPAGNIES').getValue();
        if (gid === '' || gid === null) gid = '';

        var dtStart = Ext.getCmp('dt_start').getSubmitValue();
        var dtEnd = Ext.getCmp('dt_end').getSubmitValue();
        var query = Ext.getCmp('groupeSearch').getValue() || '';

        proxy.setExtraParam('dtStart', dtStart || '');
        proxy.setExtraParam('dtEnd', dtEnd || '');
        proxy.setExtraParam('query', query);
        proxy.setExtraParam('lgGroupeId', gid);

        // si ton écran a été ouvert avec un code groupe initial
        proxy.setExtraParam('codeGroupe', codeFact || '');
    },

    reloadRestStore: function () {
        var store = Ext.getCmp('invoiceGRID').getStore();
        this.applyRestParams();
        store.loadPage(1);
    },

    onPrint: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        var lg_GROUPE_ID = rec.get('lg_GROUPE_ID');
        var CODEFACTURE = rec.get('CODEFACTURE');
        window.open("../webservices/configmanagement/groupe/group_invoice_pdf.jsp"
                + "?lg_GROUPE_ID=" + lg_GROUPE_ID + "&CODEFACTURE=" + CODEFACTURE);
    },

    onPaidFactureClick: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        if (rec.get('STATUT') !== "paid" && rec.get('ACTION_REGLER_FACTURE')) {
            var xtype = "reglementGroupeFacture";
            testextjs.app.getController('App')
                    .onLoadNewComponentWithDataSource(xtype, "Faire un r&eacute;glement", rec.get('lg_GROUPE_ID'), rec.data);
        }
    },

    shwoChoiceModal: function (grid, rowIndex) {
        var me = this;
        var rec = grid.getStore().getAt(rowIndex);

        var choice = new Ext.data.Store({
            fields: ['code', 'libelle'],
            data: [
                {code: 'GROUPE_TAUX_TVA', libelle: 'Facture'},
                {code: 'PRODUIT_DETAIL', libelle: 'Produit'}
            ]
        });

        var win = Ext.create('Ext.window.Window', {
            autoShow: true,
            height: 200,
            width: '40%',
            modal: true,
            title: 'Choix du type fe facturation',
            closeAction: 'hide',
            closable: true,
            layout: { type: 'vbox', align: 'stretch' },
            items: [{
                xtype: 'form',
                bodyPadding: 5,
                modelValidation: true,
                layout: { type: 'vbox', align: 'stretch' },
                items: [{
                    xtype: 'fieldset',
                    layout: { type: 'hbox', align: 'stretch' },
                    title: 'Type de facturation',
                    items: [{
                        xtype: 'combo',
                        fieldLabel: 'Type de facturation',
                        allowBlank: false,
                        name: 'typeInvoice',
                        flex: 1,
                        valueField: 'code',
                        displayField: 'libelle',
                        typeAhead: true,
                        queryMode: 'local',
                        pageSize: 2,
                        emptyText: 'Choisir un type...',
                        store: choice
                    }]
                }],
                dockedItems: [{
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    layout: { pack: 'end', type: 'hbox' },
                    items: [{
                        xtype: 'button',
                        text: 'Valider',
                        handler: function (btn) {
                            var formulaire = btn.up('form');
                            if (formulaire.isValid()) {
                                var formValues = formulaire.getValues();
                                me.certify(rec.get('lg_GROUPE_ID'), formValues.typeInvoice, win);
                            }
                        }
                    }, {
                        xtype: 'button',
                        text: 'Annuler',
                        handler: function () { win.destroy(); }
                    }]
                }]
            }]
        });
    },

    certify: function (idFacture, typeInvoice, win) {
        var progress = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
        Ext.Ajax.request({
            url: '../api/v1/fne/invoices/sign-group/' + idFacture + '/' + typeInvoice,
            method: 'GET',
            success: function () {
                progress.hide();
                win.destroy();
                Ext.MessageBox.alert('Info', 'Opération effectuée');
            },
            failure: function (response) {
                progress.hide();
                Ext.MessageBox.alert('Error Message', response.responseText);
            }
        });
    }
});
