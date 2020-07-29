var url_services_data_famille_select_order = '../webservices/sm_user/famille/ws_data_initial.jsp';
var url_services_transaction_order = '../webservices/commandemanagement/order/ws_transaction.jsp?mode=';
var url_services_data_grossiste_suggerer = '../webservices/configmanagement/grossiste/ws_data.jsp';
var url_services_data_orderdetails = '../webservices/commandemanagement/orderdetail/ws_data_suivi.jsp?lg_ORDER_ID=';

var url_services_transaction_order = '../webservices/commandemanagement/order/ws_transaction.jsp?mode=';

var Me;
var Omode;
var ref;
var famille_id_search;
var in_total_vente;
var int_total_formated;


Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.commandemanagement.suivi_order.action.add', {
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
        parentview: '',
        mode: '',
        titre: '',
        plain: true,
        maximizable: true,
        closable: true,
        nameintern: ''
    },
    xtype: 'ordermanagermanageIT',
    id: 'ordermanagermanageITID',
    frame: true,
    title: 'Ajouter une ligne de commande ',
    bodyPadding: 5,
    layout: 'column',
    initComponent: function () {


        Me = this;
        var itemsPerPage = 20;
        famille_id_search = "";
        in_total_vente = 0;
        int_total_formated = 0;

        ref = this.getNameintern();

        titre = this.getTitre();
        ref = this.getNameintern();
        url_services_data_orderdetails = '../webservices/commandemanagement/orderdetail/ws_data_suivi.jsp?lg_ORDER_ID=' + ref;

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
            },
            autoLoad: true

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
                }
            },
            autoLoad: true

        });

        store_details_order = new Ext.data.Store({
            model: 'testextjs.model.OrderDetail',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_orderdetails,
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
                    fieldStyle: "color:blue;",
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
                    fieldStyle: "color:blue;",
                    margin: '0 15 0 0',
                    value: "0"
                });

        this.cellEditing = new Ext.grid.plugin.CellEditing({
            clicksToEdit: 1
        });

        Ext.apply(this, {
            width: 950,
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
            items: ['rech_prod', 'gridpanelID'],
            items: [
                {
                    items: [{
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
                                            margin: '0 15 0 0',
                                            id: 'lg_GROSSISTE_ID',
                                            store: storerepartiteur,
                                            valueField: 'lg_GROSSISTE_ID',
                                            displayField: 'str_LIBELLE',
                                            typeAhead: true,
                                            queryMode: 'remote',
                                            emptyText: 'Choisir un repartiteur...'
                                        },
                                        int_BUTOIR,
                                        int_ACHAT,
                                        int_VENTE]
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
                            height: 400,
                            columns: [{
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
                                    xtype: 'rownumberer',
                                    text: 'LG',
                                    width: 45,
                                    sortable: true/*,
                                     locked: true*/
                                },
                                {
                                    text: 'CIP',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'lg_FAMILLE_CIP'
                                },
                                {
                                    text: 'DESIGNATION',
                                    flex: 2,
                                    sortable: true,
                                    dataIndex: 'lg_FAMILLE_NAME'
                                },
                                {
                                    text: 'QTE.STOCK',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'lg_FAMILLE_QTE_STOCK'
                                },
                                {
                                    text: 'SEUIL',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'int_SEUIL'
                                },
                                {
                                    text: 'PRIX.ACHAT',
                                    flex: 1,
                                    sortable: true,
                                    renderer: amountformat,
                                    align: 'right',
                                    dataIndex: 'lg_FAMILLE_PRIX_ACHAT'
                                },
                                {
                                    text: 'PRIX.VENTE',
                                    flex: 1,
                                    sortable: true,
                                    renderer: amountformat,
                                    align: 'right',
                                    dataIndex: 'lg_FAMILLE_PRIX_VENTE'
                                },
                                {
                                    header: 'Q.CDE',
                                    dataIndex: 'int_NUMBER',
                                    align: 'right',
                                    flex: 1
                                },
                                {
                                    text: 'MONTANT',
                                    flex: 1,
                                    renderer: amountformat,
                                    align: 'right',
                                    sortable: true,
                                    dataIndex: 'int_PRICE'
                                }
//                                {
//                                    xtype: 'actioncolumn',
//                                    width: 30,
//                                    sortable: false,
//                                    menuDisabled: true,
//                                    items: [{
//                                            icon: 'resources/images/icons/fam/delete.png',
//                                            tooltip: 'Delete',
//                                            scope: this,
//                                            handler: this.onRemoveClick
//                                        }]
//                                }
                            ],
                            bbar: {
                                xtype: 'pagingtoolbar',
                                pageSize: 10,
                                store: store_details_order,
                                displayInfo: true,
                                plugins: new Ext.ux.ProgressBarPager()
                            },
                            listeners: {
                                scope: this,
                                selectionchange: this.onSelectionChange
                            }
                        }
                        ,
                        {
                            xtype: 'toolbar',
                            ui: 'footer',
                            dock: 'bottom',
                            border: '0',
                            items: ['->',
                                {
                                    text: 'Annuler',
                                    id: 'btn_cancel',
                                    iconCls: 'icon-clear-group',
                                    scope: this,
                                    hidden: false,
                                    //disabled: true,
                                    handler: this.onbtncancel
                                }
//                        , 
//                        {
//                            text: 'Cloturer',
//                            id: 'btn_loturer',
//                            iconCls: 'icon-clear-group',
//                            scope: this,
//                            hidden: true,
//                            disabled: true,
//                            handler: this.onbtncloturer
//                        }
                            ]
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

        if (titre == "Detail_commande") {

            Ext.getCmp('lg_GROSSISTE_ID').setValue(this.getOdatasource().lg_GROSSISTE_ID);

        }

    },
    loadStore: function () {
        Ext.getCmp('gridpanelID').getStore().load({
            callback: this.onStoreLoad
        });
    },
    onStoreLoad: function () {

    },
    onbtnaddclt: function () {
        var mybtntitle = Ext.getCmp('btn_add_clt').getText();
        new testextjs.view.commandemanagement.dovente.action.addclt({
            obtntext: mybtntitle,
            odatasource: ref,
            nameintern: ref_vente,
            parentview: this,
            mode: "create",
            titre: "Choisir Client"
        });
    },
    onbtncancel: function () {

        var xtype = "";
        xtype = "suiviordermanager";
        testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "", "", "");

    },
    checkIfGridIsEmpty: function () {
        var gridTotalCount = Ext.getCmp('gridpanelID').getStore().getTotalCount();
        return gridTotalCount;
    },
    onValidatePreVenteClick: function () {
        var xtype = "";
        if (my_view_title === "by_cloturer_vente") {
            xtype = "cloturerventemanager";
        } else {

            xtype = "preenregistrementmanager";
        }
        var alias = 'widget.' + xtype;
        testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "", "", "");
    },
    getFamilleByName: function (str_famille_name) {
        var url_services_data_famille_select_dovente_search_suggerer = url_services_data_famille_select_order + "?search_value=" + str_famille_name;
        Ext.Ajax.request({
            url: url_services_data_famille_select_dovente_search_suggerer,
            params: {
            },
            success: function (response)
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
            failure: function (response)
            {
                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);
            }
        });

    },
    setTitleFrame: function (str_data) {
        this.title = this.title + " :: Ref " + str_data;
        ref = str_data;
        url_services_data_orderdetails = '../webservices/commandemanagement/orderdetail/ws_data_suivi.jsp?lg_ORDER_ID=' + ref;

        var OGrid = Ext.getCmp('gridpanelID');
        url_services_data_orderdetails = '../webservices/commandemanagement/orderdetail/ws_data_suivi.jsp?lg_ORDER_ID=' + ref;

        OGrid.getStore().getProxy().url = url_services_data_orderdetails;
        OGrid.getStore().reload();


    },
    getFamilleByCip: function (str_famille_cip) {
        var url_services_data_famille_select_dovente_search_suggerer = url_services_data_famille_select_order + "?search_value=" + str_famille_cip;
        Ext.Ajax.request({
            url: url_services_data_famille_select_dovente_search_suggerer,
            params: {
            },
            success: function (response)
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
            failure: function (response)
            {

                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);
            }
        });

    },
    onfiltercheck: function () {
        var str_name = Ext.getCmp('str_NAME').getValue();
        var int_name_size = str_name.length;
        if (int_name_size < 4) {
            Ext.getCmp('btn_add').disable();


        }

    },
    DisplayTotal: function (int_price, int_qte) {
        var TotalAmount_final = 0;
        var TotalAmount_temp = int_qte * int_price;
        var TotalAmount = Number(TotalAmount_temp);
        return TotalAmount;
    },
    DisplayMonnaie: function (int_total, int_amount_recu) {
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
    onbtnadd: function () {
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
                success: function (response)
                {
                    var object = Ext.JSON.decode(response.responseText, false);
                    ref = object.ref;
                    url_services_data_orderdetails = '../webservices/commandemanagement/orderdetail/ws_data_suivi.jsp?lg_ORDER_ID=' + ref;

                    Me.setTitleFrame(object.ref);

                    Ext.getCmp('int_CIP').setValue("");
                    Ext.getCmp('int_EAN13').setValue("");
                    Ext.getCmp('str_NAME').setValue("");

                    var OGrid = Ext.getCmp('gridpanelID');
                    OGrid.getStore().reload();

                },
                failure: function (response)
                {
                    var object = Ext.JSON.decode(response.responseText, false);
                    console.log("Bug " + response.responseText);
                    Ext.MessageBox.alert('Error Message', response.responseText);
                }
            });

        }

    },
    onbtncloturer: function () {

        var internal_url = "";
        var task = "";

        if (Ext.getCmp('lg_TYPE_REGLEMENT_ID').getValue() === null) {
            Ext.MessageBox.alert('Attention', 'Selectionnez un type de reglement');
            return;
        }

        if (Ext.getCmp('lg_TYPE_VENTE_ID').getValue() === null) {
            Ext.MessageBox.alert('Attention', 'Selectionnez un type de vente');
            return;
        }


        if (Ext.getCmp('lg_TYPE_VENTE_ID').getValue() === "3") {
            if (str_REF_CLIENT.getValue() === "0") {
                Ext.MessageBox.alert('Attention', 'Selectionnez un client');
                return;
            }
        }

        if (Ext.getCmp('lg_TYPE_VENTE_ID').getValue() === "CARNET") {
            type_vente_id = "3";
            client_id_clicked = cust_account_id;
        } else if (Ext.getCmp('lg_TYPE_VENTE_ID').getValue() === "ASSURANCE_MUTUELLE") {
            type_vente_id = "2";
            client_id_clicked = ref_compte_clt;
        } else {

            type_vente_id = "1";
        }


        Ext.Ajax.request({
            url: '../webservices/commandemanagement/detailsvente/ws_transaction.jsp?mode=cloturer',
            params: {
                int_TOTAL_VENTE_RECAP: in_total_vente,
                lg_PREENREGISTREMENT_ID: ref,
                lg_TYPE_REGLEMENT_ID: Ext.getCmp('lg_TYPE_REGLEMENT_ID').getValue(),
                int_AMOUNT_RECU: Number(Ext.getCmp('int_AMOUNT_RECU').getValue().replace(".", "")),
                int_AMOUNT_REMIS: int_monnaie,
                lg_COMPTE_CLIENT_ID: client_id_clicked,
                lg_TYPE_VENTE_ID: type_vente_id,
                lg_TIERS_PAYANT_ID: tp_id
            },
            success: function (response)
            {
                var object = Ext.JSON.decode(response.responseText, false);



                var OGrid = Ext.getCmp('gridpanelID');
                OGrid.getStore().reload();
                Ext.getCmp('int_CIP').setValue("");
                Ext.getCmp('int_EAN13').setValue("");
                Ext.getCmp('str_NAME').setValue("");

                in_total_vente = Number(object.total_vente);
                Ext.getCmp('int_TOTAL_VENTE').setValue(0);
                Ext.getCmp('int_TOTAL_VENTE_RECAP').setValue(0);

                int_total_product = Number(object.int_total_product);
                Ext.getCmp('int_TOTAL_PRODUIT').setValue(0 + '  Produit(s)');
                Ext.getCmp('int_NB_PROD_RECAP').setValue(0 + '  Produit(s)');



                Ext.MessageBox.confirm('Message',
                        'confirm l impression du ticket',
                        function (btn) {
                            if (btn === 'yes') {

                                Me.onPdfClick();

                                return;
                            } else {
                                var xtype = "";
                                if (my_view_title === "by_cloturer_vente") {
                                    xtype = "ventemanager";
                                    testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "", "", "");
                                } else {
                                    xtype = "preenregistrementmanager";
                                    testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "", "", "");
                                }

                            }
                        });


                if (object.errors_code === "0") {
                    Ext.MessageBox.alert('Error Message', object.errors);
                    return null;
                }



            },
            failure: function (response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);
            }
        });

    },
    onbtndevis: function () {
        var internal_url = "";

        Ext.Ajax.request({
            url: '../webservices/commandemanagement/detailsvente/ws_transaction.jsp?mode=devis',
            params: {
                lg_PREENREGISTREMENT_ID: ref
            },
            success: function (response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                var OGrid = Ext.getCmp('gridpanelID');
                OGrid.getStore().reload();
                Ext.getCmp('int_CIP').setValue("");
                Ext.getCmp('int_EAN13').setValue("");
                Ext.getCmp('str_NAME').setValue("");

                in_total_vente = Number(object.total_vente);
                Ext.getCmp('int_TOTAL_VENTE').setValue(0);
                Ext.getCmp('int_TOTAL_VENTE_RECAP').setValue(0);

                int_total_product = Number(object.int_total_product);
                Ext.getCmp('int_TOTAL_PRODUIT').setValue(0 + '  Produit(s)');
                Ext.getCmp('int_NB_PROD_RECAP').setValue(0 + '  Produit(s)');


                Ext.MessageBox.confirm('Message',
                        'confirm l impression du ticket',
                        function (btn) {
                            if (btn === 'yes') {

                                Me.onPdfClick();
                                return;
                            }
                        });



            },
            failure: function (response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);
            }
        });

    },
    onRemoveClick: function (grid, rowIndex) {
        Ext.MessageBox.confirm('Message',
                'confirm la suppresssion',
                function (btn) {
                    if (btn == 'yes') {
                        var rec = grid.getStore().getAt(rowIndex);
                        // alert(url_services_transaction_order + 'deleteDetail');

                        Ext.Ajax.request({
                            url: url_services_transaction_order + 'deleteDetail',
                            params: {
                                lg_ORDERDETAIL_ID: rec.get('lg_ORDERDETAIL_ID')
                            },
                            success: function (response)
                            {
                                var object = Ext.JSON.decode(response.responseText, false);
                                if (object.success === 0) {
                                    Ext.MessageBox.alert('Error Message', object.errors);
                                    return;
                                }
                                grid.getStore().reload();

                            },
                            failure: function (response)
                            {
                                var object = Ext.JSON.decode(response.responseText, false);

                                console.log("Bug " + response.responseText);
                                Ext.MessageBox.alert('Error Message', response.responseText);

                            }
                        });
                        return;
                    }
                });


    },
    onPdfClick: function () {
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
    changeRenderer: function (val) {
        if (val > 0) {
            return '<span style="color:green;">' + val + '</span>';
        } else if (val < 0) {
            return '<span style="color:red;">' + val + '</span>';
        }
        return val;
    },
    pctChangeRenderer: function (val) {
        if (val > 0) {
            return '<span style="color:green;">' + val + '%</span>';
        } else if (val < 0) {
            return '<span style="color:red;">' + val + '%</span>';
        }
        return val;
    },
    renderRating: function (val) {
        switch (val) {
            case 0:
                return 'A';
            case 1:
                return 'B';
            case 2:
                return 'C';
        }
    },
    onSelectionChange: function (model, records) {
        var rec = records[0];
        if (rec) {
            this.getForm().loadRecord(rec);
        }
    },
    onsplitovalue: function (Ovalue) {

        var int_ovalue;
        var string = Ovalue.split(" ");
        int_ovalue = string[0];

        return int_ovalue;

    }

});


