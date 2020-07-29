/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global Ext */

Ext.define('testextjs.view.produits.form.LaboratoireForm', {
    extend: 'Ext.window.Window',
    xtype: 'laboratoireForm',
    autoShow: false,
    height: 200,
    width: '45%',
    modal: true,
    title: 'Laboratoire produit',
    closeAction: 'hide',
    closable: true,
   layout: 'fit',
    items: [
        {
            xtype: 'form',
            bodyPadding: 5,
            border:0,
            modelValidation: true,
            
           layout: 'fit',
            items: [

                {
                    title: '',
                    layout: 'anchor',
                     border:0,
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

