
/* global Ext */



Ext.define('testextjs.view.depot.Import', {
    extend: 'Ext.form.Panel',
    xtype: 'impordepotvents',
    frame: true,
    title: 'Importer les ventes VO au format JSON',
    bodyPadding: 20,
    width: '55%',
    height: 200,
    initComponent: function () {

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
                            xtype: 'filefield',
                            margin: '10',
                            fieldLabel: 'Fichier JSON',
                            emptyText: 'Fichier JSON',
                            name: 'fichier',
                            allowBlank: false,
                            buttonText: 'Choisir un fichier JSON'

                        },
                        {
                            xtype: 'hiddenfield',
                            name: 'option',
                            value: 'import'
                        }

                    ]
                }],
            buttons: [
                {
                    text: 'Importer',
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
            url: '../ImportationVenteCtr',
            waitMsg: 'Veuillez patienter le temps du telechargemetnt du fichier...',
            timeout: 360000000,
            success: function (formulaire, action) {
                var result = Ext.JSON.decode(action.response.responseText, true);
                Ext.MessageBox.show({
                    title: 'Message d\'erreur',
                    width: 320,
                    msg: result.count + ' vente(s) prises en compte sur ' + result.ligne,
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
