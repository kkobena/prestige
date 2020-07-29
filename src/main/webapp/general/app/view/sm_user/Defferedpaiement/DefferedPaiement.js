/* global Ext */
var Me;
var valdatedebut;
var valdatefin;
var myAppController;
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}
Ext.define('testextjs.view.sm_user.Defferedpaiement.DefferedPaiement', {
    extend: 'Ext.grid.Panel',
    xtype: 'deferredpayment',
    id: 'deferredpaymentID',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.window.Window',
        'Ext.data.*',
        'Ext.util.*',
        'Ext.form.*',
        'Ext.JSON.*',
        'Ext.ux.ProgressBarPager',
        'testextjs.model.ReglementDiffered',
        'testextjs.view.sm_user.Defferedpaiement.action.DefferredDetails'

    ],
    title: 'Gestion des Diff&eacute;r&eacute;s',
    frame: true,
    initComponent: function () {


        var itemsPerPage = 20;
        var store = new Ext.data.Store({
            model: 'testextjs.model.ReglementDiffered',
            pageSize: itemsPerPage,
            groupField: 'ORGANISME',
            autoLoad: true,
            proxy: {
                type: 'ajax',
                url: '../webservices/sm_user/defferredpayment/ws_data.jsp',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }
            }

        });

        var clientsStore = new Ext.data.Store({
            model: 'testextjs.model.Client',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../webservices/sm_user/defferredpayment/ws_client_data.jsp',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }
            }

        });

        Ext.apply(this, {
            width: "98%",
            height: 580,
            cls: 'custompanel',
            features: [
                {
                    ftype: 'groupingsummary',
                    collapsible: true,
                    groupHeaderTpl: "{[values.rows[0].data.ORGANISME]}",
                    hideGroupedHeader: true,
                    showSummaryRow: true
                }],
            store: store,
            id: 'GridDeffered',
            columns: [{
                    header: 'lg_DEFFERED_ID',
                    dataIndex: 'lg_DEFFERED_ID',
                    hidden: true,
                    width: 50
                }, {
                    header: 'Organisme',
                    dataIndex: 'ORGANISME',
                    flex: 1



                }, {
                    header: 'Mode R&egrave;glement',
                    dataIndex: 'MODEREGLEMENT',
                    flex: 1.5, summaryType: "count",
                    summaryRenderer: function (value) {
                        return "<b>Nombre de R&egrave;glements </b><span style='color:blue;font-weight:800;'>" + value + "</span>";

                    }
                }, {
                    header: 'Montant R&egrave;gl&eacute;',
                    dataIndex: 'MONTANTREGL',
                    flex: 1,
                    renderer: amountformat,
                    align: 'right',
                    summaryType: "sum",
                    summaryRenderer: function (value) {
                        return " <span style='color:blue;font-weight:800;'>" + Ext.util.Format.number(Ext.Number.toFixed(value, 0), '0,000.') + "</span> ";
                    }

                }, {
                    header: 'Nontant.Attendu',
                    dataIndex: 'MONTANTATT',
                    flex: 1,
                    renderer: amountformat,
                    align: 'right'
                }, 
                
                {
                    header: 'Date R&egrave;glement',
                    dataIndex: 'DATEREGL',
                    flex: 0.8

                }, {
                    header: 'Heure R&egrave;glement',
                    dataIndex: 'HEUREREGL',
                    flex: 0.8

                }, {
                    header: 'Op&eacute;rateur',
                    dataIndex: 'OPPERATEUR',
                    flex: 1

                }


                , {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/application_view_list.png',
                            tooltip: 'Voir le detail du reglement',
                            scope: this,
                            handler: this.onManageDetailsClick
                        }
                    ]
                }


            ],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [
                {
                    text: 'Faire un r&egrave;glement',
                    scope: this,
                    iconCls: 'addicon',
                    handler: this.onAddCreate
                }, '-',
                {
                    xtype: 'combobox',
                    fieldLabel: 'Client:',
                    labelWidth: 35,
                    margins: '0 5 0 5',
                    id: 'reg_CLIENT_ID',
                    store: clientsStore,
                    valueField: 'lg_CLIENT_ID',
                    displayField: 'str_FIRST_LAST_NAME',
                    typeAhead: true,
                    queryMode: 'remote',
                    minChars: 2.5,
                    flex: 2,
                    pageSize: 20,
                    emptyText: 'Selectionner le client',
                    listeners: {
                        select: function (field, e, options) {
                            store.load({
                                params: {
                                    lg_CLIENT_ID: this.getValue(),
                                    dt_debut: Ext.getCmp('regdatedebut').getSubmitValue(),
                                    dt_fin: Ext.getCmp('regdatefin').getSubmitValue()
                                }

                            });

                        }
                    }
                }





                , '-', {
                    xtype: 'datefield',
                    id: 'regdatedebut',
                    emptyText: 'Date debut',
//                    width: 150,
                    flex: 1,
                    submitFormat: 'Y-m-d',
                    maxValue: new Date(),
                    format: 'd/m/Y',
                    listeners: {
                        'change': function (me) {
                            Ext.getCmp('regdatefin').setMinValue(me.getValue());
                        }
                    }
                }, {
                    xtype: 'tbseparator'
                }, {
                    xtype: 'datefield',
                    id: 'regdatefin',
                    emptyText: 'Date fin',
                    maxValue: new Date(),
                    submitFormat: 'Y-m-d',
//                    width: 150,
                    flex: 1,
                    format: 'd/m/Y',
                    listeners: {
                        'change': function (me) {
                            Ext.getCmp('regdatedebut').setMaxValue(me.getValue());
                        }
                    }
                }, {
                    xtype: 'tbseparator'
                }, {
                    text: 'rechercher',
                    tooltip: 'rechercher',
                    iconCls: 'searchicon',
                    flex: 0.6,
                    scope: this,
                    handler: function () {
                        var lg_client_id = Ext.getCmp('reg_CLIENT_ID').getValue();
                        if (lg_client_id === null) {
                            lg_client_id = "";
                        }
                        store.load({
                            params: {
                                lg_CLIENT_ID: lg_client_id,
                                dt_debut: Ext.getCmp('regdatedebut').getSubmitValue(),
                                dt_fin: Ext.getCmp('regdatefin').getSubmitValue()
                            }

                        });
                    }
                },
                {
                    xtype: 'tbseparator'
                },
                {
                    text: 'Imprimer',
                    tooltip: 'Imprimer',
                    iconCls: 'printable',
                    flex: 0.6,
                    scope: this,
                    handler: this.onPrint
                }

            ],
            bbar: {
                xtype: 'pagingtoolbar',
                pageSize: 10,
                store: store,
                displayInfo: true,
                plugins: new Ext.ux.ProgressBarPager()
            }
        });

        this.callParent();

        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        })

    },
    loadStore: function () {
        this.getStore().load({
            callback: this.onStoreLoad
        });
    },
    onStoreLoad: function () {
    },
    onAddCreate: function () {
        var xtype = "editderredpayment";
        var alias = 'widget.' + xtype;
        testextjs.app.getController('App').onLoadNewComponent(xtype, "R&eacute;glement de Diff&eacute;r&eacute;", "0");

    },
    onPrint: function () {
        var dt_fin = Ext.getCmp('regdatefin').getSubmitValue(), dt_debut = Ext.getCmp('regdatedebut').getSubmitValue();
        var lg_client_id = Ext.getCmp('reg_CLIENT_ID').getValue();
        if (lg_client_id === null) {
            lg_client_id = "";
        }
        var linkUrl = "../webservices/sm_user/defferredpayment/ws_deferredpayment_pdf.jsp?lg_CLIENT_ID=" + lg_client_id + "&dt_debut=" + dt_debut + "&dt_fin=" + dt_fin;
        window.open(linkUrl);
    },
    onManageDetailsClick: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        var xtype = "defferreddetails";
        var alias = 'widget.' + xtype;
        testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "Les dossiers li&eacute;s au r&eacute;glement", rec.get('lg_DEFFERED_ID'), rec.data);

    }
});