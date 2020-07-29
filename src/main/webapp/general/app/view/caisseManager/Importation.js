
/* global Ext */



Ext.define('testextjs.view.caisseManager.Importation', {
    extend: 'Ext.form.Panel',
    xtype: 'importationgp',
    frame: true,
    title: 'importation',
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
            data: [{str_TYPE_TRANSACTION: 'F', str_desc: 'FAMILLE'}, {str_TYPE_TRANSACTION: 'G', str_desc: 'GROSSISTE'}]
        });
        var me = this;

        Ext.applyIf(me, {
            items: [{
                    xtype: 'fieldset',
                    title: 'CHOISIR UN FICHIER',
                    defaultType: 'textfield',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            xtype: 'combobox',
                            fieldLabel: 'Type d\'option',
                            name: 'option',
                            itemId: 'option',
                            store: store_type,
                            allowBlank: false,
                            valueField: 'str_TYPE_TRANSACTION',
                            displayField: 'str_desc',
                            value: 'F',
                            editable: false,
                            queryMode: 'local',
                            emptyText: 'Choisir une option...'
                        },
                        {
                            xtype: 'filefield',
                            fieldLabel: 'CSV',
                            emptyText: 'CSV',
                            name: 'fichier',
                            allowBlank: false,
                            buttonText: 'Choisir un fichier CSV',
//                            width: 400,
                            itemId: 'fichier'
                        }

                    ]
                }],
            buttons: [
                {
                    text: 'Valider',
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

        form.submit({
            url: '../UpdateArticle',
            waitMsg: 'Veuillez patienter le temps du telechargemetnt du fichier...',
            timeout: 3600,
            success: function (formulaire, action) {
                var result = Ext.JSON.decode(action.response.responseText, true);


                Ext.MessageBox.show({
                    title: 'Message d\'erreur',
                    width: 320,
                    msg: result.count + ' Produit(s) prix en compte sur ' + result.ligne,
                    buttons: Ext.MessageBox.OK,
                    icon: Ext.MessageBox.INFO

                });


            },
            failure: function (formulaire, action) {
                Ext.MessageBox.alert('Erreur', 'Erreur  ' + action.result.errors);
            }
        });



    }
});
