//var url_services ='../webservices/sm_user/institution';
var url_services_data_Institution = '../webservices/sm_user/institution/ws_client_data.jsp?option=';
var url_services_transaction_addInstitution= '../webservices/sm_user/institution/ws_client_transaction.jsp?mode=';
var  OviewItem;
var OmodeItem;
var MeItem;
var refItem;
var internal_url;

Ext.define(  'testextjs.view.sm_user.client.action.addInstitutionItem', {
    extend: 'Ext.container.Container',
    xtype: 'addinstitutionItem',
    id: 'addinstitutionItemID',

    requires: [
    'Ext.grid.*',
    'Ext.layout.container.HBox',
    // 'testextjs.model.dd.Simple',
    'testextjs.model.Institution'
    ],


    width: 650,
    height: 300,
    layout: {
        type: 'hbox',
        align: 'stretch',
        padding: 5
    },


    config: {
        odatasource:'',
        parentview:'',
        mode :'',
        titre :''
    },

   

    initComponent: function(){

        function dotransaction(internalurl,lg_INSTITUTION_ID){
            Ext.Ajax.request({
                url: internalurl,
                params: {
                    lg_INSTITUTION_ID:lg_INSTITUTION_ID
                },
                success: function(response)
                {
                    var object = Ext.JSON.decode(response.responseText,false);

                    if(object.success == 1){
                        return;
                    }else{
                        Ext.MessageBox.alert('Error Message', object.errors);
                    }
                },
                failure: function(response)
                {
                     var object = Ext.JSON.decode(response.responseText,false);
                      Ext.MessageBox.alert('Error Message', object.errors);
                   // Ext.MessageBox.alert('Error Message', "Error Remove ");
                }
            });
          
            mystore.load();
            mystore_second.load();
        }


    
      
        refItem = this.getOdatasource().lg_CUSTOMER_ID;

      

        var mystore = new Ext.data.Store({
            model: testextjs.model.Institution,
            proxy: {
                type: 'ajax',
                url: url_services_data_Institution+'IN'+'&lg_CUSTOMER_ID='+refItem
            }
        })

        mystore.load();


        var mystore_second = new Ext.data.Store({
            model: testextjs.model.Institution,
            proxy: {
                type: 'ajax',
                url: url_services_data_Institution+'NOT_IN'+'&lg_CUSTOMER_ID='+refItem
            }
        })
        mystore_second.load();


        var group1 = this.id + 'group1',
        group2 = this.id + 'group2',
        columns = [{
            text: 'Record Name',
            flex: 1,
            hidden:true,
            sortable: true,
            dataIndex: 'lg_INSTITUTION_ID'
        }, {
            text: 'Nom',
            width: 290,
            sortable: true,
            dataIndex: 'str_NAME'
        },{
            text: 'Statut',
            width: 150,
            hidden:true,
            sortable: true,
            dataIndex: 'str_STATUT'
        }];

        this.items = [{
            itemId: 'grid1',
            flex: 1,
            xtype: 'grid',
            multiSelect: true,
            viewConfig: {
                plugins: {
                    ptype: 'gridviewdragdrop',
                    dragGroup: group1,
                    dropGroup: group2
                },
                listeners: {
                    drop: function(node, data, dropRec, dropPosition) {
                         
                        var dropOn = dropRec ? ' ' + dropPosition + ' ' + dropRec.get('str_NAME') : ' on empty view';

                        internal_url = url_services_transaction_addInstitution+'create&lg_CUSTOMER_ID='+refItem;
                        dotransaction( internal_url,data.records[0].get('lg_INSTITUTION_ID'));
                     // Ext.example.msg('Drag from right to left', 'Dropped ' + data.records[0].get('name') + dropOn);
                    }
                }
            },
            store: mystore,
          
          
            queryMode: 'local',
            columns: columns,
            stripeRows: true,
            title: 'Assigne',
            tools: [{
                type: 'refresh',
                tooltip: 'Actualiser',
                scope: this,
                handler: this.onResetClick
            }],
            margins: '0 5 0 0'
        }, {
            itemId: 'grid2',
            flex: 1,
            xtype: 'grid',
            viewConfig: {
                plugins: {
                    ptype: 'gridviewdragdrop',
                    dragGroup: group2,
                    dropGroup: group1
                },
                listeners: {
                    drop: function(node, data, dropRec, dropPosition) {

                        var dropOn = dropRec ? ' ' + dropPosition + ' ' + dropRec.get('str_NAME') : ' on empty view';
                        internal_url = url_services_transaction_addInstitution+'delete&lg_CUSTOMER_ID='+refItem;
                        dotransaction( internal_url,data.records[0].get('lg_INSTITUTION_ID'));
                    //  Ext.example.msg('Drag from left to right', 'Dropped ' + data.records[0].get('name') + dropOn);
                    }
                }
            },
            store:mystore_second,
            columns: columns,
            stripeRows: true,
            title: 'Disponible'
        }];

        this.callParent();
    },

    onResetClick: function(){
        //refresh source grid
         this.down('#grid1').getStore().removeAll();
        this.down('#grid1').getStore().load();//loadData(this.myData);
        // mystore.load();
        //purge destination grid
         this.down('#grid2').getStore().load()
        this.down('#grid2').getStore().removeAll();
    } 

});