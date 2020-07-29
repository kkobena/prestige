/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global Ext */

Ext.define('testextjs.view.devis.form.DevisClient', {
    extend: 'Ext.window.Window',
    xtype: 'clientDevis',
    autoShow: false,
    height: 350,
    width: '60%',
    modal: true,
    title:'Ajout de client',
    closeAction: 'hide',
    closable: false,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    items: [
        {
            xtype: 'form',
            hidden: true,
            itemId: 'clientLambdaform',
            bodyPadding: 5,
            modelValidation: true,
            layout: {
                type: 'fit',
                align: 'stretch'
            },
            items: [

                {
                    xtype: 'fieldset',
                    title: 'Information sur le client',
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
                            fieldLabel: 'Nom',
                            emptyText: 'Nom',
                            name: 'strFIRSTNAME',
                            height: 45,
                            allowBlank: false,
                            enableKeyEvents: true
                        }
                        
                        , {
                            fieldLabel: 'Prénom',
                            emptyText: 'Prénom',
                            name: 'strLASTNAME',
                            height: 45,
                            allowBlank: false,
                            enableKeyEvents: true

                        },
                        {
                            fieldLabel: 'Téléphone',
                            emptyText: 'Téléphone',
                            name: 'strADRESSE',
                            height: 45,
                            regex: /[0-9.]/,
                            allowBlank: false,
                            enableKeyEvents: true
                        },
                        {
                            xtype: 'hiddenfield',
                            name: 'lgTYPECLIENTID',
                            value: '6',
                            allowBlank: false
                        },

                        {
                            xtype: "radiogroup",
                            fieldLabel: "Genre",
                            allowBlank: true,
                            vertical: true,
                            items: [
                                {boxLabel: 'Féminin', name: 'strSEXE', inputValue: 'F'},
                                {boxLabel: 'Masculin', name: 'strSEXE', inputValue: 'M'}
                            ]
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
                    itemId: 'btnNewLambda',
                    disabled: true,
                    text: 'Enregistrer'
                },
                {
                    xtype: 'button',
                    itemId: 'btnCancelLambda',
                    text: 'Annuler'

                }
            ]
        }
    ]
});

