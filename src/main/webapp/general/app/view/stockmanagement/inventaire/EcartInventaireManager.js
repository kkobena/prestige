var url_services_data_ecartinventaire = '../webservices/stockmanagement/inventaire/ws_data_ecart_inventaire.jsp';
var url_services_pdf_inventaire = '../webservices/stockmanagement/inventaire/ws_generate_pdf.jsp';


Ext.define('testextjs.view.stockmanagement.inventaire.EcartInventaireManager', {
    extend: 'Ext.grid.Panel',
    xtype: 'ecartinventaire',
    id: 'ecartinventaireID',
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
    title: 'Gestion des ecarts inventaires',
    plain: true,
    maximizable: true,
    tools: [{type: "pin"}],
    closable: true,
    frame: true,
    initComponent: function () {

        var itemsPerPage = 20;

        var store = new Ext.data.Store({
            model: 'testextjs.model.Famille',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_ecartinventaire,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });


        var store_type = new Ext.data.Store({
            fields: ['str_TYPE', 'str_STATUT_TRANSACTION'],
            data: [{str_TYPE: 'Manquant', str_STATUT_TRANSACTION: 'MANQUANT'}, {str_TYPE: 'Surplus', str_STATUT_TRANSACTION: 'SURPLUS'}]
        });


        this.cellEditing = new Ext.grid.plugin.CellEditing({
            clicksToEdit: 1
        });


        Ext.apply(this, {
            width: 1000,
            height: 580,
            plugins: [this.cellEditing],
            store: store,
            id: 'GridEcartInventaireID',
            columns: [{
                    header: 'lg_INVENTAIRE_FAMILLE_ID',
                    dataIndex: 'lg_INVENTAIRE_FAMILLE_ID',
                    hidden: true,
                    flex: 1
                }, {
                    header: 'lg_INVENTAIRE_ID',
                    dataIndex: 'lg_INVENTAIRE_ID',
                    hidden: true,
                    flex: 1
                }, {
                    header: 'lg_FAMILLE_ID',
                    dataIndex: 'lg_FAMILLE_ID',
                    hidden: true,
                    flex: 1
                },
                {
                    header: 'CIP',
                    dataIndex: 'int_CIP',
                    flex: 1
                },
                {
                    header: 'Designation',
                    dataIndex: 'str_DESCRIPTION',
                    flex: 1
                },
                {
                    header: 'Emplacement',
                    dataIndex: 'lg_ZONE_GEO_ID',
                    flex: 1
                },
                {
                    header: 'Famille',
                    dataIndex: 'lg_FAMILLEARTICLE_ID',
                    flex: 1
                },
                {
                    header: 'Utilisateur',
                    dataIndex: 'lg_INDICATEUR_REAPPROVISIONNEMENT_ID',
                    flex: 1
                },
                {
                    header: 'Stock initial',
                    dataIndex: 'int_MOY_VENTE',
                    flex: 1
                },
                {
                    header: 'Stock final',
                    dataIndex: 'int_NUMBER_AVAILABLE',
                    flex: 1
                },
                {
                    header: 'Ecart',
                    dataIndex: 'int_QTE_SORTIE',
                    flex: 1
                }, {
                    header: 'Derniere MAJ',
                    dataIndex: 'dt_UPDATED',
                    flex: 1
                }],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [/*{
                    text: 'Imprimer la fiche',
                    scope: this,
                    handler: this.onbtnprint
                }, '-', */{
                    xtype: 'combobox',
                    name: 'str_TYPE',
                    margins: '0 0 0 10',
                    id: 'str_TYPE',
                    store: store_type,
                    valueField: 'str_STATUT_TRANSACTION',
                    displayField: 'str_TYPE',
                    typeAhead: true,
                    queryMode: 'remote',
                    flex: 0.5,
                    emptyText: 'Type...',
                    listeners: {
                        select: function (cmp) {
                            var value = cmp.getValue();
//                            var search_value = "";
//
//                            if (Ext.getCmp('rechecher').getValue() == null) {
//                                search_value = "";
//                            } else {
//                                search_value = Ext.getCmp('rechecher').getValue();
//                            }
//                            var val = Ext.getCmp('rechecher');
                            //     alert("value " + value + " recherche " + search_value);
                            var OGrid = Ext.getCmp('GridEcartInventaireID');
                            var url_services_data_suivistockvente = '../webservices/stockmanagement/inventaire/ws_data_ecart_inventaire.jsp';
//                            OGrid.getStore().getProxy().url = url_services_data_suivistockvente + "?str_TYPE=" + value + "&search_value=" + search_value;
                            OGrid.getStore().getProxy().url = url_services_data_suivistockvente + "?str_TYPE=" + value+"&ECARTP";
                            OGrid.getStore().load();
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
    loadStore: function () {
        this.getStore().load({
            callback: this.onStoreLoad
        });
    },
    onStoreLoad: function () {
    },
    onbtnprint: function (bouton) {
        Ext.MessageBox.confirm('Message',
                'Confirmation de l\'impression de la fiche d\'inventaire',
                function (btn) {
                    if (btn == 'yes') {
                        var OGrid = Ext.getCmp('GridEcartInventaireID');
                        var rec = OGrid.getStore().getAt(0);
                        var chaine = location.pathname;
                        var reg = new RegExp("[/]+", "g");
                        var tableau = chaine.split(reg);
                        var sitename = tableau[1];
                        var lg_INVENTAIRE_ID = rec.get('lg_INVENTAIRE_ID');
                        var linkUrl = url_services_pdf_inventaire + '?lg_INVENTAIRE_ID=' + lg_INVENTAIRE_ID;
                        alert("Ok ca marche " + linkUrl);
//                        window.open(linkUrl);
//
//                        var xtype = "";
//                        xtype = "retrocessionmanager";
//                        testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "", "", "");
                        return;
                    }
                });

    },
    onRechClick: function () {
        var val = Ext.getCmp('rechecher');
        var str_TYPE = "";

        if (Ext.getCmp('str_TYPE').getValue() == null) {
            str_TYPE = "";
        } else {
            str_TYPE = Ext.getCmp('str_TYPE').getValue();
        }

        alert("val.value " + val.value + " " + " str_TYPE " + str_TYPE);
        this.getStore().load({
            params: {
                search_value: val.value,
                str_TYPE: str_TYPE
            }
        }, url_services_data_ecartinventaire);
    }

});