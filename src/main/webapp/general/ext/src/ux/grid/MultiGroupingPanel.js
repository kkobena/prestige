/**********************************************************************************************
 * JAFFA - Java Application Framework For All - Copyright (C) 2008 JAFFA Development Group
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License (version 2.1 any later).
 *
 * See http://jaffa.sourceforge.net/site/legal.html for more details.
 *********************************************************************************************/

/** Based on Original Work found at http://extjs.com/forum/showthread.php?p=203828#post203828
 *
 * @author chander, PaulE
 * @TODO - Don't allow a field to be grouped by, if it is not sortable in the column model!!! 
 */
Ext.namespace("Ext.ux.grid");

Ext.ux.grid.MultiGroupingGrid = function(config) {
    config = config||{};
    
    // Cache the orignal column model, before state is applied
    if(config.cm)
      this.origColModel = Ext.ux.clone(config.cm.config);
    else if(config.colModel)
      this.origColModel = Ext.ux.clone(config.colModel.config);
      
    config.tbar = [{
       xtype:'tbtext'
      ,text:this.emptyToolbarText
    },{
       xtype:'tbfill'
      ,noDelete:true
    },{
       xtype:'tbbutton'
      ,text:'Options'
      ,noDelete:true
      ,menu:{
         items: [{
           text:'Restore Default Columns',
           scope: this,
           disabled: !this.origColModel,
           handler: function() {
             this.getColumnModel().setConfig(this.origColModel,false);
             this.saveState();
             return true;
           }
         },{
           text:'Show Group Columns'
          ,checked: !config.view.hideGroupedColumn
          ,scope:this
          ,checkHandler: function (item, checked) {
             this.view.hideGroupedColumn = !checked;
             this.view.refresh(true);
           }
         },{
           text: 'Clear Filters' // Labels.get('label.jaffa.jaffaRIA.jaffa.finder.grid.deactivateFilters')
          ,scope: this
          ,handler: function () {
             //@TODO use the clearFilters() method!
             this.plugins.filters.each(function(flt) {
               flt.setActive(false); 
             });
           }  
         }]     
       }
    }];
    Ext.ux.grid.MultiGroupingGrid.superclass.constructor.call(this, config);
    //console.debug("Create MultiGroupingGrid",config);
};
Ext.extend(Ext.ux.grid.MultiGroupingGrid, Ext.grid.GridPanel, {

   initComponent : function(){
     //console.debug("MultiGroupingGrid.initComponent",this);
     Ext.ux.grid.MultiGroupingGrid.superclass.initComponent.call(this);
     
     // Initialise DragZone
     this.on("render", this.setUpDragging, this);
   }
   
   /** @cfg emptyToolbarText String to display on tool bar when there are no groups
    */   
  ,emptyToolbarText : "Drop Columns Here To Group" 
   /** Extend basic version so the Grouping Columns State is remebered
    */   
  ,getState : function(){
     var s = Ext.ux.grid.MultiGroupingGrid.superclass.getState.call(this);
     s.groupFields = this.store.getGroupState();
     return s;
   }
   
   /** Extend basic version so the Grouping Columns State is applied
    */   
  ,applyState : function(state){
     Ext.ux.grid.MultiGroupingGrid.superclass.applyState.call(this,state);
     if(state.groupFields) {
        this.store.groupBy(state.groupFields,true);
        console.debug("Grid.applyState: Groups=",state.groupFields);
     }
   }  
    
  ,setUpDragging: function() {
     //console.debug("SetUpDragging", this);
     this.dragZone = new Ext.dd.DragZone(this.getTopToolbar().getEl(), {
       ddGroup:"grid-body" + this.getGridEl().id //FIXME - does this need to be unique to support multiple independant panels on the same page
      ,panel:this 
      ,scroll:false
       // @todo - docs
      ,onInitDrag : function(e) {
         // alert('init');
         var clone = this.dragData.ddel;
         clone.id = Ext.id('ven'); //FIXME??
         // clone.class='x-btn button';
         this.proxy.update(clone);
         return true;
       }

       // @todo - docs
      ,getDragData: function(e) {
         var target = Ext.get(e.getTarget().id);
         //console.debug("DragZone: ",e,target);
         if(!target || target.hasClass('x-toolbar x-small-editor')) {
           return false;
         }
           
         d = e.getTarget().cloneNode(true);
         d.id = Ext.id();
         console.debug("getDragData",this, target);
           
         this.dragData = {
           repairXY: Ext.fly(target).getXY(),
           ddel: d,
           btn:e.getTarget()
         };
         return this.dragData;
       }

       //Provide coordinates for the proxy to slide back to on failed drag.
       //This is the original XY coordinates of the draggable element.
      ,getRepairXY: function() {
         return this.dragData.repairXY;
       }
     });
     
     // This is the target when columns are dropped onto the toolbar (ie added to the group)
     this.dropTarget2s = new Ext.dd.DropTarget(this.getTopToolbar().getEl(), {
       ddGroup: "gridHeader" + this.getGridEl().id
      ,panel:this 
      ,notifyDrop: function(dd, e, data) {
         if (this.panel.getColumnModel().config[this.panel.getView().getCellIndex(data.header)].groupable) {
             console.debug("Adding Filter", data);
             var btname = this.panel.getColumnModel().getDataIndex(this.panel.getView().getCellIndex(data.header));
             this.panel.store.groupBy(btname);
             return true;
         }
         else {
             return false;
         }
       }
       ,notifyOver: function(dd,e,data){
         if (this.panel.getColumnModel().config[this.panel.getView().getCellIndex(data.header)].groupable) {
             return this.dropAllowed;
         }
         else {
             return this.dropNotAllowed;
         }
       }
     });

     // This is the target when columns are dropped onto the grid (ie removed from the group)
     this.dropTarget22s = new Ext.dd.DropTarget(this.getView().el.dom.childNodes[0].childNodes[1], {
       ddGroup: "grid-body" + this.getGridEl().id  //FIXME - does this need to be unique to support multiple independant panels on the same page
      ,panel:this 
      ,notifyDrop: function(dd, e, data) {
         var txt = Ext.get(data.btn).dom.innerHTML;
         var tb = this.panel.getTopToolbar();
         console.debug("Removing Filter", txt);
         var bidx = tb.items.findIndexBy(function(b) {
           console.debug("Match button ",b.text);
           return b.text==txt;
         },this);
         console.debug("Found matching button", bidx);
         if(bidx<0) return; // Error!
         var fld = tb.items.get(bidx).fieldName;
         
         // Remove from toolbar
         Ext.removeNode(Ext.getDom(tb.items.get(bidx).id));
         if(bidx>0) Ext.removeNode(Ext.getDom(tb.items.get(bidx-1).id));;

         console.debug("Remove button", fld);
         //console.dir(button);
         var cidx=this.panel.view.cm.findColumnIndex(fld);
         
         if(cidx<0)
           console.error("Can't find column for field ", fld);
         
         this.panel.view.cm.setHidden(cidx, false);

         //Ext.removeNode(Ext.getDom(data.btn.id));

         // Remove this group from the groupField array
         // @todo - replace with method on store
         // this.panel.store.removeGroupField(fld);
         var temp=[];
         for(var i=this.panel.store.groupField.length-1;i>=0;i--) {
           if(this.panel.store.groupField[i]==fld) {
             this.panel.store.groupField.pop();
             break;
           }
           temp.push(this.panel.store.groupField[i]);
           this.panel.store.groupField.pop();
         }

         for(var i=temp.length-1;i>=0;i--) {
           this.panel.store.groupField.push(temp[i]);
         }

         if(this.panel.store.groupField.length==0)
           this.panel.store.groupField=false;

         this.panel.store.fireEvent('datachanged', this);
         return true;
       }
     }); 
   }

  ,buildFilters: function (columns, record) {
     //console.debug("Grid.buildFilters: Created Filters from ", columns, record);
     var config = [];
     for(var i=0;i<columns.length;i++) {
       var col = columns[i];
       var meta = record.getField(col.dataIndex);
       //console.debug("Meta Data For ", col.dataIndex, meta)
       if(meta && (meta.filter || meta.filterFieldName)) {
         var dt = meta.dataType || 'string';
         if (dt=='int' || dt=='long' || dt=='float' || dt=='double')
           dt=='numeric';
         else if (dt=='dateonly' || dt=='datetime')
           dt='date';
         //FIXME pass caseType on this filter definition, so it can be applied to the filter field  
         var f = {dataIndex:col.dataIndex, type:dt, paramName:col.filterFieldName};
         config[config.length] = f;
       }
     }
     console.debug("Grid.buildFilters: Created Filters for ", config);
     if(config.length==0)
       return null;
     else  
       return new Ext.ux.grid.GridFilters({filters:config, local:false});
   }

  ,buildColumnModel: function (columns, record) {
     var config = [];
     for(var i=0;i<columns.length;i++) {
       var col = columns[i];
       var meta = record.getField(col.dataIndex);
       var cm = Ext.apply({},col);
       if(meta) {
         // Apply stuff from the Record's Meta Data
         if(!cm.hidden && meta.hidden==true) cm.hidden = true;   
         if(!cm.header && meta.label) cm.header = meta.label;
         if(!cm.renderer && meta.renderer) cm.renderer = meta.renderer;
         cm.sortable=(meta.sortable===true || (meta.sortFieldName&&meta.sortFieldName!=''));

         // Apply more metadata from associated ClassMetaData
         var mc = meta.metaClass || record.defaultMetaClass;
         var mfn = (meta.mapping||col.dataIndex).match(/.*\b(\w+)$/)[1];
         var mf = ClassMetaData[mc]?ClassMetaData[mc].fields[mfn]:undefined;
         if(!mf) mf = ClassMetaData[mc]?ClassMetaData[mc].fields[col.dataIndex]:undefined;
         console.debug("Meta Class=",mc,ClassMetaData[mc],', dataIndex=',col.dataIndex,', mapping=',meta.mapping,', mfn=',mfn,', mf=',mf,', meta=',meta);
         if(mf) {
           // Default the header text
           if(!cm.header && mf.label) cm.header=mf.label;
           // Default the column width
           if(!cm.width) {
             if(mf.maxLength) cm.width = Math.min(Math.max(mf.maxLength,5),40)*8;
             else if(mf.type) {
               if(mf.type=='dateonly') cm.width=100;
               else if(mf.type=='datetime') cm.width=140;
               else if(mf.type=='boolean') cm.width=50;
             }
           }
           // Default the alignment
           if(!cm.align && mf.type && (mf.type=='float'||mf.type=='int'))
             cm.align = 'right';
           // Default standard renderers  
           if(!cm.renderer && mf.type) {
             if(mf.type=='dateonly') cm.renderer = Ext.util.Format.dateRenderer();
             else if(mf.type=='datetime') cm.renderer = Ext.util.Format.dateTimeRenderer();
           }
           if(mf.hidden==true) cm.hidden = true;   
         }
       }  
       if(!cm.header) cm.header = col.dataIndex;
       cm.groupable = (cm.groupable==true || cm.sortable==true);
       config[config.length] = cm;
       console.debug("Grid.buildColumnModel: Width", cm.dataIndex, cm.width);

     }
     console.debug("Grid.buildColumnModel: Created Columns for ", config);
     return new Ext.grid.ColumnModel(config);
  }

});

