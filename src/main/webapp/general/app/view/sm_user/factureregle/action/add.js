//var url_services_data_famille_select_order = '../webservices/sm_user/famille/ws_data.jsp';
//var url_services_transaction_order = '../webservices/commandemanagement/order/ws_transaction.jsp?mode=';
var url_services_data_type_facture = '../webservices/sm_user/typefacture/ws_data.jsp';
var url_services_data_detail_facture = '../webservices/commandemanagement/orderdetail/ws_data.jsp?lg_ORDER_ID=';

url_services_data_detail_facture_tiers_payant = '../webservices/sm_user/facturation/ws_data_detail_tiers_payant.jsp';
var url_services_data_tiers_payant = '../webservices/tierspayantmanagement/tierspayant/ws_data.jsp';
var url_services_data_fournisseur = '../webservices/configmanagement/grossiste/ws_data.jsp';
var url_services_data_type_tierspayant = '../webservices/tierspayantmanagement/typetierspayant/ws_data.jsp';
var Me;
var Omode;
var ref;
var famille_id_search;
var in_total_vente;
var int_total_formated;
var int_montant_vente;
var int_NBR_DOSSIER;
var int_montant_achat;
var LaborexWorkFlow_facture;
Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.sm_user.editfacture.action.add', {
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
        nameintern: '',
        //  headerPosition :'top'
    },
    xtype: 'addeditfactureOld',
    id: 'addeditfactureOldID',
    frame: true,
    title: 'Editer une facture',
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
            autoLoad: false,
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
                url: url_services_data_fournisseur,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            },
            autoLoad: false

        });
        store_detail_tiers_payant = new Ext.data.Store({
            model: 'testextjs.model.DossierFacture',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_detail_facture_tiers_payant,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }



        });
        store_detail_facture_fournisseur = new Ext.data.Store({
            model: 'testextjs.model.Facture',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_detail_facture,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });


        store_type_tierpayant = new Ext.data.Store({
            model: 'testextjs.model.TypeTiersPayant',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_type_tierspayant,
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
        /*  this.cellEditing = new Ext.grid.plugin.CellEditing({
         clicksToEdit: 1
         });*/

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
                                        //hideLabel: 'true'
                                    },
                                    items: [{
                                            xtype: 'combobox',
                                            fieldLabel: 'Type de facturation',
                                            allowBlank: false,
                                            name: 'lg_TYPE_FACTURE_ID',
                                            margin: '0 40 0 0',
                                            id: 'lg_TYPE_FACTURE_ID',
                                            store: storetypefacture,
                                            valueField: 'lg_TYPE_FACTURE_ID',
                                            displayField: 'str_LIBELLE',
                                            typeAhead: true,
                                            queryMode: 'remote',
                                            emptyText: 'Choisir un type de facturation...',
                                            listeners: {
                                                select: function (cmp) {
                                                    var cmp_val = cmp.getValue();
                                                    var Odetailfacturevente = Ext.getCmp('detailfacturevente');
                                                    var OdetailfactureFournisseur = Ext.getCmp('detailfactureFournisseur');
                                                    var Ocmb_TYPE_TIERS_PAYANT = Ext.getCmp('cmb_TYPE_TIERS_PAYANT');

                                                    if (cmp_val == 1) {


                                                        Odetailfacturevente.show();
                                                        Ocmb_TYPE_TIERS_PAYANT.show();
                                                        OdetailfactureFournisseur.hide();
                                                        store_detail_tiers_payant.removeAll();

                                                        //store_detail_tiers_payant.removeAll();
                                                        LaborexWorkFlow_facture.RedirectUrl('lg_TYPE_FACTURE_ID', 'cmb_CUSTOMER_ID', url_services_data_tiers_payant);
                                                    } else {
                                                        OdetailfactureFournisseur.show();
                                                        Ocmb_TYPE_TIERS_PAYANT.hide();
                                                        Odetailfacturevente.hide();
                                                        store_detail_facture_fournisseur.removeAll();
                                                        LaborexWorkFlow_facture.RedirectUrl('lg_TYPE_FACTURE_ID', 'cmb_CUSTOMER_ID', url_services_data_fournisseur);
                                                    }

                                                }
                                            }
                                        },
                                        {
                                            xtype: 'combobox',
                                            hidden: true,
                                            fieldLabel: 'Type Tiers Payant: ',
                                            allowBlank: false,
                                            name: 'cmb_TYPE_TIERS_PAYANT',
                                            margin: '0 15 0 0',
                                            id: 'cmb_TYPE_TIERS_PAYANT',
                                            store: store_type_tierpayant,
                                            valueField: 'lg_TYPE_TIERS_PAYANT_ID',
                                            displayField: 'str_LIBELLE_TYPE_TIERS_PAYANT',
                                            typeAhead: true,
                                            queryMode: 'remote',
                                            emptyText: 'Choisir type payant...',
                                            listeners: {
                                                select: function (cmp) {
                                                    var cmp_val = cmp.getValue();
                                                    /*  var Odetailfacturevente = Ext.getCmp('detailfacturevente');
                                                     var OdetailfactureFournisseur = Ext.getCmp('detailfactureFournisseur');
                                                     var Ocmb_TYPE_TIERS_PAYANT = Ext.getCmp('cmb_TYPE_TIERS_PAYANT');
                                                     */
                                                    if (cmp_val == 1) {

                                                        url_services_data_tiers_payant = '../webservices/tierspayantmanagement/tierspayant/ws_data.jsp';
                                                        LaborexWorkFlow_facture.RedirectUrl('cmb_TYPE_TIERS_PAYANT', 'cmb_CUSTOMER_ID', url_services_data_tiers_payant);

                                                    } else if (cmp_val == 2) {

                                                        url_services_data_tiers_payant = '../webservices/tierspayantmanagement/tierspayant/ws_data.jsp';
                                                        LaborexWorkFlow_facture.RedirectUrl('cmb_TYPE_TIERS_PAYANT', 'cmb_CUSTOMER_ID', url_services_data_tiers_payant);


                                                    } else {
                                                        // alert('cmp');
                                                    }

                                                }
                                            }

                                        },
                                        {
                                            xtype: 'combobox',
                                            fieldLabel: 'Facture de: ',
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
                    title: 'Detail(s) de la Facture',
                  //  hidden: true,
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
                            height: 300,
                            columns: [
                                {
                                    header: 'lg_DOSSIER_FACTURE',
                                    dataIndex: 'lg_DOSSIER_FACTURE',
                                    hidden: true,
                                    flex: 1
                                }, {
                                    header: 'Nom',
                                    dataIndex: 'str_NOM',
                                    flex: 1

                                }, {
                                    header: 'Prenom',
                                    dataIndex: 'str_PRENOM',
                                    flex: 1

                                }, {
                                    header: 'Matricule',
                                    dataIndex: 'str_MATRICULE',
                                    flex: 1
                                }, {
                                    header: 'Num Dossier',
                                    dataIndex: 'str_NUM_DOSSIER',
                                    flex: 1
                                }, {
                                    header: 'Montant',
                                    dataIndex: 'dbl_MONTANT',
                                    renderer: amountformat,
                                    align: 'right',
                                    flex: 1
                                }, {
                                    header: 'Date Du Bon',
                                    dataIndex: 'dt_CREATED',
                                    flex: 1
                                }, {
                                    header: 'Montant regle',
                                    dataIndex: 'dbl_MONTANT_REGLE',
                                    hidden: true,
                                    flex: 1
                                }, {
                                    header: 'Montant Restant',
                                    dataIndex: 'dbl_MONTANT_RESTANT',
                                    hidden: true,
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
                                    text: 'Editer facture',
                                    id: 'btn_create_facture',
                                    iconCls: 'icon-clear-group',
                                    scope: this,
                                    hidden: false,
                                    //disabled: true,
                                    handler: this.CreateFacture
                                }, {
                                    text: 'RETOUR',
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

                },
                // detail facture fournisseur


                {
                    xtype: 'fieldset',
                    id: 'detailfactureFournisseur',
                    title: 'Detail(s) de la Facture Fournisseur',
                    hidden: true,
                    collapsible: true,
                    defaultType: 'textfield',
                    layout: 'anchor',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            xtype: 'gridpanel',
                            id: 'gridpanelFournisseurID',
                            // plugins: [this.cellEditing],
                            store: store_detail_facture_fournisseur,
                            height: 400,
                            columns: [
                                {
                                    text: 'lg_BON_LIVRAISON_ID',
                                    flex: 1,
                                    sortable: true,
                                    hidden: true,
                                    dataIndex: 'lg_BON_LIVRAISON_ID',
                                    id: 'lg_BON_LIVRAISON_ID'
                                },
                                {
                                    text: 'Fournisseurs',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'str_LIBELLE',
                                    id: 'str_LIBELLE'
                                }, {
                                    text: 'Num Bon Commande',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'str_REF_ORDER'
                                },
                                {
                                    text: 'Date livraison',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'dt_DATE_LIVRAISON'
                                },
                                {
                                    text: 'Montant Bon',
                                    flex: 2,
                                    sortable: true,
                                    dataIndex: 'int_HTTC'
                                }
                            ],
                            bbar: {
                                xtype: 'pagingtoolbar',
                                pageSize: 10,
                                store: store_detail_facture_fournisseur,
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
                                    text: 'Editer facture',
                                    id: 'btn_create_facture_frs',
                                    iconCls: 'icon-clear-group',
                                    scope: this,
                                    hidden: false,
                                    //disabled: true,
                                    handler: this.CreateFacture
                                }, {
                                    text: 'RETOUR',
                                    id: 'btn_cancel_fournisseur',
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
                //fin detail facture fournisseur

            ]
        });

        this.callParent();

        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        });


        Ext.Ajax.request({
            url: '../webservices/sm_user/facturation/ws_init_data.jsp?mode=init',
            params: {
            },
            success: function (response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                // alert('tes  ' + object.str_date_debut);

                Ext.getCmp('dt_debut').setValue(Ext.Date.parse(object.str_date_debut, 'd/m/Y'));
                Ext.getCmp('dt_fin').setValue(Ext.Date.parse(object.str_date_fin, 'd/m/Y'));

            },
            failure: function (response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);
            }
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

        var xtype = "facturemanager";
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
        type_facture = Ext.getCmp('lg_TYPE_FACTURE_ID').getValue();
        str_CUSTOMER = Ext.getCmp('cmb_CUSTOMER_ID').getValue();
        if (type_facture == "1") {
            url_services_data_detail_facture_tiers_payant = '../webservices/sm_user/facturation/ws_data_detail_tiers_payant.jsp?lg_customer_id=' + str_CUSTOMER + '&lg_type_facture=' + type_facture + '&dt_debut=' + dt_debut + '&dt_fin=' + dt_fin;
            var OGrid = Ext.getCmp('gridpanelID');
            OGrid.getStore().getProxy().url = url_services_data_detail_facture_tiers_payant;
            OGrid.getStore().reload();




            Ext.Ajax.request({
                url: '../webservices/sm_user/facturation/ws_data_detail_tiers_payant.jsp',
                params: {
                    dt_debut: dt_debut,
                    dt_fin: dt_fin,
                    type_facture: type_facture,
                    str_CUSTOMER: str_CUSTOMER

                },
                success: function (response)
                {
                    var object = Ext.JSON.decode(response.responseText, false);
                    //  console.log(object);
                    //alert('Montant_total  ' + object.Montant_total);
                    // var int_total = Number(object.total);
                    Ext.getCmp('int_Nbr_Dossier').setValue(object.results[0].int_NB_DOSSIER);
                    Ext.getCmp('int_MONTANT').setValue(amountformat(object.Montant_total));
                },
                failure: function (response)
                {
                    var object = Ext.JSON.decode(response.responseText, false);
                    console.log("Bug " + response.responseText);
                    Ext.MessageBox.alert('Error Message', response.responseText);
                }
            });




        } else {
            url_services_data_detail_facture = '../webservices/sm_user/facturation/ws_data_detail_fournisseur.jsp?lg_customer_id=' + str_CUSTOMER + '&lg_type_facture=' + type_facture + '&dt_debut=' + dt_debut + '&dt_fin=' + dt_fin;
            var OGrid = Ext.getCmp('gridpanelFournisseurID');
            OGrid.getStore().getProxy().url = url_services_data_detail_facture;
            OGrid.getStore().reload();
        }


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
        type_facture = Ext.getCmp('lg_TYPE_FACTURE_ID').getValue();
        str_CUSTOMER = Ext.getCmp('cmb_CUSTOMER_ID').getValue();
        url_services_data_detail_transaction = '../webservices/sm_user/facturation/ws_transaction.jsp';

        if (type_facture == 1) {
            str_mode = 'create facture tiers';
        } else {
            str_mode = 'create facture fournisseur';
        }

        Ext.Ajax.request({
            url: url_services_data_detail_transaction,
            params: {
                lg_customer_id: str_CUSTOMER,
                lg_type_facture: type_facture,
                dt_fin: dt_fin,
                dt_debut: dt_debut,
                mode: str_mode

            },
            success: function (response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                if (object.success == 0) {
                    Ext.MessageBox.alert('Error Message', 'impossible de creer la facture');
                    return;
                }
                if (object.success == 1) {
                    var xtype = "facturemanager";
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

    },
});


