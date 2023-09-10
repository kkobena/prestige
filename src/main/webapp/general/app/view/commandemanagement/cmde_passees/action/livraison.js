var url_services_data_famille_select_order = '../webservices/sm_user/famille/ws_data_initial.jsp';
var url_services_transaction_order = '../webservices/commandemanagement/order/ws_transaction.jsp?mode=';
var url_services_data_grossiste_suggerer = '../webservices/configmanagement/grossiste/ws_data.jsp';
var url_services_data_orderdetails = '../webservices/commandemanagement/orderdetail/ws_data_passed.jsp?lg_ORDER_ID=';

var Me;
var Omode;
var ref;
var famille_id_search;
var in_total_vente;
var int_total_formated;
var lgGROSSISTEID;
var strREFORDER;

var str_REF;
var int_montant_achat;

Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.commandemanagement.cmde_passees.action.livraison', {
    extend: 'Ext.form.Panel',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.form.*',
        'Ext.layout.container.Column',
        'testextjs.model.Famille',
        'testextjs.model.Grossiste',
        'testextjs.model.OrderDetail'
    ],
    config: {
        odatasource: '',
        idCmde: '',
        parentview: '',
        mode: '',
        titre: '',
        plain: true,
        maximizable: true,
        closable: false,
        nameintern: ''
    },
    xtype: 'livraisonDetail',
    id: 'livraisonDetailID',
    frame: true,
    title: 'Rapprochement avec le Bon de Livraison',
    bodyPadding: 5,
    layout: 'column',
    initComponent: function() {


        Me = this;
        var itemsPerPage = 20;
        var itemsPerPageGrid = 10;

        int_montant_achat = 0;
        var int_montant_vente = 0;

        famille_id_search = "";
        in_total_vente = 0;
        int_total_formated = 0;

        ref = this.getNameintern();

        titre = this.getTitre();
        // ref = this.getNameintern();
        url_services_data_orderdetails = '../webservices/commandemanagement/orderdetail/ws_data_passed.jsp?lg_ORDER_ID=' + ref;

        var storerepartiteur = new Ext.data.Store({
            model: 'testextjs.model.Grossiste',
            pageSize: 999,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                     url: '../api/v1/grossiste/all',
//                url: url_services_data_grossiste_suggerer,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                },
                timeout: 240000
            },


        });

        var store_famille_sug = new Ext.data.Store({
            model: 'testextjs.model.Famille',
            pageSize: itemsPerPage,
            remoteFilter: true,
            proxy: {
                type: 'ajax',
                url: url_services_data_famille_select_dovente,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                },
                timeout: 240000
            },
            autoLoad: true

        });

        store_details_order = new Ext.data.Store({
            model: 'testextjs.model.OrderDetail',
            pageSize: itemsPerPageGrid,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_orderdetails + "&str_STATUT=passed",
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                },
                timeout: 240000
            }

        });

        var str_GROSSISTE_LIBELLE = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    fieldLabel: 'GROSSISTE ::',
                    labelWidth: 95,
                    name: 'str_GROSSISTE_LIBELLE',
                    id: 'str_GROSSISTE_LIBELLE',
                    fieldStyle: "color:blue;",
                    margin: '0 15 0 0',
                    value: "0"
                });


        var str_REF_ORDER = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    fieldLabel: 'REF CMD ::',
                    labelWidth: 95,
                    name: 'str_REF_ORDER',
                    id: 'str_REF_ORDER',
                    fieldStyle: "color:blue;",
                    margin: '0 15 0 0',
                    value: "0"
                });

        var dt_CREATED = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    fieldLabel: 'Date ::',
                    labelWidth: 95,
                    name: 'dt_CREATED',
                    id: 'dt_CREATED',
                    fieldStyle: "color:blue;",
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
            // items: ['rech_prod', 'gridpanelID'],
            items: [
                {
                    items: [{
                            xtype: 'fieldset',
                            title: 'Infos Commandes',
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
                                    items: [
//                                        str_GROSSISTE_LIBELLE,
                                        str_GROSSISTE_LIBELLE,
                                        str_REF_ORDER,
                                        dt_CREATED,
                                        int_ACHAT,
                                        int_VENTE

                                    ]
                                }]
                        }
                    ]

                },
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
                            columns: [{
                                    text: 'Details Commande Id',
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
                                }, {
                                    xtype: 'rownumberer',
                                    text: 'LG',
                                    width: 30,
                                    sortable: true/*,
                                     locked: true*/
                                },
                                {
                                    text: 'CIP',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'lg_FAMILLE_CIP'
                                },
                                //str_CODE_ARTICLE
                                {
                                    text: 'CODE ARTICLE',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'str_CODE_ARTICLE'
                                },
                                {
                                    text: 'DESIGNATION',
                                    flex: 3,
                                    sortable: true,
                                    dataIndex: 'lg_FAMILLE_NAME'
                                },
                                {
                                    text: 'PRIX.VENTE',
                                    flex: 1,
                                    sortable: true,
                                    align:'right',
                                    dataIndex: 'lg_FAMILLE_PRIX_VENTE',
                                    renderer: amountformat
                                },
                                {
                                    text: 'PRIX A. FACT',
                                    flex: 1,
                                    align:'right',
                                    sortable: true,
                                    dataIndex: 'lg_FAMILLE_PRIX_ACHAT',
                                    renderer: amountformat
                                },
                                {
                                    text: 'Q.CDE',
                                    flex: 0.8,
                                    sortable: true,
                                    align:'right',
                                    dataIndex: 'int_NUMBER',
                                    editor: {
                                        xtype: 'numberfield',
                                        minValue: 1,
                                        allowBlank: false,
                                        regex: /[0-9.]/
                                    }
                                },
//                                {
//                                    header: 'Q.LIVRE',
//                                    dataIndex: 'int_QTE_LIVRE',
//                                    flex: 1/*,
//                                     editor: {
//                                     xtype: 'numberfield',
//                                     allowBlank: false,
//                                     regex: /[0-9.]/
//                                     }*/
//                                },
                                {
                                    text: 'Q.A.LIVRER',
                                    flex: 0.8,
                                    align:'right',
                                    sortable: true,
                                    dataIndex: 'int_QTE_LIVRE'
                                }
                            ],
                            tbar: [{
                                    xtype: 'textfield',
                                    id: 'rechercherDetail',
                                    name: 'rechercherDetail',
                                    emptyText: 'Recherche',
                                    width: 300,
                                    listeners: {
                                        'render': function(cmp) {
                                            cmp.getEl().on('keypress', function(e) {
                                                if (e.getKey() === e.ENTER) {
                                                    Me.onRechClick();

                                                }
                                            });
                                        }
                                    }
                                }
                            ],
                            bbar: {
                                xtype: 'pagingtoolbar',
                                pageSize: itemsPerPageGrid,
                                store: store_details_order,
                                displayInfo: true,
                                plugins: new Ext.ux.ProgressBarPager()
                            },
                            listeners: {
                                scope: this,
                                // selectionchange: this.onSelectionChange
                            }
                        }

                    ]

                }
                ,
                {
                    xtype: 'toolbar',
                    ui: 'footer',
                    dock: 'bottom',
                    border: '0',
                    items: ['->',
                        ,
                                {
                                    text: 'CREER BL',
                                    id: 'btn_creerbl',
                                    iconCls: 'icon-clear-group',
                                    scope: this,
                                    // handler: this.onbtncreerbl
                                    handler: this.onCreateBLClick
                                },
                        {
                            text: 'Retour',
                            id: 'btn_cancel',
                            iconCls: 'icon-clear-group',
                            scope: this,
                            hidden: false,
                            disabled: false,
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

        if (titre == "Rapprochement avec le Bon de Livraison") {
            lgGROSSISTEID = this.getOdatasource().str_GROSSISTE_LIBELLE;
            strREFORDER = this.getOdatasource().str_REF_ORDER;
            str_REF = this.getOdatasource().lg_ORDER_ID;
            Ext.getCmp('str_GROSSISTE_LIBELLE').setValue(this.getOdatasource().str_GROSSISTE_LIBELLE);
            Ext.getCmp('str_REF_ORDER').setValue(this.getOdatasource().str_REF_ORDER);
            Ext.getCmp('dt_CREATED').setValue(this.getOdatasource().dt_CREATED);

            int_montant_achat = this.getOdatasource().PRIX_ACHAT_TOTAL;

            int_montant_vente = Ext.util.Format.number(this.getOdatasource().PRIX_VENTE_TOTAL, '0,000.');
            Ext.getCmp('int_VENTE').setValue(int_montant_vente + '  CFA');
            Ext.getCmp('int_ACHAT').setValue(Ext.util.Format.number(int_montant_achat, '0,000.') + '  CFA');
        }
//

        Ext.getCmp('gridpanelID').on('edit', function(editor, e) {


            var qte = Number(e.record.data.int_NUMBER);

            Ext.Ajax.request({
                url: '../webservices/commandemanagement/order/ws_transaction.jsp?mode=update',
                timeout: 240000,
                params: {
                    lg_ORDERDETAIL_ID: e.record.data.lg_ORDERDETAIL_ID,
                    lg_ORDER_ID: strREFORDER,
                    lg_FAMILLE_ID: e.record.data.lg_FAMILLE_ID,
                    int_NUMBER: qte,
                    str_STATUT: e.record.data.str_STATUT,
                    lg_FAMILLE_PRIX_VENTE: e.record.data.lg_FAMILLE_PRIX_VENTE,
                    lg_FAMILLE_PRIX_ACHAT: e.record.data.lg_FAMILLE_PRIX_ACHAT,
                    int_PAF: e.record.data.lg_FAMILLE_PRIX_ACHAT,
                    int_PRIX_REFERENCE: e.record.data.lg_FAMILLE_PRIX_VENTE,
                    lg_GROSSISTE_ID: lgGROSSISTEID
                },
                success: function(response)
                {
                    var object = Ext.JSON.decode(response.responseText, false);
                    if (object.success === 0) {
                        Ext.MessageBox.alert('Error Message', object.errors);
                        return;
                    }

                    e.record.commit();
                    var OGrid = Ext.getCmp('gridpanelID');
                    OGrid.getStore().reload();
int_montant_achat = object.PRIX_ACHAT_TOTAL;
                    Ext.getCmp('int_VENTE').setValue(Ext.util.Format.number(object.PRIX_VENTE_TOTAL, '0,000.') + '  CFA');
                    Ext.getCmp('int_ACHAT').setValue(Ext.util.Format.number(object.PRIX_ACHAT_TOTAL, '0,000.') + '  CFA');

                },
                failure: function(response)
                {
                    console.log("Bug " + response.responseText);
                    Ext.MessageBox.alert('Error Message', response.responseText);
                }
            });
        });
    },
    loadStore: function() {
        Ext.getCmp('gridpanelID').getStore().load({
            callback: this.onStoreLoad
        });
    },
    onStoreLoad: function() {

    },
    onAddProductClick: function(grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        var str_REF_LIVRAISON = Ext.getCmp('str_REF_LIVRAISON').getValue();
        //  alert("str_REF_LIVRAISON "+str_REF_LIVRAISON);
//        if (str_REF_LIVRAISON == "") {
//            Ext.MessageBox.alert('Erreur', 'Le champ reference vide. Veuillez reseigner svp!');
//            return;
//        }
        new testextjs.view.stockmanagement.etatstock.action.add({
            odatasource: rec.data,
            parentview: this,
            mode: "create",
            titre: "Ajout d'article [" + rec.get('lg_FAMILLE_NAME') + "]",
            // reference: str_REF_LIVRAISON
        });
    },
    onbtncancel: function() {

        var xtype = "";
        xtype = "orderpassmanager";
        testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "", "", "");

    },
    onCreateBLClick: function() {
        var refOreder = Ext.getCmp('str_REF_ORDER').getValue();
        new testextjs.view.commandemanagement.cmde_passees.action.add({
            idOrder: ref,
            odatasource: refOreder,
            montantachat: int_montant_achat,
            parentview: this,
            mode: "create",
            titre: "Creation bon de livraison"
        });
    },
    checkIfGridIsEmpty: function() {
        var gridTotalCount = Ext.getCmp('gridpanelID').getStore().getTotalCount();
        return gridTotalCount;
    },
    onValidatePreVenteClick: function() {
        var xtype = "";
        if (my_view_title === "by_cloturer_vente") {
            xtype = "cloturerventemanager";
        } else {

            xtype = "preenregistrementmanager";
        }
        var alias = 'widget.' + xtype;
        testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "", "", "");
    },
    getFamilleByName: function(str_famille_name) {
        var url_services_data_famille_select_dovente_search_suggerer = url_services_data_famille_select_order + "?search_value=" + str_famille_name;
        Ext.Ajax.request({
            url: url_services_data_famille_select_dovente_search_suggerer,
            params: {
            },
            success: function(response)
            {

                var object = Ext.JSON.decode(response.responseText, false);
                var OFamille = object.results[0];
                var int_CIP = OFamille.int_CIP;
                var int_EAN13 = OFamille.int_EAN13;
                Ext.getCmp('int_CIP').setValue(int_CIP);
                Ext.getCmp('int_EAN13').setValue(int_EAN13);
                famille_id_search = OFamille.lg_FAMILLE_ID;



                url_services_data_famille_select_dovente_search_suggerer = url_services_data_famille_select_order;

            },
            failure: function(response)
            {
                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);
            }
        });

    },
    setTitleFrame: function(str_data) {
        this.title = this.title + " :: Ref " + str_data;
        ref = str_data;
        url_services_data_orderdetails = '../webservices/commandemanagement/orderdetail/ws_data_passed.jsp?lg_ORDER_ID=' + ref;

        var OGrid = Ext.getCmp('gridpanelID');
        url_services_data_orderdetails = '../webservices/commandemanagement/orderdetail/ws_data_passed.jsp?lg_ORDER_ID=' + ref;

        OGrid.getStore().getProxy().url = url_services_data_orderdetails + "&str_STATUT=passed";
        OGrid.getStore().reload();


    },
    getFamilleByCip: function(str_famille_cip) {
        var url_services_data_famille_select_dovente_search_suggerer = url_services_data_famille_select_order + "?search_value=" + str_famille_cip;
        Ext.Ajax.request({
            url: url_services_data_famille_select_dovente_search_suggerer,
            timeout: 240000,
            params: {
            },
            success: function(response)
            {
                var object = Ext.JSON.decode(response.responseText, false);

                var OFamille = object.results[0];
                var str_NAME = OFamille.str_NAME;
                var int_EAN13 = OFamille.int_EAN13;


                Ext.getCmp('str_NAME').setValue(str_NAME);
                Ext.getCmp('int_EAN13').setValue(int_EAN13);
                famille_id_search = OFamille.lg_FAMILLE_ID;
                famille_price_search = Number(OFamille.int_PRICE);
                famille_qte_search = 1;

                url_services_data_famille_select_dovente_search_suggerer = url_services_data_famille_select_order;

            },
            failure: function(response)
            {

                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);
            }
        });

    },
    onfiltercheck: function() {
        var str_name = Ext.getCmp('str_NAME').getValue();
        var int_name_size = str_name.length;
        if (int_name_size < 4) {
            Ext.getCmp('btn_add').disable();


        }

    },
    DisplayTotal: function(int_price, int_qte) {
        var TotalAmount_final = 0;
        var TotalAmount_temp = int_qte * int_price;
        var TotalAmount = Number(TotalAmount_temp);
        return TotalAmount;
    },
    DisplayMonnaie: function(int_total, int_amount_recu) {
        var TotalMonnaie = 0;
        Ext.getCmp('int_REEL_RESTE').setValue(int_amount_recu - int_total);
        if (int_total <= int_amount_recu) {
            var TotalMonnaie_temp = int_amount_recu - int_total;
            TotalMonnaie = Number(TotalMonnaie_temp);
            return TotalMonnaie;
        } else {
            return null;
        }
        return TotalMonnaie;
    },
    onbtnadd: function() {
        var internal_url = "";

        if (ref === "") {
            ref = null;
        } else if (ref === undefined) {
            ref = null;
        }

        if (Ext.getCmp('lg_GROSSISTE_ID').getValue() === null) {

            Ext.MessageBox.alert('Error Message', 'Renseignez le Grossiste ');

        } else {


            Ext.Ajax.request({
                url: '../webservices/commandemanagement/order/ws_transaction.jsp?mode=create',
                params: {
                    lg_FAMILLE_ID: famille_id_search,
                    lg_ORDER_ID: ref,
                    lg_ORDERDETAIL_ID: null,
                    lg_GROSSISTE_ID: Ext.getCmp('lg_GROSSISTE_ID').getValue(),
                    int_NUMBER: 1


                },
                success: function(response)
                {
                    var object = Ext.JSON.decode(response.responseText, false);
                    ref = object.ref;
                    url_services_data_orderdetails = '../webservices/commandemanagement/orderdetail/ws_data_passed.jsp?lg_ORDER_ID=' + ref;

                    Me.setTitleFrame(object.ref);

                    Ext.getCmp('int_CIP').setValue("");
                    Ext.getCmp('int_EAN13').setValue("");
                    Ext.getCmp('str_NAME').setValue("");

                    var OGrid = Ext.getCmp('gridpanelID');
                    OGrid.getStore().reload();

                },
                failure: function(response)
                {
                    var object = Ext.JSON.decode(response.responseText, false);
                    console.log("Bug " + response.responseText);
                    Ext.MessageBox.alert('Error Message', response.responseText);
                }
            });

        }

    },
    onPdfClick: function() {
        var chaine = location.pathname;
        var reg = new RegExp("[/]+", "g");
        var tableau = chaine.split(reg);
        var sitename = tableau[1];
        var linkUrl = url_services_pdf_ticket + '?lg_PREENREGISTREMENT_ID=' + ref;
        window.open(linkUrl);

        var xtype = "";
        if (my_view_title === "by_cloturer_vente") {
            xtype = "ventemanager";
            testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "", "", "");
        } else {
            xtype = "preenregistrementmanager";
            testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "", "", "");
        }

    },
    changeRenderer: function(val) {
        if (val > 0) {
            return '<span style="color:green;">' + val + '</span>';
        } else if (val < 0) {
            return '<span style="color:red;">' + val + '</span>';
        }
        return val;
    },
    pctChangeRenderer: function(val) {
        if (val > 0) {
            return '<span style="color:green;">' + val + '%</span>';
        } else if (val < 0) {
            return '<span style="color:red;">' + val + '%</span>';
        }
        return val;
    },
    renderRating: function(val) {
        switch (val) {
            case 0:
                return 'A';
            case 1:
                return 'B';
            case 2:
                return 'C';
        }
    },
    onSelectionChange: function(model, records) {
        var rec = records[0];
        if (rec) {
            this.getForm().loadRecord(rec);
        }
    },
    onsplitovalue: function(Ovalue) {

        var int_ovalue;
        var string = Ovalue.split(" ");
        int_ovalue = string[0];

        return int_ovalue;

    },
    onRechClick: function() {
        var val = Ext.getCmp('rechercherDetail');
        Ext.getCmp('gridpanelID').getStore().load({
            params: {
                search_value: val.getValue()
            }
        }, url_services_data_orderdetails);
    }

});


