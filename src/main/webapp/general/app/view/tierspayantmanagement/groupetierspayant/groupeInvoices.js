/* global Ext */


var Me;
var view_title;
//var mydataSource;
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
        var mydataSource = this.getOdatasource();
        codeFact = mydataSource.CODEGROUPE;
        var groupesInvoice = new Ext.data.Store({
            model: 'testextjs.model.groupFactureModel',
            pageSize: 20,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../webservices/configmanagement/groupe/ws_groupeInvoices.jsp?CODEGROUPE=' + codeFact,
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }
            }

        });

        var groupesStore = new Ext.data.Store({
            model: 'testextjs.model.GroupeModel',
            pageSize: 20,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../webservices/configmanagement/groupe/ws_data.jsp',
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
            columns:
                    [
                        {
                            xtype: 'rownumberer',
                            text: '#',
                            width: 45


                        },
                        {
                            header: 'ID',
                            dataIndex: 'lg_GROUPE_ID',
                            flex: 1,
                            hidden: true

                        },
                        {
                            header: 'Libellé',
                            dataIndex: 'str_LIB',
                            flex: 1.5

                        },
                        {
                            header: 'Nombre de factures',
                            dataIndex: 'NBFACTURES',
                            align: 'right',
                            renderer: amountformat,
                            flex: 1

                        },
                        {
                            header: 'Montant',
                            dataIndex: 'AMOUNT',
                            align: 'right',
                            renderer: amountformat,
                            flex: 1

                        },
                        {
                            header: 'Montant Payé',
                            dataIndex: 'AMOUNTPAYE',
                            align: 'right',
                            renderer: amountformat,
                            flex: 1

                        },
                        {
                            header: 'Montant Restant',
                            dataIndex: 'MONTANTRESTANT',
                            align: 'right',
                            renderer: amountformat,
                            flex: 1

                        },

                        {
                            header: 'Date  d\'édition',
                            dataIndex: 'DATECREATION',

                            flex: 1

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
                                            odatasource: rec.get('CODEFACTURE'),
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
//                    hidden:true,
                            menuDisabled: true,
                            items: [{
                                    getClass: function (v, meta, rec) {

                                        if (rec.get('STATUT') !== "paid") {
                                            if (rec.get('ACTION_REGLER_FACTURE')) {
                                                return 'nonregle';
                                            } else {
                                                return 'x-hide-display';
                                            }

                                        } else {
                                            return 'regle';
                                        }
                                    },
                                    getTip: function (v, meta, rec) {
                                        if (rec.get('STATUT') !== "paid") {
                                            return 'R&eacute;gler Facture';
                                        } else {
                                            return 'Sold&eacute;e ';
                                        }
                                    },
                                    // icon: 'resources/images/icons/fam/folder_go.png',
                                    // tooltip: 'R&eacute;gler Facture',
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
                                    getClass: function (v, meta, rec) {
                                        return 'printable';
                                    },
                                    getTip: function (v, meta, rec) {
                                        return 'Imprimer la facture ';
                                    },
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

                                        if (rec.get('STATUT') !== "paid") {
                                            return 'unpaid';
                                        } else {
                                            return 'paid';
                                        }
                                    },
                                    getTip: function (v, meta, rec) {
                                        if (rec.get('STATUT') !== "paid") {
                                            return 'Supprimer ';
                                        } else {
                                            return 'Sold&eacute;e ';
                                        }
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
                                            success: function (response)
                                            {
                                                testextjs.app.getController('App').StopWaitingProcess();

                                                var object = Ext.JSON.decode(response.responseText, false);
                                                if (object.status === 1) {
                                                    grid.getStore().load();
                                                    Ext.MessageBox.alert('INFO', 'Groupe Supprimé');

                                                } else {
                                                    Ext.MessageBox.alert('ERROR', 'Erreur de suppression');
                                                }

                                            },
                                            failure: function (response)
                                            {
                                                testextjs.app.getController('App').StopWaitingProcess();

                                            }
                                        });
                                    }
                                }]
                        }




                    ],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [

                {
                    xtype: 'datefield',
                    format: 'd/m/Y',
                    emptyText: 'Date début',
                    submitFormat: 'Y-m-d',
                    fieldLabel: 'Du',
                    labelWidth: 20,
                    flex: 1,
                    id: 'dt_start',
                    listeners: {
                        change: function () {
                            Ext.getCmp('dt_end').setMinValue(this.getValue());
                        }
                    }

                }, {
                    xtype: 'tbseparator'
                }

                ,
                {
                    xtype: 'datefield',
                    format: 'd/m/Y',
                    emptyText: 'Date fin',
                    submitFormat: 'Y-m-d',
                    fieldLabel: 'Au',
                    labelWidth: 20,
                    flex: 1,
                    id: 'dt_end',
                    listeners: {
                        change: function () {

                            Ext.getCmp('dt_start').setMaxValue(this.getValue());
                        }
                    }

                }, '-',

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
                        getInnerTpl: function () {
                            return '<span>{str_LIBELLE}</span>';
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
                        select: function (field, e) {
                            var mystore = Ext.getCmp('invoiceGRID').getStore();
                            mystore.getProxy().url = "../webservices/configmanagement/groupe/ws_groupeInvoices.jsp";

                            mystore.load(
                                    {params: {
                                            lg_GROUPE_ID: field.getValue(),
                                            'dt_end': Ext.getCmp('dt_end').getSubmitValue(),
                                            'dt_start': Ext.getCmp('dt_start').getSubmitValue(),
                                            "search_value": Ext.getCmp('groupeSearch').getValue()

                                        }});




                        }


                    }

                }, '-',

                {
                    xtype: 'textfield',
                    id: 'groupeSearch',

                    width: 200,
                    emptyText: 'Rechercher',
                    enableKeyEvents: true,
                    listeners: {

                        specialKey: function (field, e, options) {
                            if (e.getKey() === e.ENTER)
                            {
                                var combox = Ext.getCmp('cmb_fact_GROUPECOMPAGNIES').getValue();
                                if (combox === '' || combox === null) {
                                    combox = '';
                                }
                                var mystore = Ext.getCmp('invoiceGRID').getStore();
                                mystore.getProxy().url = "../webservices/configmanagement/groupe/ws_groupeInvoices.jsp";

                                mystore.load({params: {
                                        lg_GROUPE_ID: combox,
                                        'dt_end': Ext.getCmp('dt_end').getSubmitValue(),
                                        'dt_start': Ext.getCmp('dt_start').getSubmitValue(),
                                        search_value: this.getValue()

                                    }});

                            }


                        }


                    }

                }, {
                    xtype: 'tbseparator'
                }, {
                    text: 'rechercher',
                    tooltip: 'rechercher',
                    iconCls: 'ventesearch',
                    width: 100,
                    scope: this,
                    handler: function () {
                        {
                            var combox = Ext.getCmp('cmb_fact_GROUPECOMPAGNIES').getValue();
                            if (combox === '' || combox === null) {
                                combox = '';
                            }
                            codeFact = '';
                            var mystore = Ext.getCmp('invoiceGRID').getStore();
                            mystore.getProxy().url = "../webservices/configmanagement/groupe/ws_groupeInvoices.jsp";
                            mystore.load({params: {
                                    lg_GROUPE_ID: combox,
                                    'dt_end': Ext.getCmp('dt_end').getSubmitValue(),
                                    'dt_start': Ext.getCmp('dt_start').getSubmitValue(),
                                    search_value: Ext.getCmp('groupeSearch').getValue()

                                }});

                        }
                    }
                }



            ],
            bbar: {
                xtype: 'pagingtoolbar',
                store: groupesInvoice,
                dock: 'bottom',
                displayInfo: true,
                listeners: {
                    beforechange: function (page, currentPage) {
                        var myProxy = this.store.getProxy();

                        myProxy.params = {
                            dt_end: '',
                            dt_start: '',
                            lg_GROUPE_ID: '',
                            search_value: ''


                        };
                        var combox = Ext.getCmp('cmb_fact_GROUPECOMPAGNIES').getValue();
                        if (combox === '' || combox === null) {
                            combox = '';
                        }

                        var search_value = Ext.getCmp('groupeSearch').getValue();
                        myProxy.setExtraParam('dt_start', Ext.getCmp('dt_start').getSubmitValue());
                        myProxy.setExtraParam('dt_end', Ext.getCmp('dt_end').getSubmitValue());
                        myProxy.setExtraParam('search_value', search_value);
                        myProxy.setExtraParam('lg_GROUPE_ID', combox);



                    }

                }
            }


        });

        this.callParent();

        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        });



    },
    loadStore: function () {
        this.getStore().load({
            callback: this.onStoreLoad
        });
    },
    onStoreLoad: function () {
    },

    onPrint: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        var lg_GROUPE_ID = rec.get('lg_GROUPE_ID');
        var CODEFACTURE = rec.get('CODEFACTURE');
        var linkUrl = "../webservices/configmanagement/groupe/group_invoice_pdf.jsp" + "?lg_GROUPE_ID=" + lg_GROUPE_ID + "&CODEFACTURE=" + CODEFACTURE;
        window.open(linkUrl);



    },
    onPaidFactureClick: function (grid, rowIndex) {

        var rec = grid.getStore().getAt(rowIndex);

        if (rec.get('STATUT') !== "paid" && rec.get('ACTION_REGLER_FACTURE')) {
            var xtype = "reglementGroupeFacture";
            var alias = 'widget.' + xtype;
            var rec = grid.getStore().getAt(rowIndex);
            testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "Faire un r&eacute;glement", rec.get('lg_GROUPE_ID'), rec.data);

        }
    }


});