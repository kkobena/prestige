/* global Ext */


var url_services_transaction_order = '../webservices/commandemanagement/order/ws_transaction.jsp?mode=';
var url_services_data_grossiste_suggerer = '../webservices/configmanagement/grossiste/ws_data.jsp';
var url_services_data_orderdetails_process = '../webservices/commandemanagement/orderdetail/ws_data.jsp';
var url_services_data_famille_select_dovente = '../webservices/sm_user/famille/ws_data_jdbc.jsp';
var url_services_pdf = '../webservices/commandemanagement/order/ws_generate_pdf.jsp';
var Me_Window;
var Omode;
var ref;
var ref_final;
var famille_id_search;
var LaborexWorkFlow;
var store_famille_commande = null;
var int_montant_vente;
var int_montant_achat;
var str_STATUT;
var storerepartiteur;
var comboDefaultvalue;
var store_details_order;
Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.commandemanagement.order.action.add', {
    extend: 'Ext.form.Panel',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.form.*',
        'Ext.layout.container.Column',
        'testextjs.model.Famille',
        'testextjs.controller.LaborexWorkFlow',
        'testextjs.model.Grossiste',
        'testextjs.model.OrderDetail'
    ],
    config: {
        odatasource: '',
        parentview: '',
        mode: '',
        titre: '',
        plain: true,
        maximizable: true,
        closable: false,
        nameintern: ''
    },
    xtype: 'ordermanagerlist',
    id: 'ordermanagerlistID',
    frame: true,
    title: 'Modifier les informations de la commande',
    bodyPadding: 5,
    layout: 'column',
    initComponent: function () {
        Me_Window = this;
        var itemsPerPage = 100;
        var itemsPerPageGrid =9999999;
        famille_id_search = "";

        LaborexWorkFlow = Ext.create('testextjs.controller.LaborexWorkFlow', {});
        ref = this.getNameintern();
        if (ref === "0") {
            str_STATUT = this.getOdatasource();
        }
        ref_final = ref;
        titre = this.getTitre();
        this.title = titre;
        var store = Ext.create('testextjs.store.Search');
        comboDefaultvalue = this.getOdatasource().lg_GROSSISTE_ID;
        var store_type = new Ext.data.Store({
            fields: ['str_TYPE_TRANSACTION', 'str_desc'],
            data: [
               // {str_TYPE_TRANSACTION: 'PRIX', str_desc: 'PRIX DE VENTE BL DIFFERENT DU PRIX EN MACHINE'},
                {str_TYPE_TRANSACTION: 'PRIX_VENTE_DIFF', str_desc: 'PRIX DE VENTE BL DIFFERENT DU PRIX EN MACHINE'},
                 {str_TYPE_TRANSACTION: 'PRIX_VENTE_PLUS_30', str_desc: 'PRIX DE VENTE BL DIFFERENT DU PRIX EN MACHINE DE 30F'},
                {str_TYPE_TRANSACTION: 'ALL', str_desc: 'Tous'}
                
            ]
        });

        storerepartiteur = new Ext.data.Store({
            model: 'testextjs.model.Grossiste',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_grossiste_suggerer,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                },
                timeout: 240000
            }

        });

        store_details_order = new Ext.data.Store({
            model: 'testextjs.model.OrderDetail',
            pageSize: itemsPerPageGrid,
            autoLoad: false,
            proxy: {
                type: 'ajax',
               
                url: '../api/v1/commande/commande-en-cours-items',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                },
                timeout: 180000
            }

        });
        var int_VENTE = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    fieldLabel: 'Valeur Vente ::',
                    labelWidth: 95,
                    name: 'int_VENTE',
                    id: 'int_VENTE',
                    fieldStyle: "color:blue;font-weight:bold;font-size:1.5em",
                    margin: '0 15 0 0',
                    value: "0"
                });
        var int_ACHAT = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    fieldLabel: 'Valeur Achat ::',
                    labelWidth: 95,
                    name: 'int_ACHAT',
                    id: 'int_ACHAT',
                    fieldStyle: "color:blue;font-weight:bold;font-size:1.5em",
                    margin: '0 15 0 0',
                    value: "0"
                });

        this.cellEditing = new Ext.grid.plugin.CellEditing({
            clicksToEdit: 1
        });
        var me = this;
        Ext.apply(me, {
            width: '98%',
            fieldDefaults: {
                labelAlign: 'left',
                labelWidth: 90,
                anchor: '100%',
                msgTarget: 'side'
            },
            layout: {
                type: 'vbox',
                align: 'stretch',
                padding: 10
            },
            defaults: {
                flex: 1
            },
            id: 'panelID',
            items: [
                {
                    xtype: 'fieldset',
                    title: 'Infos Generales',
                    collapsible: true,
                    defaultType: 'textfield',
                    layout: 'anchor',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            xtype: 'fieldcontainer',
                            layout: 'hbox',
                            combineErrors: true,
                            defaultType: 'textfield',
                            defaults: {
                                hideLabel: 'true'
                            },
                            items: [{
                                    xtype: 'combobox',
                                    fieldLabel: 'Repartiteur',
                                    allowBlank: false,
                                    name: 'Code.Rep',
                                    margin: '5 15 0 0',
                                    id: 'lgGROSSISTEID',
                                    store: storerepartiteur,
                                    valueField: 'lg_GROSSISTE_ID',
                                    displayField: 'str_LIBELLE',
                                    typeAhead: true,
                                    pageSize: itemsPerPage,
                                    queryMode: 'remote',
                                    width: 450,
                                    emptyText: 'Choisir un repartiteur...',
                                    listeners: {
                                        afterrender: function (field) {
                                            field.focus(true, 50);
                                        },
                                        select: function (cmp) {
                                            var value = cmp.getValue();
                                            if (titre === 'Modifier les informations de la commande') {
                                                // alert("Titre 2" + titre);
                                                Me_Window.onchangeGrossiste();
                                            } else {
                                                Ext.getCmp('str_NAME').focus(true, 100, function () {
                                                    Ext.getCmp('str_NAME').selectText(0, 1);
                                                });
                                            }

                                        }
                                    }

                                },

                                int_ACHAT,
                                int_VENTE]
                        }]
                }
                ,
                {
                    xtype: 'fieldset',
                    title: 'Ajout Produit',
                    collapsible: true,
                    defaultType: 'textfield',
                    layout: 'anchor',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            xtype: 'fieldcontainer',
                            fieldLabel: 'Produit',
                            layout: 'hbox',
                            combineErrors: true,
                            defaultType: 'textfield',
                            defaults: {
                                hideLabel: 'true'
                            },
                            items: [
                                {
                                    xtype: 'combobox',
                                    fieldLabel: 'Article',
                                    name: 'str_NAME',
                                    id: 'str_NAME',
                                    store: store,
                                    margins: '0 10 5 10',
                                    valueField: 'CIP',
                                    displayField: 'str_DESCRIPTION',
//                                    displayField: 'str_DESCRIPTION_PLUS',
                                    enableKeyEvents: true,
                                    pageSize: 20, //ajout la barre de pagination
                                    typeAhead: true,
                                    width: 600,
//                                    flex:2
                                    queryMode: 'remote',
                                    minChars: 3,
                                    emptyText: 'Choisir un article par Nom ou Cip...',
                                    listConfig: {
                                        getInnerTpl: function () {
                                            return '<tpl for="."><tpl if="int_NUMBER_AVAILABLE <=0"><span style="color:#17987e;font-weight:bold;"><span style="width:100px;display:inline-block;">{CIP}</span>{str_DESCRIPTION} <span style="float: right;"> ( {int_PAF} ) <span>&nbsp;&nbsp;&nbsp;</span>  <span style="color:red;font-weight:bold;"> ( {int_NUMBER_AVAILABLE} ) </span></span></span><tpl else><span style="font-weight:bold;"><span style="width:100px;display:inline-block;">{CIP}</span>{str_DESCRIPTION} <span style="float: right; "> ( {int_PAF} )<span>&nbsp;&nbsp;&nbsp;</span>  <span style="color:red;font-weight:bold;"> ( {int_NUMBER_AVAILABLE} ) </span></span></span></tpl></tpl>';
                                        }
                                    },
                                    listeners: {
                                        select: function (cmp) {
                                            var value = cmp.getValue();
                                            var record = cmp.findRecord(cmp.valueField || cmp.displayField, value); //recupere la ligne de l'element selectionné
                                            Ext.getCmp('lg_FAMILLE_ID_VENTE').setValue(record.get('lg_FAMILLE_ID'));
                                            if (value === "0" || value === "Ajouter un nouvel article") {
                                                Me_Window.onbtnaddArticle();
                                            } else {
                                                Ext.getCmp('int_QUANTITE').focus(true, 100, function () {
                                                    Ext.getCmp('int_QUANTITE').selectText(0, 1);
                                                });
                                            }
                                            Ext.getCmp('btn_detail').enable();

                                        }
                                    }
                                },
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: 'Id produit :',
                                    name: 'lg_FAMILLE_ID_VENTE',
                                    id: 'lg_FAMILLE_ID_VENTE',
                                    labelWidth: 120,
                                    hidden: true,
                                    fieldStyle: "color:blue;",
                                    margin: '0 15 0 0'

                                },

                                {
                                    fieldLabel: 'Quantit&eacute;',
                                    emptyText: 'Quantite',
                                    name: 'int_QUANTITE',
                                    id: 'int_QUANTITE',
                                    xtype: 'numberfield',
                                    margin: '0 15 0 10',
                                    minValue: 1,
                                    width: 250,
                                    value: 1,
                                    allowBlank: false,
                                    enableKeyEvents: true,
                                    regex: /[0-9.]/,
                                    listeners: {
                                        specialKey: function (field, e, options) {
                                            if (e.getKey() === e.ENTER) {

                                                if (Ext.getCmp('str_NAME').getValue() !== "") {

                                                    if (Ext.getCmp('int_QUANTITE').getValue() > 0) {
                                                        // alert(ref_final);
                                                        onbtnaddCommande(ref_final);
                                                    } else {
                                                        Ext.MessageBox.alert('Error Message', 'La quantité doit être supérieure à 0 ');
                                                    }

                                                } else {
                                                    Ext.MessageBox.alert('Error Message', 'Verifiez votre selection svp');
                                                    return;
                                                }

                                            }
                                        }}

                                },
                                {
                                    text: 'Ajouter un nouvel article',
                                    id: 'btn_add_article',
                                    margins: '0 0 0 6',
                                    hidden: true,
                                    xtype: 'button',
                                    handler: this.onbtnaddArticle
//                                            disabled: true
                                },
                                {
                                    text: 'Voir detail',
                                    id: 'btn_detail',
                                    margins: '0 0 0 6',
                                    xtype: 'button',
                                    handler: this.onbtndetail,
                                    disabled: true
                                }]
                        }
                    ]
                }
                ,
                {
                    xtype: 'fieldset',
                    title: 'Detail(s) Commandes',
                    collapsible: true,
                    defaultType: 'textfield',
                    layout: 'anchor',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            columnWidth: 0.65,
                            xtype: 'gridpanel',
                            id: 'gridpanelID',
                            plugins: [this.cellEditing],
                            store: store_details_order,
                            height: 370,
                            columns: [
                                {
                                    xtype: 'rownumberer',
                                    text: '#',
                                    hidden: false,
                                    width: 40,
                                    sortable: true
                                },
                                {
                                    dataIndex: 'prixDiff',
                                    text: '',
                                    width: 40,
                                    renderer: function (v, m, r) {
                                        if (v) {
                                            m.style = 'background-color:#d9534f;';
                                        }
                                        return '';
                                    }
                                },

                                {
                                    text: 'Details Suggestion Id',
                                    flex: 1,
                                    sortable: true,
                                    hidden: true,
                                    dataIndex: 'lg_ORDERDETAIL_ID',
                                    id: 'lg_ORDERDETAIL_ID'
                                }, {
                                    text: 'Famille',
                                    flex: 1,
                                    sortable: true,
                                    hidden: true,
                                    dataIndex: 'lg_FAMILLE_ID'
                                },

                                {
                                    text: 'CIP',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'lg_FAMILLE_CIP'
                                },
                                {
                                    text: 'CODE ARTICLE',
                                    flex: 1,
                                    hidden: true,
                                    sortable: true,
                                    dataIndex: 'str_CODE_ARTICLE'
                                },
                                {
                                    text: 'DESIGNATION',
                                    flex: 2.5,
                                    sortable: true,
                                    dataIndex: 'lg_FAMILLE_NAME'
                                },
                                {
                                    text: 'STOCK',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'lg_FAMILLE_QTE_STOCK',
                                    align: 'right'
                                },

                                {
                                    text: 'PU.MACHINE',
                                    flex: 1,
//                                    hidden: true,
                                    align: 'right',
                                    sortable: true,
                                    dataIndex: 'int_PRICE_MACHINE',
                                    renderer: amountformat

                                },

                                {
                                    text: 'PRIX.VENTE',
                                    flex: 1,
                                    sortable: true,
                                    align: 'right',
                                    dataIndex: 'lg_FAMILLE_PRIX_VENTE',
                                    renderer: amountformat,
                                    editor: {
                                        xtype: 'numberfield',
                                        minValue: 1,
                                        allowBlank: false,
                                        regex: /[0-9.]/,
                                        selectOnFocus: true,
                                        hideTrigger: true


                                    }

                                },
                                {
                                    text: 'PA.MACHINE',
                                    flex: 1,
                                    align: 'right',
                                    sortable: true,
                                    dataIndex: 'lg_FAMILLE_PRIX_ACHAT',
                                    renderer: amountformat

                                },
                                {
                                    text: 'PA.FACT',
                                    flex: 1,
                                    sortable: true,
                                    align: 'right',
                                    dataIndex: 'int_PAF',
                                    renderer: amountformat,
                                    editor: {
                                        xtype: 'numberfield',
                                        minValue: 1,
                                        allowBlank: false,
                                        regex: /[0-9.]/,
                                        selectOnFocus: true,
                                        hideTrigger: true
                                    }
                                },
                                {
                                    text: 'PRIX TIPS',
                                    flex: 1,
                                    sortable: true,
                                    hidden: true,
                                    align: 'right',
                                    renderer: amountformat,
                                    dataIndex: 'int_PRIX_REFERENCE',
                                    editor: {
                                        xtype: 'numberfield',
                                        minValue: 1,
                                        allowBlank: false,
                                        regex: /[0-9.]/
                                    }
                                },
                                {
                                    header: 'QTE',
                                    dataIndex: 'int_NUMBER',
                                    flex: 1,
                                    align: 'right',
                                    editor: {
                                        minValue: 1,
                                        xtype: 'numberfield',
                                        allowBlank: false,
                                        selectOnFocus: true,
                                        maskRe: /[0-9.]/
                                    }
                                },
                                {
                                    header: 'QTE A LIVRER',
                                    align: 'right',
                                    dataIndex: 'int_QTE_REP_GROSSISTE',
                                    flex: 1
                                },
                                {
                                    text: 'MONTANT',
                                    flex: 1,
                                    align: 'right',
                                    renderer: amountformat,
                                    sortable: true,
                                    dataIndex: 'int_PRICE'
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
                                {
                                    xtype: 'actioncolumn',
                                    width: 30,
                                    sortable: false,
                                    menuDisabled: true,
                                    items: [{
                                            icon: 'resources/images/icons/fam/grossiste.png',
                                            tooltip: 'Gerer le code article grossiste',
                                            scope: this,
                                            handler: this.onAddGrossisteClick,
                                            getClass: function (value, metadata, record) {
                                                if (record.get('str_CODE_ARTICLE') == "") {
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
                                            tooltip: 'Supprimer',
                                            scope: this,
                                            handler: this.onRemoveClick
                                        }]
                                }
                            ],
                            tbar: [{
                                    xtype: 'textfield',
                                    id: 'rechercherDetail',
                                    name: 'rechercherDetail',
                                    emptyText: 'Recherche',
                                    flex: 1,
                                    listeners: {
                                        'render': function (cmp) {
                                            cmp.getEl().on('keypress', function (e) {
                                                if (e.getKey() === e.ENTER) {
                                                    Me_Window.onRechClick();
                                                }
                                            });
                                        }
                                    }
                                },
                                '-', {
                                    xtype: 'combobox',
                                    name: 'str_TYPE_TRANSACTION',
                                    margins: '0 0 0 10',
                                    id: 'str_TYPE_TRANSACTION',
                                    store: store_type,
                                    valueField: 'str_TYPE_TRANSACTION',
                                    displayField: 'str_desc',
                                    typeAhead: true,
                                    queryMode: 'local',
                                    emptyText: 'Filtre article...',
                                    value:'ALL',
                                    flex: 1,
                                    listeners: {
                                        select: function (cmp) {
                                            const value = cmp.getValue();
                                            str_TYPE_TRANSACTION = value;

                                           
                                          Me_Window.onRechClick();
                                        }
                                    }
                                }


                            ],
                            bbar: {
                                xtype: 'pagingtoolbar',
                                pageSize: itemsPerPageGrid,
                                store: store_details_order,
                                displayInfo: true,
                                plugins: new Ext.ux.ProgressBarPager(),
                                bebeforechange: function (me,page, currentPage){
                                  
        const myProxy = me.getStore().getProxy();
        const val = Ext.getCmp('rechercherDetail');
        const filtre= Ext.getCmp('str_TYPE_TRANSACTION');
    
        myProxy.params = {
            query: '',
                filtre:'ALL',
                orderId:ref

        };
          myProxy.setExtraParam('query', val.getValue());
        myProxy.setExtraParam('filtre', filtre.getValue());
        myProxy.setExtraParam('orderId', ref);
                                }
                            },
                            listeners: {
                                scope: this
                                
                            }
                        }

                    ]

                },
                {
                    xtype: 'toolbar',
                    ui: 'footer',
                    dock: 'bottom',
                    border: '0',
                    items: ['->',
                        {
                            text: 'Enregistrer',
                            id: 'btn_save',
                            iconCls: 'icon-clear-group',
                            scope: this,
                            hidden: true,
                            handler: this.onbtnsave
                        },
                        {
                            text: 'Retour',
                            id: 'btn_cancel',
                            iconCls: 'icon-clear-group',
                            scope: this,
                            hidden: false,
                            //disabled: true,
                            handler: this.onbtncancel
                        }
                    ]
                }
            ]
        });
        this.callParent();
        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        });
        if (str_STATUT === "is_Waiting") {
            Ext.getCmp('btn_save').show();
        }

        if (titre === "Modifier les informations de la commande") {
            var OgridpanelID = Ext.getCmp('gridpanelID');
            Ext.getCmp('lgGROSSISTEID').setValue(this.getOdatasource().str_GROSSISTE_LIBELLE);
            int_montant_achat = Ext.util.Format.number(this.getOdatasource().PRIX_ACHAT_TOTAL, '0,000.');
            int_montant_vente = Ext.util.Format.number(this.getOdatasource().PRIX_VENTE_TOTAL, '0,000.');
            Ext.getCmp('int_VENTE').setValue(int_montant_vente + '  CFA');
            Ext.getCmp('int_ACHAT').setValue(int_montant_achat + '  CFA');
            str_STATUT = this.getOdatasource().str_STATUT;

        }


        Ext.getCmp('gridpanelID').on('edit', function (editor, e) {
            var qte = Number(e.record.data.int_NUMBER);
            testextjs.app.getController('App').ShowWaitingProcess();
            Ext.Ajax.request({
//                url: '../webservices/commandemanagement/order/ws_transaction.jsp?mode=update',
                url: '../api/v1/commande/updateorderitem',
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                params: Ext.JSON.encode({
                    id: e.record.data.lg_ORDERDETAIL_ID,
                    grossisteId: Ext.getCmp('lgGROSSISTEID').getValue(),
                    prixAchat: e.record.data.int_PAF,
                    prixVente: e.record.data.lg_FAMILLE_PRIX_VENTE,
                    stock: qte
                }),

                /*params: {
                 lg_ORDERDETAIL_ID: e.record.data.lg_ORDERDETAIL_ID,
                 lg_ORDER_ID: e.record.data.lg_ORDER_ID,
                 lg_FAMILLE_ID: e.record.data.lg_FAMILLE_ID,
                 lg_GROSSISTE_ID: Ext.getCmp('lgGROSSISTEID').getValue(),
                 int_NUMBER: qte,
                 int_PRIX_REFERENCE: e.record.data.int_PRIX_REFERENCE,
                 int_PAF: e.record.data.int_PAF,
                 lg_FAMILLE_PRIX_ACHAT: e.record.data.lg_FAMILLE_PRIX_ACHAT,
                 lg_FAMILLE_PRIX_VENTE: e.record.data.lg_FAMILLE_PRIX_VENTE,
                 str_STATUT: e.record.data.str_STATUT
                 },*/
                success: function (response)
                {
                    testextjs.app.getController('App').StopWaitingProcess();
//                    var object = Ext.JSON.decode(response.responseText, false);
                    var object = Ext.JSON.decode(response.responseText, true);
                    if (!object.success) {
                        Ext.MessageBox.alert('Error Message', "L'opération a échoué");
                        OGrid.getStore().reload();
                        return;
                    }

                    e.record.commit();
                    var OGrid = Ext.getCmp('gridpanelID');
                    OGrid.getStore().reload();
                    Ext.getCmp('str_NAME').focus(true, 100, function () {
                        Ext.getCmp('str_NAME').setValue("");
                        Ext.getCmp('str_NAME').selectText(0, 1);
                    });
                    int_montant_achat = Ext.util.Format.number(object.prixAchat, '0,000.');
                    int_montant_vente = Ext.util.Format.number(object.prixVente, '0,000.');
                    Ext.getCmp('int_VENTE').setValue(int_montant_vente);
                    Ext.getCmp('int_ACHAT').setValue(int_montant_achat);
                },
                failure: function (response)
                {
                    testextjs.app.getController('App').StopWaitingProcess();
                    console.log("Bug " + response.responseText);
                    Ext.MessageBox.alert('Error Message', response.responseText);
                }
            });
        });
    },
    loadStore: function () {
        Me_Window.onRechClick();
    },

    onbtndetail: function () {
//        alert(Ext.getCmp('lg_FAMILLE_ID_VENTE').getValue());
        new testextjs.view.configmanagement.famille.action.detailArticleOther({
            odatasource: Ext.getCmp('lg_FAMILLE_ID_VENTE').getValue(),
            parentview: this,
            mode: "detail",
            titre: "Detail sur l'article [" + Ext.getCmp('str_NAME').getValue() + "]"
        });
    },
    onchangeGrossiste: function () {
        //alert("HELLO WORLD");
        testextjs.app.getController('App').ShowWaitingProcess();
        Ext.Ajax.request({
            url: '../webservices/commandemanagement/order/ws_transaction.jsp?mode=changeGrossiste',
            params: {
                lg_ORDER_ID: ref,
                lg_GROSSISTE_ID: Ext.getCmp('lgGROSSISTEID').getValue()
            },
            success: function (response)
            {
                testextjs.app.getController('App').StopWaitingProcess();
                var object = Ext.JSON.decode(response.responseText, false);
                if (object.success === "0") {
                    Ext.MessageBox.alert('Error Message', object.errors);
                    return;
                }
                Ext.getCmp('str_NAME').focus(true, 100, function () {
                    Ext.getCmp('str_NAME').selectText(0, 1);
                });
            },
            failure: function (response)
            {
                testextjs.app.getController('App').StopWaitingProcess();
                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);
            }
        });
    },
    onbtncancel: function () {

        var xtype = "";
        if (str_STATUT === "is_Process" || str_STATUT === "is_Process") {
            xtype = "i_order_manager";
        } else {
            xtype = "orderpassmanager";
        }

        testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "", "", "");
    },
    
    updateCip: function (win, formulaire) {
        var me = this;
        if (formulaire.isValid()) {
            var progress = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
            Ext.Ajax.request({
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                url: '../api/v1/commande/cip',
                params: Ext.JSON.encode(formulaire.getValues()),
                success: function (response, options) {
                    progress.hide();
                    var result = Ext.JSON.decode(response.responseText, true);
                    if (result.success) {
                        win.destroy();
                        me.loadStore();
                    } else {
                        Ext.MessageBox.show({
                            title: 'Message d\'erreur',
                            width: 320,
                            msg: result.msg,
                            buttons: Ext.MessageBox.OK,
                            icon: Ext.MessageBox.ERROR

                        });
                    }

                },
                failure: function (response, options) {
                    win.destroy();
                    progress.hide();
                    Ext.Msg.alert("Message", 'Erreur du système ' + response.status);
                }

            });
        }
    },
    onAddGrossisteClick: function (grid, rowIndex) {
        var me = this;
        var rec = grid.getStore().getAt(rowIndex);
        var fam = rec.get('lg_FAMILLE_NAME');
        var lg_FAMILLE_ID1 = rec.get('lg_FAMILLE_ID');
        var lg_GROSSISTE_LIBELLE = rec.get('lg_GROSSISTE_LIBELLE');
        var win = Ext.create("Ext.window.Window", {
            titre: "Ajouter un code article  [" + fam + "]",
            modal: true,
            width: 500,
            height: 200,
            maximizable: false,
            defaults: {
                anchor: '100%'
            },
            items: [
                {
                    xtype: 'form',
                    region: 'center',
//                    url: '../Grossiste',
//                    waitMsg: 'En cours  ...',
                    bodyPadding: 10,
                    fieldDefaults: {
                        labelAlign: 'right',
                        labelWidth: 160,
                        msgTarget: 'side',
                        anchor: '100%'
                    },
                    items: [
                        {
                            xtype: 'fieldset',
                            title: 'Information Grossiste',
                            defaultType: 'textfield',
                            defaults: {
                                anchor: '100%'
                            },
                            items: [
                                {
                                    xtype: 'combobox',
                                    fieldLabel: 'Grossiste',
                                    name: 'refParent',
                                    width: 400,
                                    store: storerepartiteur,
                                    valueField: 'lg_GROSSISTE_ID',
                                    displayField: 'str_LIBELLE',
                                    queryMode: 'remote',
                                    emptyText: 'Choisir un grossiste...',
                                    value: lg_GROSSISTE_LIBELLE
                                },
                                {
                                    name: 'description',
                                    fieldLabel: 'Code article',
                                    emptyText: 'Code article',
                                    flex: 1,
                                    allowBlank: false,
                                    enableKeyEvents: true,
                                    listeners: {
                                        afterrender: function (field) {
                                            field.focus(true, 50);
                                        },
                                        specialKey: function (field, e) {
                                            if (e.getKey() === e.ENTER) {
                                                var formulaire = field.up('form');
                                                if (formulaire.isValid()) {
                                                    me.updateCip(win, formulaire);
                                                }

                                            }
                                        }
                                    }
                                },
                                {
                                    name: 'ref',
                                    hidden: true,
                                    value: lg_FAMILLE_ID1,
                                    flex: 1,
                                    allowBlank: false
                                }
                            ]

                        }
                    ],
                    dockedItems: [
                        {
                            xtype: 'toolbar',
                            dock: 'bottom',
                            ui: 'footer',
                            layout: {
                                pack: 'end',
                                type: 'hbox'
                            },
                            items: [
                                {
                                    xtype: 'button',
                                    text: 'Save',
                                    handler: function (btn) {
                                        var formulaire = btn.up("form");
                                        console.log(formulaire);
                                        if (formulaire.isValid()) {
                                            me.updateCip(win, formulaire);

                                        } else {
                                            Ext.Msg.alert('Invalid Data', 'Veuillez saissir.');
                                        }
                                    }
                                },
                                {
                                    text: "Fermer",
                                    handler: function () {
                                        win.hide();
                                    }
                                }
                            ]
                        }

                    ]
                }
            ]

        });
        win.show();



    },
    onbtnaddArticle: function () {
        new testextjs.view.configmanagement.famille.action.add2({
//        new testextjs.view.configmanagement.famille.action.add({
            odatasource: "",
            parentview: this,
            mode: "create",
            titre: "Creer un nouveau produit",
            type: "commande"
        });
    },
    onRemoveClick: function (grid, rowIndex) {
        //alert('Suppression clickée');
        //return;
        var message = "Confirmer la suppresssion";
        var mode = "deleteDetail";
        var nbreRow = grid.getStore().getCount();
        var detailGridCount = Ext.getCmp('gridpanelID').getStore().getCount();
        console.log("nbreRow: ", nbreRow);
        console.log("details grid Count: ", detailGridCount);
        Ext.MessageBox.confirm('Message',
                message,
                function (btn) {
                    if (btn === 'yes') {
                        var rec = grid.getStore().getAt(rowIndex);
                        testextjs.app.getController('App').ShowWaitingProcess();
                        Ext.Ajax.request({
                            url: url_services_transaction_order + mode,
                            params: {
                                lg_ORDERDETAIL_ID: rec.get('lg_ORDERDETAIL_ID'),
                                lg_ORDER_ID: ref
                            },
                            success: function (response)
                            {
                                testextjs.app.getController('App').StopWaitingProcess();
                                var object = Ext.JSON.decode(response.responseText, false);
                                if (object.success == "0") {
                                    Ext.MessageBox.alert('Error Message', object.errors);
                                    return;
                                }
                                if (object.total_lineproduct == 0) {
                                    Me_Window.onbtncancel();
                                    return;
                                }
                                int_montant_achat = Ext.util.Format.number(object.PRIX_ACHAT_TOTAL, '0,000.');
                                int_montant_vente = Ext.util.Format.number(object.PRIX_VENTE_TOTAL, '0,000.');
                                Ext.getCmp('int_VENTE').setValue(int_montant_vente + '  CFA');
                                Ext.getCmp('int_ACHAT').setValue(int_montant_achat + '  CFA');

                                var gridStore = grid.getStore();
                                console.log(gridStore);
                                if (gridStore.getTotalCount() > 1) {
                                    if (gridStore.getCount() === 1) {
                                        gridStore.loadPage(gridStore.currentPage - 1);



                                    } else {
                                        gridStore.load();
                                    }
                                } else {
                                    gridStore.load();
                                }
                                Ext.getCmp('str_NAME').focus(true, 100, function () {
                                    Ext.getCmp('str_NAME').selectText(0, 1);
                                });
                            },
                            failure: function (response)
                            {
                                testextjs.app.getController('App').StopWaitingProcess();
                               
                                console.log("Bug " + response.responseText);
                                Ext.MessageBox.alert('Error Message', response.responseText);
                            }
                        });
                        return;
                    }
                });
    },
   
    onbtnsave: function () {

        Ext.MessageBox.confirm('Message',
                'Confirmer l\'enregistrement de la commande',
                function (btn) {
                    if (btn === 'yes') {

                        Ext.Ajax.request({
                            url: url_services_transaction_order + 'passeorder',
                            params: {
                                lg_ORDER_ID: ref
                            },
                            success: function (response)
                            {
                                var object = Ext.JSON.decode(response.responseText, false);
                                if (object.success == "0") {
                                    Ext.MessageBox.alert('Error Message', object.errors);
                                    return;
                                }
},
                            failure: function (response)
                            {
                                var object = Ext.JSON.decode(response.responseText, false);
                                console.log("Bug " + response.responseText);
                                Ext.MessageBox.alert('Error Message', response.responseText);
                            }
                        });
                    }
                });
    },
    onDetailClick: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        new testextjs.view.configmanagement.famille.action.detailArticleOther({
            odatasource: rec.get('lg_FAMILLE_ID'),
            parentview: this,
            mode: "detail",
            titre: "Detail sur l'article [" + rec.get('lg_FAMILLE_NAME') + "]"
        });
    },
    onPdfClick: function (lg_ORDER_ID) {
        var chaine = location.pathname;
        var reg = new RegExp("[/]+", "g");
        var tableau = chaine.split(reg);
        var sitename = tableau[1];
        var linkUrl = url_services_pdf + '?lg_ORDER_ID=' + lg_ORDER_ID;
        //alert("Ok ca marche " + linkUrl);
        window.open(linkUrl);
        Me_Window.onbtncancel();
    },
    onRechClick: function () {
        const val = Ext.getCmp('rechercherDetail');
        const filtre= Ext.getCmp('str_TYPE_TRANSACTION');
        Ext.getCmp('gridpanelID').getStore().load({
            params: {
                query: val.getValue(),
                filtre:filtre.getValue(),
                orderId:ref
            }
        });
    }
});
function onfiltercheckingCommande() {
    var OComponent = Ext.getCmp('str_NAME');
    var OComponent_val = OComponent.getValue();
    var OFamille_store = OComponent;
    if (OComponent_val !== null && OComponent_val !== "" && OComponent_val !== undefined) {
        var OComponent_length = OComponent_val.length;
        if (OComponent_length >= 3) {

            OFamille_store.getStore().getProxy().url = url_services_data_famille_select_order + "?search_value=" + OComponent_val;
            OFamille_store.getStore().reload();
        }
    } else {

        //alert('ici');
        OFamille_store.getStore().getProxy().url = url_services_data_famille_select_order;
        OFamille_store.getStore().reload();
    }
}
;

