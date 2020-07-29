/* global Ext, isAvoir */
var Me_dovente;
var LaborexWorkFlow;
var comboValue = '';
// out
var str_TYPE;
var db_PROMOTION_PRICE;
var lg_PROMOTION_CODE_ID;
var int_PACK_NUMBER;
var int_ACTIVE_AT;
var int_FREE_PACK_NUMBER;
var int_PRODUCT_ORIGINAL_UNIT_PRICE;

var myAppController;
var APIKEY = "pk_syca_ea4072e471431d19ca7b852a9214ba0760f16980";
var MERCHANDID = "C_57EC174E7C4BC";
var CURR = "XOF";
var URLS = "http://localhost/SYCAPAYWebSite/";
//var URLS = 'http://40.85.182.75:8090/';
var URLC = 'http://www.asconsulting.ci/';
var TYPPAIE = 'payment';
var COMMANDE = 'Paiement mobile par Prestige';


var boxWaitingProcess;
var douchette = false;
var isDEVIS = false;
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}



Ext.define('testextjs.view.sm_user.dovente.DoventeManager', {
    extend: 'Ext.form.Panel',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.form.*',
        'Ext.data.Store',
        'Ext.layout.container.Column',
        'testextjs.model.TypeReglement',
        'testextjs.model.ModeReglement',
        'testextjs.model.TypeVente',
        'testextjs.model.NatureVente',
        'testextjs.controller.LaborexWorkFlow',
        'testextjs.view.sm_user.dovente.action.updateQuantity',
        'testextjs.view.sm_user.dovente.action.displayCustomer',
        'testextjs.view.configmanagement.client.action.addStandardUser'
    ],
    config: {
        odatasource: '',
        parentview: '',
        mode: '',
        titre: 'ECRAN DE VENTE',
        closable: false,
        nameintern: '',
        isPROFORMA: false
    },
    xtype: 'doventemanagerxx',
    id: 'doventemanagerID',
    frame: true,
    title: 'VENTE',
    layout: 'column',
    successUrl: 'http://40.85.182.75:9010/purchases',
    failedUrl: '',
    defaults: {
        autoScroll: true
    },
    initComponent: function () {
        var itemsPerPage = 20;
        this.isPROFORMA = false;
        isDEVIS = false;
        douchette = false;
        myAppController = Ext.create('testextjs.controller.App', {});
        Me_dovente = this;

        if (this.getTitre() === "ECRAN DE VENTE") {

            if (this.getOdatasource().lg_TYPE_VENTE_ID !== '1' && this.getOdatasource().lg_TYPE_VENTE_ID !== undefined)
                this.titre = "VENTE A CREDIT";
            else
                this.titre = "VENTE AU COMPTANT";

        } else if (this.getTitre() === "PROFORMA" || this.getTitre() === 'EDITION DE PROFORMA') {
            this.isPROFORMA = true;
            isDEVIS = true;
        }

        Me_dovente.ViewInitialize();

        var url_services_data_utilisateur = '../webservices/sm_user/utilisateur/ws_data.jsp',
                url_services_data_nature_vente_dovente = '../webservices/configmanagement/naturevente/ws_data.jsp',
                url_services_data_typevente_dovente = '../webservices/configmanagement/typevente/ws_data.jsp',
                url_services_data_famille_select_dovente = '../webservices/sm_user/famille/ws_data_jdbc.jsp',
                url_services_data_famille_select_dovente_dci = '../webservices/sm_user/famille/ws_data_jdbc_dci.jsp',
                url_services_data_typeremise = '../webservices/configmanagement/typeremise/ws_data.jsp',
                url_services_data_remise_dovente = '../webservices/configmanagement/remise/ws_data_dovente.jsp',
                url_services_data_devise = '../webservices/configmanagement/devise/ws_data.jsp',
                url_services_data_detailsvente = '../webservices/sm_user/detailsvente/ws_data.jsp',
                url_init_vente = '../webservices/sm_user/detailsvente/ws_init_data.jsp',
                url_services_data_typereglement_dovente = '../webservices/sm_user/typereglement/ws_data.jsp',
//                url_services_data_modereglement_dovente = '../webservices/sm_user/modereglement/ws_data.jsp',
                url_transaction_vente = '../webservices/sm_user/detailsvente/ws_transaction.jsp?mode=';

        //fin url

        if (Me_dovente.getTitre() === "AVOIR") {
            url_services_data_detailsvente = '../webservices/sm_user/detailsvente/ws_data.jsp?str_STATUT=is_Closed';
        }

        //gestion des stores
//        var store_utilisateur = LaborexWorkFlow.BuildStore('testextjs.model.Utilisateur', itemsPerPage, url_services_data_utilisateur, false),
        storenaturevente = LaborexWorkFlow.BuildStore('testextjs.model.NatureVente', itemsPerPage, url_services_data_nature_vente_dovente, false),
                store_typevente = LaborexWorkFlow.BuildStore('testextjs.model.TypeVente', itemsPerPage, url_services_data_typevente_dovente, false),
                // store_famille_dovente = LaborexWorkFlow.BuildStore('testextjs.model.Famille', itemsPerPage, url_services_data_famille_select_dovente, true),
                store_famille_dci_dovente = LaborexWorkFlow.BuildStore('testextjs.model.Famille', itemsPerPage, url_services_data_famille_select_dovente_dci, false),
                store_type_remise = LaborexWorkFlow.BuildStore('testextjs.model.TypeRemise', itemsPerPage, url_services_data_typeremise, false),
                store_remise = LaborexWorkFlow.BuildStore('testextjs.model.Remise', itemsPerPage, url_services_data_remise_dovente, false),
                store_devise = LaborexWorkFlow.BuildStore('testextjs.model.Devise', itemsPerPage, url_services_data_devise, false),
                store_details = LaborexWorkFlow.BuildStore('testextjs.model.DetailsVente', itemsPerPage, url_services_data_detailsvente, false),
                store_typereglement = LaborexWorkFlow.BuildStore('testextjs.model.TypeReglement', itemsPerPage, url_services_data_typereglement_dovente + "?str_FLAG=0", false);
//                store_modereglement = LaborexWorkFlow.BuildStore('testextjs.model.ModeReglement', itemsPerPage, url_services_data_modereglement_dovente, false);
        //fin gestion des stores


// ajoute le 12/09/2017

        var store = Ext.create('testextjs.store.SearchStore');
        var store_utilisateur = Ext.create('testextjs.store.Users');


        /* 07/12/2016 pour le controle de Avoir */
        LaborexWorkFlow.isCredit(false);
        /* 07/12/2016 pour le controle de Avoir fin */

        this.cellEditing = new Ext.grid.plugin.CellEditing({
            clicksToEdit: 1
        });


        Ext.apply(this, {
            width: '98%',
            // height:valheight,
            // minHeight:Ext.getBody().getViewSize().height*0.85,
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
                //gestion type de vente et nature de vente, ainsi que le vendeur
                {
                    xtype: 'fieldset',
                    title: '<span style="color:blue;">CHOISIR LE TYPE /LA NATURE DE VENTE ET LE VENDEUR</span>',
                    collapsible: true,
                    defaultType: 'textfield',
                    cls: 'background_gray',
                    layout: 'anchor',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            xtype: 'container',
                            layout: 'hbox',
                            margin: '0 0 5 0',
                            defaultType: 'textfield',
                            items: [
                                {
                                    xtype: 'combobox',

                                    name: 'lg_TYPE_VENTE_ID',
                                    id: 'lg_TYPE_VENTE_ID',
                                    flex: 1,
                                    pageSize: 10,
//                                    store: store_typevente,
                                    store: Ext.create("Ext.data.Store", {
                                        fields: [
                                            {
                                                name: 'lg_TYPE_VENTE_ID',
                                                type: 'string'
                                            },
                                            {
                                                name: 'str_NAME',
                                                type: 'string'
                                            },
                                            {
                                                name: 'str_DESCRIPTION',
                                                type: 'string'
                                            }


                                        ],
                                        pageSize: 10,

                                        proxy: {
                                            type: 'ajax',
                                            url: '../webservices/configmanagement/typevente/ws_typeventes.jsp?exclude=' + (this.isPROFORMA === true ? "2" : ''),
                                            reader: {
                                                type: 'json',
                                                root: 'data',
                                                totalProperty: 'total'
                                            }
                                        }
                                    }),
                                    value: 'AU COMPTANT',
                                    valueField: 'lg_TYPE_VENTE_ID',
                                    displayField: 'str_NAME',
                                    editable: false, //empeche la saisie dans le combo. "typeAhead" n'est pas utilisé lorsque "editable" est utilisé
                                    queryMode: 'remote',
                                    allowBlank: true,
                                    emptyText: 'Choisir un type de vente...',
                                    listeners: {
                                        select: function (cmp) {
                                            var record = cmp.findRecord(cmp.valueField || cmp.displayField, cmp.getValue()); //recupere la ligne de l'element selectionné

                                            if (Ext.getCmp('lg_PREENREGISTREMENT_ID').getValue() === "0") {
                                                LaborexWorkFlow.ChangeViewTitle(record.get('lg_TYPE_VENTE_ID'));
                                            } else {
                                                LaborexWorkFlow.UpdateTypeVente(url_transaction_vente + "updatetypevente", Ext.getCmp('lg_PREENREGISTREMENT_ID').getValue(), record.get('lg_TYPE_VENTE_ID'));
                                            }

                                            if (cmp.getValue() !== "1") {
                                                Ext.getCmp('lg_CLIENT_ID').focus(true, 100, function () {
                                                });
                                                Ext.getCmp('REF_RC1').hide();
                                                Ext.getCmp('REF_RC2').hide();
                                                Ext.getCmp('REF_RC3').hide();
                                            } else {
                                                Ext.getCmp('str_NAME').focus(true, 100, function () {
                                                });
                                            }


                                        }
                                    }
                                },
                                {
                                    xtype: 'combobox',
//                                    fieldLabel: 'NATURE VENTE',
                                    name: 'lg_NATURE_VENTE_ID',
                                    value: 'PRESCRIPTION',
                                    margins: '0 0 0 10',
                                    flex: 1,
                                    id: 'lg_NATURE_VENTE_ID',
                                    store: storenaturevente,
                                    valueField: 'lg_NATURE_VENTE_ID',
                                    displayField: 'str_LIBELLE',
                                    editable: false, //empeche la saisie dans le combo. "typeAhead" n'est pas utilisé lorsque "editable" est utilisé
                                    queryMode: 'remote',
                                    emptyText: 'Choisir une nature de vente...'
                                },
                                {
                                    xtype: 'combobox',
//                                    fieldLabel: 'VENDEUR',
                                    name: 'lg_USER_VENDEUR_ID',
                                    margins: '0 0 0 10',
                                    flex: 1,
                                    id: 'lg_USER_VENDEUR_ID',
                                    store: store_utilisateur,
                                    minChars: 2, //code ajouté 28/06/2016
                                    valueField: 'lg_USER_ID',
                                    displayField: 'str_FIRST_LAST_NAME',
                                    typeAhead: false,
                                    pageSize: itemsPerPage, //ajout la barre de pagination
                                    queryMode: 'remote',
                                    emptyText: 'Choisir un vendeur...',
                                    listeners: {

                                        select: function (cmp, record, index) {

                                            if (Ext.getCmp('lg_TYPE_VENTE_ID').getValue() === "1" || Ext.getCmp('lg_TYPE_VENTE_ID').getValue() == "AU COMPTANT") {


                                                if (Me_dovente.isPROFORMA === false) {


                                                    if (Ext.getCmp('str_NAME').getValue() != null) {
                                                        Ext.getCmp('int_QUANTITY').focus(true, 100, function () {
                                                            Ext.getCmp('int_QUANTITY').selectText(0, Ext.getCmp('int_QUANTITY').getValue().length());
                                                        });
                                                    } else {
                                                        Ext.getCmp('str_NAME').focus(true, 100, function () {
                                                        });
                                                    }
                                                } else {
                                                    Ext.getCmp('lg_CLIENT_ID').focus(true, 100, function () {
                                                    });
                                                }
                                            } else {
                                                if (Ext.getCmp('str_NAME').getValue() !== null) {
                                                    Ext.getCmp('int_QUANTITY').focus(true, 100, function () {
                                                        Ext.getCmp('int_QUANTITY').selectText(0, Ext.getCmp('int_QUANTITY').getValue().length());
                                                    });
                                                } else {
                                                    Ext.getCmp('lg_CLIENT_ID').focus(true, 100, function () {
                                                    });
                                                }
                                            }
                                        }
                                    }
                                },
                                {
                                    xtype: 'hiddenfield',
                                    name: 'lg_PREENREGISTREMENT_ID',
                                    id: 'lg_PREENREGISTREMENT_ID',
                                    value: '0'
                                },
                                {
                                    xtype: 'hiddenfield',
                                    name: 'authorize_cloture_vente',
                                    id: 'authorize_cloture_vente',
                                    value: '0'
                                }
                            ]
                        }
                    ]
                },
                //fin gestion type de vente et nature de vente, ainsi que le vendeur

                //gestion client
                {
                    xtype: 'fieldset',
                    title: '<span style="color:blue;">IDENTIFICATION CLIENT</span>',
                    collapsible: true,
                    id: 'fieldset_identification_client',
                    hidden: true,
                    defaultType: 'textfield',
                    cls: 'background_green',
                    layout: 'anchor',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            xtype: 'container',
                            layout: 'hbox',
                            defaultType: 'textfield',
                            margin: '0 0 5 0',
                            items: [
                                {
                                    xtype: 'textfield',
                                    name: 'lg_CLIENT_ID',
                                    id: 'lg_CLIENT_ID',
                                    fieldLabel: 'CLIENT',
                                    width: '40%',
                                    labelWidth: 200,
                                    enableKeyEvents: true,
                                    emptyText: 'IDENTIFICATION CLIENT',
                                    allowBlank: true,
                                    listeners: {
                                        specialKey: function (field, e) {
                                            if (e.getKey() === e.ENTER || e.getKey() === 13) {
                                                var lg_CLIENT_ID = field.getValue(),
                                                        lg_TYPE_VENTE_ID = Ext.getCmp('lg_TYPE_VENTE_ID').getValue(),
                                                        lg_TYPE_CLIENT_ID = "";

                                                if (lg_TYPE_VENTE_ID === "2") {
                                                    lg_TYPE_CLIENT_ID = "1";
                                                } else if (lg_TYPE_VENTE_ID === "3") {
                                                    lg_TYPE_CLIENT_ID = "2";
                                                } else {
                                                    lg_TYPE_CLIENT_ID = "6";
                                                }
                                                var url_services_data_client_displayCust_final = "../webservices/sm_user/diffclient/ws_customer_data.jsp?search_value=" + lg_CLIENT_ID + "&lg_TYPE_CLIENT_ID=" + lg_TYPE_CLIENT_ID;
                                                if (lg_TYPE_VENTE_ID === "1" || lg_TYPE_VENTE_ID === "AU COMPTANT") {
                                                    if (Me_dovente.getTitre() === "PROFORMA") {
                                                        url_services_data_client_displayCust_final = "../webservices/configmanagement/client/ws_data.jsp?search_value=" + lg_CLIENT_ID;
                                                    } else {
                                                        url_services_data_client_displayCust_final = "../webservices/configmanagement/client/ws_data.jsp?search_value=" + lg_CLIENT_ID + "&lg_TYPE_CLIENT_ID=" + lg_TYPE_CLIENT_ID;
                                                    }
                                                }
                                                LaborexWorkFlow.GetCustomer(lg_TYPE_VENTE_ID, url_services_data_client_displayCust_final, lg_CLIENT_ID, lg_TYPE_CLIENT_ID);
                                            }
                                        }
                                    }
                                },
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: 'ID CLIENT :',
                                    name: 'lg_CLIENT_ID_FIND',
                                    id: 'lg_CLIENT_ID_FIND',
                                    labelWidth: 90,
                                    fieldStyle: "color:blue;",
                                    hidden: true,
                                    margin: '0 10 0 0'

                                }, {
                                    xtype: 'hiddenfield',
                                    id: 'lg_COMPTE_CLIENT_ID',
                                    name: 'lg_COMPTE_CLIENT_ID'
                                },
                                {
                                    xtype: 'hiddenfield',
                                    id: 'TELEPHONECLIENT',
                                    name: 'TELEPHONECLIENT'
                                },
                                {
                                    xtype: 'hiddenfield',
                                    name: 'int_TAUX',
                                    id: 'int_TAUX',
                                    value: 0
                                },
                                {
                                    xtype: 'hiddenfield',
                                    name: 'dbl_PLAFOND_CONSO_DIFFERENCE',
                                    id: 'dbl_PLAFOND_CONSO_DIFFERENCE',
                                    value: 0
                                },
                                {
                                    xtype: 'hiddenfield',
                                    name: 'dbl_PLAFOND',
                                    id: 'dbl_PLAFOND',
                                    value: 0
                                },
                                {
                                    xtype: 'hiddenfield',
                                    name: 'dbl_PLAFOND_BON',
                                    id: 'dbl_PLAFOND_BON',
                                    value: 0
                                }
                            ]
                        }
                    ]
                },
                //fin gestion client

                //gestion assuré et ayant droit
                {
                    xtype: 'container',
                    layout: 'hbox',
                    defaultType: 'textfield',
                    items: [
                        //assuré
                        {
                            xtype: 'fieldset',
                            title: '<span style="color:blue;">ASSURE</span>',
                            collapsible: true,
                            margin: '0 5 0 0',
                            id: 'fieldset_assure',
                            cls: 'background_green',
                            hidden: true,
                            flex: 1,
//                            width: '50%',
                            defaultType: 'textfield',
                            layout: 'anchor',
                            defaults: {
//                                anchor: '100%'
                                labelWidth: 150
                            },
                            items: [
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: 'MATRICULE/ SS :',
                                    name: 'str_NUMERO_SECURITE_SOCIAL',
                                    id: 'str_NUMERO_SECURITE_SOCIAL',
                                    fieldStyle: "color:blue;",
                                    margin: '0 15 0 0'
                                },
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: 'NOM :',
                                    name: 'str_FIRST_NAME',
                                    id: 'str_FIRST_NAME',
                                    fieldStyle: "color:blue;",
                                    margin: '0 15 0 0'
                                },
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: 'PRENOM :',
                                    name: 'str_LAST_NAME',
                                    id: 'str_LAST_NAME',
                                    fieldStyle: "color:blue;",
                                    margin: '0 15 0 0'
                                },
                                {
                                    text: 'MODIFIER INFOS',
                                    id: 'btn_modifier_info',
                                    margin: '0 0 5 50',
                                    xtype: 'button',
                                    handler: function () {
                                        var lg_CLIENT_ID = Ext.getCmp('lg_CLIENT_ID_FIND').getValue(),
                                                lg_TYPE_VENTE_ID = Ext.getCmp('lg_TYPE_VENTE_ID').getValue(),
                                                lg_TYPE_CLIENT_ID = "";

                                        if (lg_TYPE_VENTE_ID === "2" || lg_TYPE_VENTE_ID === "ASSURANCE_MUTUELLE") {
                                            lg_TYPE_CLIENT_ID = "1";
                                        } else if (lg_TYPE_VENTE_ID === "3" || lg_TYPE_VENTE_ID === "CARNET") {
                                            lg_TYPE_CLIENT_ID = "2";
                                        }

                                        LaborexWorkFlow.updateInfoCustomer(lg_CLIENT_ID, lg_TYPE_CLIENT_ID, Ext.getCmp('lg_COMPTE_CLIENT_ID').getValue());
                                        //afficher les informations du client en mode update sur l'écran de création des clients a la vente
                                    }
                                }
                            ]
                        },
                        //fin assuré

                        //ayant droit
                        {
                            xtype: 'fieldset',
                            title: '<span style="color:blue;">AYANT DROIT</span>',
                            collapsible: true,
                            id: 'fieldset_ayantdroit',
                            cls: 'background_green',
                            hidden: true,
//                            width: '49.8%',
                            flex: 1,
                            defaultType: 'textfield',
                            layout: 'anchor',
                            defaults: {
                                labelWidth: 150
                            },
                            items: [
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: 'MATRICULE/ SS :',
                                    name: 'str_NUMERO_SECURITE_SOCIAL_AD',
                                    id: 'str_NUMERO_SECURITE_SOCIAL_AD',
                                    fieldStyle: "color:blue;",
                                    margin: '0 15 0 0'
                                },
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: 'NOM :',
                                    name: 'str_FIRST_NAME_AD',
                                    id: 'str_FIRST_NAME_AD',
                                    fieldStyle: "color:blue;",
                                    margin: '0 15 0 0'
                                },
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: 'PRENOM :',
                                    name: 'str_LAST_NAME_AD',
                                    id: 'str_LAST_NAME_AD',
                                    fieldStyle: "color:blue;",
                                    margin: '0 15 0 0'

                                },
                                {
                                    text: 'AUTRE AYANT DROIT',
                                    id: 'btn_add_affilie',
                                    margin: '0 0 5 50',
                                    xtype: 'button',
                                    handler: function () {
                                        var lg_CLIENT_ID = Ext.getCmp('lg_CLIENT_ID_FIND').getValue();
                                        LaborexWorkFlow.GetAyantDroit("", lg_CLIENT_ID);
                                    }
                                },
                                {
                                    xtype: 'hiddenfield',
                                    name: 'lg_AYANTS_DROITS_ID',
                                    id: 'lg_AYANTS_DROITS_ID'
                                }
                            ]
                        }
                        //fin ayant droit
                    ]
                },
                //fin gestion assuté et ayant droit

                //gestion des tiers payants
                {
                    xtype: 'fieldset',
                    title: '<span style="color:blue;">TIERS PAYANT</span>',
                    collapsible: true,
                    id: 'fieldset_tierpayant',
                    cls: 'background_green',
                    hidden: true,
                    defaultType: 'textfield',
                    layout: 'anchor',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            xtype: 'container',
                            layout: 'hbox',
                            defaultType: 'textfield',
                            margin: '0 0 5 0',

                            items: [
                                {
                                    fieldLabel: 'TP1 :',
                                    xtype: 'displayfield',
                                    name: 'RO',
                                    id: 'RO',
                                    fieldStyle: "color:blue;font-weight:bold;cursor: pointer;",
                                    margin: '0 5 0 0',
                                    width: '24%',
                                    listeners: {
                                        afterrender: function (component) {
                                            component.getEl().on('click', function () {
                                                LaborexWorkFlow.RemoveTiersPayantVente('RO', 'RO_ID', 'REF_RO');
                                            });
                                        }
                                    }
                                },
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: 'TP2 :',
                                    name: 'RC1',
                                    id: 'RC1',
                                    fieldStyle: "color:blue;font-weight:bold;cursor: pointer;",
                                    margin: '0 5 0 0',
                                    width: '24%',
                                    listeners: {
                                        afterrender: function (component) {
                                            component.getEl().on('click', function () {
                                                LaborexWorkFlow.RemoveTiersPayantVente('RC1', 'RC1_ID', 'REF_RC1');
                                            });
                                        }
                                    }
                                },
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: 'TP3 :',
                                    name: 'RC2',
                                    id: 'RC2',
                                    fieldStyle: "color:blue;font-weight:bold;cursor: pointer;",
                                    margin: '0 5 0 0',
                                    width: '20%',
                                    listeners: {
                                        afterrender: function (component) {
                                            component.getEl().on('click', function () {
                                                LaborexWorkFlow.RemoveTiersPayantVente('RC2', 'RC2_ID', 'REF_RC2');
                                            });
                                        }
                                    }

                                },
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: 'TP4 :',
                                    name: 'RC3',
                                    id: 'RC3',
                                    fieldStyle: "color:blue;font-weight:bold;cursor: pointer;",
                                    margin: '0 5 0 0',
                                    width: '20%',
                                    listeners: {
                                        afterrender: function (component) {
                                            component.getEl().on('click', function () {
                                                LaborexWorkFlow.RemoveTiersPayantVente('RC3', 'RC3_ID', 'REF_RC3');
                                            });
                                        }
                                    }
                                },
                                {
                                    text: 'AUTRES TIERS PAYANT',
                                    id: 'btn_add_tierpayant',
                                    margins: '0 0 0 6',
                                    xtype: 'button',
                                    handler: LaborexWorkFlow.GetToTiersPayant
                                },
                                {
                                    xtype: 'hiddenfield',
                                    name: 'RO_ID',
                                    id: 'RO_ID'
                                },
                                {
                                    xtype: 'hiddenfield',
                                    name: 'RC1_ID',
                                    id: 'RC1_ID'
                                },
                                {
                                    xtype: 'hiddenfield',
                                    name: 'RC2_ID',
                                    id: 'RC2_ID'
                                },
                                {
                                    xtype: 'hiddenfield',
                                    name: 'RC3_ID',
                                    id: 'RC3_ID'
                                }
                            ]
                        },
                        //reference de bon
                        {
                            xtype: 'container',
                            layout: 'hbox',
                            margin: '0 0 5 0',
                            defaultType: 'textfield',
                            items: [
                                {
                                    fieldLabel: 'REF BON1 :',
                                    name: 'REF_RO',
                                    id: 'REF_RO',
                                    selectOnFocus: true,
                                    hidden: true,
                                    margin: '0 5 0 0',
                                    width: '24%'
                                },
                                {
                                    fieldLabel: 'REF BON2 :',
                                    name: 'REF_RC1',
                                    id: 'REF_RC1',
                                    selectOnFocus: true,
                                    hidden: true,
                                    margin: '0 5 0 0',
                                    width: '20%'
                                },
                                {
                                    fieldLabel: 'REF BON3 :',
                                    name: 'REF_RC2',
                                    id: 'REF_RC2',
                                    selectOnFocus: true,
                                    hidden: true,
                                    margin: '0 5 0 0',
                                    width: '20%'
                                },
                                {
                                    fieldLabel: 'REF BON4 :',
                                    name: 'REF_RC3',
                                    id: 'REF_RC3',
                                    hidden: true,
                                    selectOnFocus: true,
                                    margin: '0 5 0 0',
                                    width: '24%'
                                }
                            ]
                        }
                        //fin reference de bon
                    ]
                },
                //fin gestion des tiers payants,

                //recherche de produit
                {
                    xtype: 'fieldset',
                    title: '<span style="color:blue;">RECHERCHER UN PRODUIT</span>',
                    collapsible: true,
                    defaultType: 'textfield',
                    layout: 'anchor',
                    cls: 'background_gray',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            xtype: 'fieldcontainer',
                            layout: 'hbox',
                            defaultType: 'textfield',
                            fieldLabel: 'PRODUIT',
                            items: [
                                {
                                    xtype: 'combobox',
                                    name: 'str_NAME',
                                    id: 'str_NAME',
                                    // store: store_famille_dovente,
                                    store: store,
                                    margins: '0 10 5 0',
                                    valueField: 'CIP',
                                    displayField: 'str_DESCRIPTION',
                                    enableKeyEvents: true,
                                    pageSize: 20, //ajout la barre de pagination
                                    //typeAhead: true,
                                    flex: 2,
                                    queryMode: 'remote',
                                    minChars: 3,
                                    queryCaching: false,
                                    emptyText: 'Choisir un article par Nom ou Cip...',
                                    triggerAction: 'all',
                                    listConfig: {
                                        loadingText: 'Recherche...',
                                        emptyText: 'Pas de données trouvées.',
                                        getInnerTpl: function () {
                                            return '<tpl for="."><tpl if="int_NUMBER_AVAILABLE <=0"><span style="color:#17987e;font-weight:bold;"><span style="width:100px;display:inline-block;">{CIP}</span>{str_DESCRIPTION} <span style="float: right;"> ( {int_PRICE} )</span></span><tpl else><span style="font-weight:bold;"><span style="width:100px;display:inline-block;">{CIP}</span>{str_DESCRIPTION} <span style="float: right; "> ( {int_PRICE} )</span></span></tpl></tpl>';
//                                            return '<span style="width:100px;display:inline-block;">{CIP}</span>{str_DESCRIPTION} <span style="float: right; font-weight:600;"> ({int_PRICE})</span><br>';
                                        }
                                    },
                                    listeners: {
                                        afterrender: function (field) {

                                            field.focus();

                                        },
                                        keyup: function (thisField, evt, eOpts) {


                                            if (evt.getCharCode() === evt.ENTER) {


                                                if (this.getValue() !== null && this.getValue() !== "") {

                                                    this.getStore().load({
                                                        params: {
                                                            query: thisField.getValue()
                                                        },
                                                        callback: function (records, operation, success) {
                                                            if (records.length === 0) {
                                                                this.getStore().clearValue();
                                                                this.getStore().load();
                                                                return;
                                                            }
                                                            var record = records[0];
//                                                            var record = thisField.findRecord(thisField.valueField || thisField.displayField, thisField.getValue());
                                                           
                                                            Ext.getCmp('lg_FAMILLE_ID_VENTE').setValue(record.get('lg_FAMILLE_ID'));
                                                            LaborexWorkFlow.DoAjaxGetStockArticle(record);
                                                        }
                                                    });

//                                          

                                                } else {
                                                    LaborexWorkFlow.ShowNetPaid(url_transaction_vente + 'shownetpay');
                                                }

                                            }
                                        },

                                        select: function (cmp) {

                                            var value = cmp.getValue();
                                            var record = cmp.findRecord(cmp.valueField || cmp.displayField, value); //recupere la ligne de l'element selectionné
                                            Ext.getCmp('lg_FAMILLE_ID_VENTE').setValue(record.get('lg_FAMILLE_ID'));

                                            //a revoir le cas des promotions ici au cas ou

                                            LaborexWorkFlow.DoAjaxGetStockArticle(record);

                                        }
                                    }
                                },
                                {
                                    xtype: 'combobox',
                                    name: 'str_CODE',
                                    id: 'str_CODE',
                                    store: store_famille_dci_dovente,
                                    margins: '0 10 5 0',
                                    valueField: 'str_DESCRIPTION',
                                    displayField: 'str_DESCRIPTION',
                                    pageSize: 20, //ajout la barre de pagination
                                    enableKeyEvents: true,
                                    typeAhead: true,
                                    flex: 2,
                                    queryMode: 'remote',
                                    minChars: 3,
                                    emptyText: 'Choisir un article par Dci...',
                                    listConfig: {
                                        getInnerTpl: function () {
                                            return '<span style="width:100px;display:inline-block;">{CIP}</span>{str_DESCRIPTION} <span style="float: right; font-weight:600;"> ({int_PRICE})</span>';
                                        }
                                    },
                                    listeners: {
                                        keyup: function (thisField, evt, eOpts) {

                                            if (evt.getCharCode() === evt.ENTER) {
                                                if (this.getValue() !== null && this.getValue() !== "") {
                                                    this.getStore().load({
                                                        params: {
                                                            query: thisField.getValue()
                                                        },
                                                        callback: function (records, operation, success) {
                                                            if (records.length === 0) {
                                                                this.getStore().clearValue();
                                                                this.getStore().load();
                                                                return;
                                                            }
                                                            var record = records[0];
                                                            Ext.getCmp('lg_FAMILLE_ID_VENTE').setValue(record.get('lg_FAMILLE_ID'));

                                                            //a revoir le cas des promotions ici au cas ou

                                                            LaborexWorkFlow.DoAjaxGetStockArticle(record);

                                                        }
                                                    });
                                                } else {
                                                    LaborexWorkFlow.ShowNetPaid(url_transaction_vente + 'shownetpay');
                                                }
                                            }
                                        },
                                        select: function (cmp) {
                                            var value = cmp.getValue();
                                            var record = cmp.findRecord(cmp.valueField || cmp.displayField, value); //recupere la ligne de l'element selectionné

                                            Ext.getCmp('lg_FAMILLE_ID_VENTE').setValue(record.get('lg_FAMILLE_ID'));
                                            LaborexWorkFlow.DoAjaxGetStockArticle(record);
                                            cmp.setFieldStyle('border: 2px solid #C0C0C0; background: #FFFFFF;');
                                        }
                                    }
                                },
                                {
                                    xtype: 'textfield',
                                    allowBlank: true,
                                    value: 1,
                                    name: 'int_QUANTITY',
                                    id: 'int_QUANTITY',
                                    fieldLabel: 'QD :',
                                    flex: 1,
                                    emptyText: 'QD',
                                    selectOnFocus: true,
                                    enableKeyEvents: true,
                                    listeners: {
                                        specialKey: function (field, e, options) {
                                            douchette = false;
                                            if (e.getKey() === e.ENTER) {
                                                if (Ext.getCmp('lg_FAMILLE_ID_VENTE').getValue() !== null && Ext.getCmp('lg_FAMILLE_ID_VENTE').getValue() !== "") {
                                                    var int_QUANTITY = Ext.getCmp('int_QUANTITY').getValue();
                                                    if (int_QUANTITY > 0) {
                                                        if (parseInt(int_QUANTITY) >= 1000) {
                                                            Ext.MessageBox.show({
                                                                title: 'Message d\'erreur',
                                                                width: 320,
                                                                msg: "Impossible de saisir une quantit&eacute; sup&eacute;rieure &agrave; 1000",
                                                                buttons: Ext.MessageBox.OK,
                                                                icon: Ext.MessageBox.WARNING,
                                                                fn: function (buttonId) {
                                                                    if (buttonId === "ok") {
                                                                        Ext.getCmp('int_QUANTITY').focus(false, 100, function () {

                                                                        });
                                                                    }
                                                                }
                                                            });
                                                            return;
                                                        }
                                                        /*var lg_TYPE_VENTE_ID = Ext.getCmp('lg_TYPE_VENTE_ID').getValue();
                                                        var isVO = 1;
                                                        if (lg_TYPE_VENTE_ID == "1" || lg_TYPE_VENTE_ID == "AU COMPTANT") {
                                                            isVO = 0;
                                                        }*/
                                                        if (parseInt(int_QUANTITY) > parseInt(Ext.getCmp('int_NUMBER_AVAILABLE_STOCK').getValue())) {
                                                            Ext.MessageBox.confirm('Alerte',
                                                                    'Stock insuffisant, voulez-vous forcer le stock',
                                                                    function (btn) {
                                                                        if (btn === 'yes') {
                                                                          /*  if (isVO === 0) {
                                                                                LaborexWorkFlow.creerUneVenteVNO('../VenteCtr', Ext.getCmp('lg_PREENREGISTREMENT_ID').getValue(), Ext.getCmp('lg_FAMILLE_ID_VENTE').getValue(), int_QUANTITY);
                                                                            } else {

                                                                            }*/
                                                                            LaborexWorkFlow.DoAjaxRequest(url_transaction_vente + "create", Ext.getCmp('lg_PREENREGISTREMENT_ID').getValue(), Ext.getCmp('lg_FAMILLE_ID_VENTE').getValue(), int_QUANTITY);
                                                                            return;
                                                                        }
                                                                        Ext.getCmp('int_QUANTITY').focus(true, 100, function () {

                                                                        });
                                                                    });
                                                            return;

                                                        }

                                                       /* if (isVO === 0) {
                                                            LaborexWorkFlow.creerUneVenteVNO('../VenteCtr', Ext.getCmp('lg_PREENREGISTREMENT_ID').getValue(), Ext.getCmp('lg_FAMILLE_ID_VENTE').getValue(), int_QUANTITY);
                                                        } else {

                                                        }*/
                                                        LaborexWorkFlow.DoAjaxRequest(url_transaction_vente + "create", Ext.getCmp('lg_PREENREGISTREMENT_ID').getValue(), Ext.getCmp('lg_FAMILLE_ID_VENTE').getValue(), int_QUANTITY);

                                                        var cmb = Ext.getCmp('str_NAME');
                                                        cmb.clearValue();
                                                        //cmb.getStore().load();

                                                    }
                                                } else {
                                                    Ext.MessageBox.show({
                                                        title: "Message d'erreur",
                                                        width: 320,
                                                        msg: "Choisissez un produit SVP!",
                                                        buttons: Ext.MessageBox.OK,
                                                        icon: Ext.MessageBox.WARNING,
                                                        fn: function (buttonId) {
                                                            if (buttonId === "ok") {
                                                                Ext.getCmp('str_NAME').focus(true, 100, function () {
                                                                    Ext.getCmp('str_NAME').reset();
                                                                });
                                                            }

                                                        }
                                                    });
                                                }
                                            }

                                        }
                                    }
                                },
                                {
                                    xtype: 'hiddenfield',
                                    name: 'lg_FAMILLE_ID_VENTE',
                                    id: 'lg_FAMILLE_ID_VENTE'
                                }
                            ]
                        }
                    ]
                },
                //fin recherche de produit

                //information stock produit, type remise et remise
                {
                    xtype: 'fieldset',
                    title: '<span style="color:blue;">INFOS PRODUITS</span>',
                    collapsible: true,
                    defaultType: 'textfield',
                    layout: 'anchor',
                    cls: 'background_gray',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            xtype: 'container',
                            layout: 'hbox',
                            defaultType: 'textfield',
                            margin: '0 0 10 0',
                            items: [
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: 'STOCK REEL :',
                                    labelWidth: 100,
                                    name: 'int_NUMBER_AVAILABLE_STOCK',
                                    id: 'int_NUMBER_AVAILABLE_STOCK',
                                    fieldStyle: "color:blue;",
                                    flex: 1,
                                    value: 0
                                },
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: 'EMPLACEMENT :',
                                    name: 'lg_ZONE_GEO_ID',
                                    labelWidth: 110,
                                    id: 'lg_ZONE_GEO_ID',
                                    fieldStyle: "color:blue;",
                                    flex: 1
                                },
                                {
                                    xtype: 'combobox',
                                    fieldLabel: 'TYPE REMISE :',
                                    name: 'lg_TYPE_REMISE_ID',
                                    value: 'Aucun',
                                    labelWidth: 100,
                                    flex: 1,
                                    margin: '0 5 0 0',
                                    id: 'lg_TYPE_REMISE_ID',
                                    store: store_type_remise,
                                    valueField: 'lg_TYPE_REMISE_ID',
                                    displayField: 'str_DESCRIPTION',
                                    queryMode: 'remote',
                                    emptyText: 'Choisir un type remise...',
                                    listeners: {
                                        select: function (cmp) {
                                            Ext.getCmp('lg_REMISE_ID').getStore().getProxy().url = url_services_data_remise_dovente + "?lg_TYPE_REMISE_ID=" + cmp.getValue();
                                            Ext.getCmp('lg_REMISE_ID').getStore().reload();
                                            Ext.getCmp('lg_REMISE_ID').setValue("Aucun");

                                        }
                                    }
                                },
                                {
                                    xtype: 'combobox',
                                    fieldLabel: 'REMISE ',
                                    flex: 1,
                                    labelWidth: 100,
                                    name: 'lg_REMISE_ID',
                                    value: 'Aucun',
                                    id: 'lg_REMISE_ID',
                                    store: store_remise,
                                    valueField: 'lg_REMISE_ID',
                                    displayField: 'str_NAME',
                                    queryMode: 'remote',
                                    emptyText: 'Choisir une remise...',
                                    listeners: {
                                        select: function (cmp) {
                                            var lg_PREENREGISTREMENT_ID = Ext.getCmp('lg_PREENREGISTREMENT_ID').getValue();
                                            if (lg_PREENREGISTREMENT_ID !== "0") {
                                                LaborexWorkFlow.updateRemise(url_transaction_vente + 'remise', lg_PREENREGISTREMENT_ID, cmp.getValue());
                                            }
                                        }
                                    }
                                }
                            ]
                        }
                    ]
                },
                //fin information stock produit, type remise et remise

                //liste des produits de la vente
                {
                    xtype: 'fieldset',
                    title: '<span style="color:blue;">LISTE DES ARTICLES CHOISIS</span>',
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
                            id: 'gridpanelID_dovente',
                            plugins: [this.cellEditing],
                            margin: '0 0 5 0',
                            store: store_details,
                            height: 250,
                            columns: [
                                {
                                    xtype: 'rownumberer',
                                    text: 'LG',
                                    width: 45,
                                    sortable: true
                                }, {
                                    text: 'C.CIP',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'int_CIP'
                                }, {
                                    text: 'DESIGNATION',
                                    flex: 2.5,
                                    sortable: true,
                                    dataIndex: 'str_FAMILLE_NAME'
                                }, {
                                    header: 'QD',
                                    dataIndex: 'int_QUANTITY',
                                    flex: 1,
                                    editor: {
                                        xtype: 'numberfield',
                                        allowBlank: true,
                                        minValue: 1,
                                        maskRe: /[0-9.]/,
                                        selectOnFocus: true,
                                        hideTrigger: true
                                    }
                                }, {
                                    text: 'QS',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'int_QUANTITY_SERVED',
                                    MaskRe: /[0-9.]/,
                                    minValue: 0,
                                    editor: {
                                        xtype: 'numberfield',
                                        allowBlank: true,
//                                        minValue: 1,
                                        maskRe: /[0-9.]/,
                                        selectOnFocus: true,
                                        hideTrigger: true
                                    },
                                    renderer: function (value, metadata, record) {
                                        if (record.get('b_IS_AVOIR') === true) {
                                            value = '<span style="color: red;font-weight: bold;">' + value + '</span>';
                                        }
                                        return value;
                                    }
                                }, {
                                    text: 'P.U',
                                    id: 'P_BT_UPDATE_PRICE_EDIT',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'int_FAMILLE_PRICE',
                                    renderer: amountformat,
                                    align: 'right',
                                    editor: {
                                        xtype: 'numberfield',
                                        MaskRe: /[0-9.]/,
                                        minValue: 1,
                                        allowBlank: true,
                                        selectOnFocus: true,
                                        hideTrigger: true
                                    }

                                }, {
                                    text: 'UG',
                                    flex: 0.4,
                                    sortable: true,
                                    align: 'center',
                                    dataIndex: 'int_FREE_PACK_NUMBER'
                                }, {
                                    text: 'MONTANT',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'int_PRICE_DETAIL',
                                    renderer: amountformat,
                                    align: 'right'
                                },
                                {
                                    text: 'EN PROMOTION',
                                    flex: 1,
                                    hidden: true,
                                    dataIndex: 'bl_PROMOTED',
                                    align: 'center',
                                    renderer: function (value, metadata, record) {
                                        return record.get('bl_PROMOTED') == true ? "Oui" : "Non";
                                    }
                                },
                                {
                                    xtype: 'actioncolumn',
                                    width: 30,
                                    sortable: false,
                                    menuDisabled: true,
                                    items: [{
                                            icon: 'resources/images/icons/fam/delete.png',
                                            tooltip: 'Supprimer le produit',
                                            scope: this,
                                            handler: LaborexWorkFlow.onRemoveClick,
                                            getClass: function (value, metadata, record) {
                                                if (isAvoir === true) {
                                                    return 'x-hide-display';
                                                }
                                            }
                                        }]
                                }],
                            tbar: [
                                {
                                    xtype: 'textfield',
                                    id: 'rechecher_product',
                                    name: 'rechecher_product',
                                    emptyText: 'Recherche d\'un produit',
                                    width: '30%',
                                    listeners: {
                                        'render': function (cmp) {
                                            cmp.getEl().on('keypress', function (e) {
                                                if (e.getKey() === e.ENTER) {
                                                    Me_dovente.onRechClick();
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
                                }
                            ],
                            bbar: {
                                dock: 'bottom',
                                items: [
                                    {
                                        xtype: 'pagingtoolbar',
                                        displayInfo: true,
                                        flex: 2,
                                        displayMsg: 'nombre(s) de produit(s): {2}',
                                        pageSize: itemsPerPage,
                                        store: store_details, // same store GridPanel is using
                                        listeners: {
                                            beforechange: function (page, currentPage) {
                                                var myProxy = this.store.getProxy();
                                                myProxy.params = {
                                                    search_value: '',
                                                    lg_PREENREGISTREMENT_ID: ''
                                                };

                                                myProxy.setExtraParam('lg_PREENREGISTREMENT_ID', Ext.getCmp('lg_PREENREGISTREMENT_ID').getValue());
                                                myProxy.setExtraParam('search_value', Ext.getCmp('rechecher_product').getValue());
                                            }

                                        }
                                    },
                                    {
                                        xtype: 'tbseparator'
                                    },
                                    {
                                        xtype: 'displayfield',
                                        fieldLabel: 'TOTAL VENTE :',
                                        name: 'int_PRICE',
                                        id: 'int_PRICE',
                                        flex: 1,
                                        labelWidth: 120,
                                        fieldStyle: "color:blue;font-weight: bold;font-size: 1.3em",
                                        margin: '0 15 0 0',
                                        value: 0
                                    }
                                ]
                            },
                            listeners: {
                                scope: this,
                                //selectionchange: this.onSelectionChange,
                                beforeedit: function (editor, e) {
                                    /*if (e.record.get('int_AVOIR') == 0 && e.record.get('b_IS_AVOIR') == false && e.record.get('str_STATUT') == "is_Closed") {
                                     return false;
                                     }*/

                                    //a rechercher comment empecher la modification d'un cellule
                                    // e.record.get('bool_UPDATE_PRICE') == false

                                    if (e.record.get('str_STATUT') === "is_Closed") {
                                        return false;
                                    }
                                    if (e.record.get('bool_UPDATE_PRICE') == false && e.colIdx == 5) {
                                        return false;
                                    }
                                }
                            }
                        }
                    ]
                },
                //fin liste des produits de la vente

                //gestion du reglement,
                {
                    xtype: 'fieldset',
                    labelAlign: 'right',
                    title: '<span style="color:blue;">REMISE</span>',
                    id: 'reglementDevis',
                    layout: 'anchor',
                    hidden: true,
                    cls: 'background_green',
                    defaults: {
                        anchor: '100%'
                    },
                    collapsible: true,
                    defaultType: 'textfield',
                    items: [
                        {
                            xtype: 'container',
                            layout: 'hbox',
                            defaultType: 'textfield',
                            margin: '0 0 2 0',
                            items: [
                                {

                                    xtype: 'displayfield',
                                    fieldLabel: 'MONTANT REMISE :',
                                    labelWidth: 130,
                                    name: 'int_REMISE_DEVIS',
                                    id: 'int_REMISE_DEVIS',
                                    flex: 1,
                                    fieldStyle: "color:green;font-size:1.5em;font-weight: bold;",
                                    margin: '0 10 0 0',
                                    value: 0 + ' CFA'

                                }]
                        }]
                },
                {
                    xtype: 'fieldset',
                    labelAlign: 'right',
                    title: '<span style="color:blue;">REGLEMENT</span>',
                    id: 'reglementID',
                    layout: 'anchor',
                    hidden: true,
                    cls: 'background_green',
                    defaults: {
                        anchor: '100%'
                    },
                    collapsible: true,
                    defaultType: 'textfield',
                    items: [
                        {
                            xtype: 'container',
                            layout: 'hbox',
                            defaultType: 'textfield',
                            margin: '0 0 2 0',
                            items: [
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: 'NET A PAYER :',
                                    flex: 1,
                                    labelWidth: 110,
                                    name: 'int_NET_A_PAYER_RECAP',
                                    id: 'int_NET_A_PAYER_RECAP',
                                    fieldStyle: "color:red;font-size:1.5em;font-weight: bold;",
                                    margin: '0 40 0 0',
                                    value: 0 + ' CFA'
                                },
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: 'MONTANT REMISE :',
                                    labelWidth: 130,
                                    name: 'int_REMISE',
                                    id: 'int_REMISE',
                                    flex: 1,
                                    fieldStyle: "color:green;font-size:1.5em;font-weight: bold;",
                                    margin: '0 10 0 0',
                                    value: 0 + ' CFA'
                                },
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: 'PART TIERS PAYANT :',
                                    labelWidth: 140,
                                    flex: 1,
                                    name: 'int_PART_TIERSPAYANT',
                                    id: 'int_PART_TIERSPAYANT',
                                    fieldStyle: "color:blue;font-size:1.5em;font-weight: bold;",
                                    margin: '0 10 0 0',
                                    value: 0 + ' CFA'
                                },
                                {
                                    xtype: 'combobox',
                                    labelWidth: 130,
                                    fieldLabel: 'TYPE R&Egrave;GLEMENT',
                                    name: 'lg_TYPE_REGLEMENT_ID',
                                    id: 'lg_TYPE_REGLEMENT_ID',
                                    store: store_typereglement,
                                    flex: 1,
                                    value: 'Especes',
                                    valueField: 'lg_TYPE_REGLEMENT_ID',
                                    displayField: 'str_NAME',
                                    editable: false,
                                    queryMode: 'remote',
                                    allowBlank: true,
                                    emptyText: 'Choisir un type de reglement...',
                                    listeners: {
                                        select: function (cmp) {
                                            var val = cmp.getValue();
                                            if (val === '7') {
                                                //console.log('Mobile payment selected');
                                                var montantCFA = Ext.getCmp('int_PRICE').getValue();
                                                // Me_dovente.loginToSICAPAY(montantCFA.split(' ')[0]);
                                                Ext.getCmp('btn_verif_mobile').show();
                                                Me_dovente.openMobilePaymentWindow(montantCFA.split(' ')[0]);
                                            } else {
                                                Ext.getCmp('btn_verif_mobile').hide();
                                            }

                                            LaborexWorkFlow.ManagneTypereglement(val, Ext.getCmp('int_AMOUNT_RECU').getValue());
                                        }
                                    }
                                }
                            ]
                        },
                        {
                            xtype: 'container',
                            layout: 'hbox',
                            defaultType: 'textfield',
                            id: 'info_cheque_carte',
                            margin: '0 0 2 0',
                            items: [
                                {
                                    name: 'str_NOM',
                                    id: 'str_NOM',
                                    hidden: true,
                                    margin: '0 10 0 0',
                                    labelWidth: 110,
                                    fieldLabel: 'NOM',
                                    flex: 1
                                },
                                {
                                    name: 'str_BANQUE',
                                    id: 'str_BANQUE',
                                    margin: '0 10 0 0',
                                    labelWidth: 130,
                                    fieldLabel: 'BANQUE',
                                    hidden: true,
                                    flex: 1
                                },
                                {
                                    name: 'str_LIEU',
                                    id: 'str_LIEU',
                                    labelWidth: 130,
                                    margin: '0 10 0 0',
                                    hidden: true,
                                    fieldLabel: 'LIEU',
                                    flex: 1
                                },
                                {
                                    xtype: 'combobox',
                                    labelWidth: 130,
                                    fieldLabel: 'CODE DEVISE',
                                    name: 'str_CODE_MONNAIE',
                                    id: 'str_CODE_MONNAIE',
                                    store: store_devise,
                                    value: 'CFA',
                                    margin: '0 10 0 0',
                                    hidden: true,
                                    valueField: 'lg_DEVISE_ID',
                                    displayField: 'str_DESCRIPTION',
                                    editable: false,
                                    queryMode: 'remote',
                                    allowBlank: true,
                                    emptyText: 'Choisir une devise...',
                                    listeners: {
                                        select: function (cmp) {
                                            var val = cmp.getValue();
                                            var record = cmp.findRecord(cmp.valueField || cmp.displayField, val); //recupere la ligne de l'element selectionné
                                            Ext.getCmp('int_TAUX_CHANGE').setValue(record.get('int_TAUX'));
                                        }
                                    }
                                },
                                {
                                    xtype: 'hiddenfield',
                                    name: 'int_TAUX_CHANGE',
                                    id: 'int_TAUX_CHANGE',
                                    value: 1
                                }
                            ]
                        },
                        {
                            xtype: 'container',
                            layout: 'hbox',
                            id: 'info_encaissement',
                            defaultType: 'textfield',
                            margin: '0 0 2 0',
                            items: [
                                {
                                    name: 'int_AMOUNT_RECU',
                                    id: 'int_AMOUNT_RECU',
                                    fieldLabel: 'MONTANT RECU',
                                    emptyText: 'Montant recu',
                                    flex: 1,
                                    labelWidth: 110,
                                    regex: /[0-9.]/,
                                    margin: '0 30 0 0',
                                    minValue: 0,
//                                    value: 0,
                                    allowBlank: true,
                                    enableKeyEvents: true,
                                    selectOnFocus: true,
                                    listeners: {
                                        change: function (cmp) {
                                            LaborexWorkFlow.CalculateMontantRemis(Ext.getCmp('lg_TYPE_REGLEMENT_ID').getValue(), cmp.getValue());
                                        },
                                        specialKey: function (field, e) {
                                            if (e.getKey() === e.ENTER || e.getKey() === 13) {
                                                LaborexWorkFlow.onbtncloturer();
                                            }
                                        },
                                        'render': function (cmp) {
                                            cmp.getEl().on('click', function (e) {
                                                if (Ext.getCmp('authorize_cloture_vente').getValue() == '0') {
                                                    LaborexWorkFlow.ShowNetPaid(url_transaction_vente + 'shownetpay');
                                                }
                                            });
                                        }

                                    }
                                },
                                {
                                    xtype: 'displayfield',
                                    labelWidth: 130,
                                    margin: '0 20 0 0',
                                    fieldLabel: 'MONNAIE :',
                                    name: 'int_AMOUNT_REMIS',
                                    id: 'int_AMOUNT_REMIS',
                                    fieldStyle: "color:blue;font-size:1.5em;font-weight: bold;",
                                    value: 0 + " CFA",
                                    align: 'right'
                                },
                                {
                                    xtype: 'checkbox',
                                    labelWidth: 130,
                                    margin: '0 10 0 0',
                                    flex: 1,
                                    fieldLabel: 'Vente sans bon',
                                    name: 'b_WITHOUT_BON',
                                    id: 'b_WITHOUT_BON'
                                },
                                {
                                    xtype: 'displayfield',
                                    flex: 1,
                                    labelWidth: 130,
                                    fieldLabel: 'Derni&egrave;re Monnaie :',
                                    name: 'int_AMOUNT_REMIS_LAST',
                                    id: 'int_AMOUNT_REMIS_LAST',
                                    fieldStyle: "color:blue;font-size:1.5em;font-weight: bold;",
//                                    margin: '0 15 0 15',
                                    value: 0 + " CFA",
                                    align: 'right'
                                }
                            ]
                        }
                    ]
                },
                //fin gestion du reglement

                //gestion avoir et facture
                {
                    xtype: 'fieldset',
                    title: '<span style="color:blue;">INFORMATIONS AVOIR</span>',
                    collapsible: true,
                    id: 'infoFacture',
                    cls: 'background_gray',
                    defaultType: 'textfield',
                    hidden: true,
                    layout: 'anchor',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            xtype: 'container',
                            layout: 'hbox',
                            defaultType: 'textfield',
                            margin: '0 0 5 0',
                            items: [
                                {
                                    fieldLabel: 'NOM',
                                    emptyText: 'Nom du porteur',
                                    name: 'str_FIRST_NAME_FACTURE',
                                    id: 'str_FIRST_NAME_FACTURE',
                                    flex: 1,
                                    margin: '0 15 0 0',
                                    enableKeyEvents: true,

                                    listeners: {

                                        specialKey: function (field, e) {
                                            if (e.getKey() === e.ENTER || e.getKey() === 13) {

                                                Ext.getCmp('str_LAST_NAME_FACTURE').focus();
                                            }
                                        }}
                                },
                                {
                                    fieldLabel: 'PRENOM(s)',
                                    emptyText: 'Prenom du porteur',
                                    name: 'str_LAST_NAME_FACTURE',
                                    id: 'str_LAST_NAME_FACTURE',
                                    flex: 1,
                                    margin: '0 15 0 0',
                                    enableKeyEvents: true,

                                    listeners: {

                                        specialKey: function (field, e) {
                                            if (e.getKey() === e.ENTER || e.getKey() === 13) {

                                                Ext.getCmp('int_NUMBER_FACTURE').focus();
                                            }
                                        }}





                                },
                                {
                                    fieldLabel: 'T&Eacute;L&Eacute;PHONE',
                                    emptyText: 'Telephone du porteur',
                                    name: 'int_NUMBER_FACTURE',
                                    id: 'int_NUMBER_FACTURE',
                                    flex: 1,
                                    margin: '0 15 0 0',
                                    enableKeyEvents: true,

                                    listeners: {

                                        specialKey: function (field, e) {
                                            if (e.getKey() === e.ENTER || e.getKey() === 13) {
                                                LaborexWorkFlow.onbtncloturer();
                                            }
                                        },
                                        'render': function (cmp) {
                                            cmp.getEl().on('click', function (e) {
                                                if (Ext.getCmp('authorize_cloture_vente').getValue() == '0') {
                                                    LaborexWorkFlow.ShowNetPaid(url_transaction_vente + 'shownetpay');
                                                }
                                            });
                                        }

                                    }



                                }
                            ]
                        }
                    ]
                },
                //fin gestion avoir et facture

                {
                    xtype: 'toolbar',
                    ui: 'footer',
                    dock: 'bottom',
                    border: '0',
                    items: ['->',
                        {
                            text: 'Enregistrer Devis',
                            id: 'btn_devis',
                            iconCls: 'icon-clear-group',
                            scope: this,
                            hidden: true,
                            // disabled: true,
                            handler: function () {
                                LaborexWorkFlow.onbtndevis(url_transaction_vente + 'devis');
                            }
                        }

                        ,
                        {
                            text: 'VERIF. PAIMENT MOBILE',
                            id: 'btn_verif_mobile',
                            hidden: true,
                            // iconCls: 'afficheur_caisse',
                            scope: this,
                            handler: function () {
                                Me_dovente.checkIfPurchaseExistsForToken();

                            }

                        },
                        {
                            text: 'AFFICHER NET A PAYER',
                            id: 'btn_afficheur_caisse',
                            hidden: true,
                            iconCls: 'afficheur_caisse',
                            scope: this,
                            handler: function () {

                                LaborexWorkFlow.ShowNetPaid(url_transaction_vente + 'shownetpay');
                            }

                        }, {
                            text: 'Terminer vente',
                            id: 'btn_loturer',
                            iconCls: 'icon-clear-group',
                            scope: this,
                            hidden: true,
                            disabled: true,
                            handler: function () {
                                LaborexWorkFlow.onbtncloturer();
                            }

                        }, {
                            text: 'Retour',
                            id: 'btn_go_back',
                            iconCls: 'icon-clear-group',
                            scope: this,
                            handler: function () {
                                Me_dovente.GoBack();
                            }

                        }]
                }
            ]
        });





        this.callParent();

        if (Me_dovente.getNameintern() != "0") {
            LaborexWorkFlow.LoadApp(url_init_vente, Me_dovente.getNameintern(), Me_dovente.getOdatasource());
        }
        Me_dovente.ViewLoading(Me_dovente.getTitre());
