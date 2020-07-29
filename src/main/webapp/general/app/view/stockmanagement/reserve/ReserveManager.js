var url_services_data_reserve = '../webservices/stockmanagement/reserve/ws_data.jsp';
var url_services_transaction_reserve = '../webservices/stockmanagement/reserve/ws_transaction.jsp?mode=';
var url_services_pdf_liste_reassort = '../webservices/stockmanagement/reserve/ws_generate_pdf.jsp';

var Me;
Ext.define('testextjs.view.stockmanagement.reserve.ReserveManager', {
    extend: 'Ext.grid.Panel',
    xtype: 'reservemanager',
    id: 'reservemanagerID',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.window.Window',
        'Ext.data.*',
        'Ext.util.*',
        'Ext.form.*',
        'Ext.JSON.*',
        'Ext.ux.ProgressBarPager',
        'Ext.ux.grid.Printer'

    ],
    title: 'Gestion des reserves',
    plain: true,
    maximizable: true,
//    tools: [{type: "pin"}],
    closable: false,
    frame: true,
    initComponent: function() {
        Me = this;
        var itemsPerPage = 20;

        var store = new Ext.data.Store({
            model: 'testextjs.model.FamilleStock',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_reserve,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });

        var store_type = new Ext.data.Store({
            fields: ['str_TYPE_TRANSACTION', 'str_desc'],
            data: [{str_TYPE_TRANSACTION: 'ALL', str_desc: 'Tous'}, {str_TYPE_TRANSACTION: 'REASSORT', str_desc: 'Les articles a reassortir'}]
        });

        this.cellEditing = new Ext.grid.plugin.CellEditing({
            clicksToEdit: 1
        });


        Ext.apply(this, {
            width: '98%',
            height: valheight,
            plugins: [this.cellEditing],
            store: store,
            id: 'GridReserveID',
            columns: [{
                    header: 'lg_FAMILLE_ID',
                    dataIndex: 'lg_FAMILLE_ID',
                    hidden: true,
                    flex: 1
                }, {
                    header: 'CIP',
                    dataIndex: 'int_CIP',
                    flex: 1
                },
                {
                    header: 'Designation',
                    dataIndex: 'str_NAME',
                    flex: 1
                },
                {
                    header: 'Emplacement',
                    dataIndex: 'lg_ZONE_GEO_ID',
                    flex: 1
                },
                {
                    header: 'Quantite.Rayon',
                    dataIndex: 'int_NUMBER',
                    flex: 1

                },
                {
                    header: 'Quantite.Reserve',
                    dataIndex: 'int_STOCK_REAPROVISONEMENT',
                    flex: 1

                },
                {
                    header: 'Quantite.Reassort',
                    dataIndex: 'int_NUMBER_ENTREE',
                    flex: 1
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/add.png',
                            tooltip: 'Faire un assort',
                            scope: this,
                            handler: this.onAssortClick,
                            getClass: function(value, metadata, record) {
                                if (record.get('int_NUMBER') > 0) {  //read your condition from the record
                                    return 'x-display-hide'; //affiche l'icone
                                } else {
                                    return 'x-hide-display'; //cache l'icone
                                }
                            }
                        }]
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/delete.png',
                            tooltip: 'Faire un reassort',
                            scope: this,
                            handler: this.onRemoveClick,
                            getClass: function(value, metadata, record) {
                                if (record.get('int_STOCK_REAPROVISONEMENT') > 0) {  //read your condition from the record
                                    return 'x-display-hide'; //affiche l'icone
                                } else {
                                    return 'x-hide-display'; //cache l'icone
                                }
                            }
                        }]
                }],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [/*{
             text: 'Ajouter',
             scope: this,
             tooltip: 'Ajouter un article en reserve',
             handler: this.onAddClick
             }, */{
                    xtype: 'combobox',
                    name: 'str_TYPE_TRANSACTION',
                    margins: '0 0 0 10',
                    id: 'str_TYPE_TRANSACTION',
                    store: store_type,
                    valueField: 'str_TYPE_TRANSACTION',
                    displayField: 'str_desc',
                    typeAhead: true,
                    queryMode: 'remote',
//                    flex: 1,
                    emptyText: 'Filtre article...',
                    listeners: {
                        select: function(cmp) {
                            var value = cmp.getValue();

                            var OGrid = Ext.getCmp('GridReserveID');
                            OGrid.getStore().getProxy().url = url_services_data_reserve + "?str_TYPE_TRANSACTION=" + value;
                            OGrid.getStore().reload();
                        }
                    }
                }, '-', {
                    xtype: 'textfield',
                    id: 'rechecher',
                    name: 'facture',
                    emptyText: 'Rech'
                }, {
                    text: 'rechercher',
                    tooltip: 'rechercher',
                    scope: this,
                    handler: this.onRechClick
                }, '-', {
                    text: 'Imprimer liste reassort',
                    id: 'btn_devis',
//                    iconCls: 'icon-clear-group',
                    scope: this,
                    handler: this.onbtnprint
                }],
            bbar: {
                xtype: 'pagingtoolbar',
                store: store, // same store GridPanel is using
                dock: 'bottom',
                displayInfo: true
            }
        });

        this.callParent();

        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        })


    },
    loadStore: function() {
        this.getStore().load({
            callback: this.onStoreLoad
        });
    },
    onStoreLoad: function() {
    },
    onbtnprint: function() {
        Ext.MessageBox.confirm('Message',
                'Confirmation de l\'impression de la liste',
                function(btn) {
                    if (btn == 'yes') {
                        onPdfClickReserve();
                        return;
                    }
                });

    },
    onAssortClick: function(grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);

        new testextjs.view.stockmanagement.reserve.action.add({
            odatasource: rec.data,
            parentview: this,
            mode: "assort",
            titre: "Assort de l'article [" + rec.get('str_NAME') + "]"
        });
    },
    onAddClick: function() {
        new testextjs.view.stockmanagement.reserve.action.addToReserve({
            odatasource: "",
            parentview: this,
            mode: "assort",
            titre: "Ajouter un article dans la reserve"
        });
    },
    onRemoveClick: function(grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);

        new testextjs.view.stockmanagement.reserve.action.add({
            odatasource: rec.data,
            parentview: this,
            mode: "reassort",
            titre: "Reassort de l'article [" + rec.get('str_NAME') + "]"
        });
    },
    onRechClick: function() {
        var val = Ext.getCmp('rechecher');
        var str_TYPE_TRANSACTION = "ALL";

        if (Ext.getCmp('str_TYPE_TRANSACTION').getValue() == null) {
            str_TYPE_TRANSACTION = "ALL";
        } else {
            str_TYPE_TRANSACTION = Ext.getCmp('str_TYPE_TRANSACTION').getValue();
        }

        this.getStore().load({
            params: {
                search_value: val.value,
                str_TYPE_TRANSACTION: str_TYPE_TRANSACTION
            }
        }, url_services_data_reserve);
    }

});

function onPdfClickReserve() {
    var chaine = location.pathname;
    var reg = new RegExp("[/]+", "g");
    var tableau = chaine.split(reg);
    var sitename = tableau[1];
    var linkUrl = url_services_pdf_liste_reassort;
    window.open(linkUrl);
}