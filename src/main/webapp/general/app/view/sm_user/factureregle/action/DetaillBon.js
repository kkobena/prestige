//var url_services ='../webservices/sm_user/menu';
var url_services_data_detail_bon;
var url_services_transaction_facturation = '../webservices/sm_user/facturation/ws_transaction.jsp?mode=';
var ref;
var ref_facture;
var Odatasource;
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}
Ext.define('testextjs.view.sm_user.editfacture.action.DetaillBon', {
    extend: 'Ext.grid.Panel',
    xtype: 'detailbon',
    id: 'detailbonID',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.window.Window',
        'Ext.data.*',
        'Ext.util.*',
        'Ext.form.*',
        'Ext.JSON.*',
        'testextjs.model.DetailBon',
        //'testextjs.view.sms_user.facturation.action.add',
        'Ext.ux.ProgressBarPager'

    ],
    config: {
        odatasource: '',
        odatatasourceparent: '',
        nameintern: '',
        parentview: '',
        mode: '',
        titre: ''
    },
    title: 'Detail Bon',
    frame: true,
    initComponent: function () {

        ref = this.getOdatasource().lg_PREENREGISTREMENT_ID;
        // Odatasource = this.getOdatasource();
        Odatasource = this.getOdatatasourceparent();
        ref_facture = this.getNameintern();
        // alert(ref_facture);
        this.setTitle("Detail Bon  ::  " + this.getOdatasource().str_NUM_DOSSIER + " De " + Odatasource.str_CUSTOMER_NAME);

        url_services_data_detail_bon = '../webservices/sm_user/facturation/ws_data_detail_Bon.jsp?lg_PREENREGISTREMENT_ID=' + ref;
        var itemsPerPage = 20;
        var store = new Ext.data.Store({
            model: 'testextjs.model.DetailBon',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_detail_bon,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });



        this.cellEditing = new Ext.grid.plugin.CellEditing({
            clicksToEdit: 1
        });

        Ext.apply(this, {
            width: "90%",
            height: 580,
            plugins: [this.cellEditing],
            store: store,
            columns: [{
                    header: 'lg_PREENREGISTREMENT_DETAIL_ID',
                    dataIndex: 'lg_PREENREGISTREMENT_DETAIL_ID',
                    hidden: true,
                    flex: 1
                }, {
                    header: 'Nom',
                    dataIndex: 'str_NAME',
                    flex: 1

                }, {
                    header: 'Qte servie',
                    dataIndex: 'int_QUANTITY_SERVED',
                    flex: 1

                }, {
                    header: 'Montant',
                    dataIndex: 'int_PRICE',
                    //hidden: true,
                    renderer: amountformat,
                    align: 'right',
                    flex: 1
                }, {
                    header: 'Montant Supporte',
                    dataIndex: 'Quote_part',
                    //hidden: true,
                    renderer: amountformat,
                    align: 'right',
                    flex: 1
                }, {
                    header: 'Pourcentage',
                    dataIndex: 'int_PERCENT',
                    //hidden: true,
                    align: 'right',
                    flex: 1
                }],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [
                {
                    xtype: 'textfield',
                    id: 'rechecher_detail',
                    name: 'user',
                    emptyText: 'Rech'
                }, {
                    text: 'rechercher',
                    tooltip: 'rechercher',
                    scope: this,
                    handler: this.onRechClick
                }, {
                    text: 'retour',
                    tooltip: 'retour',
                    scope: this,
                    handler: this.onbtnretour
                }],
            bbar: {
                xtype: 'pagingtoolbar',
                pageSize: 10,
                store: store,
                displayInfo: true,
                plugins: new Ext.ux.ProgressBarPager()
            }
        });

        this.callParent();

        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        })


        this.on('edit', function (editor, e) {

        });


    },
    loadStore: function () {
        this.getStore().load({
            callback: this.onStoreLoad
        });
    },
    onStoreLoad: function () {
    },
    onbtnretour: function () {

        var xtype = "detailfacture";
        //   testextjs.app.getController('App').onLoadNewComponent(xtype, "", "");
        testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "Detail Bon", ref_facture, Odatasource);

    },
    onRechClick: function () {
        var val = Ext.getCmp('rechecher_detail');
        this.getStore().load({
            params: {
                search_value: val.value
            }
        }, url_services_data_detail_bon);
    }

})