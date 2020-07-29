var url_services_data_dovente_checkdif = '../webservices/sm_user/diffclient/ws_data.jsp';



var Oview;
var Omode;
var Me_check;
var OCltDiffgridpanelID;
var win_add_cltdiff;
var cust_ref;
var cust_name;
var cust_total_diff;
var title_checkdif;

Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}


Ext.define('testextjs.view.sm_user.dovente.action.checkdif', {
    extend: 'Ext.window.Window',
    xtype: 'checkdif',
    id: 'checkdifID',
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
        o2nddatasource: '',
        o3rdddatasource: '',
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
        Me_check = this;

        this.title = this.getTitre();
        title_checkdif = Me_check.title;
        cust_ref = Me_check.getOdatasource();
        cust_name = Me_check.getO2nddatasource();
        cust_total_diff = Me_check.getO3rdddatasource();

        url_services_data_dovente_checkdif = url_services_data_dovente_checkdif + "?str_BENEFICIAIRE=" + cust_ref + "&lg_TYPE_ECART_MVT=1&str_task=VENTE";


        var itemsPerPage = 20;
        var store = new Ext.data.Store({
            model: 'testextjs.model.Differes',
            pageSize: itemsPerPage,
            //autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_dovente_checkdif,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            },
            autoLoad: true
        });




        var form = new Ext.form.Panel({
            width: 850,
            layout: {
                type: 'hbox'
            },
            defaults: {
                flex: 1
            },
            items: ['CltDiffgridpanelID'],
            autoHeight: true,
            fieldDefaults: {
                labelAlign: 'left',
                labelWidth: 90,
                anchor: '100%',
                msgTarget: 'side'
            },
            items: [{
                    columnWidth: 0.65,
                    xtype: 'gridpanel',
                    id: 'CltDiffgridpanelID',
                    store: store,
                    height: 400,
                    columns: [{
                            text: 'lg_ECART_MVT_ID',
                            flex: 1,
                            sortable: true,
                            hidden: true,
                            dataIndex: 'lg_ECART_MVT_ID'
                        }, {
                            text: 'Reference',
                            flex: 1,
                            sortable: true,
                            hidden: true,
                            dataIndex: 'str_REF'
                        }, {
                            text: 'Beneficiaire',
                            flex: 1,
                            dataIndex: 'str_BENEFICIAIRE'
                        }, {
                            text: 'Montant',
                            flex: 1,
                            dataIndex: 'int_AMOUNT',
                            renderer: amountformat,
                            align: 'right'
                        }, {
                            text: 'Date Creation',
                            flex: 1,
                            dataIndex: 'dt_CREATED'
                        }],
                    tbar: [
                        {
                            text: 'Regler',
                            scope: this,
                            handler: this.onDebtPayClick
                        }, '-', {
                            xtype: 'textfield',
                            id: 'rechercher',
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
                        store: store,
                        dock: 'bottom',
                        displayInfo: true
                    }
                }]
        });

        this.callParent();
        OCltDiffgridpanelID = Ext.getCmp('CltDiffgridpanelID');


        win_add_cltdiff = new Ext.window.Window({
            autoShow: true,
            id: 'cltdifwinID',
            title: this.getTitre(),
            width: 850,
            Height: 500,
            layout: 'fit',
            plain: true,
            items: form,
            buttons: [{
                    text: 'OK',
                    //id: 'btn_clt_saveID',
                    hidden:true,
                    handler: function() {
                        Me_check.onbtnsave();
                    }
                }, {
                    text: 'Annuler',
                    // id: 'btn_clt_anulerID',
                    handler: function() {
                        win_add_cltdiff.close();
                    }
                }]

        });
    },
    onbtnsave: function() {


    },
    onDebtPayClick: function() {

        new testextjs.view.sm_user.dovente.action.PayDebt({
            odatasource: cust_total_diff,
            o2ndatasource: cust_ref,
            parentview: this,
            mode: "createcarnet",
            titre: "Regler Differe(s) Client [" + cust_name + "]"
        });
    },
    onRechClick: function() {

        var val = Ext.getCmp('rechercher');
        OCltDiffgridpanelID.getStore().load({
            params: {
                search_value: val.value
            }
        }, url_services_data_dovente_checkdif);
    }
});