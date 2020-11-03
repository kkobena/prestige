/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/* global Ext */

Ext.define('testextjs.model.caisse.ClientLambda', {
    extend: 'Ext.data.Model',
    idProperty: 'lgCLIENTID',
    fields: [
        {
            name: 'lgCLIENTID',
            type: 'string'
        },
        {
            name: 'strFIRSTNAME',
            type: 'string'
        },
        {
            name: 'strLASTNAME',
            type: 'string'
        },
        {
            name: 'strADRESSE',
            type: 'string'
        }, {
            name: 'lgTYPECLIENTID',
            type: 'string'
        }, {
            name: 'strSEXE',
            type: 'string'
        }
        , {
            name: 'fullName',
            type: 'string'
        }, {
            name: 'email',
            type: 'string'
        }



    ]
});
