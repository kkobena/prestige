/* global Ext */

var url_services_data_inventaire = '../webservices/stockmanagement/inventaire/ws_data.jsp';
var url_services_transaction_inventaire = '../webservices/stockmanagement/inventaire/ws_transactions.jsp?mode=';
var Me;
var url_services_pdf_fiche_inventaire = '../webservices/stockmanagement/inventaire/ws_generate_pdf.jsp';

Ext.define('testextjs.view.stockmanagement.inventaire.InventaireManager', {
    extend: 'Ext.grid.Panel',
    xtype: 'inventaire',
    id: 'inventaireID',
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
    title: 'Gestion des inventaires',
    plain: true,
    maximizable: true,
//    tools: [{type: "pin"}],
    closable: false,
    frame: true,
    initComponent: function() {

        Me = this;
        var itemsPerPage = 20;

        var store = new Ext.data.Store({
            model: 'testextjs.model.Inventaire',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_inventaire,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        }); 

        var store_type = new Ext.data.Store({
            fields: ['str_TYPE', 'str_STATUT_TRANSACTION'],
            data: [{str_TYPE: 'En cours', str_STATUT_TRANSACTION: 'enable'}, {str_TYPE: 'Cloture', str_STATUT_TRANSACTION: 'is_Closed'}]
        });


        this.cellEditing = new Ext.grid.plugin.CellEditing({
            clicksToEdit: 1
        });


        Ext.apply(this, {
            width: '98%',
            height: 580,
            plugins: [this.cellEditing],
            store: store,
            id: 'GridInventaireID',
            columns: [{
                    xtype: 'rownumberer',
                    text: 'Num.Ligne',
                    width: 45,
                    sortable: true/*,
                     locked: true*/
                }, {
                    header: 'lg_INVENTAIRE_ID',
                    dataIndex: 'lg_INVENTAIRE_ID',
                    hidden: true,
                    flex: 1
                }, {
                    header: 'Libelle',
                    dataIndex: 'str_NAME',
                    flex: 1
                },
                {
                    header: 'Commentaire',
                    dataIndex: 'str_DESCRIPTION',
                    flex: 1
                },
                {
                    header: 'Utilisateur',
                    dataIndex: 'lg_USER_ID',
                    flex: 1
                }, {
                    header: 'Date de creation',
                    dataIndex: 'dt_CREATED',
                    flex: 1
                }, {
                    header: 'Statut',
                    dataIndex: 'str_STATUT',
                    flex: 1
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/add.png',
                            tooltip: 'Ajouter des articles a une fiche d\'inventaire',
                            scope: this,
                            handler: this.onAddArticleClick,
                            getClass: function(value, metadata, record) {
                                if (record.get('etat') == "is_Closed") {  //read your condition from the record
                                    return 'x-hide-display'; //cache l'icone
                                } else {
                                    if (record.get('str_TYPE') == "unitaire") {  //read your condition from the record
                                        return 'x-display-hide'; //affiche l'icone
                                    } else {
                                        return 'x-hide-display'; //cache l'icone
                                    }
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
                            icon: 'resources/images/icons/fam/paste_plain.png',
                            tooltip: 'Editer un inventaire',
                            scope: this,
                            handler: this.onEditClick,
                            getClass: function(value, metadata, record) {
                                if (record.get('etat') == "enable") {  //read your condition from the record
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
                            tooltip: 'Supprimer un inventaire',
                            scope: this,
                            handler: this.onRemoveClick
                        }]
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    hidden: true,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/application_view_list.png',
                            tooltip: 'Valorisation d\'inventaire',
                            scope: this,
                            handler: this.onDetailClick
                        }]
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/printer.png',
                            tooltip: 'Imprimer une fiche',
                            scope: this,
                            handler: this.onbtnprint,
                            getClass: function(value, metadata, record) {
                                if (record.get('etat') == "is_Closed") {  //read your condition from the record
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
            tbar: [{
                    text: 'Cr&eacute;er inventaire',
                    scope: this,
                    iconCls: 'addicon',
                    handler: this.onAddClick
                }, '-', {
                    text: 'Cr&eacute;er inventaire unitaire',
                    scope: this,
                    iconCls: 'addicon',
                    handler: this.onAddUnitaireClick
                }, '-', {
                    xtype: 'combobox',
                    name: 'str_TYPE',
                    margins: '0 0 0 10',
                    id: 'str_TYPE',
                    store: store_type,
                    valueField: 'str_STATUT_TRANSACTION',
                    displayField: 'str_TYPE',
                    typeAhead: true,
                    queryMode: 'remote',
//                    flex: 0.5,
                    emptyText: 'Type...',
                    listeners: {
                        select: function(cmp) {
                            var value = cmp.getValue();
                            var OGrid = Ext.getCmp('GridInventaireID');
                            var url_services_data = '../webservices/stockmanagement/inventaire/ws_data.jsp';
                            OGrid.getStore().getProxy().url = url_services_data + "?str_TYPE=" + value;
                            OGrid.getStore().reload();
                        }
                    }
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
    onbtnprint: function(grid, rowIndex) {

        var rec = grid.getStore().getAt(rowIndex);

        Ext.MessageBox.confirm('Message',
                'Confirmation de l\'impression de la fiche d\'inventaire',
                function(btn) {
                    if (btn == 'yes') {
                      
                        var linkUrl = url_services_pdf_fiche_inventaire + '?lg_INVENTAIRE_ID=' + rec.get('lg_INVENTAIRE_ID') + "&str_NAME_FILE=final";
                          window.open(linkUrl);
                        return;
                    }
                });

    },
    onPdfClick: function(lg_INVENTAIRE_ID) {
        var chaine = location.pathname;
        var reg = new RegExp("[/]+", "g");
        var tableau = chaine.split(reg);
        var sitename = tableau[1];
        var linkUrl = url_services_pdf_fiche_inventaire + '?lg_INVENTAIRE_ID=' + lg_INVENTAIRE_ID + "&str_NAME_FILE=final";
        //alert("Ok ca marche " + linkUrl);
        window.open(linkUrl);

    },
    onStoreLoad: function() {
    },
    onAddClick: function() {

        new testextjs.view.stockmanagement.inventaire.action.addBis({
            odatasource: "",
            parentview: this,
            mode: "create",
            titre: "Cr&eacute;er inventaire"
        });
    },
    onAddUnitaireClick: function() {
        new testextjs.view.stockmanagement.inventaire.action.add({
            odatasource: "",
            parentview: this,
            mode: "create",
            titre: "Cr&eacute;er inventaire"
        });
    },
    onAddArticleClick: function(grid, rowIndex) {

        var rec = grid.getStore().getAt(rowIndex);

        new testextjs.view.stockmanagement.inventaire.action.addArticle({
            odatasource: rec.data,
            parentview: this,
            mode: "create",
            titre: "Inventaire [" + rec.get('str_NAME') + "]"
        });

    },
    onEditClick: function(grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        var xtype = "editinventaireManager";
//        alert('xtype '+xtype);
        var alias = 'widget.' + xtype;
        testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "Modification de la fiche d'inventaire", rec.get('lg_INVENTAIRE_ID'), rec.data);
    },
    onDetailClick: function(grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        var xtype = "detailinventaireManager";
//        alert('xtype '+xtype);
        var alias = 'widget.' + xtype;
        testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "Detail de la fiche d'inventaire", rec.get('lg_INVENTAIRE_ID'), rec.data);
    },
    onRemoveClick: function(grid, rowIndex) {
        Ext.MessageBox.confirm('Message',
                'Confirmer la suppresssion',
                function(btn) {
                    if (btn === 'yes') {
                        var rec = grid.getStore().getAt(rowIndex);
                        testextjs.app.getController('App').ShowWaitingProcess();
                        Ext.Ajax.request({
                            url: url_services_transaction_inventaire + 'delete',
                            timeout: 1800000,
                            params: {
                                lg_INVENTAIRE_ID: rec.get('lg_INVENTAIRE_ID')
                            },
                            success: function(response)
                            {
                                testextjs.app.getController('App').StopWaitingProcess();
                                var object = Ext.JSON.decode(response.responseText, false);
//                                alert(object.code_statut + "|" + object.desc_satut);
                                if (object.code_statut == "0") {
                                    Ext.MessageBox.alert('Error Message', object.desc_satut);
                                    return;
                                } else {
                                    Ext.MessageBox.alert('Confirmation', object.desc_satut);
                                }
                                grid.getStore().reload();
                            },
                            failure: function(response)
                            {
//                                 alert("non ok");
                                testextjs.app.getController('App').StopWaitingProcess();
                                var object = Ext.JSON.decode(response.responseText, false);
                                //  alert(object);

                                console.log("Bug " + response.responseText);
                                Ext.MessageBox.alert('Error Message', response.responseText);

                            }
                        });
                        return;
                    }
                });


    }

});