function onbtnaddCommande(ref_f) {
    var internal_url = "";
    if (ref === "") {
        ref = null;
    } else if (ref === undefined) {
        ref = null;
    }
    ref = ref_f;
    /*alert("ref " + ref_f);
     return;*/

    if (Ext.getCmp('lgGROSSISTEID').getValue() == null) {

        Ext.MessageBox.alert('Error Message', 'Renseignez le Grossiste ');
    } else {

        testextjs.app.getController('App').ShowWaitingProcess();
        Ext.Ajax.request({
            url: '../webservices/commandemanagement/order/ws_transaction.jsp?mode=create',
            params: {
//                lg_FAMILLE_ID: Ext.getCmp('str_NAME').getValue(),
                lg_FAMILLE_ID: Ext.getCmp('lg_FAMILLE_ID_VENTE').getValue(),
                lg_ORDER_ID: ref,
                lg_ORDERDETAIL_ID: null,
                lg_GROSSISTE_ID: Ext.getCmp('lgGROSSISTEID').getValue(),
                int_NUMBER: Ext.getCmp('int_QUANTITE').getValue(),
                str_STATUT: str_STATUT

            },
            success: function (response)
            {
                testextjs.app.getController('App').StopWaitingProcess();
                var object = Ext.JSON.decode(response.responseText, false);
                if (object.success == "0") {
                    Ext.MessageBox.alert('Error Message', object.errors);
                    return;
                } else {
                    ref = object.ref;
                    ref_final = ref;
                    // url_services_data_orderdetails = '../webservices/commandemanagement/orderdetail/ws_data.jsp?lg_ORDER_ID=' + ref;
                    setTitleFrame(object.ref);
                    var OGrid = Ext.getCmp('gridpanelID');
                    OGrid.getStore().reload();
                    Ext.getCmp('int_QUANTITE').setValue(1);
                    Ext.getCmp('str_NAME').focus(true, 100, function () {
                        Ext.getCmp('str_NAME').setValue("");
                        Ext.getCmp('str_NAME').selectText(0, 1);
                    });
                    int_montant_achat = Ext.util.Format.number(object.PRIX_ACHAT_TOTAL, '0,000.');
                    int_montant_vente = Ext.util.Format.number(object.PRIX_VENTE_TOTAL, '0,000.');

                    Ext.getCmp('btn_detail').disable();

                    Ext.getCmp('int_VENTE').setValue(int_montant_vente + '  CFA');
                    Ext.getCmp('int_ACHAT').setValue(int_montant_achat + '  CFA');
                }


            },
            failure: function (response)
            {
                testextjs.app.getController('App').StopWaitingProcess();
                var object = Ext.JSON.decode(response.responseText, false);
                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);
            }
        });
    }

}


function setTitleFrame(str_data) {
    this.title = this.title + " :: Ref " + str_data;
    ref = str_data;
    // url_services_data_orderdetails_process = '../webservices/commandemanagement/orderdetail/ws_data.jsp?lg_ORDER_ID=' + ref;
    var OGrid = Ext.getCmp('gridpanelID');
    //   url_services_data_orderdetails_process = '../webservices/commandemanagement/orderdetail/ws_data.jsp?lg_ORDER_ID=' + ref;
    OGrid.getStore().getProxy().url = url_services_data_orderdetails_process + '?lg_ORDER_ID=' + ref;
    OGrid.getStore().reload();
}