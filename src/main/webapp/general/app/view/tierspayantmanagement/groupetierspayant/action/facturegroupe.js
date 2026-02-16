/* global Ext */

var url_services_data_typefacture = '../webservices/sm_user/typefacture/ws_data.jsp';
var url_services_transaction_facturation = '../webservices/sm_user/facturation/ws_transaction.jsp?mode=';
var url_services_pdf_tiers_payant = '../webservices/sm_user/facturation/ws_rp_facture_tiers_payant.jsp?lg_FACTURE_ID=';
var url_services_pdf_fournisseurs = '../webservices/sm_user/facturation/ws_rp_facture_fournisseur.jsp?lg_FACTURE_ID=';
var url_services_data_tiers_payant = '../webservices/tierspayantmanagement/tierspayant/ws_search_data.jsp';

var Me;
var myAppController;
var CODEFACTURE = "";
var win;

function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.tierspayantmanagement.groupetierspayant.action.facturegroupe', {
    extend: 'Ext.window.Window',
    maximizable: true,
    xtype: 'facturegroupe',
    id: 'facturegroupeID',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.window.Window',
        'Ext.data.*',
        'Ext.util.*',
        'Ext.form.*',
        'Ext.JSON.*',
        'testextjs.model.Facture',
        'testextjs.view.sm_user.editfacture.action.add',
        'Ext.ux.ProgressBarPager'
    ],
    title: 'Gestion des facturations ',
    frame: true,
    config: {
        odatasource: '',
        parentview: '',
        mode: '',
        titre: ''
    },

    initComponent: function () {

        Me = this;
        CODEFACTURE = this.getOdatasource(); // ✅ c'est le code groupe (TGroupeFactures.strCODEFACTURE)
        myAppController = Ext.create('testextjs.controller.App', {});
        var itemsPerPage = 20;

        // ✅ REST URL
        var REST_DETAILS_URL = '../api/v1/groupe-invoices/details';

        var store = new Ext.data.Store({
            model: 'testextjs.model.Facture',
            pageSize: itemsPerPage,
            autoLoad: false,
            remoteSort: true,
            proxy: {
                type: 'ajax',
                url: REST_DETAILS_URL, // ✅ REST
                timeout: 240000,
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                },
                extraParams: {
                    CODEFACTURE: CODEFACTURE || '',
                    search_value: '',
                    lgTP: ''
                }
            }
        });

        // ✅ premier load
        store.load({params: {CODEFACTURE: CODEFACTURE}});

        var searchstore = Ext.create('testextjs.store.Statistics.TiersPayans');

        win = new Ext.window.Window({
            autoShow: true,
            title: this.getTitre(),
            maximizable: true,
            width: '80%',
            height: 600,
            minWidth: 300,
            minHeight: 200,
            layout: 'fit',
            plain: true,
            items: [
                {
                    xtype: 'grid',
                    store: store,
                    id: 'goupInvoiceGRIDID',
                    columns: [
                        { header: 'lg_FACTURE_ID', dataIndex: 'lg_FACTURE_ID', hidden: true, width: 50 },
                        { header: 'Code Facture', dataIndex: 'str_CODE_FACTURE', flex: 0.5 },
                        { header: 'Organisme', dataIndex: 'str_CUSTOMER_NAME', flex: 1 },
                        { header: 'P&eacute;riode', dataIndex: 'str_PERIODE', flex: 1.5 },
                        { header: 'Nombre de Dossiers', dataIndex: 'int_NB_DOSSIER', flex: 0.5, align: 'right', renderer: amountformat },
                        { header: 'Montant Brut', dataIndex: 'MONTANTBRUT', flex: 1, renderer: amountformat, align: 'right' },
                        { header: 'Montant Remise', dataIndex: 'MONTANTREMISE', flex: 1, renderer: amountformat, align: 'right' },
                        { header: 'Montant Forfaitaire', dataIndex: 'MONTANTFORFETAIRE', flex: 1, renderer: amountformat, align: 'right' },
                        { header: 'Montant.Net', dataIndex: 'dbl_MONTANT_CMDE', flex: 1, renderer: amountformat, align: 'right' },
                        { header: 'Montant Pay&eacute;', dataIndex: 'dbl_MONTANT_PAYE', flex: 1, renderer: amountformat, align: 'right' },
                        { header: 'Montant Restant', dataIndex: 'dbl_MONTANT_RESTANT', flex: 1, renderer: amountformat, align: 'right' },
                        { header: 'Date', dataIndex: 'dt_CREATED', flex: 1 },

                        {
                            xtype: 'actioncolumn',
                            width: 30,
                            sortable: false,
                            menuDisabled: true,
                            items: [{
                                getClass: function (v, meta, rec) {
                                    return (rec.get('str_STATUT') !== "paid") ? 'unpaid' : 'paid';
                                },
                                getTip: function (v, meta, rec) {
                                    return (rec.get('str_STATUT') !== "paid") ? 'Supprimer' : 'Sold&eacute;e';
                                },
                                scope: this,
                                handler: this.onRemoveClick
                            }]
                        },
                        {
                            xtype: 'actioncolumn',
                            width: 30,
                            sortable: false,
                            menuDisabled: true,
                            items: [{
                                icon: 'resources/images/icons/fam/grid.png',
                                tooltip: 'Detail Bordereau',
                                scope: this,
                                handler: this.viewdetailFacture
                            }]
                        },
                        {
                            xtype: 'actioncolumn',
                            width: 30,
                            sortable: false,
                            menuDisabled: true,
                            items: [{
                                getClass: function () { return 'printable'; },
                                getTip: function () { return 'Imprimer Bordereau'; },
                                scope: this,
                                handler: this.onPdfClick
                            }]
                        }
                    ],

                    selModel: { selType: 'cellmodel' },

                    tbar: [
                        {
                            xtype: 'textfield',
                            id: 'rechecherFactureGroupe',
                            width: 300,
                            emptyText: 'Rech',
                            listeners: {
                                specialKey: function (field, e) {
                                    if (e.getKey() === e.ENTER) {
                                        Me.onRechClick();
                                    }
                                }
                            }
                        },
                        '-',
                        {
                            xtype: 'combobox',
                            id: 'lgTIERSPAYANTID',
                            flex: 2,
                            store: searchstore,
                            pageSize: 10,
                            valueField: 'lg_TIERS_PAYANT_ID',
                            displayField: 'str_FULLNAME',
                            minChars: 2,
                            queryMode: 'remote',
                            enableKeyEvents: true,
                            emptyText: 'Selectionner tiers payant...',
                            listConfig: {
                                loadingText: 'Recherche...',
                                emptyText: 'Pas de donn&eacute;es trouv&eacute;es.',
                                getInnerTpl: function () {
                                    return '<span>{str_FULLNAME}</span>';
                                }
                            },
                            listeners: {
                                keypress: function (field, e) {
                                    if (e.getKey() === e.BACKSPACE || e.getKey() === 46) {
                                        if (field.getValue().length <= 2) {
                                            field.getStore().load();
                                        }
                                    }
                                },
                                select: function () {
                                    Me.onRechClick();
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
                            handler: this.onRechClick
                        }
                    ],

                    bbar: {
                        xtype: 'pagingtoolbar',
                        pageSize: itemsPerPage,
                        store: store,
                        displayInfo: true,
                        plugins: new Ext.ux.ProgressBarPager(),
                        listeners: {
                            beforechange: function () {
                                // ✅ on pousse les bons params REST avant pagination
                                var myProxy = this.store.getProxy();

                                var val = Ext.getCmp('rechecherFactureGroupe').getValue() || '';
                                var lg_customer_id = '';

                                if (Ext.getCmp('lgTIERSPAYANTID').getValue() !== null && Ext.getCmp('lgTIERSPAYANTID').getValue() !== "") {
                                    lg_customer_id = Ext.getCmp('lgTIERSPAYANTID').getValue();
                                }

                                myProxy.setExtraParam('CODEFACTURE', CODEFACTURE);
                                myProxy.setExtraParam('lgTP', lg_customer_id);
                                myProxy.setExtraParam('search_value', val);
                            }
                        }
                    }
                }
            ],
            buttons: [
                {
                    text: 'Fermer',
                    handler: function () {
                        win.close();
                    }
                }
            ]
        });
    },

    viewdetailFacture: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);

        new testextjs.view.sm_user.editfacture.action.detailTransactionTiersPayant({
            odatasource: rec.data,
            parentview: this,
            mode: "detail_transaction",
            titre: "Detail Bordereau [" + rec.get('str_CUSTOMER_NAME') + "]"
        });
    },

    onRemoveClick: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        if (rec.get('str_STATUT') !== "paid") {

            Ext.MessageBox.confirm('Message', 'confirmez la suppresssion', function (btn) {
                if (btn === 'yes') {
                    myAppController.ShowWaitingProcess();
                    Ext.Ajax.request({
                        url: url_services_transaction_facturation + 'delete',
                        params: {
                            lg_FACTURE_ID: rec.get('lg_FACTURE_ID'),
                            mode: 'delete'
                        },
                        success: function (response) {
                            myAppController.StopWaitingProcess();
                            var object = Ext.JSON.decode(response.responseText, false);
                            if (object.success === "1") {
                                Ext.MessageBox.alert('Infos', "La facture a &eacute;t&eacute; supprim&eacute;e");
                            } else {
                                Ext.MessageBox.show({
                                    title: 'Avertissement',
                                    width: 320,
                                    msg: 'Cette facture a subit un r&eacute;glement',
                                    buttons: Ext.MessageBox.OK,
                                    icon: Ext.MessageBox.WARNING
                                });
                            }
                            grid.getStore().load();
                        },
                        failure: function (response) {
                            myAppController.StopWaitingProcess();
                            Ext.MessageBox.alert('Error Message', response.responseText);
                        }
                    });
                }
            });
        }
    },

    onRechClick: function () {
        var val = Ext.getCmp('rechecherFactureGroupe').getValue() || '';
        var lg_customer_id = '';

        if (Ext.getCmp('lgTIERSPAYANTID').getValue() !== null && Ext.getCmp('lgTIERSPAYANTID').getValue() !== "") {
            lg_customer_id = Ext.getCmp('lgTIERSPAYANTID').getValue();
        }

        Ext.getCmp('goupInvoiceGRIDID').getStore().load({
            params: {
                search_value: val,
                CODEFACTURE: CODEFACTURE,
                lgTP: lg_customer_id
            }
        });
    },

    onPdfClick: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        var lg_FACTURE_ID = rec.get('lg_FACTURE_ID');
        var linkUrl = url_services_pdf_tiers_payant + lg_FACTURE_ID;
        window.open(linkUrl);
    }
});
