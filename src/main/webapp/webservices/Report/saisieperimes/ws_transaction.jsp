<%-- 
    Document   : ws_transaction
    Created on : 10 nov. 2016, 14:21:40
    Author     : KKOFFI
--%>
<%@page import="org.json.JSONObject"%>
<%@page import="bll.warehouse.WarehouseManager"%>
<%@page import="dal.dataManager"%>
<%@page import="dal.TUser"%>
<%@page import="toolkits.parameters.commonparameter"%>
<%
    String lg_FAMILLE_ID = "";
    Integer int_NUMBER = 0;
    String int_NUM_LOT = "";
    String dt_peremption = "";
  
     if (request.getParameter("lg_FAMILLE_ID") != null && !"".equals(request.getParameter("lg_FAMILLE_ID"))) {
         lg_FAMILLE_ID = request.getParameter("lg_FAMILLE_ID");
        
    }
   
     if (request.getParameter("int_NUMBER") != null && !"".equals(request.getParameter("int_NUMBER"))) {
        int_NUMBER = Integer.valueOf(request.getParameter("int_NUMBER"));
        
    }
    int_NUM_LOT = request.getParameter("int_NUM_LOT");
     if (request.getParameter("int_NUM_LOT") != null && !"".equals(request.getParameter("int_NUM_LOT"))) {
         int_NUM_LOT = request.getParameter("int_NUM_LOT");
        
    }
   
     if (request.getParameter("dt_peremption") != null && !"".equals(request.getParameter("dt_peremption"))) {
        dt_peremption = request.getParameter("dt_peremption");
        
    }

    TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
    dataManager OdataManager = new dataManager();

    WarehouseManager OWarehouseManager = new WarehouseManager(OdataManager, OTUser);

    JSONObject json = new JSONObject();
    if ("PENDING".equals(request.getParameter("MODE"))) {
        json = OWarehouseManager.AddStock(lg_FAMILLE_ID, int_NUMBER, int_NUM_LOT, dt_peremption);
    } else if ("ALL".equals(request.getParameter("MODE"))) {
        String ID = request.getParameter("ID");
        json = OWarehouseManager.AddProduitPerimes(ID);

    } else if ("UPDATE".equals(request.getParameter("MODE"))) {
        String ID = request.getParameter("ID");
        json = OWarehouseManager.updateStock(ID, int_NUMBER, int_NUM_LOT, dt_peremption);

    } else if ("REMOVE".equals(request.getParameter("MODE"))) {
        String ID = request.getParameter("ID");
        json = OWarehouseManager.deleteStock(ID);

    }

%>
<%=json%>