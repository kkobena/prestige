//var url_services ='../webservices/sm_user/institution';
var url_services_data_addprivilege = '../webservices/sm_user/privilege/ws_role_data.jsp?option=';
var url_services_transaction_addprivilege= '../webservices/sm_user/privilege/ws_role_transaction.jsp?mode=';
var OviewItem;
var OmodeItem;
var MeItem;
var refItem;
var internal_url;

Ext.define('testextjs.view.sm_user.role.action.addPrivilegeItem', {
    extend: 'Ext.container.Container',
    xtype: 'addprivilegeItem',
    id: 'addprivilegeItemID',

    requires: [
    'Ext.grid.*',
    'Ext.layout.container.HBox',
    // 'testextjs.model.dd.Simple',
    'testextjs.model.Privilege'
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

        function dotransaction(internalurl,lg_PRIVELEGE_ID){
            Ext.Ajax.request({
                url: internalurl,
                params: {
                    lg_PRIVILEGE_ID:lg_PRIVELEGE_ID
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


    
      
        refItem = this.getOdatasource().lg_ROLE_ID;

        // alert("Ouf jai mon ID "+refItem);

        var mystore = new Ext.data.Store({
            model: testextjs.model.Privilege,
            proxy: {
                type: 'ajax',
                url: url_services_data_addprivilege+'IN'+'&lg_ROLE_ID='+refItem
            }
        })

        mystore.load();


        var mystore_second = new Ext.data.Store({
            model: testextjs.model.Privilege,
            proxy: {
                type: 'ajax',
                url: url_services_data_addprivilege+'NOT_IN'+'&lg_ROLE_ID='+refItem
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
            dataIndex: 'lg_PRIVELEGE_ID'
        }, {
            text: 'Nom',
            width: 150,
            hidden:true,
            sortable: true,
            dataIndex: 'str_NAME'
        }, {
            text: 'Description',
            width: 290,
            sortable: true,
            dataIndex: 'str_DESCRIPTION'
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
                         
                        var dropOn = dropRec ? ' ' + dropPosition + ' ' + dropRec.get('str_DESCRIPTION') : ' on empty view';

                        internal_url = url_services_transaction_addprivilege+'create&lg_ROLE_ID='+refItem;
                        dotransaction( internal_url,data.records[0].get('lg_PRIVELEGE_ID'));
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
                tooltip: 'Reset both grids',
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

                        var dropOn = dropRec ? ' ' + dropPosition + ' ' + dropRec.get('str_DESCRIPTION') : ' on empty view';
                        internal_url = url_services_transaction_addprivilege+'delete&lg_ROLE_ID='+refItem;
                        dotransaction( internal_url,data.records[0].get('lg_PRIVELEGE_ID'));
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