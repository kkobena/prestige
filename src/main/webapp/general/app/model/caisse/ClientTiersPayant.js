/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/* global Ext */

Ext.define('testextjs.model.caisse.ClientTiersPayant', {
    extend: 'Ext.data.Model',
    idProperty: 'compteTp',
    fields: [
        {name: 'compteTp', type: 'string'},
        {name: 'message', type: 'string'},
        {name: 'taux', type: 'int'},
        {name: 'numBon', type: 'string'},
        {name: 'tpFullName', type: 'string'},
        {name: 'lgTIERSPAYANTID', type: 'string'},
        {name: 'lgCOMPTECLIENTID', type: 'string'},
        {name: 'tpnet', type: 'number'},
        {name: 'discount', type: 'number'},
        {name: 'dbPLAFONDENCOURS', type: 'number'},
        {name: 'dbCONSOMMATIONMENSUELLE', type: 'number'},
        {name: 'dblPLAFOND', type: 'number'},
        {name: 'dblQUOTACONSOMENSUELLE', type: 'number'},
        {name: 'principal', type: 'boolean'},
        {name: 'bIsAbsolute', type: 'boolean'},
        {name: 'order', type: 'int'}


    ]
});

