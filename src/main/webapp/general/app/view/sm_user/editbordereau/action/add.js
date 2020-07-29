

/* global Ext, my_view_title, url_services_pdf_ticket */

url_services_data_detail_facture_tiers_payant_bordereau = '../webservices/sm_user/bordereau/ws_data_detail_tiers_payant_bordereau.jsp';
//var url_services_data_tiers_payant = '../webservices/tierspayantmanagement/tierspayant/ws_data.jsp';
var url_services_data_tiers_payant = '../webservices/tierspayantmanagement/tierspayant/ws_data.jsp';
var url_services_data_type_facture = '../webservices/';




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

Ext.define('testextjs.view.sm_user.editbordereau.action.add', {
    extend: 'Ext.form.Panel',
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
    xtype: 'addeditbordereau',
    id: 'addeditbordereauID',
    //frame: true,
    title: 'Ajouter un Bordereau',
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

        var int_NBR_DOSSIER = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    fieldLabel: 'Nombre de Dossier ::',
                    labelWidth: 140,
                    name: 'int_Nbr_Dossier',
                    id: 'int_Nbr_Dossier',
                    fieldStyle: "color:blue;",
                    margin: '0 15 0 0',
                    value: "0"
                });
        var int_MONTANT = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    fieldLabel: 'Montant Total ::',
                    labelWidth: 95,
                    name: 'int_MONTANT',
                    id: 'int_MONTANT',
                    fieldStyle: "color:blue;",
                    margin: '0 15 0 0',
                    value: "0"
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
                padding: 0
            },
            defaults: {
                flex: 1
            },
            id: 'panelID',
            //items: ['rech_prod', 'gridpanelID'],
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
                                        //hideLabel: 'true'
                                    },
                                    items: [
                                        {
                                            xtype: 'combobox',
                                            fieldLabel: 'Bordereau de: ',
                                            allowBlank: false,
                                            name: 'cmb_CUSTOMER_ID',
                                            margin: '0 15 0 0',
                                            id: 'cmb_CUSTOMER_ID',
                                            store: store_fournisseur,
                                            valueField: 'lg_CUSTOMER_ID',
                                            displayField: 'str_LIBELLE',
                                            typeAhead: true,
                                            queryMode: 'remote',
                                            emptyText: 'Choisir...'

                                        }]
                                }]
                        }
                    ]

                },
                {
                    items: [{
                            xtype: 'fieldset',
                            //  title: 'Ajout Produit',
                            collapsible: true,
                            defaultType: 'textfield',
                            layout: 'anchor',
                            defaults: {
                                anchor: '100%'
                            },
                            items: [
                                {
                                    xtype: 'fieldcontainer',
                                    // fieldLabel: 'Produit',
                                    layout: 'hbox',
                                    combineErrors: true,
                                    defaultType: 'textfield',
                                    defaults: {
                                        // hideLabel: 'true'
                                    },
                                    items: [
                                        {
                                            xtype: 'datefield',
                                            fieldLabel: 'Du',
                                            id: 'dt_debut',
                                            name: 'dt_debut',
                                            submitFormat: 'Y-m-d',
                                            format: 'd/m/Y',
                                            default: new Date()
                                                    // limited to the current date or prior

                                        }, {
                                            xtype: 'datefield',
                                            fieldLabel: 'Au',
                                            id: 'dt_fin',
                                            name: 'dt_fin',
                                            submitFormat: 'Y-m-d',
                                            format: 'd/m/Y',
                                            default: new Date()
                                        }, {
                                            text: 'Rechercher',
                                            id: 'btn_add',
                                            margins: '0 0 0 6',
                                            xtype: 'button',
                                            handler: this.onbtnadd

                                        }]
                                }
                            ]
                        }
                    ]
                }, {
                    items: [{
                            xtype: 'fieldset',
                            //  title: 'Ajout Produit',
                            collapsible: true,
                            layout: 'hbox',
                            defaultType: 'textfield',
                            defaults: {
                                anchor: '100%'
                            },
                            items: [
                                int_MONTANT,
                                int_NBR_DOSSIER

                            ]
                        }
                    ]
                },
                {
                    xtype: 'fieldset',
                    id: 'detailfacturevente',
                    title: 'Detail(s) du Bordereau',
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
                            //  plugins: [this.cellEditing],

                            store: store_detail_tiers_payant,
                            height: 400,
                            columns: [
                                {
                                    header: 'lg_FACTURE_ID',
                                    dataIndex: 'lg_FACTURE_ID',
                                    hidden: true,
                                    flex: 1
                                }, {
                                    header: 'Code Facture',
                                    dataIndex: 'str_CODE_FACTURE',
                                    flex: 1

                                }, {
                                    header: 'Client',
                                    dataIndex: 'str_CUSTOMER_NAME',
                                    flex: 1
                                }, {
                                    header: 'Periode',
                                    dataIndex: 'str_PERIODE',
                                    flex: 2

                                }, {
                                    header: 'Montant',
                                    dataIndex: 'dbl_MONTANT_CMDE',
                                    flex: 1,
                                    renderer: amountformat,
                                    align: 'right'
                                }, {
                                    header: 'Montant Regler',
                                    //dataIndex: 'dbl_MONTANT_CMDE',
                                    flex: 1,
                                    renderer: amountformat,
                                    align: 'right'
                                }, {
                                    header: 'Montant Restant',
                                    // dataIndex: '',
                                    flex: 1,
                                    renderer: amountformat,
                                    align: 'right'
                                }, {
                                    header: 'Nombre de Dossier',
                                    //dataIndex: '',
                                    flex: 1,
                                    renderer: amountformat,
                                    align: 'right'
                                },
                                {
                                    header: 'Date',
                                    dataIndex: 'dt_CREATED',
                                    flex: 1

                                }
                            ],
                            //  selModel: selModel,
                            bbar: {
                                xtype: 'pagingtoolbar',
                                pageSize: 10,
                                store: store_detail_tiers_payant,
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
                                    text: 'Editer Bordereau',
                                    id: 'btn_create_facture',
                                    iconCls: 'icon-clear-group',
                                    scope: this,
                                    hidden: false,
                                    //disabled: true,
                                    handler: this.CreateFacture
                                }, {
                                    text: 'Annuler',
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

                }
            ]
        });
        this.callParent();
        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        });
        if (titre === "Modifier_detail_commande") {

            /*   Ext.getCmp('lg_GROSSISTE_ID').setValue(this.getOdatasource().lg_GROSSISTE_ID);
             int_montant_achat = Ext.util.Format.number(this.getOdatasource().PRIX_ACHAT_TOTAL, '0,000.');
             //  int_montant_vente = Ext.util.Format.number(this.getOdatasource().PRIX_VENTE_TOTAL, '0,000.');
             Ext.getCmp('int_VENTE').setValue(int_montant_vente + '  CFA');
             Ext.getCmp('int_ACHAT').setValue(int_montant_achat + '  CFA');*/
        }


        Ext.getCmp('gridpanelID').on('edit', function (editor, e) {


            var qte = Number(e.record.data.int_NUMBER);
            Ext.Ajax.request({
                url: '../webservices/commandemanagement/order/ws_transaction.jsp?mode=update',
                params: {
                    lg_ORDERDETAIL_ID: e.record.data.lg_ORDERDETAIL_ID,
                    lg_ORDER_ID: ref,
                    lg_FAMILLE_ID: e.record.data.lg_FAMILLE_ID,
                    lg_GROSSISTE_ID: Ext.getCmp('lg_GROSSISTE_ID').getValue(),
                    int_NUMBER: qte
                },
                success: function (response)
                {
                    var object = Ext.JSON.decode(response.responseText, false);
                    if (object.success === 0) {
                        Ext.MessageBox.alert('Error Message', object.errors);
                        return;
                    }

                    e.record.commit();
                    /*    var OGrid = Ext.getCmp('gridpanelID');
                     OGrid.getStore().reload();
                     Ext.getCmp('int_CIP').setValue("");
                     Ext.getCmp('int_EAN13').setValue("");
                     Ext.getCmp('str_NAME').setValue("");
                     int_montant_achat = Ext.util.Format.number(this.getOdatasource().PRIX_ACHAT_TOTAL, '0,000.');
                     int_montant_vente = Ext.util.Format.number(this.getOdatasource().PRIX_VENTE_TOTAL, '0,000.');
                     Ext.getCmp('int_VENTE').setValue(int_montant_vente + '  CFA');
                     Ext.getCmp('int_ACHAT').setValue(int_montant_achat + '  CFA');*/
                },
                failure: function (response)
                {
                    console.log("Bug " + response.responseText);
                    Ext.MessageBox.alert('Error Message', response.responseText);
                }
            });
        });
    },
    loadStore: function () {
        Ext.getCmp('gridpanelID').getStore().load({
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
        var gridTotalCount = Ext.getCmp('gridpanelID').getStore().getTotalCount();
        return gridTotalCount;
    },
    setTitleFrame: function (str_data) {
        /*this.title = this.title + " :: Ref " + str_data;
         ref = str_data;
         url_services_data_detail_facture = '../webservices/commandemanagement/orderdetail/ws_data.jsp?lg_ORDER_ID=' + ref;
         var OGrid = Ext.getCmp('gridpanelID');
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
        var OGrid = Ext.getCmp('gridpanelID');
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


