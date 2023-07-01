/* global Ext */

var url_services_transaction_suggerercde = '../webservices/sm_user/suggerercde/ws_transaction.jsp?mode=';
var url_services_data_grossiste_suggerer = '../webservices/configmanagement/grossiste/ws_data.jsp';
var url_services_pdf_liste_suggerercde = '../webservices/sm_user/suggerercde/ws_generate_pdf.jsp';
var url_services_data_famille_select_suggestion = '../webservices/sm_user/famille/ws_data_initial.jsp';


var Me_Window;
var Omode;
var ref;
var famille_id_search;
var in_total_vente;
var int_total_formated;
var titre;
var int_montant_vente;
var LaborexWorkFlow, myAppController;
var store_famille_dovente = null;
var int_montant_achat;


Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';

function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.sm_user.suggerercde.SuggerercdeManager', {
    extend: 'Ext.form.Panel',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.form.*',
        'Ext.layout.container.Column',
        'testextjs.model.Famille',
        'testextjs.controller.LaborexWorkFlow',
        'testextjs.model.Grossiste',
        'testextjs.model.TSuggestionOrderDetails',
        'Ext.window.Window'
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
    xtype: 'suggerercdemanager',
    id: 'suggerercdemanagerID',
    frame: true,
    title: 'Suggerer une Commande',
    bodyPadding: 5,
    layout: 'column',
    initComponent: function () {
        Me_Window = this;
        int_montant_vente = 0;
        int_montant_achat = 0;
        var itemsPerPage = 20;
        var itemsPerPageGrid = 10;
        famille_id_search = "";
        in_total_vente = 0;
        int_total_formated = 0;
        titre = this.getTitre();
        myAppController = Ext.create('testextjs.controller.App', {});
        LaborexWorkFlow = Ext.create('testextjs.controller.LaborexWorkFlow', {});
        store_famille_dovente = LaborexWorkFlow.BuildStore('testextjs.model.Famille', itemsPerPage, url_services_data_famille_select_suggestion);
        var AppController = testextjs.app.getController('App');
        ref = this.getNameintern();
        var store = Ext.create('testextjs.store.SearchStore');
        var storerepartiteur = new Ext.data.Store({
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
                }
            }

        });

        var store_details_sugg = new Ext.data.Store({
            model: 'testextjs.model.TSuggestionOrderDetails',
            pageSize: itemsPerPageGrid,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../suggestion',
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });
        var int_BUTOIR = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    fieldLabel: 'Butoir ::',
                    labelWidth: 50,
                    name: 'int_DATE_BUTOIR_ARTICLE',
                    id: 'int_DATE_BUTOIR_ARTICLE',
                    fieldStyle: "color:blue;font-size:1.5em;font-weight: bold;",
                    margin: '0 15 0 0',
                    value: "0"
                });
        var int_VENTE = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    fieldLabel: 'Valeur Vente ::',
                    labelWidth: 95,
                    name: 'int_VENTE',
                    id: 'int_VENTE',
                    fieldStyle: "color:blue;font-size:1.5em;font-weight: bold;",
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
                    fieldStyle: "color:blue;font-size:1.5em;font-weight: bold;",
                    margin: '0 15 0 0',
                    value: "0"
                });
        this.cellEditing = new Ext.grid.plugin.CellEditing({
            clicksToEdit: 1
        });
        Ext.apply(this, {
            width: '98%',
//            height:Ext.getBody().getViewSize().height*0.85,         
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
            items: [{
                    xtype: 'fieldset',
                    title: 'Infos Generales',
                    collapsible: true,
                    defaultType: 'textfield',
                    margin: '5 0 5 0',
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
                                    margin: '0 15 0 0',
                                    id: 'lg_GROSSISTE_ID',
                                    store: storerepartiteur,
                                    valueField: 'lg_GROSSISTE_ID',
                                    displayField: 'str_LIBELLE',
                                    typeAhead: true,
                                    queryMode: 'remote',
                                    emptyText: 'Choisir un repartiteur...',
                                    listeners: {
                                        select: function (cmp) {
                                            var value = cmp.getValue();

                                            if (titre === 'Suggerer une commande') {
                                                // alert("Titre 2" + titre);
                                                Me_Window.onchangeGrossiste();
                                            }

                                            if (titre === "Ajouter detail commande") {
                                                Me_Window.onIsGrossisteExist(value);
                                            }
                                        }
                                    }
                                },
                                int_BUTOIR,
                                int_ACHAT,
                                int_VENTE]
                        }]
                },
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
                                    enableKeyEvents: true,
                                    valueField: 'CIP',
                                    displayField: 'str_DESCRIPTION',
                                    pageSize: 20, //ajout la barre de pagination

