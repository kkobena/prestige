/* global Ext */

var OFFICINE = localStorage.getItem("OFFICINE");
var str_PIC = localStorage.getItem("str_PIC");
var lg_EMPLACEMENT_ID = localStorage.getItem("lg_EMPLACEMENT_ID");
var lg_USER_ID;
Ext.define('testextjs.view.Header', {
    extend: 'Ext.Container',
    xtype: 'appHeader',
    id: 'app-header',
    height: 52,
    bodyStyle: "background-image:url(../../../resources/images/headerlb.png) !important",
    layout: {
        type: 'hbox',
        align: 'middle'
    },
    tools: [
        {
            type: 'toggle'
        },
        {
            type: 'close'
        },
        {
            type: 'minimize'
        },
        {
            type: 'maximize'
        },
        {
            type: 'restore'
        },
        {
            type: 'gear'
        },
        {
            type: 'pin'
        },
        {
            type: 'unpin'
        },
        {
            type: 'right'
        },
        {
            type: 'left'
        },
        {
            type: 'down'
        },
        {
            type: 'refresh'
        },
        {
            type: 'minus'
        },
        {
            type: 'plus'
        },
        {
            type: 'help'
        },
        {
            type: 'search'
        },
        {
            type: 'save'
        },
        {
            type: 'print'
        }
    ],
    initComponent: function () {
        Me_header = this;
//alert("str_PIC:"+str_PIC);
        this.items = [{
                xtype: 'component',
                id: 'app-header-title',
                html: '<a href="#" onclick="loadMainMenu();" style="text-decoration:none; color:#FFFFFF;">PRESTIGE 2</a>',
                flex: 1
            }
        ];

        lg_USER_ID = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    fieldLabel: 'User Id',
                    hidden: true,
                    name: 'lg_USER_ID',
                    id: lg_USER_ID,
                    emptyText: 'lg_USER_ID'
                });



        var btnConfig = new Ext.button.Split({
            xtype: 'splitbutton',
            icon: 'resources/images/icons/fam/cog.png',
            id: 'commonsettingapp',
            text: '',
            menu: [{
                    text: 'Mon compte',
                    handler: function () {
                        testextjs.app.getController('App').onLoadNewComponent("myaccountmanager", "Mon compte", "");
                    }
                }, {
                    text: 'Deconnexion',
                    handler: function () {
                        Me_header.Deconnexion();
                    }

                }, {
                    text: 'Aide',
                    handler: function () {
                        alert("Pas implementé");
                    }
                }
                , {
                    text: 'Metro',
                    handler: function () {
//                        testextjs.app.getController('App').onLoadNewComponent("mainmenumanager", "", "");
                        testextjs.app.getController('App').onLoadNewComponent(xtypeload, "", "");

                    }
                }

                , {
                    text: 'A propos',
                    handler: function () {
                        // testextjs.app.getController('App').onLoadNewComponent("aboutmanager", "A Propos","");
                        testextjs.app.getController('App').onLoadNewComponent("aboutmanager", "A Propos", "");
                    }
                }]
        });




        if (!Ext.getCmp('options-toolbar')) {
            this.items.push(
                    {
                        xtype: 'component',
                        cls: 'liner',
                       
                       
                         html: '<p class="microsoft marquee"><span id="bienvenu" >Bienvenue à   <span id="officine">  * ' + OFFICINE + ' *</span>  </span></p>'
                                //  html: '<span style="font-size: 2.5em;font-weight:bold;font-family:Buxton Sketch;color:white;display:inline-block;margin-right:350px;margin-top:20px;width: 100%;">' + OFFICINE + '</span>'
                    }, {
                xtype: 'component',
                html: '<img src="' + str_PIC + '" style="cursor: pointer; width: 45px; height: 45px; border-radius: 5px; margin-right: 5px;" alt="photo_profile" id="photo_profile" onclick="changePicture()"/>'
            },
                    {
                        xtype: 'themeSwitcher'
                    }, lg_USER_ID, btnConfig

                    );
        }

        testextjs.app.getController('App').inituserName(); // a decommenter en cas de probleme
        this.callParent();
    },
    Deconnexion: function () {
        var internal_url = '../webservices/usermanagement/ws_transaction.jsp?mode=deconnexion';
        Ext.Ajax.request({
            url: internal_url,
            params: {
//                lg_USER_ID: lg_USER_ID.getValue()

            },
            success: function (response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                if (object.success === "0") {
                    Ext.MessageBox.alert('Error Message', object.errors);
                    return;
                }
                localStorage.clear();
                window.location.replace("../index.jsp?content=panelInfos.jsp&lng=fr&action=logout");

            },
            failure: function (response)
            {

                var object = Ext.JSON.decode(response.responseText, false);
                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);

            }
        });

    }
});


function changePicture() {
    alert("My picture");
    //testextjs.app.getController('App').onLoadNewComponent("updatepicture", "Mise a jour de la photo de profil", "");
}