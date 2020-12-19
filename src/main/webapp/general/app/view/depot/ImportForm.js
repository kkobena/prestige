/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global Ext */

Ext.define('testextjs.view.depot.ImportForm', {
    extend: 'Ext.window.Window',
    xtype: 'importform',
    autoShow: true,
    height: 180,
    width: '40%',
    modal: true,
    title: 'CHOISIR UN FICHIER',
    closeAction: 'hide',
    closable: true,
    layout: 'fit',
    items: [
        {
            xtype: 'form',
            bodyPadding: 5,
            border: 0,
            modelValidation: true,
            layout: 'fit',
            items: [

                {
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
                            value: 'asstock'
                        }

                    ]
                }

            ]
        }
    ],
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            layout: {
                pack: 'end',
                type: 'hbox'
            },
            items: [
                {
                    xtype: 'button',
                    itemId: 'btnsave',
                    text: 'Enregistrer'
                },
                {
                    xtype: 'button',
                    itemId: 'btnCancel',
                    text: 'Annuler'

                }
            ]
        }
    ]
});

