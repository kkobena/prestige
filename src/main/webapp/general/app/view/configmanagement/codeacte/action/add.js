Ext.define('testextjs.view.configmanagement.codeacte.action.add', {
    extend: 'Ext.window.Window',
    xtype: 'addcodeacte',
    id: 'addcodeacteID',
    requires: [
    'Ext.form.*',
    'Ext.window.Window'
    ],
    height: 500,
    width: 700,
    title: 'Ajouter Code Acte',
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
                fieldLabel: 'lg_CODE_ACTE_ID',
                name: 'lg_CODE_ACTE_ID',
                padding: 10,
                anchor:'80%'  // anchor width by percentage 
            },{
                fieldLabel: 'Libelle',
                name: 'str_LIBELLEE',
                padding: 10,
                anchor: '80%'  // anchor width by percentage
            }]
        });



     
    }
});


