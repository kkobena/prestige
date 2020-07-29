

/* global Ext, my_view_title, url_services_pdf_ticket */

url_services_data_detail_facture_tiers_payant_bordereau = '../webservices/sm_user/bordereau/ws_data_detail_tiers_payant_bordereau.jsp';
//var url_services_data_tiers_payant = '../webservices/tierspayantmanagement/tierspayant/ws_data.jsp';
var url_services_data_tiers_payant = '../webservices/tierspayantmanagement/tierspayant/ws_data.jsp';
var url_services_data_type_facture = '../webservices/';



var Oview;


var Me;
var Omode;
var ref;
var famille_id_search;
var in_total_vente;
var int_total_formated;
var int_montant_vente;
var int_montant_achat;
var LaborexWorkFlow_facture;
Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.sm_user.editbordereau.action.addBordereau', {
//    extend: 'Ext.form.Panel',
    extend: 'Ext.window.Window',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.form.*',
        'Ext.layout.container.Column',
        'testextjs.model.Famille',
        'testextjs.model.Grossiste',
        'testextjs.model.OrderDetail',
        'testextjs.controller.LaborexWorkFlow',
        'Ext.ux.CheckColumn',
        'Ext.selection.CheckboxModel',
        'testextjs.model.DossierFacture'

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
    xtype: 'addBordereau',
    id: 'addBordereauID',
    //frame: true,
    title: "Création d'un un Bordereau",
    bodyPadding: 5,
    layout: 'column',
    initComponent: function () {

        Oview = this.getParentview();


        Omode = this.getMode();
        Me = this;
        var itemsPerPage = 20;
        famille_id_search = "";
        in_total_vente = 0;
        int_total_formated = 0;
//        ref = this.getNameintern();
        titre = this.getTitre();
//        ref = this.getNameintern();
        LaborexWorkFlow_facture = Ext.create('testextjs.controller.LaborexWorkFlow', {});
        url_services_data_detail_facture = '../webservices/commandemanagement/orderdetail/ws_data.jsp?lg_ORDER_ID=' + ref;

        var storetypefacture = new Ext.data.Store({
            model: 'testextjs.model.TypeFacture',
            pageSize: itemsPerPage,
            // autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_type_facture,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            },
            autoLoad: true

        });

        var store_fournisseur = new Ext.data.Store({
            model: 'testextjs.model.Facture',
            pageSize: itemsPerPage,
            remoteFilter: true,
            proxy: {
                type: 'ajax',
                url: url_services_data_tiers_payant,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            },
            autoLoad: true

        });
        store_detail_tiers_payant = new Ext.data.Store({
            model: 'testextjs.model.Facture',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_detail_facture_tiers_payant_bordereau,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }



        });

        /*
        var str_NAME = new Ext.form.field.Text({
            xtype: 'textfield',
            fieldLabel: 'Nom',
            name: 'str_NAME',
            emptyText: 'NOM DU BORDEREAU',
            id: 'str_BORDEREAU_NAME',
            fieldStyle: 'color: green;'
        });
        */

        var str_BORDEREAU_ID = new Ext.form.field.Text({
            xtype: 'textfield',
            fieldLabel: 'ID Bordereau',
            emptyText: 'IDENTIFIANT DU BORDEREAU',
            name: 'str_BORDEREAU_ID',
            id: 'str_BORDEREAU_ID'
                    //fieldStyle: 'color: green;'
        });

        var str_BORDEREAU_CODE = new Ext.form.field.Text({
            xtype: 'textfield',
            fieldLabel: 'CODE BOREDERAU',
            emptyText: 'CODE BORDEREAU',
            name: 'str_BORDEREAU_CODE',
            id: 'str_BORDEREAU_CODE'
                    //fieldStyle: 'color: green;'
        });

        var str_BORDEREAU_BANK_NAME = new Ext.form.field.Text({
            xtype: 'textfield',
            fieldLabel: 'Banque',
            emptyText: 'NOM DE LA BANQUE',
            name: 'str_BORDEREAU_BANK_NAME',
            id: 'str_BORDEREAU_BANK_NAME',
            fieldStyle: 'color: green;'
        });

        /*   var int_NBR_DOSSIER = new Ext.form.field.Display(
         {
         xtype: 'displayfield',
         fieldLabel: 'Nombre de Dossier ::',
         //                    labelWidth: 140,
         //                    width: 145, 
         name: 'int_Nbr_Dossier',
         id: 'int_Nbr_Dossier',
         //fieldStyle: "color:blue;",
         margin: '0 15 0 0',
         value: "0"
         });*/

        var int_MONTANT_BORDEREAU = new Ext.form.field.Number(
                {
                    fieldLabel: 'Montant Total ',
                    name: 'int_MONTANT',
                    id: 'int_MONTANT_BORDEREAU',
                    emptyText: 'MONTANT DU BORDEREAU',
                    minValue: 0
                });




        var dt_TRANSACTION_DATE = new Ext.form.field.Date({
            fieldLabel: 'Date du:',
            allowBlank: false,
            format: 'd/m/Y',
            submitFormat: 'Y-m-d',
            name: 'dt_TRANSACTION_DATE',
            id: 'dt_TRANSACTION_DATE_ID',
            minValue: new Date(),
            emptyText: 'Date de la trasaction',
            listeners: {
                'change': function (me) {

                }
            }
        });

        var clientStore = new Ext.data.Store({
            model: 'testextjs.model.Client',
            pageSize: 20,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../webservices/sm_user/clients/ws_data.jsp?method=list',
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });


        var int_nb_FACTURE = new Ext.form.field.Number({
            fieldLabel: 'Nbre de Factures',
            id: 'int_nb_FACTURE',
            name: 'int_nb_FACTURE',
            emptyText: 'Nombre de Factures',
            minValue: 0
        });


        var dbl_MONTANT_RESTANT = new Ext.form.field.Number({
            fieldLabel: 'Montant Restant',
            id: 'dbl_MONTANT_RESTANT',
            name: 'dbl_MONTANT_RESTANT',
            minValue: 0,
            emptyText: 'MONTANT RESTANT, A PAYER',
            maxValue: Ext.getCmp('int_MONTANT_BORDEREAU').getValue()
        });

        var dbl_MONTANT_PAYE = new Ext.form.field.Number({
            fieldLabel: 'Montant Payé',
            id: 'dbl_MONTANT_PAYE',
            name: 'dbl_MONTANT_PAYE',
            emptyText: 'Montant payé',
            minValue: 0,
            maxValue: Ext.getCmp('int_MONTANT_BORDEREAU').getValue()

        });

        var str_STATUT = new Ext.form.field.Text({
            fieldLabel: 'Statut ',
            name: 'str_STATUT',
            id: 'str_STATUT',
            emptyText: 'Status du bordereau'

        });

        var type_store = Ext.create('Ext.data.Store', {
            autoLoad: true,
            fields: ['name', 'value'],
            data: [{"name": "Carte Bancaire", "value": "CB"},
                {"name": "Chèque P", "value": "CP"},
                {"name": "Chèque H", "value": "CH"}]
        });


        var str_CLIENT_NAME = new Ext.form.field.ComboBox({
            fieldLabel: 'Client',
            id: 'clientBox',
            emptyText: 'Choisir un client',
            store: clientStore,
            pageSize: 10,
            valueField: 'lg_CLIENT_ID',
            displayField: 'str_FULLNAME',
            minChars: 3,
            queryMode: 'remote',
            typeAhead: true,
            enableKeyEvents: true,
            listConfig: {
                getInnerTpl: function () {
                    return '<span style="width:100px;display:inline-block;">{str_FIRST_NAME}</span>{str_LAST_NAME}';
                }
            },
            listeners: {
                specialKey: function (field, e, options) {
                    if (e.getKey() === e.ENTER) {

                    }
                    if (e.getKey() === e.BACKSPACE || e.getKey() === 46) {
                        /* if (field.getValue().length == 1) { // a decommenter si demande de reload toute la liste des produits
                         field.getStore().load();
                         }*/
                    }
                },
                select: function (cmp) {
                    //var record = cmp.getStore().findRecord('lg_CLIENT_ID', this.getValue());
                    var clientID = cmp.getValue();
                    //str_NAME = record.get('str_NAME');
                    Me.getFacturesForClient(clientID);

                },
                render: function (cmp) {
                    cmp.getEl().on('keypress', function (e) {
                        if (e.getKey() === e.ENTER) {
                            Ext.getCmp('productSearchFieldID').setValue("");
                            //Me.processFocusAfterProductSelection();
                        }
                    });
                }

            }
        }

        );

        var form = new Ext.form.Panel({
            bodyPadding: 10,
            fieldDefaults: {
                labelAlign: 'right',
                labelWidth: 215,
                msgTarget: 'side'
            },
            items: [
                {
                    xtype: 'fieldset',
                    title: 'Information Générales du Bordereau',
                    defaultType: 'textfield',
                    collapsible: true,
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        str_BORDEREAU_ID, str_BORDEREAU_CODE, int_nb_FACTURE, int_MONTANT_BORDEREAU
                    ]
                },
                {
                    xtype: 'fieldset',
                    collapsible: true,
                    title: 'Informations de la transaction',
                    defaultType: 'textfield',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        str_CLIENT_NAME,
                        dbl_MONTANT_RESTANT,
                        dbl_MONTANT_PAYE, str_STATUT, dt_TRANSACTION_DATE,
                        str_BORDEREAU_BANK_NAME
                    ]
                }]

        });

        var win = new Ext.window.Window({
            autoShow: true,
            modal: true,
            title: this.getTitre(),
            width: 700,
            autoHeight: true,
            minWidth: 300,
            minHeight: 200,
            layout: 'fit',
            plain: true,
            items: form,
            buttons: [{
                    text: 'Créer le bordereau',
                    handler: Me.onbtnsave
                }, {
                    text: 'Annuler',
                    handler: function () {
                        win.close();
                    }
                }]
        });


        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        });




    },
    getFacturesForClient: function (clientID) {
        console.log('str_CUSTOMER: ', clientID);
        Ext.Ajax.request({
            url: '../webservices/sm_user/facturation/ws_facture_for_customer_data.jsp',
            params: {
                str_CUSTOMER: clientID
            },
            success: function (response) {
                var responseJSON = Ext.JSON.decode(response.responseText);
                console.log(responseJSON);
            },
            failure: function (error) {
                console.error(error);
            }
        });
    },
    onbtnsave: function () {
        var lg_Bordereau_ID = Ext.getCmp('str_BORDEREAU_ID').getValue();
        var int_nb_Facture = Ext.getCmp('int_nb_FACTURE').getValue();
        var str_Bordereau_Code = Ext.getCmp('str_BORDEREAU_CODE').getValue();
        var int_Montant_Bordereau = Ext.getCmp('int_MONTANT_BORDEREAU').getValue();
        var dbl_Montant_Restant = Ext.getCmp('dbl_MONTANT_RESTANT').getValue();
        var dbl_Montant_Paye = Ext.getCmp('dbl_MONTANT_PAYE').getValue();
        var str_statut = Ext.getCmp('str_STATUT').getValue();
        var str_bank_name = Ext.getCmp('str_BORDEREAU_BANK_NAME').getValue();
        var dt_transaction_date = Ext.getCmp('dt_TRANSACTION_DATE_ID').getValue();

        Me.createBordereau(lg_Bordereau_ID, str_Bordereau_Code,
                int_Montant_Bordereau, int_nb_Facture,
                dbl_Montant_Restant, dbl_Montant_Paye, str_statut);


    },
    createBordereau: function (lg_Bordereau_ID, str_Bordereau_Code,
            int_Montant_Bordereau, int_nb_Facture,
            int_Montant_Restant, int_Montant_Paye,
            str_statut) {


        alert("button submit clicked!");


    },
    loadStore: function () {
        Ext.getCmp('gridpanelIDBorderau').getStore().load({
            callback: this.onStoreLoad
        });
    },
    onStoreLoad: function () {

    },
    onbtncancel: function () {

        var xtype = "bordereaumanager";
        testextjs.app.getController('App').onLoadNewComponent(xtype, "", "");
    },
    checkIfGridIsEmpty: function () {
        var gridTotalCount = Ext.getCmp('gridpanelIDBorderau').getStore().getTotalCount();
        return gridTotalCount;
    },
    setTitleFrame: function (str_data) {
        /*this.title = this.title + " :: Ref " + str_data;
         ref = str_data;
         url_services_data_detail_facture = '../webservices/commandemanagement/orderdetail/ws_data.jsp?lg_ORDER_ID=' + ref;
         var OGrid = Ext.getCmp('gridpanelIDBorderau');
         url_services_data_detail_facture = '../webservices/commandemanagement/orderdetail/ws_data.jsp?lg_ORDER_ID=' + ref;
         OGrid.getStore().getProxy().url = url_services_data_detail_facture;
         OGrid.getStore().reload();*/
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
    DisplayAmount: function () {
        alert('amount');
    },
    onbtnadd: function () {
        var internal_url = "";
        if (ref === "") {
            ref = null;
        } else if (ref === undefined) {
            ref = null;
        }

        /*   if (Ext.getCmp('cmb_CUSTOMER_ID').getValue() === null) {
         
         Ext.MessageBox.alert('Error Message', 'Renseignez le Destinateur ');
         } else  {*/

        /*  alert(Ext.getCmp('cmb_CUSTOMER_ID').getValue());
         alert(Ext.getCmp('lg_TYPE_FACTURE_ID').getValue());
         alert(Ext.getCmp('dt_debut').getValue());
         alert(Ext.getCmp('dt_fin').getValue());*/
        dt_debut = Ext.Date.format(Ext.getCmp('dt_debut').getValue(), 'Y-m-d');
        dt_fin = Ext.Date.format(Ext.getCmp('dt_fin').getValue(), 'Y-m-d');
        // type_facture = Ext.getCmp('lg_TYPE_FACTURE_ID').getValue();
        str_CUSTOMER = Ext.getCmp('cmb_CUSTOMER_ID').getValue();



        url_services_data_detail_facture_tiers_payant_bordereau = '../webservices/sm_user/bordereau/ws_data_detail_tiers_payant_bordereau.jsp?lg_customer_id=' + str_CUSTOMER + '&dt_debut=' + dt_debut + '&dt_fin=' + dt_fin;
        var OGrid = Ext.getCmp('gridpanelIDBorderau');
        OGrid.getStore().getProxy().url = url_services_data_detail_facture_tiers_payant_bordereau;
        OGrid.getStore().reload();
        //int_montant_vente = Ext.util.Format.number(this.getOdatasource().PRIX_VENTE_TOTAL, '0,000.');
        //Ext.getCmp('int_VENTE').setValue(int_montant_vente + '  CFA');



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
    },
    CreateFacture: function (val) {
        dt_debut = Ext.Date.format(Ext.getCmp('dt_debut').getValue(), 'Y-m-d');
        dt_fin = Ext.Date.format(Ext.getCmp('dt_fin').getValue(), 'Y-m-d');
        //type_facture = Ext.getCmp('lg_TYPE_FACTURE_ID').getValue();
        str_CUSTOMER = Ext.getCmp('cmb_CUSTOMER_ID').getValue();
        url_services_data_detail_transaction = '../webservices/sm_user/bordereau/ws_transaction.jsp';


        str_mode = 'create';


        Ext.Ajax.request({
            url: url_services_data_detail_transaction,
            params: {
                lg_customer_id: str_CUSTOMER,
                //lg_type_facture: type_facture,
                dt_fin: dt_fin,
                dt_debut: dt_debut,
                mode: str_mode

            },
            success: function (response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                if (object.success === 0) {
                    Ext.MessageBox.alert('Error Message', 'impossible de creer le bordereau');
                    return;
                }
                if (object.success === 1) {
                    var xtype = "bordereaumanager";
                    testextjs.app.getController('App').onLoadNewComponent(xtype, "", "");
                }
                //Oview.getStore().reload();

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


