
/* global Ext */

Ext.define('testextjs.view.vente.user.Medecin', {
    extend: 'Ext.window.Window',
    xtype: 'medecin',
    autoShow: false,
    height: 320,
    width: '60%',
    modal: true,
    title: 'GESTION DES MEDECINS',
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
            itemId: 'medecinform',
            bodyPadding: 5,
            modelValidation: true,
            layout: {
                type: 'fit',
                align: 'stretch'
            },
            items: [

                {
                    xtype: 'fieldset',
                    title: 'Information médecin',
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
                            fieldLabel: 'Nom et prénom(s)',
                            emptyText: 'Nom et prénom(s)',
                            name: 'nom',
                            height: 45,
                            allowBlank: false,
                            enableKeyEvents: true

                        }, {
                            fieldLabel: 'Numéro ordre',
                            emptyText: 'Numéro ordre',
                            name: 'numOrdre',
                            height: 45,
                            allowBlank: false,
                            enableKeyEvents: true

                        },
                        {
                            fieldLabel: 'Commentaire',
                            name: 'commentaire',
                            xtype: 'textareafield',
                            grow: true
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
                    itemId: 'btnNewMedecin',
                    disabled: true,
                    text: 'Enregistrer'
                },
                {
                    xtype: 'button',
                    itemId: 'btnCancelMedecin',
                    text: 'Annuler'

                }
            ]
        }
    ]
});