//        LaborexWorkFlow.LoadApp(init_url, ref);
        LaborexWorkFlow.OnGridEditor(url_transaction_vente + "update");
        //Iit 
    },
    ViewLoading: function (title) {
        if (title === "PRE-VENTE") {
            Ext.getCmp('lg_USER_VENDEUR_ID').hide();
            Ext.getCmp('reglementID').show();
            Ext.getCmp('lg_TYPE_REGLEMENT_ID').hide();
            Ext.getCmp('info_encaissement').hide();
            Ext.getCmp('btn_afficheur_caisse').show();
        } else if (title === "VENTE AU COMPTANT" || title === "VENTE A CREDIT" || title === "TRANFORMER UN DEVIS EN VENTE" || title === "ECRAN DE VENTE") {
            Ext.getCmp('reglementID').show();
            Ext.getCmp('btn_afficheur_caisse').show();
            Ext.getCmp('btn_loturer').show();
        } else if (title === "AVOIR") {
            Ext.getCmp('btn_loturer').show();
            Ext.getCmp('btn_loturer').setText("Terminer avoir");
            LaborexWorkFlow.isAvoir = true;
        } else if (title === "PROFORMA") {

            this.isPROFORMA = true;
            isDEVIS = true;

            Ext.getCmp('btn_devis').show();
            Ext.getCmp('fieldset_assure').setTitle('<span style="color:blue;">CLIENT</span>');
            Ext.getCmp('fieldset_identification_client').show();
            Ext.getCmp('fieldset_assure').show();
            Ext.getCmp('btn_go_back').hide();
            Ext.getCmp('btn_modifier_info').hide();
            //   Ext.getCmp('lg_TYPE_VENTE_ID').disable();
            Ext.getCmp('lg_TYPE_VENTE_ID');
        } else if ("EDITION DE PROFORMA" === title) {
            this.isPROFORMA = true;
            isDEVIS = true;
            Ext.getCmp('btn_devis').show();
            Ext.getCmp('fieldset_assure').setTitle('<span style="color:blue;">CLIENT</span>');

            Ext.getCmp('fieldset_assure').show();
            Ext.getCmp('btn_go_back').hide();
            Ext.getCmp('btn_modifier_info').hide();
//            Ext.getCmp('lg_TYPE_VENTE_ID').disable();
        }

    },
    openMobilePaymentWindow: function (montant) {

        Ext.Ajax.request({
            url: '/laborex/mobile',
//           url: 'http://192.168.2.158:9010/purchases',
//           url: 'http://40.85.182.75:9010/purchases',
            params: {
                amount: montant,
                commande: "Paiement mobile",
                currency: "XOF",
                X_SYCA_MERCHANDID: "C_57EC174E7C4BC",
                X_SYCA_APIKEY: "pk_syca_ea4072e471431d19ca7b852a9214ba0760f16980",
                urls: "http://40.85.182.75:9010/purchases",
                urlc: "http://40.85.182.75:8090",
                typpaie: "payement"
            },
            success: function (response) {
                console.log(response.responseText);
                var jsonResponse = Ext.JSON.decode(response.responseText);
                if (jsonResponse.code_statut == "1") {
                    window.open('../cycapay.jsp');
                }

            },
            failure: function (error) {
                console.error(error);
            }
        });
    },

    getCookie: function (cname) {
        var name = cname + "=";
        var ca = document.cookie.split(';');
        for (var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) === ' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name) === 0) {
                return c.substring(name.length, c.length);
            }
        }
        return "";
    },
    setCookie: function (cname, cvalue, exdays) {
        var d = new Date();
        d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
        var expires = "expires=" + d.toUTCString();
        document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
    },
    checkCookie: function () {
        var username = getCookie("username");
        if (username !== "") {
            alert("Welcome again " + username);
        } else {
            username = prompt("Please enter your name:", "");
            if (username !== "" && username !== null) {
                Me_dovente.setCookie("username", username, 365);
            }
        }
    },
    checkIfPurchaseExistsForToken: function () {
        var token = Me_dovente.getCookie("token");
        if (token !== null || token !== "") {

            Ext.Ajax.request({
                url: '../webservices/MOBILES/purchases/ws_purchases_data.jsp?str_qty=single&str_PURCHASE_TOKEN=' + token,
                success: function (response) {
                    var jsonResponse = Ext.JSON.decode(response.responseText);
                    console.log("response: ", jsonResponse, " and its type is: " + typeof (jsonResponse));
                    if (jsonResponse.success === false) { // purchase does not exist
                        // setInterval(Me_dovente.checkIfPurchaseExistsForToken, 3000);
                        Ext.MessageBox.show({
                            title: 'Mobile Payment',
                            width: 320,
                            msg: 'Paiment mobile pas encore effectué',
                            buttons: Ext.MessageBox.OK,
                            icon: Ext.MessageBox.WARNING
                        });
                    } else { // purchase found
                        //clearInterval();
                        Ext.MessageBox.show({
                            title: 'Mobile Payment',
                            width: 320,
                            msg: 'Paiment mobile effectué',
                            buttons: Ext.MessageBox.OK,
                            icon: Ext.MessageBox.SUCC
                        });
                    }
                },
                failure: function (error) {

                }
            });
        }
    },
    onSelectionChange: function (model, records) {
        var rec = records[0];
        if (rec) {
            this.getForm().loadRecord(rec);
            // alert("int_FAMILLE_PRICE:"+rec.get('int_FAMILLE_PRICE'));
        }
    },
    ViewInitialize: function () {
        LaborexWorkFlow = Ext.create('testextjs.controller.LaborexWorkFlow', {});

        Me_dovente.setTitle(Me_dovente.getTitre());
    },
    EndVente: function () {

        //  var cust_is_solvable = Ext.getCmp('lg_SOLDE_CLT_ID').getValue();
        var strtypevente_end = Ext.getCmp('lg_TYPE_VENTE_ID').getValue();
        //A decommenter en cas de problèmes
        //        if ((strtypevente_end === "2" || strtypevente_end === "ASSURANCE_MUTUELLE") || (strtypevente_end === "3" || strtypevente_end === "CARNET")) {
        if (strtypevente_end === "3" || strtypevente_end === "CARNET") {


            LaborexWorkFlow.onbtncloturer();
            Ext.getCmp('btn_loturer').disable();
        } else {

            LaborexWorkFlow.onbtncloturer();
            Ext.getCmp('btn_loturer').disable();

        }




        Ext.getCmp('str_LIEU').hide();
        Ext.getCmp('str_BANQUE').hide();
        Ext.getCmp('str_NOM').hide();
        Ext.getCmp('int_TAUX_CHANGE').hide();
        Ext.getCmp('str_CODE_MONNAIE').hide();
        Ext.getCmp('int_AMOUNT_REMIS').show();

        Ext.getCmp('lg_MODE_REGLEMENT_ID').hide();
        var infoFacture = Ext.getCmp('infoFacture');
        if (infoFacture.isVisible()) {
            infoFacture.hide();
        }



        Me_dovente.setTitle("VENTE");



    },
    VerifyModeReglement: function () {
        var typeReg = Ext.getCmp('lg_TYPE_REGLEMENT_ID').getValue();
        var typevente = Ext.getCmp('lg_TYPE_VENTE_ID').getValue();
        var ModReg = Ext.getCmp('lg_MODE_REGLEMENT_ID').getValue();
        if ((typeReg !== "1" && ModReg === null) && (typeReg !== "Especes" && ModReg === null)) {

            Ext.MessageBox.show({title: 'Avertissement',
                width: 320,
                msg: 'Veuillez choisir le mode reglement',
                buttons: Ext.MessageBox.OK,
                icon: Ext.MessageBox.WARNING});

            return;
        } else if ((typevente === "2" && str_REFBON === "") || (typevente === "3" && str_REFBON === "")) {

            Ext.MessageBox.show({
                title: 'Avertissement',
                width: 320,
                msg: 'Veuillez renseignez la r&eacute;f&eacute;rence du bon', buttons: Ext.MessageBox.OK,
                icon: Ext.MessageBox.WARNING});

            return;
        }
        var lg_TYPEVENTEID = Ext.getCmp('lg_TYPE_VENTE_ID').getValue();
        var lgTYPEREGLEMENTID = Ext.getCmp('lg_TYPE_REGLEMENT_ID').getValue();

        var strLASTNAME = Ext.getCmp('str_LAST_NAME').getValue();

        if ((lg_TYPEVENTEID === "1" || lg_TYPEVENTEID === "AU COMPTANT") && lgTYPEREGLEMENTID === "4") {
            if (strLASTNAME === "") {
                Ext.MessageBox.show({
                    title: 'Avertissement',
                    width: 320,
                    msg: 'Veuillez renseigner les informations du clients',
                    buttons: Ext.MessageBox.OK, icon: Ext.MessageBox.WARNING,
                    fn: function (buttonId) {
                        if (buttonId === "ok") {
                            Ext.getCmp('lg_CLIENT_ID').focus(false, 100, function () {

                            });
                        }
                    }
                });

                return;

            }
        }
        var infoFacture = Ext.getCmp('infoFacture');
        if (infoFacture.isVisible()) {
            var int_NUMBER_FACTURE = Ext.getCmp('int_NUMBER_FACTURE').getValue(), str_LAST_NAME_FACTURE = Ext.getCmp('str_LAST_NAME_FACTURE').getValue(), str_FIRST_NAME_FACTURE = Ext.getCmp('str_FIRST_NAME_FACTURE').getValue();
            if (int_NUMBER_FACTURE === "" || str_LAST_NAME_FACTURE === "" || str_FIRST_NAME_FACTURE === "") {
                Ext.MessageBox.show({
                    title: 'Avertissement',
                    width: 320,
                    msg: "Veuillez renseigner les informations sur l'avoir",
                    buttons: Ext.MessageBox.OK, icon: Ext.MessageBox.WARNING,
                    fn: function (buttonId) {
                        if (buttonId === "ok") {
                            Ext.getCmp('str_FIRST_NAME_FACTURE').focus(false, 100, function () {
                                // this.setFieldStyle('border: 2px solid #C0C0C0; background: #FFFFFF;');
                            });

                        }
                    }


                });
                return;
            }
        }
        Me_dovente.EndVente();
    },
    GoBack: function () {
        LaborexWorkFlow.venteTierspayant = [];
        var xtype = "cloturerventemanager";

        if (Me_dovente.getTitre() === "PRE-VENTE") {
            xtype = "preenregistrementmanager";
        } else if (Me_dovente.getTitre() === "PROFORMA" || Me_dovente.getTitre() === "TRANFORMER UN DEVIS EN VENTE") {
            xtype = "devismanager";
        } else if (Me_dovente.getTitre() === "PROFORMA" || Me_dovente.getTitre() === "EDITER LE DEVIS") {
            xtype = "devismanager";
        } else if (Me_dovente.getTitre() === "AVOIR") {
            xtype = "venteavoirmanager";
        }
        testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "", "", "");
    },
    ShowWaitingProcess: function () {
        boxWaitingProcess = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
    }
    , StopWaitingProcess: function () {
        boxWaitingProcess.hide();
    },
    onRechClick: function () {
        Ext.getCmp('gridpanelID_dovente').getStore().load({
            params: {
                search_value: Ext.getCmp('rechecher_product').getValue(),
                lg_PREENREGISTREMENT_ID: Ext.getCmp('lg_PREENREGISTREMENT_ID').getValue()
            }
        }, url_services_data_detailsvente);
    },
    doActionAfterDialogMessage: function (task) {

        if (task === 'TYPE_VENTE') {
            Ext.getCmp('lg_TYPE_VENTE_ID').setValue(Ext.getCmp('lg_TYPE_VENTE_ID_HIDE').getValue());

        }
    },
    processPromotedProduct: function (responseJSON) {
        str_TYPE = responseJSON['str_TYPE'];
        console.log("str_TYPE: " + str_TYPE);
        if (str_TYPE === 'REMISE' || str_TYPE === 'PRIX SPECIAL') {
            db_PROMOTION_PRICE = responseJSON['db_PROMOTION_PRICE'];
            console.log("db_PROMOTION_PRICE: " + db_PROMOTION_PRICE);
        } else if (str_TYPE === 'UNITES GRATUITES') {
            int_PACK_NUMBER = responseJSON['int_PACK_NUMBER'];
            int_ACTIVE_AT = responseJSON['int_ACTIVE_AT'];
            console.log("int_PACK_NUMBER: " + int_PACK_NUMBER + "  int_ACTIVE_AT: " + int_ACTIVE_AT);


        } else {

        }


    },
    onClearStore: function () {
        Ext.getCmp('gridpanelID_dovente').getStore().removeAll();
    }
});


