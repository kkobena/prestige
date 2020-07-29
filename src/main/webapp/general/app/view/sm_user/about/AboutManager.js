Ext.define('testextjs.view.sm_user.about.AboutManager', {
    extend: 'Ext.window.Window',
    xtype: 'aboutmanager',
    id: 'aboutmanagerID',
   // frame: true,
    title: 'A Propos',
    bodyPadding: 10,
    //autoScroll: true,
    width: 365,
    height:590,

    /*fieldDefaults: {
        labelAlign: 'right',
        labelWidth: 115,
        msgTarget: 'side'  
    },*/
    initComponent: function() {




        var Version_Produit = new Ext.form.field.Display(
        {
            xtype: 'displayfield',
            fieldLabel: 'Version.Produit:',
            forId: 'myFieldId',
            value: '<span style="color: blue;">'+'PRESTIGE II '+'</span>',
            margin: '0 0 0 10'
        });


        var Date_Edition = new Ext.form.field.Display(
        {
            xtype: 'displayfield',
            fieldLabel: 'Date.Edition:',
            forId: 'myFieldId',
            value: '<span style="color: blue;">'+'03/2014'+'</span>',
            margin: '0 0 0 10'
        });

        var Description = new Ext.form.field.Display(
        {
            xtype: 'displayfield',
            fieldLabel: 'Description:',
            forId: 'myFieldId',
            value: '<span style="color: blue;">'+'Prestige II  est un logiciel qui facilite la gestion de votre officine'+'</span>',
            margin: '0 0 0 10'
        });

        

        this.items = [{
            xtype: 'image',
            src: 'resources/images/about.jpg',
            mode: 'image',
            anchor: '100%'
        },
        {
            xtype: 'fieldset',
            title: 'Detail.Infos.Logiciel',
            defaultType: 'textfield',
            defaults: {
                anchor: '100%'
            },
            items: [
            Version_Produit,
            Date_Edition,
            Description
            ]
        }];

        this.callParent();
    }/*,
    buttons: [

    {
        text: 'Fermer',
        id: 'btn_FermerID',
        name: 'btn_Fermer',
        disabled: true,
        formBind: true,
        handler: function() {
            // alert("close")
            close();

        }

    }]*/

      

});





