/**
 * @class Ext.ux.grid.MultiGroupingPagingStore
 * @extends Ext.ux.grid.MultiGroupingStore
 * A specialized {@link Ext.data.Store} that allows data to be appended a page at
 * a time as the user scrolls through. It is based on performing server-side sorting
 * and grouping and should be used in conjunction with a {@link Ext.ux.grid.MultiGroupPagingGrid}  
 * @constructor
 * Create a new MultiGroupingPagingStore
 * @param {Object} config The config object
 * 
 * @author PaulE  
 */
Ext.ux.grid.MultiGroupingPagingGrid = Ext.extend(Ext.ux.grid.MultiGroupingGrid, {

  /** When creating the store, register an internal callback for post load processing
   */  
    constructor: function(config) {
      config = config||{};
      config.bbar = [].concat(config.bbar);
      config.bbar = config.bbar.concat([
        {xtype:'tbfill'}
       ,{xtype:'tbtext',id:'counter', text: '? of ?'}
       ,{xtype:'tbspacer'}
       ,{xtype:'tbbutton',id:'loading',hidden: true,iconCls: "x-tbar-loading"}
       ,{xtype:'tbseparator'}
       ,{xtype:'tbbutton',id:'more',text: 'More...',handler: function() { this.store.loadMore(false); }, scope: this}
      ]);
      
      Ext.ux.grid.MultiGroupingPagingGrid.superclass.constructor.apply(this, arguments);
      
      // Create Event that asks for more data when we scroll to the end
      this.on("bodyscroll",  function() {
            var s = this.view.scroller.dom;
            if( (s.offsetHeight+s.scrollTop+5 > s.scrollHeight) && !this.isLoading) {
              console.debug("Grid.on.bodyscroll: Get more...");
              this.store.loadMore(false);
            }  
      }, this);
      
      // When the grid start loading, display a loading icon    
      this.store.on("beforeload", function(store,o) {
        if(this.isLoading) {
          console.debug("Store.on.beforeload: Reject Load, one is in progress");
          return false;
        }
        this.isLoading = true;
        if(this.rendered) {
          this.barLoading.show();
        }
        console.debug("Store.on.beforeload: options=",o, this);
        return true;
      }, this);
      
      // When loading has finished, disable the loading icon, and update the row count  
      this.store.on("load", function() {
        delete this.isLoading;
        if(this.rendered) {
          this.barLoading.hide();
          console.debug("Store.on.load: Finished loading.. ",this.store.totalCount);
          this.barCounter.getEl().innerHTML = "Showing " + this.store.getCount()+' of ' +
                (this.store.totalCount?this.store.totalCount:'?');
          if(this.store.totalCount)
            this.barMore.disable();
          else
            this.barMore.enable();  
        }
        return true;
      }, this);

      // When a loading error occurs, disable the loading icon and display error  
      this.store.on("loadexception", function(store, e) {
        console.debug("Store.loadexception.Event:",arguments);
        delete this.isLoading;
        if(this.rendered) {
          this.barLoading.hide();
        }
        if(e)
          Ext.Msg.show({
            title:'Show Details',
            msg: "Error Loading Records - " + e,
            buttons: Ext.Msg.OK,
            icon: Ext.MessageBox.ERROR
          });
        return false;
      }, this);

      // As the default onLoad to refocus on the first row has been disabled,
      // This has been added so if a load does happen, and its an initial load
      // it refocuses. If this is a refresh caused by a sort/group or a new page
      // of data being loaded, it does not refocus  
      this.store.on("load", function(r,o) {
         if(o&&o.initial==true)
           Ext.ux.grid.MultiGroupingView.superclass.onLoad.call(this);        
      }, this.view);
    }
    
    // private
   ,onRender : function(ct, position){
        Ext.ux.grid.MultiGroupingPagingGrid.superclass.onRender.call(this, ct, position);
        var bb=this.getBottomToolbar();
        this.barCounter = bb.items.itemAt(bb.items.length-5);
        this.barMore = bb.items.itemAt(bb.items.length-1);
        this.barLoading = bb.items.itemAt(bb.items.length-3);
    }
});
