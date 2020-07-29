/* global Ext */

var url_services_data_modereglement_mvt_caisse = '../webservices/sm_user/modereglement/ws_data.jsp';
var url_services_data_typereglement_mvt_caisse = '../webservices/sm_user/typereglement/ws_data.jsp';
var url_services_data_typemvtcaisse_mvt_caisse = '../webservices/sm_user/typemvtcaisse/ws_data.jsp';
var url_services_data_mvtcaisse_mvt_caisse = '../webservices/sm_user/mvtcaisse/ws_data.jsp';
var url_services_transaction_mvtcaisse_mvt_caisse = '../webservices/sm_user/mvtcaisse/ws_transaction.jsp?mode=';
var url_services_pdf_ticket_mouvement = '../webservices/sm_user/mvtcaisse/ws_generate_pdf.jsp';

var Me_add_mvt_caisse;
var Omode;
var ref;
var Oview;
var LaborexWorkFlow_Add_MvtCaisse;
Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.sm_user.mvtcaisse.action.addBis', {
    extend: 'Ext.window.Window',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.form.*',
        'Ext.layout.container.Column',
        'testextjs.model.MvtCaisse'

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
    xtype: 'addmvtcaissemanager',
    id: 'addmvtcaissemanagerID',
    title: 'Mouvement De Caisse',
    bodyPadding: 5,
    layout: 'column',
    initComponent: function () {



        Me_add_mvt_caisse = this;
        var itemsPerPage = 20;
        ref = this.getNameintern();
        titre = this.getTitre();
        ref = this.getNameintern();
        Omode = this.getMode();
        Oview = this.getParentview();
        LaborexWorkFlow_Add_MvtCaisse = Ext.create('testextjs.controller.LaborexWorkFlow', {});


        var store_modereglement_Add_MvtCaisse = LaborexWorkFlow_Add_MvtCaisse.BuildStore('testextjs.model.ModeReglement', itemsPerPage, url_services_data_modereglement_mvt_caisse);
        var store_typereglement_Add_MvtCaisse = LaborexWorkFlow_Add_MvtCaisse.BuildStore('testextjs.model.TypeReglement', itemsPerPage, url_services_data_typereglement_mvt_caisse);
        var store_typemvtcaisse_Add_MvtCaisse = LaborexWorkFlow_Add_MvtCaisse.BuildStore('testextjs.model.TypeEcartMvt', itemsPerPage, url_services_data_typemvtcaisse_mvt_caisse);

        var form = new Ext.form.Panel({
            bodyPadding: 10,
            //    id: 'AddCltGridId',

            fieldDefaults: {
                labelAlign: 'right',
                labelWidth: 115,
                msgTarget: 'side'
            },
            items: [
                {
                    xtype: 'container',
                    layout: 'vbox',
                    defaultType: 'textfield',
                    margin: '0 0 5 0',
                    items: [
                        {
                            xtype: 'container',
                            layout: 'hbox',
                            defaultType: 'textfield',
                            margin: '0 10 5 0',
                            items: [
                                {
                                    xtype: 'datefield',
                                    fieldLabel: 'Date Mouvement',
                                    id: 'dt_DATE_MVT',
                                    name: 'dt_DATE_MVT',
//                                    maxValue: new Date(),
                                    emptyText: 'Date Mouvement',
                                    submitFormat: 'Y-m-d'


                                },
                                {
                                    xtype: 'textfield',
                                    name: 'str_NUM_PIECE_COMPTABLE',
                                    id: 'str_NUM_PIECE_COMPTABLE',
                                    fieldLabel: 'Reference',
                                    emptyText: 'Reference',
                                    hidden: true,
//                                    allowBlank: false
                                }

                            ]
                        },
                        {
                            xtype: 'container',
                            layout: 'hbox',
                            defaultType: 'textfield',
                            margin: '0 10 5 0',
                            items: [
                                {
                                    xtype: 'combobox',
                                    fieldLabel: 'Type.Mouvement',
                                    name: 'lg_TYPE_MVT_CAISSE_ID',
                                    id: 'lg_TYPE_MVT_CAISSE_ID',
                                    store: store_typemvtcaisse_Add_MvtCaisse,
                                    valueField: 'lg_TYPE_MVT_CAISSE_ID',
                                    displayField: 'str_NAME',
                                    typeAhead: true,
                                    queryMode: 'remote',
                                    hidden: true,
//                                    allowBlank: false,
                                    emptyText: 'Choisir un type de mouvement...',
                                    listeners: {
                                        select: function (cmp) {
                                            var value = cmp.getValue();
                                            var record = cmp.findRecord(cmp.valueField || cmp.displayField, value); //recupere la ligne de l'element selectionné
                                            Ext.getCmp('str_NUM_COMPTE').setValue(record.get('str_CODE_COMPTABLE'));
                                        }
                                    }

                                }, {
                                    xtype: 'textfield',
                                    name: 'str_NUM_COMPTE',
                                    id: 'str_NUM_COMPTE',
                                    disabled: true,
                                    fieldLabel: 'Numero Compte',
                                    hidden: true,
                                    emptyText: 'Numero Compte',
//                                    allowBlank: false
                                }

                            ]
                        },
                        //**************

                        {
                            xtype: 'container',
                            layout: 'hbox',
                            defaultType: 'textfield',
                            margin: '0 10 5 0',
                            items: [
                                {
                                    xtype: 'combobox',
                                    hidden: true,
                                    fieldLabel: 'Type.Reglement',
                                    name: 'lg_TYPE_REGLEMENT_ID',
                                    id: 'lg_TYPE_REGLEMENT_ID',
                                    store: store_typereglement_Add_MvtCaisse,
                                    value: 'Especes',
                                    valueField: 'lg_TYPE_REGLEMENT_ID',
                                    displayField: 'str_NAME',
                                    typeAhead: true,
                                    queryMode: 'remote',
//                                    allowBlank: false,
                                    emptyText: 'Choisir un type de reglement...',
                                    listeners: {
                                        select: function (cmp) {
                                            LaborexWorkFlow_Add_MvtCaisse.RedirectUrl('lg_TYPE_REGLEMENT_ID', 'lg_MODE_REGLEMENT_ID', url_services_data_modereglement_mvt_caisse);
                                            LaborexWorkFlow_Add_MvtCaisse.FindComponentToHideDisplay('lg_TYPE_REGLEMENT_ID', 'str_LIEU', 'str_BANQUE', 'str_NOM', 'int_TAUX_CHANGE', 'str_CODE_MONNAIE');
                                            LaborexWorkFlow_Add_MvtCaisse.ManageComponentLigth('lg_TYPE_REGLEMENT_ID', 'lg_MODE_REGLEMENT_ID', '1', 'Especes', 'hide');
                                            // LaborexWorkFlow.ManageComponentLigth('lg_TYPE_REGLEMENT_ID', 'lg_MODE_REGLEMENT_ID', '4', 'Differe', 'hide');
                                            LaborexWorkFlow_Add_MvtCaisse.ManageComponentLigth('lg_TYPE_REGLEMENT_ID', 'lg_MODE_REGLEMENT_ID', '2', 'Cheques', 'show');
                                            LaborexWorkFlow_Add_MvtCaisse.ManageComponentLigth('lg_TYPE_REGLEMENT_ID', 'lg_MODE_REGLEMENT_ID', '3', 'Carte Bancaire', 'show');
                                            LaborexWorkFlow_Add_MvtCaisse.ManageComponentLigth('lg_TYPE_REGLEMENT_ID', 'lg_MODE_REGLEMENT_ID', '5', 'Devise', 'show');

                                        }

                                    }


                                },
                                {
                                    xtype: 'combobox',
                                    hidden: true,
                                    fieldLabel: 'Mode.Reglement',
                                    name: 'lg_MODE_REGLEMENT_ID',
                                    id: 'lg_MODE_REGLEMENT_ID',
                                    store: store_modereglement_Add_MvtCaisse,
                                    valueField: 'lg_MODE_REGLEMENT_ID',
                                    displayField: 'str_NAME',
                                    typeAhead: true,
                                    queryMode: 'remote',
//                                    allowBlank: false,
                                    emptyText: 'Choisir un mode de reglement...'

                                }

                            ]
                        },
                        {
                            xtype: 'container',
                            layout: 'hbox',
                            margin: '0 10 5 0',
                            items: [
                                {
                                    xtype: 'textfield',
                                    name: 'str_NOM',
                                    id: 'str_NOM',
                                    fieldLabel: 'Nom',
                                    hidden: true,
                                    flex: 2,
                                },
                                {
                                    xtype: 'textfield',
                                    name: 'str_BANQUE',
                                    id: 'str_BANQUE',
                                    fieldLabel: 'Banque',
                                    hidden: true,
                                    flex: 1
                                }]}, {
                            xtype: 'container',
                            layout: 'hbox',
                            margin: '0 10 5 0',
                            items: [
                                {
                                    xtype: 'textfield',
                                    name: 'str_CODE_MONNAIE',
                                    id: 'str_CODE_MONNAIE',
                                    fieldLabel: 'Code.Monnaie',
                                    hidden: true,
                                    value: "Fr",
                                    flex: 1
                                },
                                {
                                    xtype: 'textfield',
                                    name: 'int_TAUX_CHANGE',
                                    id: 'int_TAUX_CHANGE',
                                    fieldLabel: 'Taux.Change',
                                    hidden: true,
                                    flex: 1,
                                    value: 0,
                                    maskRe: /[0-9.]/,
                                    minValue: 0
                                }]
                        },
                        {
                            xtype: 'container',
                            layout: 'hbox',
                            margin: '0 10 5 0',
                            items: [
                                {
                                    xtype: 'textfield',
                                    name: 'int_MONTANT_Add_MvtCaisse',
                                    id: 'int_MONTANT_Add_MvtCaisse',
                                    fieldLabel: 'Montant',
                                    flex: 1,
                                    emptyText: 'Montant',
                                    allowBlank: false
                                },
                                {
                                    xtype: 'textfield',
                                    name: 'str_LIEU',
                                    id: 'str_LIEU',
                                    fieldLabel: 'Lieu',
                                    hidden: true,
                                    flex: 1
                                },
                                {
                                    xtype: 'textfield',
                                    id: 'lg_OPERATEUR_ID',
                                    fieldLabel: 'Nom',
                                    hidden: true,
                                    flex: 1
                                }


                            ]
                        },
                        {
                            xtype: 'container',
                            layout: 'hbox',
                            margin: '0 10 5 0',
                            items: [
                                {
                                    xtype: 'textareafield',
                                    grow: true,
                                    name: 'str_COMMENTAIRE_MOUVEMENT',
                                    fieldLabel: 'Commentaire',
                                    id: 'str_COMMENTAIRE_MOUVEMENT',
                                    anchor: '100%',
                                    hidden: true,
                                    emptyText: 'Saisir un commentaire'
                                }


                            ]
                        }
                    ]
                }

            ]
        });

        //this.callParent();

        /*if (Omode === "create") {
         
         var lg_lg_MODE_REGLEMENT_ID = Ext.getCmp("lg_MODE_REGLEMENT_ID");
         lg_lg_MODE_REGLEMENT_ID.getStore().on(
         "load", function() {
         
         var CODEstore = lg_lg_MODE_REGLEMENT_ID.getStore();
         CODEstore.each(function(r, id) {
         
         switch (r.get('lg_MODE_REGLEMENT_ID')) {
         case '1':
         
         lg_lg_MODE_REGLEMENT_ID.setValue(r.get('lg_MODE_REGLEMENT_ID'));
         //L'id de CASH  doit etre 1 ou remplacer la valeur  par l'id dans la bd
         break;
         }
         
         
         
         });
         
         },
         this,
         {
         single: true
         }
         );
         
         }
         */
        if (Omode === "update") {

            ref = this.getOdatasource().lg_MVT_CAISSE_ID;

            Ext.getCmp('lg_TYPE_MVT_CAISSE_ID').setValue(this.getOdatasource().lg_TYPE_MVT_CAISSE_ID);
            Ext.getCmp('str_NUM_COMPTE').setValue(this.getOdatasource().str_NUM_COMPTE);
            Ext.getCmp('int_MONTANT_Add_MvtCaisse').disable();
            Ext.getCmp('int_MONTANT_Add_MvtCaisse').setValue(this.getOdatasource().int_AMOUNT);
            Ext.getCmp('str_NUM_PIECE_COMPTABLE').setValue(this.getOdatasource().str_NUM_PIECE_COMPTABLE);
            Ext.getCmp('lg_MODE_REGLEMENT_ID').setValue(this.getOdatasource().lg_MODE_REGLEMENT_ID);
            Ext.getCmp('str_BANQUE').setValue(this.getOdatasource().str_BANQUE);
            Ext.getCmp('str_LIEU').setValue(this.getOdatasource().str_LIEU);
            Ext.getCmp('str_CODE_MONNAIE').setValue(this.getOdatasource().str_CODE_MONNAIE);
            Ext.getCmp('dt_DATE_MVT').setValue(this.getOdatasource().dt_DATE_MVT);
            Ext.getCmp('int_TAUX_CHANGE').setValue(this.getOdatasource().int_TAUX_CHANGE);
            Ext.getCmp('str_COMMENTAIRE_MOUVEMENT').setValue(this.getOdatasource().str_COMMENTAIRE);
            Ext.getCmp('lg_TYPE_REGLEMENT_ID').setValue(this.getOdatasource().lg_TYPE_REGLEMENT_ID);
            Ext.getCmp('lg_OPERATEUR_ID').setVisible(true);
            Ext.getCmp('lg_OPERATEUR_ID').setValue(this.getOdatasource().str_client_infos);



        }

        win_add_mvt_caisse = new Ext.window.Window({
            autoShow: true,
            id: 'paydebtID',
            height: 200,
            width: 400,
            layout: {
                type: 'fit'
            },
            // closable: false,
            resizable: false,
            title: this.getTitre(),
            items: form,
            buttons: [{
                    text: 'Enregistrer',
                    id: 'btn_clt_saveID',
                    handler: function () {
                        Me_add_mvt_caisse.onbtnsave_mvtcaisse();
                    }
                }, {
                    text: 'Annuler',
                    id: 'btn_clt_anulerID',
                    handler: function () {
                        win_add_mvt_caisse.close();
                    }
                }]

        });


        if (Omode === "update") {
            Ext.getCmp('btn_clt_saveID').hide();
            Ext.getCmp('btn_clt_anulerID').hide();
        }

    },
    onbtnsave_mvtcaisse: function () {

        var fenetre = Ext.getCmp("paydebtID"),
                formulaire = fenetre.down('form');
        if (formulaire.isValid()) {


            var internal_url = "";
            if (Omode === "create") {
                internal_url = url_services_transaction_mvtcaisse_mvt_caisse + 'createfalse';

            } else {
                internal_url = url_services_transaction_mvtcaisse_mvt_caisse + 'update&lg_MVT_CAISSE_ID=' + ref;
            }

            //code ajouté
            var str_NUM_COMPTE = '52000000000', str_NUM_PIECE_COMPTABLE = "", lg_TYPE_MVT_CAISSE_ID = "4", lg_MODE_REGLEMENT_ID = "1";
            //fin code ajouté
            testextjs.app.getController('App').ShowWaitingProcess();
            Ext.Ajax.request({
                url: internal_url,
                params: {
                    str_NUM_COMPTE: str_NUM_COMPTE,
                    str_NUM_PIECE_COMPTABLE: str_NUM_PIECE_COMPTABLE,
                    lg_TYPE_MVT_CAISSE_ID: lg_TYPE_MVT_CAISSE_ID,
                    lg_MODE_REGLEMENT_ID: lg_MODE_REGLEMENT_ID,
                    int_TAUX_CHANGE: Ext.getCmp('int_TAUX_CHANGE').getValue(),
                    str_CODE_MONNAIE: Ext.getCmp('str_CODE_MONNAIE').getValue(),
                    str_BANQUE: Ext.getCmp('str_BANQUE').getValue(),
                    str_NOM: Ext.getCmp('str_NOM').getValue(),
                    str_LIEU: Ext.getCmp('str_LIEU').getValue(),
                    int_AMOUNT: Ext.getCmp('int_MONTANT_Add_MvtCaisse').getValue(),
                    str_COMMENTAIRE_MOUVEMENT: Ext.getCmp('str_COMMENTAIRE_MOUVEMENT').getValue(),
                    dt_DATE_MVT: Ext.Date.format(Ext.getCmp('dt_DATE_MVT').getValue(), 'Y/m/d'),
                    bool_CHECKED: false
                },
                success: function (response)
                {
                    testextjs.app.getController('App').StopWaitingProcess();
                    var object = Ext.JSON.decode(response.responseText, false);

                    if (object.errors_code == "0") {
                        Ext.MessageBox.alert('Error Message', object.errors);
                        return;
                    } else {
                        Me_add_mvt_caisse.lunchPrinter(object.str_REF);
                        Ext.MessageBox.alert('Confirmation', object.errors);
                        Oview.getStore().reload();
                        win_add_mvt_caisse.close();
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


        } else {
            Ext.MessageBox.show({
                title: 'Echec',
                msg: 'Veuillez renseignez les champs obligatoires',
                // width: 300,
                height: 150,
                buttons: Ext.MessageBox.OK,
                icon: Ext.MessageBox.WARNING
            });
        }
    },
    lunchPrinter: function (str_REF) {

        Ext.Ajax.request({
            url: url_services_pdf_ticket_mouvement + "?str_REF=" + str_REF,
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


