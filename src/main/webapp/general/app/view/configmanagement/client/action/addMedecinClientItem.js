var url_services_data_addMedecin_client = '../webservices/configmanagement/client/ws_medecin_client_data.jsp?option=';
var url_services_transaction_addMedecin_client= '../webservices/configmanagement/client/ws_medecin_client_transaction.jsp?mode=';
var  OviewItem;
var OmodeItem;
var MeItem;
var refItem;
var internal_url;

Ext.define('testextjs.view.configmanagement.client.action.addMedecinClientItem', {
    extend: 'Ext.container.Container',
    xtype: 'addMedecinClientItem',
    id: 'addMedecinClientItemID',

    requires: [
    'Ext.grid.*',
    'Ext.layout.container.HBox',
    // 'testextjs.model.dd.Simple',
//    'testextjs.model.Medecin'
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

        function dotransaction(internalurl,lg_MEDECIN_ID){
            //alert("internalurl "+internalurl);
            Ext.Ajax.request({
                url: internalurl,
                params: {
                    lg_MEDECIN_ID:lg_MEDECIN_ID
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


    
      
        refItem = this.getOdatasource().lg_CLIENT_ID;

        // alert("Ouf jai mon ID "+refItem);

        var mystore = new Ext.data.Store({
            model: testextjs.model.Medecin,
            proxy: {
                type: 'ajax',
                url: url_services_data_addMedecin_client+'IN'+'&lg_CLIENT_ID='+refItem
            }
        })

        mystore.load();


        var mystore_second = new Ext.data.Store({
            model: testextjs.model.Medecin,
            proxy: {
                type: 'ajax',
                url: url_services_data_addMedecin_client+'NOT_IN'+'&lg_CLIENT_ID='+refItem
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
            dataIndex: 'lg_MEDECIN_ID'
        }, {
            text: 'str_FIRST_LAST_NAME',
            width: 150,
            hidden:true,
            sortable: true,
            dataIndex: 'str_FIRST_LAST_NAME'
        }, {
            text: 'Nom et Prenom',
            width: 290,
            sortable: true,
            dataIndex: 'str_FIRST_LAST_NAME'
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
                         
                        var dropOn = dropRec ? ' ' + dropPosition + ' ' + dropRec.get('str_CODE_ORGANISME') : ' on empty view';

                        internal_url = url_services_transaction_addMedecin_client+'create&lg_CLIENT_ID='+refItem;
                        dotransaction(internal_url,data.records[0].get('lg_MEDECIN_ID'));
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

                        var dropOn = dropRec ? ' ' + dropPosition + ' ' + dropRec.get('lg_CLIENT_ID') : ' on empty view';
                        internal_url = url_services_transaction_addMedecin_client+'delete&lg_CLIENT_ID='+refItem;
                        dotransaction( internal_url,data.records[0].get('lg_MEDECIN_ID'));
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