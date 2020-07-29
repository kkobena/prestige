var url_services_data_dovente_checkdif__ = '../webservices/sm_user/diffclient/ws_data.jsp';
var url_services_data_typereglement__ = '../webservices/sm_user/typereglement/ws_data.jsp';
var url_services_data_modereglement__ = '../webservices/sm_user/modereglement/ws_data.jsp';


var Oview;
var Omode;
var Me_debt;
var in_total_vente__ = 0;
var int_monnaie__ = 0;
var title_debt;
var cust_total_dif_debt = 0;
var cust_id_dif_debt;

var int_AMOUNT_REMIS__;

Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}


Ext.define('testextjs.view.sm_user.dovente.action.PayDebt', {
    extend: 'Ext.window.Window',
    xtype: 'PayDebt',
    id: 'PayDebtID',
    requires: [
        'Ext.form.*',
        'Ext.window.Window',
        'testextjs.store.Statut',
        'Ext.grid.*',
        'Ext.layout.container.Column',
        'testextjs.model.Differes'

    ],
    config: {
        odatasource: '',
        o2ndatasource: '',
        parentview: '',
        mode: '',
        titre: '',
        obtntext: '',
        nameintern: ''
    },
    title: 'Differe(s) Client',
    bodyPadding: 5,
    layout: 'column',
    initComponent: function() {


        Oview = this.getParentview();
        Omode = this.getMode();
        Me_debt = this;

        this.title = this.getTitre();
        title_debt = Me_debt.title;

        cust_total_dif_debt = Me_debt.getOdatasource();
        cust_id_dif_debt = Me_debt.getO2ndatasource();

        var itemsPerPage = 20;

        store_typereglement__ = new Ext.data.Store({
            model: 'testextjs.model.TypeReglement',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_typereglement__,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });


        store_modereglement__ = new Ext.data.Store({
            model: 'testextjs.model.ModeReglement',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_typereglement__,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });




        int_AMOUNT_REMIS__ = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    //allowBlank: false,
                    fieldLabel: 'Monnaie :',
                    name: 'int_AMOUNT_REMIS',
                    id: 'int_AMOUNT_REMIS__',
                    fieldStyle: "color:blue;",
                    margin: '0 15 0 0',
                    value: 0,
                    // renderer: amountformat,
                    align: 'right'

                });


        var int_REEL_RESTE__ = new Ext.form.field.Hidden(
                {
                    xtype: 'hiddenfield',
                    name: 'int_REEL_RESTE',
                    id: 'int_REEL_RESTE__',
                    value: 0
                });



        var int_AMOUNTTOPAY__ = new Ext.form.field.Display(
                {
                    xtype: 'hiddenfield',
                    name: 'int_AMOUNTTOPAY',
                    id: 'int_AMOUNTTOPAY__',
                    fieldLabel: 'Montant A Payer  :',
                    value: 0,
                    fieldStyle: "color:red;",
                    margin: '0 15 0 0',
                    align: 'right'
                });

        var int_CUST_PART__ = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    fieldLabel: 'Part.Client :',
                    name: 'int_CUST_PART',
                    id: 'int_CUST_PART__',
                    fieldStyle: "color:blue;",
                    margin: '0 15 0 0',
                    hidden: true,
                    value: '0',
                    align: 'right'
                });






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
                        int_CUST_PART__,
                        int_REEL_RESTE__,
                        int_AMOUNTTOPAY__,
                        {
                            name: 'int_AMOUNT_RECU',
                            id: 'int_AMOUNT_RECU__',
                            fieldLabel: 'Montant Recu',
                            flex: 1,
                            emptyText: 'Montant Recu',
                            maskRe: /[0-9.]/,
                            allowBlank: false,
                            listeners: {
                                change: function() {

                                    var int_total = 0;
                                    var int_montant_recu = (Number(Ext.getCmp('int_AMOUNT_RECU__').getValue()));
                                    var int_montant_a_payer = (Number(Ext.getCmp('int_AMOUNTTOPAY__').getValue()));

                                    /*var clt_part_temp = Me_debt.onsplitovalue(Ext.getCmp('int_CUST_PART__').getValue());
                                     var clt_part = Number(clt_part_temp);
                                     if (clt_part !== 0) {
                                     int_total = clt_part;
                                     } else {
                                     int_total = in_total_vente__;
                                     }*/

                                    int_monnaie__ = Number(Me_debt.DisplayMonnaie(int_montant_a_payer, int_montant_recu));
                                    Ext.getCmp('int_AMOUNT_REMIS__').setValue(int_monnaie__ + ' CFA');



                                }

                            }
                        },
                        int_AMOUNT_REMIS__,
                        {
                            xtype: 'combobox',
                            fieldLabel: 'Type.Reglement',
                            name: 'lg_TYPE_REGLEMENT_ID',
                            id: 'lg_TYPE_REGLEMENT_ID__',
                            store: store_typereglement,
                            valueField: 'lg_TYPE_REGLEMENT_ID',
                            displayField: 'str_NAME',
                            typeAhead: true,
                            queryMode: 'remote',
                            allowBlank: false,
                            emptyText: 'Choisir un type de reglement...',
                            listeners: {
                                select: function(cmp) {
                                    var valuemodereglt = cmp.getValue();
                                    var OModeStore = Ext.getCmp('lg_MODE_REGLEMENT_ID__');
                                    url_services_data_modereglement__ = '../webservices/sm_user/modereglement/ws_data.jsp?lg_TYPE_REGLEMENT_ID=' + valuemodereglt;
                                    OModeStore.getStore().getProxy().url = url_services_data_modereglement__;
                                    OModeStore.getStore().reload();
                                    Me_debt.GetLabelText(valuemodereglt);


                                }
                            }

                        },
                        {
                            xtype: 'combobox',
                            fieldLabel: 'Mode.Reglement',
                            name: 'lg_MODE_REGLEMENT_ID',
                            id: 'lg_MODE_REGLEMENT_ID__',
                            store: store_modereglement,
                            valueField: 'lg_MODE_REGLEMENT_ID',
                            displayField: 'str_NAME',
                            typeAhead: true,
                            queryMode: 'remote',
                            allowBlank: false,
                            emptyText: 'Choisir un mode de reglement...'

                        }
                    ]
                }, {
                    xtype: 'container',
                    id: 'reglement_details__',
                    // hidden: true,
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    defaultType: 'textfield',
                    margin: '0 0 5 0',
                    items: [
                        {
                            name: 'str_CODE_MONNAIE__',
                            id: 'str_CODE_MONNAIE__',
                            fieldLabel: 'Code.Monnaie',
                            hidden: true,
                            value: "fr",
                            flex: 1,
                            //  emptyText: 'Code.Monnaie',
                            allowBlank: false
                        },
                        {
                            name: 'int_TAUX_CHANGE__',
                            id: 'int_TAUX_CHANGE__',
                            fieldLabel: 'Taux.Change',
                            hidden: true,
                            flex: 1,
                            value: 0,
                            // emptyText: 'Montant Recu',
                            maskRe: /[0-9.]/,
                            allowBlank: false
                        },
                        {
                            name: 'str_NOM__',
                            id: 'str_NOM__',
                            fieldLabel: 'Nom',
                            hidden: true,
                            flex: 1,
                            //emptyText: 'Montant.Du',
                            // maskRe: /[0-9.]/,
                            allowBlank: false
                        },
                        {
                            name: 'str_BANQUE__',
                            id: 'str_BANQUE__',
                            fieldLabel: 'Banque',
                            hidden: true,
                            flex: 1,
                            //  emptyText: 'Montant.verse',
                            //maskRe: /[0-9.]/,
                            allowBlank: false
                        },
                        {
                            name: 'str_LIEU__',
                            id: 'str_LIEU__',
                            fieldLabel: 'Lieu',
                            hidden: true,
                            flex: 1,
                            //  emptyText: 'Montant.verse',
                            // maskRe: /[0-9.]/,
                            allowBlank: false
                        }


                    ]
                }

            ]
        });

        this.callParent();

        win_add_paydebt = new Ext.window.Window({
            autoShow: true,
            id: 'paydebtID',
            title: this.getTitre(),
            width: 550,
            height: 350,
            layout: 'fit',
            plain: true,
            items: form,
            buttons: [{
                    text: 'OK',
                    //id: 'btn_clt_saveID',
                    handler: function() {
                        Me_debt.onbtnsave_debt();
                    }
                }, {
                    text: 'Annuler',
                    // id: 'btn_clt_anulerID',
                    handler: function() {
                        win_add_paydebt.close();
                    }
                }]

        });


        Ext.getCmp('int_AMOUNTTOPAY__').setValue(cust_total_dif_debt);

    },
    onbtnsave_debt: function() {
        Ext.Ajax.request({
            url: '../webservices/sm_user/diffclient/ws_transaction.jsp?mode=reglerdif',
            params: {
                lg_MODE_REGLEMENT_ID: Ext.getCmp('lg_MODE_REGLEMENT_ID__').getValue(),
                int_TAUX_CHANGE: Ext.getCmp('int_TAUX_CHANGE__').getValue(),
                str_CODE_MONNAIE: Ext.getCmp('str_CODE_MONNAIE__').getValue(),
                str_BANQUE: Ext.getCmp('str_BANQUE__').getValue(),
                str_LIEU: Ext.getCmp('str_LIEU__').getValue(),
                str_BENEFICIAIRE: cust_id_dif_debt
            },
            success: function(response)
            {
                var object = Ext.JSON.decode(response.responseText, false);

                if (object.errors_code === "0") {
                    Ext.MessageBox.alert('Error Message', object.errors);
                    return null;
                }
            },
            failure: function(response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);
            }
        });


    },
    onDebtPayClick: function() {
        alert(" EN COURS DE TRAITEMENT...")
        /* new testextjs.view.sm_user.dovente.action.add({
         odatasource: "",
         parentview: this,
         mode: "createcarnet",
         titre: "Ajouter Client"
         });*/
    },
    DisplayMonnaie: function(int_total, int_amount_recu) {
        var TotalMonnaie = 0;
        var TotalMonnaie_temp = 0;
        Ext.getCmp('int_REEL_RESTE__').setValue(int_amount_recu - int_total);
        if (int_total <= int_amount_recu) {
            TotalMonnaie_temp = int_amount_recu - int_total;
            TotalMonnaie = Number(TotalMonnaie_temp);
            return TotalMonnaie;
        } else {
            return null;
        }

        return TotalMonnaie;
    },
    GetLabelText: function(Ovalue) {


        if (Ovalue === "1") {

            Ext.getCmp('str_CODE_MONNAIE__').hide();
            Ext.getCmp('int_TAUX_CHANGE').hide();
            Ext.getCmp('str_NOM__').hide();
            Ext.getCmp('str_BANQUE__').hide();
            Ext.getCmp('str_LIEU__').hide();

        } else if (Ovalue === "2") {
            //  Me.onbtnaddclt("by_type_reglmt");
            Ext.getCmp('str_NOM__').setFieldLabel('Nom');
            Ext.getCmp('str_BANQUE__').setFieldLabel('Banque');
            Ext.getCmp('str_LIEU__').setFieldLabel('Lieu');
            Ext.getCmp('str_NOM__').show();
            Ext.getCmp('str_BANQUE__').show();
            Ext.getCmp('str_LIEU__').show();
            Ext.getCmp('int_TAUX_CHANGE__').hide();
            Ext.getCmp('str_CODE_MONNAIE__').hide();


        } else if (Ovalue === "3") {
            Ext.getCmp('str_CODE_MONNAIE__').hide();
            Ext.getCmp('int_TAUX_CHANGE__').hide();
            Ext.getCmp('str_NOM__').hide();
            Ext.getCmp('str_BANQUE__').hide();
            Ext.getCmp('str_LIEU__').hide();
        } else if (Ovalue === "4") {

            Ext.getCmp('str_NOM__').setFieldLabel('Commentaire');
            Ext.getCmp('str_NOM__').show();
            Ext.getCmp('int_TAUX_CHANGE__').hide();
            Ext.getCmp('str_BANQUE__').hide();
            Ext.getCmp('str_LIEU__').hide();
            Ext.getCmp('str_CODE_MONNAIE__').hide();

        } else if (Ovalue === "5") {
            Ext.getCmp('str_CODE_MONNAIE__').setFieldLabel('Code.Monnaie');
            Ext.getCmp('int_TAUX_CHANGE__').setFieldLabel('Taux.Change');


            Ext.getCmp('str_CODE_MONNAIE__').show();
            Ext.getCmp('int_TAUX_CHANGE__').show();
            Ext.getCmp('str_BANQUE__').hide();
            Ext.getCmp('str_LIEU__').hide();
            Ext.getCmp('str_NOM__').hide();
        }


    },
    onRechClick: function() {

        var val = Ext.getCmp('rechercher');
        OCltDiffgridpanelID__.getStore().load({
            params: {
                search_value: val.value
            }
        }, url_services_data_dovente_checkdif__);
    }
});