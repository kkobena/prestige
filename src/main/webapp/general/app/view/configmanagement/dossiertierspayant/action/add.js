Ext.define('testextjs.view.configmanagement.dossiertierspayant.action.add', {
    extend: 'Ext.window.Window',
    xtype: 'addcdossiertierspayant',
    id: 'adddossiertierspayantID',
    requires: [
    'Ext.form.*',
    'Ext.window.Window'
    ],
    height: 500,
    width: 700,
    title: 'Add Dossier Tiers Payant',
    autoScroll: true,
    bodyPadding: 10,
    // html: testextjs.DummyText.extraLongText,
    constrain: true ,
    initComponent: function() {

        var field = new Ext.form.field.Text({
            renderTo: document.body
        }), fieldHeight = field.getHeight(),
        padding = 5,
        remainingHeight;

        field.destroy();
        var form = new Ext.form.Panel({
            border: false,
            fieldDefaults: {
                labelWidth: 60
            },
            defaultType: 'textfield',
            bodyPadding: padding,

            items: [{
                fieldLabel: 'Libelle',
                name: 'Libelle',
                padding: 10,
                anchor:'80%'  // anchor width by percentage
            } ]
        });



      /*  new Ext.window.Window({
            autoShow: true,
            title: 'Ajouter skin',
            width: 500,
            height:300,
            minWidth: 300,
            minHeight: 200,
            layout: 'fit',
            plain: true,
            items: form,

            buttons: [{
                text: 'Enregistrer'
            },{
                text: 'Annuler',
                handler: this.onbtnsave

            }]
        });*/


    }
});


