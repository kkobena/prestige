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

Ext.define('testextjs.view.sm_user.mvtcaisse.action.add', {
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
//                            labelWidth: 100,
                                    name: 'dt_DATE_MVT',
                                    maxValue: new Date(),
                                    emptyText: 'Date Mouvement',
                                    submitFormat: 'Y-m-d'


                                },
                                {
                                    xtype: 'textfield',
                                    name: 'str_NUM_PIECE_COMPTABLE',
                                    id: 'str_NUM_PIECE_COMPTABLE',
                                    fieldLabel: 'Reference',
//                            flex: 1,
                                    emptyText: 'Reference',
                                    allowBlank: false
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
//                            labelWidth: 120,
                                    fieldLabel: 'Type.Mouvement',
                                    name: 'lg_TYPE_MVT_CAISSE_ID',
//                            flex: 1,
                                    id: 'lg_TYPE_MVT_CAISSE_ID',
                                    store: store_typemvtcaisse_Add_MvtCaisse,
                                    valueField: 'lg_TYPE_MVT_CAISSE_ID',
                                    displayField: 'str_NAME',
                                    typeAhead: true,
                                    queryMode: 'remote',
                                    allowBlank: false,
                                    emptyText: 'Choisir un type de mouvement...',
                                    listeners: {
                                        select: function (cmp) {
                                            var value = cmp.getValue();
                                            var record = cmp.findRecord(cmp.valueField || cmp.displayField, value); //recupere la ligne de l'element selectionné
                                            // var index = cmp.store.indexOf(record); // recupere la position de l'element selectionné de l'
                                            // alert("montant " + index + " record " + record.get('str_CLIENT'));
                                            Ext.getCmp('str_NUM_COMPTE').setValue(record.get('str_CODE_COMPTABLE'));
                                        }
                                    }

                                }, {
                                    xtype: 'textfield',
                                    name: 'str_NUM_COMPTE',
                                    id: 'str_NUM_COMPTE',
                                    disabled: true,
                                    fieldLabel: 'Numero Compte',
//                            flex: 1,
                                    emptyText: 'Numero Compte',
                                    allowBlank: false
                                }

                            ]
                        }
                        ,
                        //**************

                        {
                            xtype: 'container',
                            layout: 'hbox',
                            defaultType: 'textfield',
                            margin: '0 10 5 0',
                            items: [
                                {
                                    xtype: 'combobox',
//                            labelWidth: 120,
                                    fieldLabel: 'Type.Reglement',
                                    name: 'lg_TYPE_REGLEMENT_ID',
//                            flex: 1,
                                    id: 'lg_TYPE_REGLEMENT_ID',
                                    store: store_typereglement_Add_MvtCaisse,
                                    value: 'Especes',
                                    valueField: 'lg_TYPE_REGLEMENT_ID',
                                    displayField: 'str_NAME',
                                    typeAhead: true,
                                    queryMode: 'remote',
                                    allowBlank: false,
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
//                            labelWidth: 120,
                                    fieldLabel: 'Mode.Reglement',
                                    name: 'lg_MODE_REGLEMENT_ID',
                                    id: 'lg_MODE_REGLEMENT_ID',
//                            flex: 1,
                                    store: store_modereglement_Add_MvtCaisse,
                                    // value: 'Cash',
                                    valueField: 'lg_MODE_REGLEMENT_ID',
                                    displayField: 'str_NAME',
                                    typeAhead: true,
                                    queryMode: 'remote',
                                    allowBlank: false,
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
//                                    allowBlank: false
                                },
                                {
                                    xtype: 'textfield',
                                    name: 'str_BANQUE',
                                    id: 'str_BANQUE',
                                    fieldLabel: 'Banque',
                                    hidden: true,
                                    flex: 1,
//                                    allowBlank: false
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
                                    flex: 1,
//                                    allowBlank: false
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
                                    minValue: 0,
//                                    allowBlank: false
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
                                    emptyText: 'Montant'

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
                            layout: 'anchor',
                            margin: '0 10 5 0',
                            width: '100%',
                            items: [
                                {
                                    xtype: 'textareafield',
                                    grow: true,
                                    maxLength: 50,
                                    name: 'str_COMMENTAIRE_MOUVEMENT',
                                    fieldLabel: 'Commentaire',
                                    id: 'str_COMMENTAIRE_MOUVEMENT',
                                    anchor: '100%',
                                    emptyText: 'Saisir 50 lettres maximun'
                                }


                            ]
                        }
                    ]
                }

            ]
        });

        //this.callParent();

        if (Omode === "create") {

            var lg_lg_MODE_REGLEMENT_ID = Ext.getCmp("lg_MODE_REGLEMENT_ID");
            lg_lg_MODE_REGLEMENT_ID.getStore().on(
                    "load", function () {

                        var CODEstore = lg_lg_MODE_REGLEMENT_ID.getStore();
                        CODEstore.each(function (r, id) {

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
        if (Omode === "update") {

            ref = this.getOdatasource().lg_MVT_CAISSE_ID;

            Ext.getCmp('lg_TYPE_MVT_CAISSE_ID').setValue(this.getOdatasource().lg_TYPE_MVT_CAISSE_ID);
            Ext.getCmp('str_NUM_COMPTE').setValue(this.getOdatasource().str_NUM_COMPTE);
            Ext.getCmp('int_MONTANT_Add_MvtCaisse').disable();
            Ext.getCmp('int_MONTANT_Add_MvtCaisse').setValue(this.getOdatasource().int_AMOUNT);
//   Ext.getCmp('str_NUM_PIECE_COMPTABLE').setValue(this.getOdatasource().str_NUM_PIECE_COMPTABLE);
            Ext.getCmp('str_NUM_PIECE_COMPTABLE').setValue(this.getOdatasource().str_ref);
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
            height: 300,
            width: 700,
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
                internal_url = url_services_transaction_mvtcaisse_mvt_caisse + 'create';
            } else {
                internal_url = url_services_transaction_mvtcaisse_mvt_caisse + 'update&lg_MVT_CAISSE_ID=' + ref;
            }

            testextjs.app.getController('App').ShowWaitingProcess();
            var datas = {
                "idTypeRegl": Ext.getCmp('lg_TYPE_REGLEMENT_ID').getValue(),
                "numPieceComptable": Ext.getCmp('str_NUM_PIECE_COMPTABLE').getValue(),
                "idTypeMvt": Ext.getCmp('lg_TYPE_MVT_CAISSE_ID').getValue(),
                "idModeRegle": Ext.getCmp('lg_MODE_REGLEMENT_ID').getValue(),
                "taux": Ext.getCmp('int_TAUX_CHANGE').getValue(),
                "codeMonnaie": Ext.getCmp('str_CODE_MONNAIE').getValue(),
                "banque": Ext.getCmp('str_BANQUE').getValue(),
//                    str_NOM: Ext.getCmp('str_NOM').getValue(),
                "lieux": Ext.getCmp('str_LIEU').getValue(),
                "amount": Ext.getCmp('int_MONTANT_Add_MvtCaisse').getValue(),
                "commentaire": Ext.getCmp('str_COMMENTAIRE_MOUVEMENT').getValue(),
                "dateMvt": Ext.getCmp('dt_DATE_MVT').getSubmitValue()
            };
            Ext.Ajax.request({
                url: '../api/v1/caisse/addmvtCaisse',
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                params: Ext.JSON.encode(datas),

                success: function (response)
                {
                    testextjs.app.getController('App').StopWaitingProcess();
                    var object = Ext.JSON.decode(response.responseText, false);

                    if (!object.success) {
                        Ext.MessageBox.alert('Error Message', object.msg);
                        return;
                    } else {
                        Ext.MessageBox.show({
                            title: 'Message d\'erreur',
                            width: 320,
                            msg: "Voulez-vous imprimer le ticket ?",
                            buttons: Ext.MessageBox.OKCANCEL,
                            icon: Ext.MessageBox.INFO,
                            fn: function (buttonId) {
                                if (buttonId === "ok") {
                                    win_add_mvt_caisse.close();
                                    Me_add_mvt_caisse.lunchPrinter(object.mvtId);
                                }
                            }
                        });
                        Oview.getStore().reload();
                        fenetre.destroy();

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
    checkIfGridIsEmpty: function () {
        var gridTotalCount = Ext.getCmp('gridpanelID').getStore().getTotalCount();
        return gridTotalCount;
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
                    Ext.getCmp('int_MONTANT').setValue(object.Montant_total);
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


