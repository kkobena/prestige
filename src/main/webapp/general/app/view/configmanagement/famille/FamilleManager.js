/* global Ext */

var url_services_data_famille_famille = '../webservices/sm_user/famille/ws_data.jsp';
var url_services_transaction_famille = '../webservices/sm_user/famille/ws_transaction.jsp?mode=';
var url_services_transaction_app_remise = '../webservices/configmanagement/famillearticle/ws_transaction_maxVente.jsp?mode=';
var url_services_data_max_app_remise = '../webservices/configmanagement/famillearticle/ws_data_maxVente.jsp';
var url_services_data_dci = '../webservices/configmanagement/dci/ws_data.jsp';
var url_services_article_generate_pdf = '../webservices/sm_user/famille/ws_generate_pdf.jsp';
var lg_EMPLACEMENT_ID = "";
var Me_Workflow;


Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}


Ext.define('testextjs.view.configmanagement.famille.FamilleManager', {
    extend: 'Ext.grid.Panel',
    xtype: 'famillemanager',
    id: 'famillemanagerID',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.window.Window',
        'Ext.data.*',
        'Ext.util.*',
        'Ext.form.*',
        'Ext.JSON.*',
        'testextjs.model.Famille',
        'testextjs.view.configmanagement.famille.action.add',
        'testextjs.view.configmanagement.famille.action.infogenerale',
        'testextjs.view.configmanagement.famille.action.comptabilite',
        'testextjs.view.configmanagement.famille.action.autreinfos',
        'Ext.ux.ProgressBarPager',
        'testextjs.view.stockmanagement.suivistockvente.action.detailStock'

    ],
    title: 'Gestion des Articles',
    plain: true,
    maximizable: true,
//    tools: [{type: "pin"}],
    closable: false,
    frame: true,
    initComponent: function () {

        Me_Workflow = this;
        lg_EMPLACEMENT_ID = loadEmplacement();


        let itemsPerPage = 20;
        const store = new Ext.data.Store({
            model: 'testextjs.model.Famille',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../api/v1/produit-search/fiche',
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                },
                timeout: 240000
            }

        });

        const store_dci = new Ext.data.Store({
            model: 'testextjs.model.Dci',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_dci,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });


        const store_type = new Ext.data.Store({
            fields: ['str_TYPE_TRANSACTION', 'str_desc'],
            data: [{str_TYPE_TRANSACTION: 'ALL', str_desc: 'Tous'}, {str_TYPE_TRANSACTION: 'DECONDITION', str_desc: 'Les articles deconditionnables'}, {str_TYPE_TRANSACTION: 'DECONDITIONNE', str_desc: 'Les articles deconditionnes'}, {str_TYPE_TRANSACTION: 'SANSEMPLACEMENT', str_desc: 'Les articles sans emplacement'}]
        });


        this.cellEditing = new Ext.grid.plugin.CellEditing({
            clicksToEdit: 1
        });

        Ext.apply(this, {
            width: '98%',
            height: valheight,
            plugins: [this.cellEditing],
            store: store,
            id: 'GridArticleID',
            columns: [
                {
                    header: 'lg_FAMILLE_ID',
                    dataIndex: 'lg_FAMILLE_ID',
                    hidden: true,
                    flex: 1
                },
                {

                    header: 'Etat.cmde',
                    dataIndex: 'produitState',
                    renderer: function (v, m, r) {
                        const produitState = r.data.produitState;
                        switch (produitState) {
                            case 1:
                                m.style = 'background-color:#73C774;';
                                break;
                            case 2:
                                m.style = 'background-color:#5fa2dd;';
                                break;
                            case 3:
                                m.style = 'background-color:#f98012;';
                                break;
                            case 4:
                                m.style = 'background-color:#a62a3e;';
                                break;
                            default:
                                m.style = 'background-color:#d4d4d4;';
                                break;
                        }


                        return  r.data.produitStates;
                    },
                    width: 35
                },

                {
                    header: 'CIP',
                    dataIndex: 'int_CIP',
                    flex: 0.6,
                    renderer: function (v, m, r) {
                        const stock = r.data.int_NUMBER_AVAILABLE;
                        if (stock == 0) {
                            m.style = 'background-color:#B0F2B6;font-weight:800;';
                        } else if (stock > 0) {
                            m.style = 'font-weight:800;';
                        } else if (stock < 0) {
                            m.style = 'background-color:#F5BCA9;font-weight:800;';
                        }
                        return v;
                    }
                },
                {
                    header: 'Designation',
                    dataIndex: 'str_DESCRIPTION',
                    flex: 2,
                    renderer: function (v, m, r) {

                        const stock = r.data.int_NUMBER_AVAILABLE;
                        if (stock == 0) {
                            m.style = 'background-color:#B0F2B6;font-weight:800;';
                        } else if (stock > 0) {
                            m.style = 'font-weight:800;';
                        } else if (stock < 0) {
                            m.style = 'background-color:#F5BCA9;font-weight:800;';
                        }
                        return v;
                    }
                },
                {
                    header: 'P.Vente',
                    dataIndex: 'int_PRICE',
                    align: 'right',
                    flex: 0.5,
                    renderer: function (v, m, r) {

                        const stock = r.data.int_NUMBER_AVAILABLE;
                        if (stock == 0) {
                            m.style = 'background-color:#B0F2B6;font-weight:800;';
                        } else if (stock > 0) {
                            m.style = 'font-weight:800;';
                        } else if (stock < 0) {
                            m.style = 'background-color:#F5BCA9;font-weight:800;';
                        }
                        return amountformat(v);
                    }
                },
                {
                    header: 'P.CMU',
                    dataIndex: 'cmu_price',
                    align: 'right',
                    flex: 0.5,
                    renderer: function (v, m, r) {
                        const stock = r.data.int_NUMBER_AVAILABLE;
                        if (stock == 0) {
                            m.style = 'background-color:#B0F2B6;font-weight:800;';
                        } else if (stock > 0) {
                            m.style = 'font-weight:800;';
                        } else if (stock < 0) {
                            m.style = 'background-color:#F5BCA9;font-weight:800;';
                        }
                        return amountformat(v);
                    }
                },

                {
                    header: 'P.A F',
                    dataIndex: 'int_PAF',
                    align: 'right',
                    flex: 0.5,
                    renderer: function (v, m, r) {

                        const stock = r.data.int_NUMBER_AVAILABLE;
                        if (stock == 0) {
                            m.style = 'background-color:#B0F2B6;font-weight:800;';
                        } else if (stock > 0) {
                            m.style = 'font-weight:800;';
                        } else if (stock < 0) {
                            m.style = 'background-color:#F5BCA9;font-weight:800;';
                        }
                        return amountformat(v);
                    }
                },
                {
                    header: 'Stock',
                    dataIndex: 'int_NUMBER_AVAILABLE',
                    align: 'center',
                    flex: 0.5,
                    renderer: function (v, m, r) {

                        const stock = r.data.int_NUMBER_AVAILABLE;
                        if (stock == 0) {
                            m.style = 'background-color:#B0F2B6;font-weight:800;';
                        } else if (stock > 0) {
                            m.style = 'font-weight:800;';
                        } else if (stock < 0) {
                            m.style = 'background-color:#F5BCA9;font-weight:800;';
                        }
                        return v;
                    }
                }, {
                    header: 'Seuil.Reap',
                    dataIndex: 'int_STOCK_REAPROVISONEMENT',
                    align: 'center',
                    flex: 0.5
                    ,
                    renderer: function (v, m, r) {

                        const stock = r.data.int_NUMBER_AVAILABLE;
                        if (stock == 0) {
                            m.style = 'background-color:#B0F2B6;font-weight:800;';
                        } else if (stock > 0) {
                            m.style = 'font-weight:800;';
                        } else if (stock < 0) {
                            m.style = 'background-color:#F5BCA9;font-weight:800;';
                        }
                        return v;
                    }
                }, {
                    header: 'Qte.Reap',
                    dataIndex: 'int_QTE_REAPPROVISIONNEMENT',
                    align: 'center',
                    flex: 0.5
                    ,
                    renderer: function (v, m, r) {

                        const stock = r.data.int_NUMBER_AVAILABLE;
                        if (stock == 0) {
                            m.style = 'background-color:#B0F2B6;font-weight:800;';
                        } else if (stock > 0) {
                            m.style = 'font-weight:800;';
                        } else if (stock < 0) {
                            m.style = 'background-color:#F5BCA9;font-weight:800;';
                        }
                        return v;
                    }
                }, {
                    header: 'Emplacement',
                    dataIndex: 'lg_ZONE_GEO_ID',
                    align: 'center',
                    flex: 1
                    ,
                    renderer: function (v, m, r) {

                        const stock = r.data.int_NUMBER_AVAILABLE;
                        if (stock == 0) {
                            m.style = 'background-color:#B0F2B6;font-weight:800;';
                        } else if (stock > 0) {
                            m.style = 'font-weight:800;';
                        } else if (stock < 0) {
                            m.style = 'background-color:#F5BCA9;font-weight:800;';
                        }
                        return v;
                    }
                },
                {
                    text: 'P',
                    dataIndex: 'checkExpirationdate',
                    flex: 0.4,
                    xtype: 'checkcolumn',
                    listeners: {
                        checkChange: function (column, rowIndex, checked, eOpts) {
                            const record = store.getAt(rowIndex);
                            Ext.Ajax.request({
                                url: '../webservices/sm_user/famille/ws_updateperemptiondate.jsp',
                                params: {
                                    lg_FAMILLE_ID: record.get('lg_FAMILLE_ID'),
                                    checked: checked
                                },
                                success: function (response)
                                {
                                    const object = Ext.JSON.decode(response.responseText, false);
                                    if (object.success === 1) {
                                        record.commit();
                                    }

                                },
                                failure: function (response)
                                {


                                    Ext.MessageBox.alert('Error Message', response.responseText);

                                }
                            });
                        }
                    }
                },

                {
                    text: 'O',
                    dataIndex: 'scheduled',
                    flex: 0.4,
                    xtype: 'checkcolumn',
                    listeners: {
                        checkChange: function (column, rowIndex, checked, eOpts) {
                            const record = store.getAt(rowIndex);
                            Ext.Ajax.request({
                                url: '../api/v1/commande/update/scheduled',
                                method: 'POST',
                                headers: {'Content-Type': 'application/json'},
                                params: Ext.JSON.encode({
                                    ref: record.get('lg_FAMILLE_ID'),
                                    scheduled: checked
                                }),
                                success: function (response)
                                {
                                    const result = Ext.JSON.decode(response.responseText, true);
                                    if (result.success) {
                                        record.commit();
                                    }

                                },
                                failure: function (response)
                                {

                                    const object = Ext.JSON.decode(response.responseText, false);

                                    Ext.MessageBox.alert('Error Message', response.responseText);

                                }
                            });
                        }
                    }
                },

                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            iconCls: 'calendar',
                            tooltip: 'Modifier la date de péremption',
                            scope: this,
                            handler: this.addPeremptiondate

                        }]
                },

                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/page_white_edit.png',
                            tooltip: 'Modifier',
                            scope: this,
                            handler: this.onEditClick,
                            getClass: function (value, metadata, record) {
                                if (record.get('P_BT_UPDATE')) {
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
                            icon: 'resources/images/icons/fam/grossiste.png',
                            tooltip: 'Gerer Grossiste',
                            scope: this,
                            handler: this.onAddGrossisteClick,
                            getClass: function (value, metadata, record) {
                                if (record.get('bool_DECONDITIONNE') == "0") {  //read your condition from the record
                                    if (record.get('lg_EMPLACEMENT_ID') == "1") {  //read your condition from the record
                                        return 'x-display-hide'; //affiche l'icone
                                    } else {
                                        return 'x-hide-display'; //cache l'icone
                                    }
                                    //  return 'x-display-hide'; //affiche l'icone
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
                            tooltip: 'Supprimer',
                            scope: this,
                            getClass: function (value, metadata, record) {
                                if (record.get('BTNDELETE')) {
                                    if (record.get('lg_EMPLACEMENT_ID') == "1") {  //read your condition from the record
                                        return 'x-display-hide'; //affiche l'icone
                                    } else {
                                        return 'x-hide-display'; //cache l'icone
                                    }
                                    // return 'x-display-hide';
                                } else {

                                    return 'x-hide-display';
                                }
                            },
                            handler: this.onRemoveClick
                        }]
                }, {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/connect.png',
                            tooltip: 'Créer deconditionné',
                            scope: this,
                            handler: this.onCreateDeconditionClick,
                            getClass: function (value, metadata, record) {
                                if (record.get('bool_DECONDITIONNE_EXIST') == "0") {  //read your condition from the record
                                    if (record.get('lg_EMPLACEMENT_ID') == "1") {  //read your condition from the record
                                        return 'x-display-hide'; //affiche l'icone
                                    } else {
                                        return 'x-hide-display'; //cache l'icone
                                    }
                                    // return 'x-display-hide'; //affiche l'icone
                                } else {
                                    return 'x-hide-display'; //cache l'icone
                                }
                            }
                        }]
                }, {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/cut.png',
                            tooltip: 'Deconditionner l\'article',
                            scope: this,
                            handler: this.onDeconditionClick,
                            getClass: function (value, metadata, record) {
                                if (record.get('bool_DECONDITIONNE') == "0") {  //read your condition from the record
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
                            icon: 'resources/images/icons/fam/application_view_list.png',
                            tooltip: 'Detail sur l\'article',
                            scope: this,
                            handler: this.onDetailClick
                        }]
                },
                //onDesableClick
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/disable.png',
                            tooltip: 'Désactiver l\'article',
                            scope: this,
                            handler: this.onDesableClick,
                            getClass: function (value, metadata, record) {
                                if (record.get('lg_EMPLACEMENT_ID') === "1" && record.get('ACTION_DESACTIVE_PRODUIT')) {  //read your condition from the record
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
                            iconCls: 'suivimvt',
                            tooltip: 'Suivi de cet Article',
                            scope: this,
                            handler:
                                    function (grid, rowIndex) {
                                        const rec = grid.getStore().getAt(rowIndex);
                                        Me_Workflow.showPeriodeForm(rec.get('lg_FAMILLE_ID'), rec.get('str_NAME'));

                                    }

                        }]
                }



            ],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [{
                    text: 'Créer',
                    scope: this,
                    iconCls: 'addicon',
                    id: 'btn_add',
//                    width: 90,
                    hidden: true,
                    handler: this.onAddClick
                }, {
                    xtype: 'combobox',
                    name: 'str_TYPE_TRANSACTION',
                    margins: '0 0 0 10',
                    id: 'str_TYPE_TRANSACTION',
                    store: store_type,
                    valueField: 'str_TYPE_TRANSACTION',
                    displayField: 'str_desc',
                    typeAhead: true,
                    queryMode: 'local',
//                    flex: 1,
                    emptyText: 'Filtre article...',
                    listeners: {
                        select: function (cmp) {
                            Me_Workflow.onRechClick();
                        }
                    }
                }, '-', {
                    xtype: 'combobox',
                    name: 'lg_DCI_PRINCIPAL_ID',
                    margins: '0 0 0 10',
                    id: 'lg_DCI_PRINCIPAL_ID',
                    store: store_dci,
                    valueField: 'lg_DCI_ID',
                    pageSize: 20, //ajout la barre de pagination
                    displayField: 'str_NAME',
                    typeAhead: true,
//                    editable: false,
                    width: 350,
                    minChars: 2,
                    queryMode: 'remote',
//                    flex: 2,
                    emptyText: 'Selectionner un DCI...',
                    listeners: {
                        select: function (cmp) {
                            Me_Workflow.onRechClick();
                        }

                    }
                }, '-', {
                    xtype: 'textfield',
                    id: 'rechecher',
                    name: 'user',
                    emptyText: 'Recherche',
                    listeners: {
                        'render': function (cmp) {
                            cmp.getEl().on('keypress', function (e) {
                                if (e.getKey() === e.ENTER) {
                                    Me_Workflow.onRechClick();

                                }
                            });
                        }
                    }
                }, '-', {
                    text: 'rechercher',
                    tooltip: 'rechercher',
                    scope: this,
                    iconCls: 'searchicon',
                    handler: this.onRechClick
                }, '-', {
                    text: 'Imprimer',
                    tooltip: 'imprimer',
                    iconCls: 'printable',
                    scope: this,
                    handler: this.onPdfClick
                }, '-', {
                    text: 'Importer',
                    tooltip: 'Importer',
                    id: 'btn_import',
                    iconCls: 'importicon',
//                    hidden: true, //a decommenter en cas de probleme 10/06/2016
                    scope: this,
                    handler: this.onbtnimport
                }, '-',

                {
                    text: 'Verifier l\'importation',
                    tooltip: 'Verifier l\'importation',
                    id: 'btn_checkimport',
                    iconCls: 'check_icon',
                    scope: this,
                    handler: this.onbtncheckimport
                }, '-',

                {
                    text: 'Importer des articles',
                    tooltip: 'Importer stock',
                    iconCls: 'importicon',
                    scope: this,
                    hidden: (lg_EMPLACEMENT_ID === '1'),
                    handler: function () {

                        var win = new Ext.window.Window({
                            autoShow: false,
                            title: 'Importer stock dépôt',
                            width: 500,
                            height: 150,
                            layout: 'fit',
                            plain: true,
                            items: {
                                xtype: 'form',
                                bodyPadding: 10,
                                defaults: {
                                    anchor: '100%'
                                },
                                items: [{
                                        xtype: 'fieldset',
                                        bodyPadding: 20,
                                        defaultType: 'filefield',
                                        defaults: {
                                            anchor: '100%'
                                        },
                                        items: [
                                            {
                                                xtype: 'filefield',
                                                style: 'margin:5px !important;',
                                                fieldLabel: 'Fichier xls',
                                                emptyText: 'Fichier xls ',
                                                name: 'fichier',
                                                allowBlank: false,
                                                buttonText: 'Choisir un fichier ',
                                                width: 400
                                            }


                                        ]
                                    }]
                            }
                            ,
                            buttons: [{
                                    text: 'Enregistrer',
                                    handler: this.onbtnImporter
                                }, {
                                    text: 'Annuler',
                                    handler: function () {
                                        win.close();
                                    }
                                }]
                        });
                        win.show();
                    }
                }
            ],
            bbar: {
                xtype: 'pagingtoolbar',
                pageSize: itemsPerPage,
                store: store,
                displayInfo: true,
                plugins: new Ext.ux.ProgressBarPager(),
                listeners: {
                    beforechange: function (page, currentPage) {
                        const myProxy = this.store.getProxy();
                        myProxy.params = {
                            search_value: '',
                            str_TYPE_TRANSACTION: '',
                            lg_DCI_ID: ''
                        };

                        const lg_DCI_PRINCIPAL_ID = Ext.getCmp('lg_DCI_PRINCIPAL_ID').getValue();
                        const str_TYPE_TRANSACTION = Ext.getCmp('str_TYPE_TRANSACTION').getValue();
                        const search_value = Ext.getCmp('rechecher').getValue();

                        myProxy.setExtraParam('str_TYPE_TRANSACTION', str_TYPE_TRANSACTION);
                        myProxy.setExtraParam('lg_DCI_ID', lg_DCI_PRINCIPAL_ID);
                        myProxy.setExtraParam('search_value', search_value);
                    }

                }
            },
            listeners: {
                afterrender: function () { // a decommenter apres les tests
                    Ext.getCmp('rechecher').focus();
                    if (lg_EMPLACEMENT_ID == "1") {
                        Ext.getCmp('btn_add').show();
                        Ext.getCmp('btn_import').show();
                        Ext.getCmp('btn_checkimport').show();
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
 
    onAddClick: function () {
        new testextjs.view.configmanagement.famille.action.add({
            odatasource: "",
            parentview: this,
            mode: "create",
            titre: "Ajouter Article",
            type: "famillemanager"
        });
    },
    onbtnimport: function () {
        if (lg_EMPLACEMENT_ID == "1") {
            new testextjs.view.configmanagement.famille.action.importOrder({
                odatasource: 'TABLE_FAMILLE',
                parentview: this,
                mode: "importfile",
                titre: "Importation des differents articles de l'officine"
            });
        } else {
            new testextjs.view.stockmanagement.dodepot.action.importOrder({
                odatasource: 'TABLE_MISEAJOUR_STOCKDEPOT',
                parentview: this,
                mode: "importfile",
                titre: "Importation des differents articles vendus au d&eacute;p&ocirc;t"
            });
        }

    },
    onbtncheckimport: function () {
        new testextjs.view.configmanagement.famille.action.importOrder({
            odatasource: 'TABLE_FAMILLE',
            parentview: this,
            mode: "checkimportfile",
            titre: "Verification de l'importation des differents articles de l'officine"
        });
    },
    onbtnexportCsv: function () {
        var lg_DCI_PRINCIPAL_ID = "", str_TYPE_TRANSACTION = "";
        if (Ext.getCmp('lg_DCI_PRINCIPAL_ID').getValue() != null) {
            lg_DCI_PRINCIPAL_ID = Ext.getCmp('lg_DCI_PRINCIPAL_ID').getValue();
        }
        if (Ext.getCmp('str_TYPE_TRANSACTION').getValue() != null) {
            str_TYPE_TRANSACTION = Ext.getCmp('str_TYPE_TRANSACTION').getValue();
        }
        var liste_param = "search_value:" + Ext.getCmp('rechecher').getValue() + ";str_TYPE_TRANSACTION:" + str_TYPE_TRANSACTION + ";lg_DCI_ID:" + lg_DCI_PRINCIPAL_ID;
        var extension = "csv";
        window.location = '../MigrationServlet?table_name=TABLE_FAMILLE' + "&extension=" + extension + "&liste_param=" + liste_param;
    },
    onbtnexportExcel: function () {
        var lg_DCI_PRINCIPAL_ID = "", str_TYPE_TRANSACTION = "";
        if (Ext.getCmp('lg_DCI_PRINCIPAL_ID').getValue() != null) {
            lg_DCI_PRINCIPAL_ID = Ext.getCmp('lg_DCI_PRINCIPAL_ID').getValue();
        }
        if (Ext.getCmp('str_TYPE_TRANSACTION').getValue() != null) {
            str_TYPE_TRANSACTION = Ext.getCmp('str_TYPE_TRANSACTION').getValue();
        }
        var liste_param = "search_value:" + Ext.getCmp('rechecher').getValue() + ";str_TYPE_TRANSACTION:" + str_TYPE_TRANSACTION + ";lg_DCI_ID:" + lg_DCI_PRINCIPAL_ID;
        var extension = "xls";
        window.location = '../MigrationServlet?table_name=TABLE_FAMILLE' + "&extension=" + extension + "&liste_param=" + liste_param;
    },
    onPdfClick: function () {
        let lg_DCI_PRINCIPAL_ID = "", str_TYPE_TRANSACTION = "";
        if (Ext.getCmp('lg_DCI_PRINCIPAL_ID').getValue() != null) {
            lg_DCI_PRINCIPAL_ID = Ext.getCmp('lg_DCI_PRINCIPAL_ID').getValue();
        }
        if (Ext.getCmp('str_TYPE_TRANSACTION').getValue() != null) {
            str_TYPE_TRANSACTION = Ext.getCmp('str_TYPE_TRANSACTION').getValue();
        }
        const linkUrl = url_services_article_generate_pdf + '?str_TYPE_TRANSACTION=' + str_TYPE_TRANSACTION + '&lg_DCI_ID=' + lg_DCI_PRINCIPAL_ID + '&search_value=' + Ext.getCmp('rechecher').getValue();


        window.open(linkUrl);
    },

    onRemoveClick: function (grid, rowIndex) {
        Ext.MessageBox.confirm('Message',
                'Confirmer la suppresssion',
                function (btn) {
                    if (btn === 'yes') {
                        var rec = grid.getStore().getAt(rowIndex);
                        var progress = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
                        Ext.Ajax.request({
                            method: 'POST',
                            url: '../api/v1/produit/remove-desactive/' + rec.get('lg_FAMILLE_ID'),
                            success: function (response, options) {
                                progress.hide();
                                var result = Ext.JSON.decode(response.responseText, true);
                                if (result.success) {
                                    grid.getStore().reload();
                                } else {
                                    Ext.MessageBox.show({
                                        title: 'Message d\'erreur',
                                        width: 320,
                                        msg: "L'opération a échouée",
                                        buttons: Ext.MessageBox.OK,
                                        icon: Ext.MessageBox.ERROR

                                    });
                                }
                            },
                            failure: function (response, options) {
                                progress.hide();
                                Ext.Msg.alert("Message", 'server-side failure with status code' + response.status);
                            }

                        });

                    }
                });


    },
    onDesableClick: function (grid, rowIndex) {

        var rec = grid.getStore().getAt(rowIndex);

        Ext.MessageBox.confirm('Message',
                "Desactiver ce produit?" + "<br>Stock actuel: " + rec.get('int_NUMBER_AVAILABLE'),
                function (btn) {
                    if (btn === 'yes') {
                        var progress = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
                        Ext.Ajax.request({
                            method: 'POST',
                            url: '../api/v1/produit/disable-produit/' + rec.get('lg_FAMILLE_ID'),
                            success: function (response, options) {
                                progress.hide();
                                var result = Ext.JSON.decode(response.responseText, true);
                                if (result.success) {
                                    grid.getStore().reload();
                                } else {
                                    Ext.MessageBox.show({
                                        title: 'Message d\'erreur',
                                        width: 320,
                                        msg: "L'opération a échouée",
                                        buttons: Ext.MessageBox.OK,
                                        icon: Ext.MessageBox.ERROR

                                    });
                                }
                            },
                            failure: function (response, options) {
                                progress.hide();
                                Ext.Msg.alert("Message", 'server-side failure with status code' + response.status);
                            }

                        });


                    }
                });


    },

    onEditClick: function (grid, rowIndex) {
        const rec = grid.getStore().getAt(rowIndex);

        if (rec.get('lg_EMPLACEMENT_ID') == "1") {
            new testextjs.view.configmanagement.famille.action.add({
                odatasource: rec.data,
                parentview: this,
                mode: "update",
                type: "famillemanager",
                titre: "Modification Article [" + rec.get('str_DESCRIPTION') + "]"
            });
        } else {

            new testextjs.view.configmanagement.famille.action.updatezonegeo({
                odatasource: rec.data,
                parentview: this,
                mode: "update",
                titre: "Modification de l'emplacement de Article [" + rec.get('str_DESCRIPTION') + "]"
            });
        }


    },
    onDetailClick: function (grid, rowIndex) {
        const rec = grid.getStore().getAt(rowIndex);
        new testextjs.view.configmanagement.famille.action.detailArticle({
            odatasource: rec.data,
            produitId: rec.get('lg_FAMILLE_ID'),
            parentview: this,
            mode: "update",
            titre: "Detail sur l'article [" + rec.get('str_DESCRIPTION') + "]"
        });
    },
    onCreateDeconditionClick: function (grid, rowIndex) {
        const rec = grid.getStore().getAt(rowIndex);

        if (rec.get('bool_DECONDITIONNE') == "1") {
            Ext.MessageBox.alert('Alerte Message', 'Ceci est un article deconditionne');
        } else {
            if (rec.get('bool_DECONDITIONNE_EXIST') == "1") {
                Ext.MessageBox.alert('Alerte Message', 'La version deconditionne existe deja');
            } else {
                new testextjs.view.configmanagement.famille.action.add({
                    odatasource: rec.data,
                    parentview: this,
                    mode: "decondition",
                    type: 'famillemanager',
                    titre: "Creation Article [" + rec.get('str_DESCRIPTION') + "] deconditionne"
                });
            }
        }

    }, onDeconditionClick: function (grid, rowIndex) {
        const rec = grid.getStore().getAt(rowIndex);

        if (rec.get('bool_DECONDITIONNE') == "1") {
            Ext.MessageBox.alert('Alerte Message', 'Ceci est un article deconditionne. Il ne peut pas etre deconditionne');
        } else {
            if (rec.get('bool_DECONDITIONNE_EXIST') == "0") {
                Ext.MessageBox.alert('Alerte Message', 'Aucune version deconditionne existe');
            } else {
                if (rec.get('int_NUMBER_AVAILABLE') <= 0) {
                    Ext.MessageBox.alert('Alerte Message', 'Stock insuffisant');
                } else {
                    new testextjs.view.configmanagement.famille.action.doDecondition({
                        odatasource: rec.data,
                        parentview: this,
                        mode: "deconditionarticle",
                        type: 'famillemanager',
                        titre: "Article [" + rec.get('str_DESCRIPTION_DECONDITION') + "]"
                    });
                }
            }

        }

    },
    onRechClick: function () {
        const val = Ext.getCmp('rechecher');

        Ext.getCmp('GridArticleID').getStore().load({
            params: {
                search_value: val.getValue(),
                str_TYPE_TRANSACTION: Ext.getCmp('str_TYPE_TRANSACTION').getValue(),
                lg_DCI_ID: Ext.getCmp('lg_DCI_PRINCIPAL_ID').getValue()

            }
        });
        Ext.getCmp('rechecher').focus(true, 100, function () {
//            Ext.getCmp('rechecher').selectText(0, 1);
        });
    },
    onAddGrossisteClick: function (grid, rowIndex) {

        const rec = grid.getStore().getAt(rowIndex);

        new testextjs.view.configmanagement.famille.action.addgrossiste({
            obtntext: "Grossiste",
            odatasource: rec.data,
            nameintern: "Grossiste",
            parentview: this,
            mode: "detail",
            titre: "Gestion des grossistes du produit [" + rec.get('str_DESCRIPTION') + "]",
            type: "famillemanager"
        });

    },
    onValeurMaxClick: function ()
    {
        new testextjs.view.configmanagement.famille.action.maxVente({
            odatasource: "",
            parentview: this,
            mode: "create",
            titre: "Valeur maximale de vente des produits"
        });
    },

    showPeriodeForm: function (id, str_NAME) {
        var win = Ext.create("Ext.window.Window", {
            title: "Choisir une periode",

            width: 520,
            layout: {
                type: 'fit'
            },
            height: 180,
            items: [{
                    xtype: 'form',
                    id: 'periodeform',
                    type: 'fit',
                    bodyPadding: 5,
                    modelValidation: true,
                    items: [
                        {
                            xtype: 'fieldset',

                            height: 60,
                            title: 'Choisir une periode',
                            layout: 'hbox',
                            defaults: {
                                anchor: '100%',

                                labelAlign: 'left',
                                labelWidth: 20
                            },
                            items: [
                                {
                                    xtype: 'datefield',
                                    fieldLabel: 'Du',
                                    name: 'dt_debut',
                                    id: 'dt_debut',

                                    labelWidth: 20,
                                    allowBlank: false,
                                    submitFormat: 'Y-m-d',
                                    value: new Date(),
                                    maxValue: new Date(),
                                    format: 'd/m/Y',
                                    listeners: {
                                        'change': function (me) {

                                            Ext.getCmp('dt_fin').setMinValue(me.getValue());

                                        }
                                    }
                                }, {
                                    xtype: 'datefield',
                                    fieldLabel: 'Au',
                                    name: 'dt_fin',
                                    id: 'dt_fin',
                                    labelWidth: 20,
                                    style: 'margin-left:25px;',
                                    allowBlank: false,
                                    maxValue: new Date(),
                                    value: new Date(),
                                    submitFormat: 'Y-m-d',
                                    format: 'd/m/Y',
                                    listeners: {
                                        'change': function (me) {

                                            Ext.getCmp('dt_debut').setMaxValue(me.getValue());

                                        }
                                    }
                                }

                            ]}]

                }]
            ,
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    layout: {
                        pack: 'end', //#22
                        type: 'hbox'
                    },
                    items: [
                        {
                            xtype: 'button',
                            text: 'Valider',
                            listeners: {
                                click: function () {
                                    var form = Ext.getCmp('periodeform');

                                    if (form && form.isValid()) {

                                        var dt_debut = Ext.getCmp('dt_debut').getSubmitValue();
                                        var dt_fin = Ext.getCmp('dt_fin').getSubmitValue();
                                        /*   new testextjs.view.stockmanagement.suivistockvente.action.detailstock({
                                         odatasource: id,
                                         parentview: this,
                                         mode: "update",
                                         datedebut: dt_debut,
                                         datedin: dt_fin,
                                         titre: str_NAME
                                         });
                                         */
                                        Me_Workflow.buildDetail(id, dt_debut, dt_fin, str_NAME);


                                        win.close();
                                    }
                                }
                            }
                        },
                        {
                            xtype: 'button',
                            text: 'Annuler',
//                   
                            listeners: {
                                click: function () {
                                    win.close();
                                }

                            }
                        }
                    ]
                }
            ]

        });
        win.show();

    },
    onbtnImporter: function (button) {
        var fenetre = button.up('window'),
                formulaire = fenetre.down('form');
        if (!formulaire.isValid()) {
            return;
        }
        formulaire.submit({
            url: '../ImportDepot',
            waitMsg: 'Veuillez patienter le temps du telechargemetnt du fichier...',
            timeout: 2400000,
            success: function (formulaire, action) {

                if (action.result.statut === 1) {
                    const grid = Ext.getCmp('GridArticleID');
                    Ext.MessageBox.alert('Confirmation', action.result.success);
                    grid.getStore().reload();
                } else {
                    Ext.MessageBox.alert('Erreur', action.result.success);
                }

                var bouton = button.up('window');
                bouton.close();
            },
            failure: function (formulaire, action) {
                Ext.MessageBox.alert('Erreur', 'Erreur  ' + action.result.errors);
            }
        });

    },

    addPeremptiondate: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);

        var win = Ext.create("Ext.window.Window", {
            title: "[ " + rec.get('str_NAME') + " ]",
            modal: true,
            width: 400,
            layout: {
                type: 'anchor'
            },
            height: 180,
            items: [{
                    xtype: 'form',
                    id: 'peremptionform',
                    type: 'anchor',
                    bodyPadding: 5,

                    modelValidation: true,
                    items: [
                        {
                            xtype: 'fieldset',
                            anchor: '100%',
                            height: 90,

                            title: 'Ajouter date de péremption',
                            layout: 'anchor',
                            defaults: {
                                anchor: '100%',
                                labelAlign: 'top'
                            },
                            items: [
                                {
                                    xtype: 'datefield',
                                    fieldLabel: 'Date de péremption',
                                    name: 'dt_peremption',
                                    id: 'dt_peremption',
                                    autofocus: true,
                                    allowBlank: false,
                                    submitFormat: 'Y-m-d',
                                    value: new Date(rec.get('dtPEREMPTION')),

                                    format: 'd/m/Y',
                                    listeners: {
                                        specialKey: function (field, e, Familletion) {
                                            if (e.getKey() === e.ENTER) {
                                                var form = Ext.getCmp('peremptionform');
                                                if (form && form.isValid()) {
                                                    var dt_debut = field.getSubmitValue();
                                                    var progress = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
                                                    Ext.Ajax.request({
                                                        method: 'PUT',
                                                        url: '../api/v1/fichearticle/dateperemption/' + rec.get('lg_FAMILLE_ID') + '/' + dt_debut,

                                                        success: function (response)
                                                        {
                                                            progress.hide();
                                                            var object = Ext.JSON.decode(response.responseText, false);
                                                            if (!object.success) {
                                                                Ext.MessageBox.alert('Error Message', "Echec d'ajout");
                                                                return;
                                                            } else {
                                                                win.close();
                                                                grid.getStore().reload();
                                                            }

                                                        },
                                                        failure: function (response)
                                                        {
                                                            progress.hide();
                                                            var object = Ext.JSON.decode(response.responseText, false);
                                                            console.log("Bug " + response.responseText);
                                                            Ext.MessageBox.alert('Error Message', response.responseText);

                                                        }
                                                    });

                                                    //focus sur le champ cip
                                                    Ext.getCmp('rechecher').focus(true, 100, function () { });
                                                }


                                            }
                                        }/*,
                                         afterrender: function (field) {
                                         console.log('fffffff  ', field);
                                         field.focus();
                                         }*/
                                    }
                                }


                            ]}]

                }]
            ,
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'bottom',
                    ui: 'footer',
                    layout: {
                        pack: 'end', //#22
                        type: 'hbox'
                    },
                    items: [
                        {
                            xtype: 'button',
                            text: 'Valider',
                            listeners: {
                                click: function () {
                                    var form = Ext.getCmp('peremptionform');

                                    if (form && form.isValid()) {
                                        var progress = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
                                        var dt_debut = Ext.getCmp('dt_peremption').getSubmitValue();
                                        Ext.Ajax.request({
                                            method: 'PUT',
                                            url: '../api/v1/fichearticle/dateperemption/' + rec.get('lg_FAMILLE_ID') + '/' + dt_debut,

                                            success: function (response)
                                            {
                                                progress.hide();
                                                var object = Ext.JSON.decode(response.responseText, false);
                                                if (!object.success) {
                                                    Ext.MessageBox.alert('Error Message', "Echec d'ajout");
                                                    return;
                                                } else {
                                                    win.close();
                                                    grid.getStore().reload();
                                                }

                                            },
                                            failure: function (response)
                                            {
                                                progress.hide();
                                                var object = Ext.JSON.decode(response.responseText, false);
                                                console.log("Bug " + response.responseText);
                                                Ext.MessageBox.alert('Error Message', response.responseText);

                                            }
                                        });


                                    }
                                }
                            }
                        },
                        {
                            xtype: 'button',
                            text: 'Annuler',
//                   
                            listeners: {
                                click: function () {
                                    win.close();
                                }

                            }
                        }
                    ]
                }
            ]

        });
        win.show();
        Ext.getCmp('dt_peremption').focus(true, 100, function () {

        });

    },
    buildDetail: function (id, dtStart, dtEnd, libelle) {
        var me = this;
        var storeProduits = new Ext.data.Store({
            fields:
                    [
                        {
                            name: 'dateOp',
                            type: 'string'
                        },
                        {
                            name: 'produitId',
                            type: 'string'
                        },
                        {
                            name: 'cip',
                            type: 'string'
                        },
                        {
                            name: 'produitName',
                            type: 'string'
                        }, {
                            name: 'qtyVente',
                            type: 'number'
                        }, {
                            name: 'stockInit',
                            type: 'number'
                        }, {
                            name: 'stockFinal',
                            type: 'number'
                        }
                        , {
                            name: 'qtyAjust',
                            type: 'number'
                        }, {
                            name: 'qtyAnnulation',
                            type: 'number'
                        }
                        , {
                            name: 'qtyRetour',
                            type: 'number'
                        }, {
                            name: 'qtyRetourDepot',
                            type: 'number'
                        }, {
                            name: 'qtyInv',
                            type: 'number'
                        }, {
                            name: 'qtyPerime',
                            type: 'number'
                        }, {
                            name: 'qtyAjustSortie',
                            type: 'number'
                        }, {
                            name: 'qtyDeconEntrant',
                            type: 'number'
                        }, {
                            name: 'qtyDecondSortant',
                            type: 'number'
                        }, {
                            name: 'qtyEntree',
                            type: 'number'
                        },
                        {
                            name: 'ecartInventaire',
                            type: 'number'
                        }
                    ],
            pageSize: null,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../api/v1/produit/monitoringproduct',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total',
                    metaProperty: 'metaData'
                }
            }
        });
        storeProduits.addListener('metachange', function (store, rec) {
//            console.log(form.query('#imprimer'));
            form.query('#qtyEntree')[0].setValue(rec.qtyEntree);
            form.query('#qtyDecondSortant')[0].setValue(rec.qtyDecondSortant);
            form.query('#qtyDeconEntrant')[0].setValue(rec.qtyDeconEntrant);
            form.query('#qtyAjustSortie')[0].setValue(rec.qtyAjustSortie);
            form.query('#qtyVente')[0].setValue(rec.qtyVente);
            form.query('#qtyAjust')[0].setValue(rec.qtyAjust);
            form.query('#qtyAnnulation')[0].setValue(rec.qtyAnnulation);
            form.query('#qtyRetour')[0].setValue(rec.qtyRetour);
            form.query('#qtyRetourDepot')[0].setValue(rec.qtyRetourDepot);
            form.query('#qtyInv')[0].setValue(rec.qtyInv);
            form.query('#qtyPerime')[0].setValue(rec.qtyPerime);

        }, this);
        storeProduits.load({
            params: {
                produitId: id,
                dtStart: dtStart,
                dtEnd: dtEnd
            }
        });
        var form = Ext.create('Ext.window.Window',
                {
                    xtype: 'mvtdetail',
                    alias: 'widget.mvtdetail',
                    autoShow: true,
                    height: 530,
                    width: '80%',
                    modal: true,
                    title: "Détail de l'article [ " + libelle + " ]",
                    closeAction: 'hide',

                    closable: true,
                    maximizable: false,
                    layout: {
                        type: 'fit'

                    },
                    dockedItems: [
                        {
                            xtype: 'toolbar',
                            dock: 'top',
                            items: [
                                {
                                    text: 'imprimer',
                                    itemId: 'imprimer',
                                    iconCls: 'printable',
                                    tooltip: 'imprimer',
                                    scope: this,
                                    handler: function () {

                                        var linkUrl = '../BalancePdfServlet?mode=SUIVIMVT&dtStart=' + dtStart + '&dtEnd=' + dtEnd + "&produitId=" + id;
                                        window.open(linkUrl);
                                    }
                                }
                            ]
                        },
                        {
                            xtype: 'toolbar',
                            dock: 'bottom',
                            items: [
                                {
                                    xtype: 'displayfield',
                                    flex: 1,
                                    fieldLabel: 'Vente',
                                    labelWidth: 50,
                                    itemId: 'qtyVente',
                                    renderer: function (v) {
                                        return Ext.util.Format.number(v, '0,000.');
                                    },
                                    fieldStyle: "color:blue;font-weight:600;",
                                    value: 0

                                },
                                {
                                    xtype: 'displayfield',
                                    flex: 1,
                                    fieldLabel: 'Retour Fournisseur',
                                    labelWidth: 120,
                                    renderer: function (v) {
                                        return Ext.util.Format.number(v, '0,000.');
                                    },
                                    fieldStyle: "color:blue;font-weight:600;",
                                    itemId: 'qtyRetour',
                                    value: 0
                                },
                                {
                                    xtype: 'displayfield',
                                    flex: 1,
                                    fieldLabel: 'Périmée',
                                    labelWidth: 55,
                                    renderer: function (v) {
                                        return Ext.util.Format.number(v, '0,000.');
                                    },
                                    fieldStyle: "color:blue;font-weight:600;",
                                    itemId: 'qtyPerime',
                                    value: 0
                                },
                                {
                                    xtype: 'displayfield',
                                    flex: 1,
                                    fieldLabel: 'Entrée en stock',
                                    labelWidth: 100,
                                    renderer: function (v) {
                                        return Ext.util.Format.number(v, '0,000.');
                                    },
                                    fieldStyle: "color:blue;font-weight:600;",
                                    itemId: 'qtyEntree',
                                    value: 0

                                },
                                {
                                    xtype: 'displayfield',
                                    flex: 1,
                                    fieldLabel: 'Ajust (+)',
                                    labelWidth: 80,
                                    renderer: function (v) {
                                        return Ext.util.Format.number(v, '0,000.');
                                    },
                                    fieldStyle: "color:blue;font-weight:600;",
                                    itemId: 'qtyAjust',
                                    value: 0

                                },
                                {
                                    xtype: 'displayfield',
                                    flex: 1,
                                    fieldLabel: 'Ajust (-)',
                                    labelWidth: 80,
                                    renderer: function (v) {
                                        return Ext.util.Format.number(v, '0,000.');
                                    },
                                    fieldStyle: "color:blue;font-weight:600;",
                                    itemId: 'qtyAjustSortie',
                                    value: 0

                                }
                            ]
                        },

                        {
                            xtype: "toolbar",
                            dock: 'bottom',
                            items: [
                                {
                                    xtype: 'displayfield',
                                    flex: 1,
                                    fieldLabel: 'Décond. Detail',
                                    labelWidth: 100,
                                    renderer: function (v) {
                                        return Ext.util.Format.number(v, '0,000.');
                                    },
                                    fieldStyle: "color:blue;font-weight:600;",
                                    itemId: 'qtyDeconEntrant',
                                    value: 0
                                },
                                {
                                    xtype: 'displayfield',
                                    flex: 1,
                                    fieldLabel: 'Décond Boite CH',
                                    labelWidth: 120,
                                    renderer: function (v) {
                                        return Ext.util.Format.number(v, '0,000.');
                                    },
                                    fieldStyle: "color:blue;font-weight:600;",
                                    itemId: 'qtyDecondSortant',
                                    value: 0
                                },
                                {
                                    xtype: 'displayfield',
                                    flex: 1,
                                    fieldLabel: 'Retour Dépôt',
                                    labelWidth: 100,
                                    renderer: function (v) {
                                        return Ext.util.Format.number(v, '0,000.');
                                    },
                                    fieldStyle: "color:blue;font-weight:600;",
                                    itemId: 'qtyRetourDepot'
                                },
                                {
                                    xtype: 'displayfield',
                                    flex: 1,
                                    fieldLabel: 'Inventaire',
                                    labelWidth: 100,
                                    renderer: function (v) {
                                        return Ext.util.Format.number(v, '0,000.');
                                    },
                                    fieldStyle: "color:blue;font-weight:600;",
                                    itemId: 'qtyInv',
                                    value: 0
                                },
                                {
                                    xtype: 'displayfield',
                                    flex: 1,
                                    fieldLabel: 'Annulation',
                                    labelWidth: 100,
                                    renderer: function (v) {
                                        return Ext.util.Format.number(v, '0,000.');
                                    },
                                    fieldStyle: "color:blue;font-weight:600;",
                                    itemId: 'qtyAnnulation', value: 0
                                }

                            ]
                        }
                    ],
                    items: [
                        {
                            xtype: 'gridpanel',
                            store: storeProduits,
                            viewConfig: {
                                forceFit: true,
                                columnLines: true,
                                enableColumnHide: false

                            },
                            columns: [

                                {
                                    header: 'Date',
                                    sortable: false,
                                    menuDisabled: true,
                                    dataIndex: 'dateOp',
                                    width: 90
                                }, {
                                    text: 'Stock Debut',
                                    xtype: 'numbercolumn',
                                    dataIndex: 'stockInit',
                                    width: 95,
                                    align: 'right',
                                    format: '0,000.'
                                },
                                {
                                    text: 'Sortie',
                                    columns:
                                            [
                                                {
                                                    text: 'Vente',
                                                    xtype: 'numbercolumn',
                                                    dataIndex: 'qtyVente',
                                                    width: 55,
                                                    align: 'right',
                                                    format: '0,000.'
                                                },
                                                {
                                                    text: 'Retour',
                                                    xtype: 'numbercolumn',
                                                    dataIndex: 'qtyRetour',
                                                    width: 60,
                                                    align: 'right',
                                                    format: '0,000.'
                                                },
                                                {
                                                    text: 'Périmé',
                                                    xtype: 'numbercolumn',
                                                    dataIndex: 'qtyPerime',
                                                    width: 60,
                                                    align: 'right',
                                                    format: '0,000.'
                                                },
                                                {
                                                    text: 'Ajust(-)',
                                                    xtype: 'numbercolumn',
                                                    dataIndex: 'qtyAjustSortie',
                                                    width: 60,
                                                    align: 'right',
                                                    format: '0,000.'
                                                },
                                                {
                                                    text: 'Décon(-)',
                                                    xtype: 'numbercolumn',
                                                    dataIndex: 'qtyDecondSortant',
                                                    width: 70,
                                                    align: 'right',
                                                    format: '0,000.'
                                                }
                                            ]
                                },
                                {
                                    text: 'Entrée',
                                    columns:
                                            [
                                                {
                                                    text: 'Entrée',
                                                    xtype: 'numbercolumn',
                                                    dataIndex: 'qtyEntree',
                                                    width: 60,
                                                    align: 'right',
                                                    format: '0,000.'
                                                },
                                                {
                                                    text: 'Ajust(+)',
                                                    xtype: 'numbercolumn',
                                                    dataIndex: 'qtyAjust',
                                                    width: 60,
                                                    align: 'right',
                                                    format: '0,000.'
                                                },
                                                {
                                                    text: 'Décon(+)',
                                                    xtype: 'numbercolumn',
                                                    dataIndex: 'qtyDeconEntrant',
                                                    width: 70,
                                                    align: 'right',
                                                    format: '0,000.'
                                                },
                                                {
                                                    text: 'Annulation',
                                                    xtype: 'numbercolumn',
                                                    dataIndex: 'qtyAnnulation',
                                                    width: 80,
                                                    align: 'right',
                                                    format: '0,000.'
                                                },
                                                {
                                                    text: 'Retour Depot',
                                                    xtype: 'numbercolumn',
                                                    dataIndex: 'qtyRetourDepot',
                                                    width: 100,
                                                    align: 'right',
                                                    format: '0,000.'
                                                }
                                            ]
                                }
                                ,
                                {
                                    text: 'INV',
                                    xtype: 'numbercolumn',
                                    dataIndex: 'qtyInv',
                                    width: 50,
                                    align: 'right',
                                    format: '0,000.'
                                },
                                {
                                    text: 'Ecart INV',
                                    xtype: 'numbercolumn',
                                    dataIndex: 'ecartInventaire',
                                    width: 80,
                                    align: 'right',
                                    format: '0,000.'
                                },
                                {
                                    text: 'Stock Final',
                                    xtype: 'numbercolumn',
                                    dataIndex: 'stockFinal',
                                    width: 90,
                                    align: 'right',
                                    format: '0,000.'
                                }

                            ],

                            bbar: {
                                xtype: 'pagingtoolbar',
                                store: storeProduits,
                                dock: 'bottom',
                                displayInfo: true,
                                beforechange: function (page, currentPage) {
                                    const myProxy = storeProduits.getProxy();
                                    myProxy.params = {
                                        produitId: null,
                                        dtStart: null,
                                        dtEnd: null
                                    };
                                    myProxy.setExtraParam('produitId', rec.get('produitId'));
                                    myProxy.setExtraParam('dtStart', me.getDtStart().getSubmitValue());
                                    myProxy.setExtraParam('dtEnd', me.getDtEnd().getSubmitValue());

                                }
                            }
                        }
                    ]
                });

    }


});

function loadEmplacement() {
    return localStorage.getItem("lg_EMPLACEMENT_ID");
}