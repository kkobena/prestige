var url_services_data_commande = '../webservices/sm_user/commande/ws_data.jsp';
var url_services_transaction_commande= '../webservices/sm_user/commande/ws_transaction.jsp?mode=';
Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';


Ext.define('testextjs.view.grid.RowExpander', {
    extend: 'Ext.grid.Panel',
  
    xtype: 'commandemanagereee',
    id: 'commandemanagerIDeeee',
  
    // title: 'Gest.Commande',
    frame: true,
    collapsible: true,
    animCollapse: false,
    title: 'Gest.Commande',
    iconCls: 'icon-grid',

    

    plugins: [{
        ptype: 'rowexpander',
        rowBodyTpl : new Ext.XTemplate(
            '<p> {str_PRODUCT_ITEM}</p>',
           // '<p><b>Change:</b> {int_PRICE:this.formatChange}</p><br>',
           // '<b>Ref:</b> {str_REF}</p>',
            {
                formatChange: function(v){
                    var color = v >= 0 ? 'green' : 'red';
                    return '<span style="color: ' + color + ';">' + Ext.util.Format.usMoney(v) + '</span>';
                }
            })
    }],

    initComponent: function() {


        var itemsPerPage = 20;
        var store = new Ext.data.Store({
            model: 'testextjs.model.Commande',
            pageSize: itemsPerPage,
            autoLoad: true,
            proxy: {
                type: 'ajax',
                url: url_services_data_commande,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });


        Ext.apply(this, {



            width: 950,
            height: 580,
            store: store,

            columns: [{
                header: 'lg_ORDER_ID',
                dataIndex: 'lg_ORDER_ID',
                hidden:true,
                flex: 1
            /* editor: {
                    allowBlank: false
                }*/
            },{
                header: 'Reference',
                dataIndex: 'str_REF',
                flex: 1
            /*editor: {
                    allowBlank: false
                }*/
            } ,{
                header: 'Prix',
                dataIndex: 'int_PRICE',
                flex: 1
            /*editor: {
                    allowBlank: false
                }*/

            },{
                header: 'Table',
                dataIndex: 'lg_TABLE_ID',
                flex: 1
            /*editor: {
                    allowBlank: false
                }*/
            } ,{
                header: 'Date',
                dataIndex: 'dt_CREATED',
                flex: 1
            /*editor: {
                    allowBlank: false
                }*/
            } ,{
                header: 'Utilisateur',
                dataIndex: 'lg_USER_ID',
                flex: 1
            /* editor: {
                    allowBlank: false
                }*/

            },{
                header: 'Statut',
                dataIndex: 'str_STATUT',
                flex: 1
            /* editor: {
                    allowBlank: false
                }*/




            },{
                xtype: 'actioncolumn',
                width: 30,
                sortable: false,
                menuDisabled: true,
                items: [{
                    icon: 'resources/images/icons/fam/accept.png',
                    tooltip: 'Valider',
                    scope: this,
                    handler: this.onValidateClick
                }]
            },{
                xtype: 'actioncolumn',
                width: 30,
                sortable: false,
                menuDisabled: true,
                items: [{
                    icon: 'resources/images/icons/fam/delete.png',
                    tooltip: 'Suprimer',
                    scope: this,
                    handler: this.onEditClick
                }]


            }],
           
            /*selModel: {
                selType: 'cellmodel'
            },*/
            tbar: [
            {
                xtype: 'textfield',
                id:'rechecher',
                name: 'user',

                emptyText: 'Rech'
            },{
                text: 'rechercher',
                tooltip: 'rechercher',
                scope: this,
                handler: this.onRechClick
            }],
            bbar: {
                xtype: 'pagingtoolbar',
                store: store,   // same store GridPanel is using
                dock: 'bottom',
                displayInfo: true
            }

        })

        this.callParent();
    /* this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        })*/
    /*
        this.callParent();

        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        })*/
    }, onValidateClick: function(grid, rowIndex){

        var rec = grid.getStore().getAt(rowIndex);


        new testextjs.view.sm_user.commande.action.add_validate({
            odatasource: rec.data,
            parentview: this,
            mode: "update",
            titre: "Validation Commande  ["+rec.get('lg_TABLE_ID')+"] .Ref ["+rec.get('lg_ORDER_ID')+"]"
        });


       
    }


    /*,


    loadStore: function() {
        this.getStore().load({
            callback: this.onStoreLoad
        });
    },

    onStoreLoad: function(){
    },
    onValidateClick: function(grid, rowIndex){
        var rec = grid.getStore().getAt(rowIndex);


        new testextjs.view.sm_user.commande.action.add_validate({
            odatasource: rec.data,
            parentview: this,
            mode: "update",
            titre: "Validation Commande  ["+rec.get('lg_TABLE_ID')+"] .Ref ["+rec.get('lg_ORDER_ID')+"]"
        });


    }*/




   
});