//code ajouté
function removeTierspayantToVente(id_label_tp, id_compteclient_tp, id_ref_bon) {
    alert('id_label_tp:' + id_label_tp + '|id_compteclient_tp:' + id_compteclient_tp + '|id_ref_bon:' + id_ref_bon);
//    return;
//    LaborexWorkFlow.removeTierspayantToVente(Ext.getCmp('lg_PREENREGISTREMENT_ID').getValue(), id_label_tp, id_compteclient_tp, id_ref_bon);
}


function resetComponentValue(mode, component, RC_ID) {
    var OCurrentComponentHidden = "";
    var str_cptclt_id_edit = "";
    var str_ro_edit = Ext.getCmp('RO_ID').getValue();
    var str_rc1_edit = Ext.getCmp('RC1_ID').getValue();
    var str_rc2_edit = Ext.getCmp('RC2_ID').getValue();
    var str_rc3_edit = Ext.getCmp('RC3_ID').getValue();
    var int_TAUX = 0;
    if (mode === "delete") {
        if (RC_ID === 'RO_ID') {
            str_ro_edit = "";
        } else if (RC_ID === 'RC1_ID') {
            str_rc1_edit = "";
        } else if (RC_ID === 'RC2_ID') {
            str_rc2_edit = "";
        } else if (RC_ID === 'RC3_ID') {
            str_rc3_edit = "";
        }
    }


    if (str_ro_edit !== "" && str_ro_edit !== undefined && str_ro_edit !== null) {
        str_cptclt_id_edit = str_ro_edit + ";" + str_cptclt_id_edit;
        int_TAUX += LaborexWorkFlow.onsplitovalueother(Ext.getCmp('RO').getValue(), '--', 1);
    }
    if (str_rc1_edit !== "" && str_rc1_edit !== undefined && str_rc1_edit !== null) {
        str_cptclt_id_edit = str_rc1_edit + ";" + str_cptclt_id_edit;
        int_TAUX += LaborexWorkFlow.onsplitovalueother(Ext.getCmp('RC1').getValue(), '--', 1);

    }
    if (str_rc2_edit !== "" && str_rc2_edit !== undefined && str_rc2_edit !== null) {
        str_cptclt_id_edit = str_rc2_edit + ";" + str_cptclt_id_edit;
        int_TAUX += LaborexWorkFlow.onsplitovalueother(Ext.getCmp('RC2').getValue(), '--', 1);
    }
    if (str_rc3_edit !== "" && str_rc3_edit !== undefined && str_rc3_edit !== null) {
        str_cptclt_id_edit = str_rc3_edit + ";" + str_cptclt_id_edit;
        int_TAUX += LaborexWorkFlow.onsplitovalueother(Ext.getCmp('RC3').getValue(), '--', 1);
    }
    Ext.getCmp('int_TAUX').setValue(int_TAUX);


    var remise_id = Ext.getCmp('lg_REMISE_ID').getValue();
    var Ovalue_add_url = '../webservices/sm_user/detailsvente/ws_transaction.jsp?mode=updatertp';
    var oref_vente_edit = Ext.getCmp('str_ref_vente_hidden').getValue();
    if (mode !== "remise") {
        if (mode === "delete") {
            OCurrentComponentHidden = LaborexWorkFlow.GetComponentById(RC_ID);
            Ext.MessageBox.confirm('Message',
                    'Voulez-vous vraiment retirer ce tiers payant',
                    function (btn) {
                        if (btn === 'yes') {
                            if (LaborexWorkFlow.GetComponentById('gridpanelID_dovente').getStore().getCount() > 0) {
                                LaborexWorkFlow.updateVenteByTierpayantAndRemise(Ovalue_add_url, oref_vente_edit, mode, remise_id, str_cptclt_id_edit, OCurrentComponentHidden.getValue(), component, OCurrentComponentHidden);
                            } else {
                                LaborexWorkFlow.SetComponentValue(component, "");
                                OCurrentComponentHidden.setValue("");
                            }

                        }
                    });
        }
        if (mode === "add") {
            if (LaborexWorkFlow.GetComponentById('gridpanelID_dovente').getStore().getCount() > 0) {
                LaborexWorkFlow.updateVenteByTierpayantAndRemise(Ovalue_add_url, oref_vente_edit, mode, remise_id, str_cptclt_id_edit);
            }

        }
    } else {
        if (LaborexWorkFlow.GetComponentById('gridpanelID_dovente').getStore().getCount() > 0) {
            LaborexWorkFlow.updateVenteByTierpayantAndRemise(Ovalue_add_url, oref_vente_edit, mode, remise_id, str_cptclt_id_edit);
        }
    }



}
//fin code ajouté
