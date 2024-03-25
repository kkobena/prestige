/* global Ext */

var url_services_data_client_addclt = '../webservices/configmanagement/compteclient/ws_data.jsp';
var url_services_transaction_client_add = '../webservices/configmanagement/client/ws_transaction.jsp?mode=';
var url_services_data_diffclient_addclt = '../webservices/sm_user/diffclient/ws_data.jsp';
var url_services_data_client_addclt_temp = '';


var OCltgridpanelID;
var Oview;
var Omode;
var Me_addclt;
var ref_add;
var cust_name;
var cust_id;
var cust_account_id;
var Ogrid;
var OmyType;
var str_customer_chosen;
var id_customer_chosen;
var solde_customer_chosen;
var OcustGrid;
var ref_vente;

var win_add_clt;
var view_title_clt;
var oclient_idval;
var oclient_nameval;
var total_cust_dif;
var store_addclt;

Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.sm_user.dovente.action.addclt', {
    extend: 'Ext.window.Window',
    xtype: 'adddovente',
    id: 'adddoventeID',
    requires: [
        'Ext.form.*',
        'Ext.window.Window',
        'testextjs.store.Statut',
        'Ext.grid.*',
        'Ext.layout.container.Column',
        'testextjs.model.Famille',
        'testextjs.model.CompteClient',
        'testextjs.view.sm_user.dovente.action.add'

    ],
    config: {
        odatasource: '',
        parentview: '',
        mode: '',
        titre: '',
        obtntext: '',
        nameintern: ''
    },
    title: 'Choix.Client',
    bodyPadding: 5,
    layout: 'column',
    initComponent: function() {

        url_services_data_client_addclt = '../webservices/configmanagement/compteclient/ws_data.jsp';

        Oview = this.getParentview();
        Omode = this.getMode();
        Me_addclt = this;
        Ogrid = this.getObtntext();
        str_customer_chosen = "";
        id_customer_chosen = "";
        solde_customer_chosen = 0;

        ref_add = this.getOdatasource();
        ref_vente = this.getNameintern();


        this.title = this.getTitre();
        view_title_clt = Me_addclt.title;

        //  alert("view_title_clt  " + view_title_clt);

        //alert("   hhh  " + Ogrid);

        var itemsPerPage = 20;
        store_addclt = new Ext.data.Store({
            model: 'testextjs.model.CompteClient',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_client_addclt,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            },
//            autoLoad: true

        });

        str_customer_chosen = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    //allowBlank: false,
                    fieldLabel: 'Nom.Client : ',
                    name: 'str_customer_chosen',
                    id: 'str_customer_chosen',
                    fieldStyle: "color:blue;",
                    emptyText: 'Client.Name'
                });

        id_customer_chosen = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    //allowBlank: false,
                    fieldLabel: 'Id.Client : ',
                    name: 'id_customer_chosen',
                    id: 'id_customer_chosen',
                    fieldStyle: "color:blue;",
                    emptyText: 'Client.Id'
                });


        solde_customer_chosen = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    fieldLabel: 'Solde.Client : ',
                    name: 'solde_customer_chosen',
                    id: 'solde_customer_chosen',
                    emptyText: 'Client.Solde',
                    value: 0,
                    renderer: amountformat,
                    fieldStyle: "color:blue;",
                    align: 'right'

                });



        var form = new Ext.form.Panel({
            width: 200,
            layout: {
                type: 'hbox'
            },
            defaults: {
                //flex: 1
            },
            items: ['CltgridpanelID', 'resume_choix'],
           // autoHeight: true,
            fieldDefaults: {
                labelAlign: 'left',
                labelWidth: 90,
                anchor: '100%',
                msgTarget: 'side'
            },
            items: [{
                    columnWidth: 0.65,
                    xtype: 'gridpanel',
                    id: 'CltgridpanelID',
                    store: store_addclt,
                    height: 150,
                    columns: [{
                            text: 'lg_COMPTE_CLIENT_ID',
                            flex: 1,
                            sortable: true,
                            hidden: true,
                            dataIndex: 'lg_COMPTE_CLIENT_ID'
                        }, {
                            text: 'lg_CLIENT_ID',
                            flex: 1,
                            sortable: true,
                            hidden: true,
                            dataIndex: 'lg_CLIENT_ID'
                        }, {
                            text: 'Prenom',
                            flex: 1,
                            dataIndex: 'str_FIRST_NAME'
                        }, {
                            text: 'Nom',
                            flex: 1,
                            dataIndex: 'str_LAST_NAME'
                        }, {
                            text: 'Genre',
                            flex: 1,
                            dataIndex: 'str_SEXE'
                        }, {
                            text: 'Securite.Social',
                            flex: 1,
                            dataIndex: 'str_NUMERO_SECURITE_SOCIAL'
                        }, {
                            text: 'Adresse',
                            flex: 1,
                            dataIndex: 'str_ADRESSE'
                        }, {
                            header: 'Solde',
                            dataIndex: 'dbl_SOLDE',
                            flex: 1

                        }, {
                            xtype: 'actioncolumn',
                            width: 30,
                            sortable: false,
                            menuDisabled: true,
                            items: [{
                                    icon: 'resources/images/icons/fam/add.gif',
                                    tooltip: 'Choisir Client',
                                    scope: this,
                                    handler: this.onChooseCustomerClick,
                                    getClass: function() {
                                        if (Ogrid === "Client") {
                                            return 'x-display-hide';
                                        } else if (Ogrid === "Tiers.Payant") {
                                            return 'x-hide-display';
                                        }
                                    }
                                }]
                        }, {
                            xtype: 'actioncolumn',
                            width: 30,
                            sortable: false,
                            menuDisabled: true,
                            items: [{
                                    icon: 'resources/images/icons/fam/page_white_edit.png',
                                    tooltip: 'Modifier',
                                    scope: this,
                                    handler: this.onEditClick
                                }]
                        }, {
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
                        }, {
                            xtype: 'actioncolumn',
                            width: 30,
                            sortable: false,
                            menuDisabled: true,
                            items: [{
                                    icon: 'resources/images/icons/fam/application_view_list.png',
                                    tooltip: 'Tiers.Payant',
                                    scope: this,
                                    handler: this.onChooseTiersClick,
                                    getClass: function() {
                                        if (Ogrid === "Client") {
                                            return 'x-hide-display';
                                        } else if (Ogrid === "Tiers.Payant") {
                                            return 'x-display-hide';
                                        }
                                    }
                                }]
                        }],
                    tbar: [
                        {
                            text: 'Creer',
                            scope: this,
                            handler: this.onAddClick
                        }, '-', {
                            xtype: 'textfield',
                            id: 'rechercher_addclt',
                            name: 'user',
                            emptyText: 'Rech'
                        }, {
                            text: 'rechercher',
                            tooltip: 'rechercher',
                            scope: this,
                            handler: this.onRechClick
                        }],
                    listeners: {
                        scope: this},
                    bbar: {
                        xtype: 'pagingtoolbar',
                        store: store_addclt,
                        dock: 'bottom',
                        displayInfo: true
                    }
                }, {
                    columnWidth: 0.35,
                    margin: '10 10 10 10',
                    xtype: 'fieldset',
                    title: 'Resume Choix',
                    id: 'resume_choix',
                    layout: 'anchor',
                    defaultType: 'textfield',
                    items: [id_customer_chosen,
                        str_customer_chosen,
                        solde_customer_chosen

                    ]

                }]
        });

        this.callParent();
        OCltgridpanelID = Ext.getCmp('CltgridpanelID');

        win_add_clt = new Ext.window.Window({
            autoShow: true,
            id: 'cltwinID',
            title: this.getTitre(),
            //  autoHeight: true,
            width: 200,
            Height: 150,
           // layout: 'fit',
          //  plain: true,
            items: form,
            buttons: [{
                    text: 'OK',
                    id: 'btn_clt_saveID',
                    handler: function() {
                        Me_addclt.onbtnsave_addclt();
                    }
                }, {
                    text: 'Annuler',
                    id: 'btn_clt_anulerID',
                    handler: function() {
                        win_add_clt.close();
                    }
                }]

        });

        // alert("Ogrid  " + Ogrid);

        if (Ogrid === "Tiers.Payant") {
            Ext.getCmp('resume_choix').hide();
            win_add_clt.down('#btn_clt_saveID').hide();
            win_add_clt.down('#btn_clt_anulerID').hide();
            Me_addclt.onRedirectSeacrchClick("1");

        } else {
            win_add_clt.down('#btn_clt_saveID').disable();
            Me_addclt.onRedirectSeacrchClick("2");
        }






    },
    onChooseCustomerClick: function(grid, rowIndex) {

        var rec = OCltgridpanelID.getStore().getAt(rowIndex);
        str_customer_chosen.setValue(rec.get('str_FIRST_NAME'));
        id_customer_chosen.setValue(rec.get('lg_CLIENT_ID'));
        solde_customer_chosen.setValue(rec.get('dbl_SOLDE'));
        cust_id = rec.get('lg_CLIENT_ID');
        cust_name = rec.get('str_FIRST_NAME');
        cust_account_id = rec.get('lg_COMPTE_CLIENT_ID');
        Me_addclt.checkCustDif(rec.get('lg_COMPTE_CLIENT_ID'), rec.get('str_FIRST_NAME'));

    },
    onbtnsave_addclt: function() {
        var xtype = "doventemanager";
        win_add_clt.close();
        if (view_title_clt === "by_cloturer_vente_add") {

            testextjs.app.getController('App').onLoadNewComponentWith2DataSource(xtype, "by_cloturer_vente_add", cust_name, ref_add, cust_account_id);

        } else if (view_title_clt === "by_type_reglmt") {

            testextjs.app.getController('App').onLoadNewComponentWith2DataSource(xtype, "by_type_reglmt", cust_name, ref_add, cust_account_id);

        } else if (view_title_clt === "by_type_vente") {

            testextjs.app.getController('App').onLoadNewComponentWith2DataSource(xtype, "by_type_vente", cust_name, ref_add, cust_account_id);

        } else {


        }




    }, onEditClick: function(grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        new testextjs.view.sm_user.dovente.action.add({
            odatasource: rec.data,
            parentview: this,
            mode: "updatecarnet",
            titre: "Modification Client  [" + rec.get('str_LAST_NAME') + "]"
        });
    },
    onChooseTiersClick: function(grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        new testextjs.view.sm_user.dovente.action.addTiersPayant({
            odatasource: rec.data,
            parentview: this,
            mode: "updatecarnet",
            titre: "Tiers.Payant  Client  [" + rec.get('str_LAST_NAME') + "]"
        });
    },
    onAddClick: function() {

        new testextjs.view.sm_user.dovente.action.add({
            odatasource: "",
            parentview: this,
            mode: "createcarnet",
            titre: "Ajouter Client"
        });
    },
    onRemoveClick: function(grid, rowIndex) {
        Ext.MessageBox.confirm('Message',
                'confirm la suppresssion',
                function(btn) {
                    if (btn == 'yes') {
                        var rec = grid.getStore().getAt(rowIndex);
                        Ext.Ajax.request({
                            url: url_services_transaction_client_add + 'delete',
                            params: {
                                lg_CLIENT_ID: rec.get('lg_CLIENT_ID')
                            },
                            success: function(response)
                            {
                                var object = Ext.JSON.decode(response.responseText, false);
                                if (object.success == 0) {
                                    Ext.MessageBox.alert('Error Message', object.errors);
                                    return;
                                }
                                grid.getStore().reload();
                            },
                            failure: function(response)
                            {

                                var object = Ext.JSON.decode(response.responseText, false);
                                //  alert(object);

                                console.log("Bug " + response.responseText);
                                Ext.MessageBox.alert('Error Message', response.responseText);

                            }
                        });
                        return;
                    }
                });


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
    onsplitovalue: function(Ovalue) {
        var int_ovalue;
        var string = Ovalue.split(" ");
        int_ovalue = string[0];
        return int_ovalue;
    },
    checkCustDif: function(Ocustomer_id, Ocustomer_name) {
        var url_services_data_diffclient_addclt_final = url_services_data_diffclient_addclt + "?str_BENEFICIAIRE=" + Ocustomer_id + "&lg_TYPE_ECART_MVT=1&str_task=VENTE";
        Ext.Ajax.request({
            url: url_services_data_diffclient_addclt_final,
            params: {
            },
            success: function(response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                var in_total_diff_clt = object.total;

                if (in_total_diff_clt > 0) {
                    var cust_total_dif = object.total_differe;
                    Me_addclt.onDiffereClick(Ocustomer_id, Ocustomer_name, cust_total_dif);
                } else {
                    win_add_clt.down('#btn_clt_saveID').enable();
                }
                url_services_data_diffclient_addclt_final = url_services_data_diffclient_addclt;
            },
            failure: function(response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);
            }
        });
        // win_add_clt.close();
    },
    onDiffereClick: function(Ocust_id, Ocust_name, Ocust_total_diff) {
      /*  oclient_idval = Ocust_id;
        oclient_nameval = Ocust_name;
        total_cust_dif = Ocust_total_diff;
        new testextjs.view.sm_user.dovente.action.checkdif({
            odatasource: oclient_idval,
            o2nddatasource: oclient_nameval,
            o3rdddatasource: total_cust_dif,
            parentview: this,
            mode: "create",
            titre: "by_addclt"
        });*/
    },
    onRechClick: function() {

        var val = Ext.getCmp('rechercher_addclt');
        OCltgridpanelID.getStore().load({
            params: {
                search_value: val.value
            }
        }, url_services_data_client_addclt);
    },
    onRedirectSeacrchClick: function(Ovalue) {
        url_services_data_client_addclt_temp = '../webservices/configmanagement/compteclient/ws_data.jsp?lg_TYPE_CLIENT_ID=' + Ovalue;
        store_addclt.getProxy().url = url_services_data_client_addclt_temp;
        store_addclt.reload();
    }





});