/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global Ext */

Ext.define('testextjs.view.produits.form.GammeProduitForm', {
    extend: 'Ext.window.Window',
    xtype: 'gammeProduitForm',
    autoShow: false,
    height: 250,
    width: '50%',
    modal: true,
    title: 'Formulaire gamme produit',
    closeAction: 'hide',
    closable: true,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    items: [
        {
            xtype: 'form',
            bodyPadding: 5,
            modelValidation: true,
            layout: {
                type: 'fit',
                align: 'stretch'
            },
            items: [

                {
                    title: '',
                    layout: 'anchor',
                    defaults: {
                        anchor: '100%',
                        xtype: 'textfield',
                        msgTarget: 'side',
                        labelAlign: 'right',
                        labelWidth: 115
                    },
                    items: [
                        {
                            xtype: 'textfield',
                            fieldLabel: 'Code',
                            emptyText: 'Code',
                            name: 'code',
                            height: 45,
                            allowBlank: false,
                            enableKeyEvents: true
                        },
                        {
                            xtype: 'textareafield',
                            grow: true,
                            fieldLabel: 'Libellé',
                            emptyText: 'Libellé',
                            name: 'libelle',
                            anchor: '100%',
                            allowBlank: false

                        },
                        {
                            xtype: 'hiddenfield',
                            value:null,
                            name: 'id'

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