//                                            hideTrigger:true,
                                    typeAhead: true,
                                    flex: 2,
                                    queryMode: 'remote',
                                    emptyText: 'Choisir un article par Nom ou Cip...',
                                    listConfig: {
                                        loadingText: 'Recherche...',
                                        emptyText: 'Pas de données trouvées.',
                                        getInnerTpl: function () {
                                            return '<tpl for="."><tpl if="int_NUMBER_AVAILABLE <=0"><span style="color:#17987e;font-weight:bold;"><span style="width:100px;display:inline-block;">{CIP}</span>{str_DESCRIPTION} <span style="float: right;"> ( {int_PRICE} )</span></span><tpl else><span style="font-weight:bold;"><span style="width:100px;display:inline-block;">{CIP}</span>{str_DESCRIPTION} <span style="float: right; "> ( {int_PRICE} )</span></span></tpl></tpl>';
//                                            return '<span style="width:100px;display:inline-block;">{CIP}</span>{str_DESCRIPTION} <span style="float: right; font-weight:600;"> ({int_PRICE})</span><br>';
                                        }
                                    },
                                    listeners: {
                                        select: function (cmp) {
                                            var value = cmp.getValue();
                                            var record = cmp.findRecord(cmp.valueField || cmp.displayField, value); //recupere la ligne de l'element selectionné

                                            Ext.getCmp('lg_FAMILLE_ID_VENTE').setValue(record.get('lg_FAMILLE_ID'));
                                            if (cmp.getValue() === "0" || cmp.getValue() === "Ajouter un nouvel article") {
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
                                    width: 400,
                                    value: 1,
                                    allowBlank: false,
                                    enableKeyEvents: true,
                                    regex: /[0-9.]/,
                                    listeners: {
                                        specialKey: function (field, e) {
                                            if (e.getKey() === e.ENTER) {
                                                if (Ext.getCmp('str_NAME').getValue() !== "") {
                                                    Me_Window.onEdit();
                                                } else {
                                                    Ext.MessageBox.alert('Error Message', 'Verifiez votre selection svp');

                                                }
                                            }
                                        }


                                    }
                                },
                                {
                                    text: 'Ajouter',
                                    id: 'btn_add',
                                    margins: '0 0 0 6',
                                    hidden: true,
                                    xtype: 'button',
                                    handler: this.onbtnadd,
                                    disabled: true
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
                    title: 'Liste des produits de la suggestion',
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
                            id: 'gridpanelSuggestionID',
                            plugins: [this.cellEditing],
                            store: store_details_sugg,
                            height: 370,
                            columns: [

                                {
                                    text: 'CIP',
                                    flex: 0.7,
                                    sortable: true,
                                    dataIndex: 'str_FAMILLE_CIP',
                                    renderer: function (v, m, r) {
                                        if (r.data.STATUS === 1) {
                                            m.style = 'background-color:#73C774;';
                                        }
                                        if (r.data.STATUS === 2) {
                                            m.style = 'background-color:#5fa2dd;';
                                        }


                                        return v;
                                    }
                                },
                                {
                                    text: 'LIBELLE',
                                    flex: 2.5,
                                    sortable: true,
                                    dataIndex: 'str_FAMILLE_NAME',
                                    renderer: function (v, m, r) {
                                        if (r.data.STATUS === 1) {
                                            m.style = 'background-color:#73C774;';
                                        }
                                        if (r.data.STATUS === 2) {
                                            m.style = 'background-color:#5fa2dd;';
                                        }

                                        return v;
                                    }
                                },
                                {
                                    text: 'PRIX.VENTE',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'lg_FAMILLE_PRIX_VENTE',
                                    renderer: function (v, m, r) {
                                        if (r.data.STATUS === 1) {
                                            m.style = 'background-color:#73C774;';
                                        }
                                        if (r.data.STATUS === 2) {
                                            m.style = 'background-color:#5fa2dd;';
                                        }


                                        return amountformat(v);
                                    },

                                    editor: {
                                        xtype: 'numberfield',
                                        minValue: 1,
                                        selectOnFocus: true,
                                        allowBlank: false,
                                        regex: /[0-9.]/
                                    }
                                },
                                {
                                    text: 'PRIX A. TARIF',
                                    flex: 1,
                                    sortable: true,
                                    hidden: true,
                                    renderer: function (v, m, r) {
                                        if (r.data.STATUS === 1) {
                                            m.style = 'background-color:#73C774;';
                                        }
                                        if (r.data.STATUS === 2) {
                                            m.style = 'background-color:#5fa2dd;';
                                        }

                                        return amountformat(v);
                                    },
                                    dataIndex: 'lg_FAMILLE_PRIX_ACHAT',
                                    editor: {
                                        xtype: 'numberfield',
                                        minValue: 1,
                                        allowBlank: false,
                                        selectOnFocus: true,
                                        regex: /[0-9.]/
                                    }
                                },
                                {
                                    text: 'PRIX A. FACT',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'int_PAF_SUGG',
                                    renderer: function (v, m, r) {
                                        if (r.data.STATUS === 1) {
                                            m.style = 'background-color:#73C774;';
                                        }
                                        if (r.data.STATUS === 2) {
                                            m.style = 'background-color:#5fa2dd;';
                                        }


                                        return amountformat(v);
                                    },
                                    editor: {
                                        xtype: 'numberfield',
                                        minValue: 1,
                                        allowBlank: false,
                                        regex: /[0-9.]/
                                    }
                                },
                                {
                                    text: 'PRIX TIPS',
                                    flex: 1,
                                    sortable: true,
                                    hidden: true,
                                    renderer: function (v, m, r) {
                                        if (r.data.STATUS === 1) {
                                            m.style = 'background-color:#73C774;';
                                        }
                                        if (r.data.STATUS === 2) {
                                            m.style = 'background-color:#5fa2dd;';
                                        }


                                        return amountformat(v);
                                    },
                                    dataIndex: 'int_PRIX_REFERENCE'
                                },
                                {
                                    text: 'STOCK',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'int_STOCK',
                                    renderer: function (v, m, r) {
                                        if (r.data.STATUS === 1) {
                                            m.style = 'background-color:#73C774;';
                                        }
                                        if (r.data.STATUS === 2) {
                                            m.style = 'background-color:#5fa2dd;';
                                        }


                                        return amountformat(v);
                                    }
                                },
                                {
                                    text: 'SEUIL',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'int_SEUIL',
                                    renderer: function (v, m, r) {
                                        if (r.data.STATUS === 1) {
                                            m.style = 'background-color:#73C774;';
                                        }
                                        if (r.data.STATUS === 2) {
                                            m.style = 'background-color:#5fa2dd;';
                                        }


                                        return amountformat(v);
                                    },
                                    editor: {
                                        xtype: 'numberfield',
                                        minValue: 1,
                                        selectOnFocus: true,
                                        allowBlank: false,
                                        regex: /[0-9.]/
                                    }
                                },
                                {
                                    header: 'Q.CDE',
                                    dataIndex: 'int_NUMBER',
                                    renderer: function (v, m, r) {
                                        if (r.data.STATUS === 1) {
                                            m.style = 'background-color:#73C774;';
                                        }
                                        if (r.data.STATUS === 2) {
                                            m.style = 'background-color:#5fa2dd;';
                                        }


                                        return amountformat(v);
                                    },
                                    flex: 1,
                                    editor: {
                                        xtype: 'numberfield',
                                        minValue: 1,
                                        selectOnFocus: true,
                                        allowBlank: false,
                                        regex: /[0-9.]/
                                    }
                                },
                                {
                                    header: AppController.getMonthToDisplay(0, currentMonth),
                                    dataIndex: 'int_VALUE0',
                                    align: 'center',
                                    flex: 1,
                                    renderer: function (v, m, r) {
                                        if (r.data.STATUS === 1) {
                                            m.style = 'background-color:#73C774;';
                                        }
                                        if (r.data.STATUS === 2) {
                                            m.style = 'background-color:#5fa2dd;';
                                        }


                                        return amountformat(v);
                                    }
                                },
                                {
                                    header: AppController.getMonthToDisplay(1, currentMonth),
                                    dataIndex: 'int_VALUE1',
                                    align: 'center',
                                    renderer: function (v, m, r) {
                                        if (r.data.STATUS === 1) {
                                            m.style = 'background-color:#73C774;';
                                        }
                                        if (r.data.STATUS === 2) {
                                            m.style = 'background-color:#5fa2dd;';
                                        }


                                        return amountformat(v);
                                    },
                                    flex: 0.7
                                },
                                {
                                    header: AppController.getMonthToDisplay(2, currentMonth),
                                    dataIndex: 'int_VALUE2',
                                    align: 'center',
                                    renderer: function (v, m, r) {
                                        if (r.data.STATUS === 1) {
                                            m.style = 'background-color:#73C774;';
                                        }
                                        if (r.data.STATUS === 2) {
                                            m.style = 'background-color:#5fa2dd;';
                                        }


                                        return amountformat(v);
                                    },
                                    flex: 0.7
                                },
                                {
                                    header: AppController.getMonthToDisplay(3, currentMonth),
                                    dataIndex: 'int_VALUE3',
                                    align: 'center',
                                    renderer: function (v, m, r) {
                                        if (r.data.STATUS === 1) {
                                            m.style = 'background-color:#73C774;';
                                        }
                                        if (r.data.STATUS === 2) {
                                            m.style = 'background-color:#5fa2dd;';
                                        }


                                        return amountformat(v);
                                    },
                                    flex: 0.7
                                },
                                {
                                    xtype: 'actioncolumn',
                                    width: 30,
                                    sortable: false,
                                    menuDisabled: true,
                                    items: [
                                        {
                                            icon: 'resources/images/icons/fam/cog.png',
                                            tooltip: 'Qté détail',
                                            scope: this,
                                            handler: this.onQtyDetail,

                                            getClass: function (value, metadata, record) {
                                                if (record.get('bool_DECONDITIONNE_EXIST') === 1) {  //read your condition from the record
                                                    return 'x-display-hide'; //affiche l'icone
                                                } else {
                                                    return 'x-hide-display'; //cache l'icone
                                                }
                                            }
                                        }


                                    ]
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
                                            icon: 'resources/images/icons/fam/delete.png',
                                            tooltip: 'Supprimer',
                                            scope: this,
                                            handler: this.onRemoveClick
                                        }]
                                }],
                            tbar: [{
                                    xtype: 'textfield',
                                    id: 'rechercherDetail',
                                    name: 'rechercherDetail',
                                    emptyText: 'Recherche',
                                    width: 300,
                                    listeners: {
                                        'render': function (cmp) {
                                            cmp.getEl().on('keypress', function (e) {
                                                if (e.getKey() === e.ENTER) {
                                                    Me_Window.onRechClick();

                                                }
                                            });
                                        }
                                    }
                                }
                            ],
                            bbar: {
                                xtype: 'pagingtoolbar',
                                pageSize: itemsPerPageGrid,
                                store: store_details_sugg,
                                displayInfo: true,
                                plugins: new Ext.ux.ProgressBarPager(), // same store GridPanel is using
                                listeners: {
                                    beforechange: function (page, currentPage) {
                                        const myProxy = this.store.getProxy();
                                        myProxy.params = {
                                            search_value: '', lg_SUGGESTION_ORDER_ID: ''
                                        };

                                        myProxy.setExtraParam('search_value', Ext.getCmp('rechercherDetail').getValue());
                                        myProxy.setExtraParam('lg_SUGGESTION_ORDER_ID', ref);
                                    }

                                }
                            },
                            listeners: {
                                scope: this
                                        //selectionchange: this.onSelectionChange
                            }
                        }]
                },
                {
                    xtype: 'toolbar',
                    ui: 'footer',
                    dock: 'bottom',
                    border: '0',
                    items: ['->',
                        {
                            text: 'Retour',
                            id: 'btn_cancel',
                            iconCls: 'icon-clear-group',
                            scope: this,
                            hidden: false,
                            //disabled: true,
                            handler: this.onbtncancel
                        },
                        {
                            text: 'Imprimer',
                            id: 'btn_print',
                            iconCls: 'icon-clear-group',
                            scope: this,
                            hidden: true,
                            handler: this.onbtnprint
                        }
                    ]
                }]
        });
        this.callParent();
        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        });
        if (titre === "Suggerer une commande") {
            const OgridpanelSuggestionID = Ext.getCmp('gridpanelSuggestionID');
            Ext.getCmp('lg_GROSSISTE_ID').setValue(this.getOdatasource().lg_GROSSISTE_ID);
            Ext.getCmp('btn_print').show();

            int_montant_achat = Ext.util.Format.number(this.getOdatasource().int_TOTAL_ACHAT, '0,000.');
            int_montant_vente = Ext.util.Format.number(this.getOdatasource().int_TOTAL_VENTE, '0,000.');
            Ext.getCmp('int_VENTE').setValue(int_montant_vente + '  CFA');
            Ext.getCmp('int_ACHAT').setValue(int_montant_achat + '  CFA');
            Ext.getCmp('int_DATE_BUTOIR_ARTICLE').setValue(this.getOdatasource().int_DATE_BUTOIR_ARTICLE);
            const url = '../suggestion?lg_SUGGESTION_ORDER_ID=' + ref;
            OgridpanelSuggestionID.getStore().getProxy().url = url;
            OgridpanelSuggestionID.getStore().load();


        }

        Ext.getCmp('gridpanelSuggestionID').on('edit', function (editor, e) {
            const OGrid = Ext.getCmp('gridpanelSuggestionID');
            let datas;
            let url;
            const suggId = e.record.data.lg_SUGGESTION_ORDER_ID;
            if (e.field === 'int_NUMBER') {
                const qte = Number(e.record.data.int_NUMBER);
                url = '../api/v1/suggestion/item/update-qte-cmde';
                datas = {
                    itemId: e.record.data.lg_SUGGESTION_ORDER_DETAILS_ID,
                    qte
                };

            } else if (e.field === 'int_SEUIL') {
                url = '../api/v1/suggestion/item/update-seuil';
                const qtySeuil = Number(e.record.data.int_SEUIL);
                datas = {
                    itemId: e.record.data.lg_SUGGESTION_ORDER_DETAILS_ID,
                    seuil: qtySeuil
                };
            } else if (e.field === 'lg_FAMILLE_PRIX_VENTE') {
                url = '../api/v1/suggestion/item/update-prixvente';
                const prixVente = Number(e.record.data.lg_FAMILLE_PRIX_VENTE);
                datas = {
                    itemId: e.record.data.lg_SUGGESTION_ORDER_DETAILS_ID,
                    prixVente
                };
            } else if (e.field === 'int_PAF_SUGG') {
                url = '../api/v1/suggestion/item/update-prixachat';
                const prixPaf = Number(e.record.data.int_PAF_SUGG);
                datas = {
                    itemId: e.record.data.lg_SUGGESTION_ORDER_DETAILS_ID,
                    prixPaf
                };
            } else {
                datas = null;
                url = null;
            }

            if (datas !== null && url !== null) {
                Ext.Ajax.request({
                    method: 'POST',
                    url: url,
                    headers: {'Content-Type': 'application/json'},
                    params: Ext.JSON.encode(datas),
                    success: function (response, options) {
                        e.record.commit();
                        OGrid.getStore().load();

                        Me_Window.getSuggestionAmount(suggId);
                    },
                    failure: function (response, options) {

                        Ext.Msg.alert("Message", 'server-side failure with status code ' + response.status);
                    }
                });
            }

        });


    },
    loadStore: function () {

    },

    onbtnprint: function () {
        Ext.MessageBox.confirm('Message',
                'Confirmation de l\'impression de cette suggestion',
                function (btn) {
                    if (btn == 'yes') {
                        Me_Window.onPdfClick(ref);
                        return;
                    }
                });

    },
    onPdfClick: function (lg_SUGGESTION_ORDER_ID) {
        var chaine = location.pathname;
        var reg = new RegExp("[/]+", "g");
        var tableau = chaine.split(reg);
        var sitename = tableau[1];
        var linkUrl = url_services_pdf_liste_suggerercde + "?lg_SUGGESTION_ORDER_ID=" + lg_SUGGESTION_ORDER_ID;

        window.open(linkUrl);
    },
    checkIfGridIsEmpty: function () {
        var gridTotalCount = Ext.getCmp('gridpanelSuggestionID').getStore().getTotalCount();
        return gridTotalCount;
    },
    setTitleFrame: function (str_data) {
        this.title = this.title + " :: Ref " + str_data;

    },
    onfiltercheck: function () {
        var str_name = Ext.getCmp('str_NAME').getValue();
        var int_name_size = str_name.length;
        if (int_name_size < 4) {
            Ext.getCmp('btn_add').disable();
        }

    },
    onbtnadd: function () {
        var internal_url = "";
        if (ref === "") {
            ref = null;
        } else if (ref === undefined) {
            ref = null;
        }

        if (Ext.getCmp('lg_GROSSISTE_ID').getValue() === null) {

            Ext.MessageBox.alert('Error Message', 'Renseignez le Grossiste ', function () {
                Ext.getCmp('lg_GROSSISTE_ID').focus();
            });
        } else {

            testextjs.app.getController('App').ShowWaitingProcess();
            Ext.Ajax.request({
                url: url_services_transaction_suggerercde + 'create',
                params: {
//                    lg_FAMILLE_ID: Ext.getCmp('str_NAME').getValue(), ancienne bonne version
                    lg_FAMILLE_ID: Ext.getCmp('lg_FAMILLE_ID_VENTE').getValue(),
                    lg_SUGGESTION_ORDER_ID: ref,
                    lg_SUGGESTION_ORDER_DETAILS_ID: null,
                    lg_GROSSISTE_ID: Ext.getCmp('lg_GROSSISTE_ID').getValue(),
//                    int_NUMBER: 1
                    int_NUMBER: Ext.getCmp('int_QUANTITE').getValue()

                },
                success: function (response) {
                    testextjs.app.getController('App').StopWaitingProcess();
                    var object = Ext.JSON.decode(response.responseText, false);


                    if (object.errors_code == "0") {
                        Ext.MessageBox.alert('Error Message', object.errors);
                        return;
                    } else {
                        ref = object.ref;
                        Me_Window.setTitleFrame(object.ref);
                        var OGrid = Ext.getCmp('gridpanelSuggestionID');
                        OGrid.getStore().reload();
                        Ext.getCmp('str_NAME').setValue("");
                        Ext.getCmp('int_QUANTITE').setValue(1);


                        int_montant_achat = Ext.util.Format.number(object.int_TOTAL_ACHAT, '0,000.');
                        int_montant_vente = Ext.util.Format.number(object.int_TOTAL_VENTE, '0,000.');

                        Ext.getCmp('btn_detail').disable();

                        Ext.getCmp('int_VENTE').setValue(int_montant_vente + '  CFA');
                        Ext.getCmp('int_ACHAT').setValue(int_montant_achat + '  CFA');
                        Ext.getCmp('str_NAME').focus();

                    }


                },
                failure: function (response) {
                    testextjs.app.getController('App').StopWaitingProcess();
                    var object = Ext.JSON.decode(response.responseText, false);
                    console.log("Bug " + response.responseText);
                    Ext.MessageBox.alert('Error Message', response.responseText);
                }
            });
        }

    },

    onEdit: function () {
        const me = this;
        const suggestionId = me.getNameintern();

        if (Ext.getCmp('lg_GROSSISTE_ID').getValue() === null) {

            Ext.MessageBox.alert('Error Message', 'Renseignez le Grossiste ', function () {
                Ext.getCmp('lg_GROSSISTE_ID').focus();
            });
        } else {
            testextjs.app.getController('App').ShowWaitingProcess();
            const data = {
                "qte": Ext.getCmp('int_QUANTITE').getValue(),
                "familleId": Ext.getCmp('lg_FAMILLE_ID_VENTE').getValue(),
                suggestionId

            };
            Ext.Ajax.request({
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                url: '../api/v1/suggestion/item/add',
                params: Ext.JSON.encode(data),
                success: function (response, options) {
                    testextjs.app.getController('App').StopWaitingProcess();
                    Me_Window.getSuggestionAmount(suggestionId);
                    me.updateAmountFields(response.data);
                    const OGrid = Ext.getCmp('gridpanelSuggestionID');
                    OGrid.getStore().reload();
                },
                failure: function (response) {
                    console.log("Bug " + response.responseText);
                    Ext.MessageBox.alert('Error Message', response.responseText);
                    testextjs.app.getController('App').StopWaitingProcess();
                }
            });

        }

    },
    updateAmountFields: function (data) {
        if (data) {
            int_montant_achat = Ext.util.Format.number(data.montantAchat, '0,000.');
            int_montant_vente = Ext.util.Format.number(data.montantVente, '0,000.');
            Ext.getCmp('int_VENTE').setValue(int_montant_vente + '  CFA');
            Ext.getCmp('int_ACHAT').setValue(int_montant_achat + '  CFA');
        }

    },
    onchangeGrossiste: function () {

        let lg_GROSSISTE_ID = Ext.getCmp('lg_GROSSISTE_ID').getValue();
        let url_transaction = "../webservices/sm_user/suggerercde/ws_transaction.jsp?mode=";
        myAppController.ShowWaitingProcess();
        Ext.Ajax.request({
            url: url_transaction + 'changeGrossiste',
            timeout: 2400000,
            params: {
                lg_SUGGESTION_ORDER_ID: ref,
                lg_GROSSISTE_ID: lg_GROSSISTE_ID
            },
            success: function (response) {
                myAppController.StopWaitingProcess();
                var object = Ext.JSON.decode(response.responseText, false);
                if (object.errors_code == "0") {
                    if (object.answer_fusion == "true") {
                        Ext.MessageBox.confirm('Message', object.errors,
                                function (btn) {
                                    if (btn == 'yes') {
                                        Me_Window.doFusion(ref, lg_GROSSISTE_ID, url_transaction);
                                    }
                                });
                    } else {
                        myAppController.StopWaitingProcess();
                        Ext.MessageBox.alert('Error Message', object.errors);

                    }

                }

            },
            failure: function (response) {

                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);
                myAppController.StopWaitingProcess();
            }
        });
    },
    onIsGrossisteExist: function (valeur) {
        let url_transaction = "../webservices/sm_user/suggerercde/ws_transaction.jsp?mode=";
        ref = "0";
        Ext.Ajax.request({
            url: url_transaction + 'onIsGrossisteExist',
            params: {
                lg_GROSSISTE_ID: valeur
            },
            success: function (response) {
                var object = Ext.JSON.decode(response.responseText, false);
                if (object.errors_code == "0") {
                    ref = object.ref;
                }
                var OgridpanelSuggestionID = Ext.getCmp('gridpanelSuggestionID');

                OgridpanelSuggestionID.getStore().reload();

            },
            failure: function (response) {
                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);
            }
        });
    },
    doFusion: function (lg_SUGGESTION_ORDER_ID, lg_GROSSISTE_ID, url_transaction) {
        myAppController.ShowWaitingProcess();
        var internal_url = url_transaction + "doFusion";
        Ext.Ajax.request({
            url: internal_url,
            timeout: 2400000,
            params: {
                lg_SUGGESTION_ORDER_ID: lg_SUGGESTION_ORDER_ID,
                lg_GROSSISTE_ID: lg_GROSSISTE_ID
            },
            success: function (response) {
                myAppController.StopWaitingProcess();
                var object = Ext.JSON.decode(response.responseText, false);
                if (object.success == "0") {
                    Ext.MessageBox.alert('Error Message', object.errors);

                } else {
                    Ext.MessageBox.alert('Confirmation', object.errors);
                    Me_Window.onbtncancel();
                }
            },
            failure: function (response) {
                myAppController.StopWaitingProcess();
                var object = Ext.JSON.decode(response.responseText, false);
                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);
            }
        });
    },
    onDetailClick: function (grid, rowIndex) {
        const rec = grid.getStore().getAt(rowIndex);
        new testextjs.view.configmanagement.famille.action.detailArticleOther({
            odatasource: rec.get('lg_FAMILLE_ID'),
            parentview: this,
            mode: "detail",
            titre: "Detail sur l'article [" + rec.get('str_FAMILLE_NAME') + "]"
        });
    },

    onQtyDetail: function (grid, rowIndex) {
        const rec = grid.getStore().getAt(rowIndex);
        Ext.Ajax.request({
            method: 'GET',
            headers: {'Content-Type': 'application/json'},
            url: '../api/v1/suggestion/qty-detail/' + rec.get('lg_FAMILLE_ID'),
            success: function (response, options) {
                var result = Ext.JSON.decode(response.responseText, true);
                if (result.success) {
                    var stock = result.stock;
                    var form = Ext.create('Ext.window.Window',
                            {
                                autoShow: true,
                                height: 200,
                                width: 300,
                                modal: true,
                                title: 'STOCK DETAIL',
                                closeAction: 'hide',
                                closable: true,
                                maximizable: false,
                                layout: {
                                    type: 'fit'

                                },
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
                                                iconCls: 'cancelicon',
                                                handler: function (btn) {
                                                    form.destroy();
                                                },
                                                text: 'FERMER'

                                            }
                                        ]
                                    }
                                ],
                                items: [
                                    {
                                        xtype: 'fieldset',
                                        bodyPadding: 50,
                                        defaults: {
                                            anchor: '100%'
                                        },
                                        collapsible: false,

                                        items: [
                                            {
                                                xtype: 'displayfield',
                                                margin: '40 9 0 0',
                                                fieldLabel: 'STOCK DETAIL',
                                                value: stock,
                                                renderer: function (v) {
                                                    return Ext.util.Format.number(v, '0,000.');
                                                },
                                                fieldStyle: "color:blue;font-weight:800;"


                                            }
                                        ]
                                    }

                                ]
                            });
                }
            }
        });

    },

    onbtndetail: function () {
        new testextjs.view.configmanagement.famille.action.detailArticleOther({
            odatasource: Ext.getCmp('lg_FAMILLE_ID_VENTE').getValue(),
            parentview: this,
            mode: "detail",
            titre: "Detail sur l'article [" + Ext.getCmp('str_NAME').getValue() + "]"
        });
    },
    onbtncancel: function () {

        const xtype = "i_sugg_manager";
        testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "", "", "");
    },
    onRemoveClick: function (grid, rowIndex) {
        const rec = grid.getStore().getAt(rowIndex);
        const idSugg = rec.get('lg_SUGGESTION_ORDER_ID');
        Ext.Ajax.request({
            method: 'DELETE',

            url: '../api/v1/suggestion/item/' + rec.get('lg_SUGGESTION_ORDER_DETAILS_ID'),

            success: function (response) {
                grid.getStore().reload();
                Me_Window.getSuggestionAmount(idSugg);

            },
            failure: function (response) {
                Ext.MessageBox.alert('Error Message', response.responseText);
            }
        });
    },
    onSelectionChange: function (model, records) {
        const rec = records[0];
        if (rec) {
            this.getForm().loadRecord(rec);
        }
    },
    onbtnaddArticle: function () {
        new testextjs.view.configmanagement.famille.action.add2({
            odatasource: "",
            parentview: this,
            mode: "create",
            titre: "Ajouter un nouvel article",
            type: "commande"
        });
    },
    onRechClick: function () {
        const val = Ext.getCmp('rechercherDetail');
        Ext.getCmp('gridpanelSuggestionID').getStore().load({
            params: {
                search_value: val.getValue()
            }
        });
    },

    getSuggestionAmount: function (id) {

        Ext.Ajax.request({
            method: 'GET',
            headers: {'Content-Type': 'application/json'},
            url: '../api/v1/suggestion/amount/' + id,
            success: function (response, options) {
                const data = Ext.JSON.decode(response.responseText, true);
                if (data.montantAchat === 0) {
                    Me_Window.onbtncancel();

                }
                Me_Window.updateAmountFields(data);

            }
        });

    }
});
