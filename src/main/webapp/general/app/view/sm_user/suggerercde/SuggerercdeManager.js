/* global Ext */

var url_services_transaction_suggerercde = '../webservices/sm_user/suggerercde/ws_transaction.jsp?mode=';

var url_services_pdf_liste_suggerercde = '../webservices/sm_user/suggerercde/ws_generate_pdf.jsp';



var Me_Window;
var Omode;
var ref;
var famille_id_search;
var in_total_vente;
var int_total_formated;
var titre;
var int_montant_vente;
var LaborexWorkFlow, myAppController;
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

        const AppController = testextjs.app.getController('App');
        ref = this.getNameintern();
        const store = Ext.create('testextjs.store.SearchStore');
        const storerepartiteur = new Ext.data.Store({
            model: 'testextjs.model.Grossiste',
            pageSize: 999,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../api/v1/grossiste/all',

                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                },
                timeout: 240000
            }

        });

        const store_details_sugg = new Ext.data.Store({
            model: 'testextjs.model.TSuggestionOrderDetails',
            pageSize: itemsPerPageGrid,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../api/v1/suggestion/list/items',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }
            }

        });
        const int_BUTOIR = new Ext.form.field.Display(
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
        const int_VENTE = new Ext.form.field.Display(
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
        const int_ACHAT = new Ext.form.field.Display(
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
                                    pageSize: 999,
                                    emptyText: 'Choisir un repartiteur...',
                                    listeners: {
                                        select: function (cmp) {
                                           
                                            if (titre === 'Suggerer une commande') {

                                                Me_Window.onchangeGrossiste();
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
                                        }
                                    },
                                    listeners: {
                                        select: function (cmp) {
                                            const value = cmp.getValue();
                                            const record = cmp.findRecord(cmp.valueField || cmp.displayField, value); //recupere la ligne de l'element selectionné

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
                                    renderer: Me_Window.columnRenderer
                                },
                                {
                                    text: 'LIBELLE',
                                    flex: 2.5,
                                    sortable: true,
                                    dataIndex: 'str_FAMILLE_NAME',
                                    renderer: Me_Window.columnRenderer
                                },
                                {
                                    text: 'PRIX.VENTE',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'lg_FAMILLE_PRIX_VENTE',
                                    align: 'right',
                                    renderer: Me_Window.numberColumnRenderer,

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

                                    align: 'right',
                                    renderer: Me_Window.numberColumnRenderer,
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
                                    align: 'right',
                                    renderer: Me_Window.numberColumnRenderer,
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
                                    align: 'right',
                                    renderer: Me_Window.numberColumnRenderer,
                                    dataIndex: 'int_PRIX_REFERENCE'
                                },
                                {
                                    text: 'STOCK',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'int_STOCK',
                                    align: 'right',
                                    renderer: Me_Window.numberColumnRenderer
                                },
                                {
                                    text: 'SEUIL',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'int_SEUIL',
                                    align: 'right',
                                    renderer: Me_Window.numberColumnRenderer,
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
                                    align: 'right',
                                    renderer: Me_Window.numberColumnRenderer,
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

                                    flex: 1,
                                    align: 'right',
                                    renderer: Me_Window.numberColumnRenderer
                                },
                                {
                                    header: AppController.getMonthToDisplay(1, currentMonth),
                                    dataIndex: 'int_VALUE1',
                                    align: 'right',

                                    renderer: Me_Window.numberColumnRenderer,
                                    flex: 0.7
                                },
                                {
                                    header: AppController.getMonthToDisplay(2, currentMonth),
                                    dataIndex: 'int_VALUE2',
                                    align: 'right',
                                    format: '0,000.',
                                    renderer: Me_Window.numberColumnRenderer,
                                    flex: 0.7
                                },
                                {
                                    header: AppController.getMonthToDisplay(3, currentMonth),
                                    dataIndex: 'int_VALUE3',
                                    align: 'right',
                                    renderer: Me_Window.numberColumnRenderer,
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
                                            query: '', orderId: ref
                                        };

                                        myProxy.setExtraParam('query', Ext.getCmp('rechercherDetail').getValue());
                                        myProxy.setExtraParam('orderId', ref);
                                    }

                                }
                            },
                            listeners: {
                                scope: this

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

            OgridpanelSuggestionID.getStore().load({
                params: {
                    orderId: ref,
                    query: null
                }
            });


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
                        OGrid.getStore().load({
                            params: {
                                orderId: suggId,
                                query: Ext.getCmp('rechercherDetail').getValue()
                            }
                        });

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
                    lg_FAMILLE_ID: Ext.getCmp('lg_FAMILLE_ID_VENTE').getValue(),
                    lg_SUGGESTION_ORDER_ID: ref,
                    lg_SUGGESTION_ORDER_DETAILS_ID: null,
                    lg_GROSSISTE_ID: Ext.getCmp('lg_GROSSISTE_ID').getValue(),
                    int_NUMBER: Ext.getCmp('int_QUANTITE').getValue()

                },
                success: function (response) {
                    testextjs.app.getController('App').StopWaitingProcess();
                    let object = Ext.JSON.decode(response.responseText, false);


                    if (object.errors_code == "0") {
                        Ext.MessageBox.alert('Error Message', object.errors);

                    } else {
                        ref = object.ref;
                        Me_Window.setTitleFrame(object.ref);
                        const OGrid = Ext.getCmp('gridpanelSuggestionID');
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
                    Ext.getCmp('str_NAME').setValue("");
                    Ext.getCmp('int_QUANTITE').setValue(1);
                    Ext.getCmp('btn_detail').disable();
                    Ext.getCmp('str_NAME').focus();


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

        myAppController.ShowWaitingProcess();
        Ext.Ajax.request({
            url: '../api/v1/suggestion/change-grossiste',
            method: 'GET',
            timeout: 2400000,
            params: {
                suggestionId: ref,
                grossisteId: lg_GROSSISTE_ID
            },
            success: function (response) {
                myAppController.StopWaitingProcess();
                const result = Ext.JSON.decode(response.responseText, true);
                if (result.response) {

                    Ext.MessageBox.confirm('Message', 'Une suggestion existe déjà pour ce grossiste. Voulez-vous les fusionner',
                            function (btn) {
                                if (btn == 'yes') {
                                    Me_Window.doFusion(ref, lg_GROSSISTE_ID);
                                }
                            });


                } else {
                    Ext.MessageBox.alert(' Message', "Operation effectuée avec succes");
                }

            },
            failure: function (response) {

                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);
                myAppController.StopWaitingProcess();
            }
        });
    },

    doFusion: function (lg_SUGGESTION_ORDER_ID, lg_GROSSISTE_ID) {
        myAppController.ShowWaitingProcess();

        Ext.Ajax.request({
             method: 'GET',
            url: '../api/v1/suggestion/merge-suggestion',
            timeout: 2400000,
            params: {
                suggestionId: lg_SUGGESTION_ORDER_ID,
                grossisteId: lg_GROSSISTE_ID
            },
            success: function (response) {
                myAppController.StopWaitingProcess();
                Me_Window.onbtncancel();
            },
            failure: function (response) {
                myAppController.StopWaitingProcess();

                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);
            }
        });
    },
    onDetailClick: function (grid, rowIndex) {
        const rec = grid.getStore().getAt(rowIndex);
        new testextjs.view.configmanagement.famille.action.detailArticle({
            odatasource: rec.data,
            produitId: rec.get('lg_FAMILLE_ID'),
            parentview: this,
            mode: "detail",
            titre: "Detail sur l'article [" + rec.get('str_DESCRIPTION') + "]"
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

                orderId: ref,
                query: val.getValue()
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

    },
    manageColor: function (r) {
        const produitStates = r?.data?.produitStates;
        const SUGGESTION = produitStates?.SUGGESTION;
        const COMMANDE_EN_COURS = produitStates?.COMMANDE_EN_COURS;
        const COMMANDE_PASSE = produitStates?.COMMANDE_PASSE;
        const ENTREE = produitStates?.ENTREE;
        if (SUGGESTION !== undefined && SUGGESTION > 1) {
            return 'background-color:#73C774;';
        }
        if (COMMANDE_EN_COURS !== undefined && COMMANDE_EN_COURS > 0) {
            return 'background-color:#5fa2dd;';
        }
        if (COMMANDE_PASSE !== undefined && COMMANDE_PASSE > 0) {
            return 'background-color:#f98012;';
        }
        if (ENTREE !== undefined && ENTREE > 0) {
            return 'background-color:#a62a3e;';
        }

        return '';

    },
    columnRenderer: function (v, m, r) {
        const st = Me_Window.manageColor(r);
        m.style = st;
        return v;

    },
    numberColumnRenderer: function (v, m, r) {
        const st = Me_Window.manageColor(r);
        m.style = st;
        return amountformat(v);
    }
});
