
/* global Ext */



Ext.define('testextjs.view.caisseManager.TicketzManager', {
    extend: 'Ext.form.Panel',
    xtype: 'ticketzmanager',
    frame: true,
    title: 'TICKET Z',
    bodyPadding: 10,
    width: 500,
    fieldDefaults: {
        labelAlign: 'right',
        labelWidth: 115,
        msgTarget: 'side'
    },
    initComponent: function () {
        Me = this;
        var store_type = new Ext.data.Store({
            fields: ['str_TYPE_TRANSACTION', 'str_desc'],
            data: [{str_TYPE_TRANSACTION: 'ALL', str_desc: 'Tous les mouvements'}, {str_TYPE_TRANSACTION: 'VENTE', str_desc: 'Ventes uniquements'}]
        });
        var me = this;

        Ext.applyIf(me, {
            items: [{
                    xtype: 'fieldset',
                    title: 'Infos sur le TICKET Z',
                    defaultType: 'textfield',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            xtype: 'combobox',
                            fieldLabel: 'Type d\'option',
                            name: 'description',
                            itemId: 'description',
                            store: store_type,
                            allowBlank: false,
                            valueField: 'str_TYPE_TRANSACTION',
                            displayField: 'str_desc',
                            value:'ALL', 
                            editable: false,
                            queryMode: 'local',
                            emptyText: 'Choisir une option...'
                        },
                        {
                            xtype: 'datefield',
                            fieldLabel: 'Date',
                            name: 'dtStart',
                            itemId: 'dtStart',
                            format: 'd/m/Y',
                            value: new Date(),
                            submitFormat: 'Y-m-d',
                            allowBlank: false
                        }

                    ]
                }],
            buttons: [
                {
                    text: 'Imprimer',
                    itemId: 'btn_savemyaccountID',
                    formBind: true,
                    handler: function (btn) {
                        me.onbtnsave(btn);
                    }

                }]
        });
        this.callParent();

    },

    onbtnsave: function (buton) {
        var form = buton.up('form');
        var progress = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
        Ext.Ajax.request({
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            url: '../api/v1/caisse/tickez',
            params: Ext.JSON.encode(form.getValues()),
            success: function (response, options) {
                progress.hide();
                var result = Ext.JSON.decode(response.responseText, true);
                if (!result.success) {
                    Ext.MessageBox.show({
                        title: 'Message d\'erreur',
                        width: 320,
                        msg: result.msg,
                        buttons: Ext.MessageBox.OK,
                        icon: Ext.MessageBox.ERROR

                    });

                }

            },
            failure: function (response, options) {
                progress.hide();
//                Ext.Msg.alert("Message", 'Erreur du serveur  ' + response.status);
            }

        });
   
    }
});
