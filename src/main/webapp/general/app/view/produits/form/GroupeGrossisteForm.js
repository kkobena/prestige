/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global Ext */

Ext.define('testextjs.view.produits.form.GroupeGrossisteForm', {
    extend: 'Ext.window.Window',
    xtype: 'groupeGrossisteForm',
    autoShow: false,
 height: 200,
    width: '45%',
    modal: true,
    title: 'Groupe grossiste',
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
                            xtype: 'textareafield',
                            grow: true,
                            fieldLabel: 'Libellé',
                            emptyText: 'Libellé',
                            name: 'libelle',
                            allowBlank: false
                        },
                        {
                            xtype: 'numberfield',
                            name: 'id',
                            value:0,
                            hidden:true

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

