<%@page import="toolkits.parameters.commonparameter"%>
<%@page import="dal.TUser"%>
<%@page import="toolkits.utils.jdom"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
  jdom.InitRessource();
  jdom.LoadRessource();
  TUser OTUser = (TUser) session.getAttribute(commonparameter.AIRTIME_USER);
  if (OTUser != null) {
%>
<script>
  sessionStorage.setItem("user", '<%=OTUser%>');
  window.location.replace("../general/index.jsp?content=panelInfos.jsp&lng=fr");
</script>
<% } %>
<!doctype html>
<html lang="fr">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><%= jdom.APP_NAME %> · v<%= jdom.APP_VERSION %></title>

  <link rel="shortcut icon" href="../resources/images/favicon.ico"/>
  <link rel="icon" type="image/png" href="../resources/images/favicon.png"/>


  <!-- ton bundle css global -->
  <link rel="stylesheet" href="../resources/css/style.css"/>

  <script src="../lib/jquery-1.4.2.js"></script>
  <script src="../app/controler.js"></script>
  <script src="../app/login.js"></script>
  <script src="../app/toolkits.js"></script>
  <script src="../app/static_data.js"></script>
  <script src="../lib/modernizr-1.5.min.js"></script>
</head>

<body class="pl-body">
  <main class="pl-center">
    <!-- Carte principale (style “3D pill UI”) -->
    <section class="pl-card" role="dialog" aria-labelledby="pl-title" aria-describedby="pl-desc">
      <!-- décor gélules (optionnel) -->
      <img class="pl-pill pl-pill--tl" src="../resources/images/pill-red.png" alt="" aria-hidden="true">
      <img class="pl-pill pl-pill--tr" src="../resources/images/pill-white.png" alt="" aria-hidden="true">
      <img class="pl-pill pl-pill--br" src="../resources/images/pill-blue.png" alt="" aria-hidden="true">
      <img class="pl-pill pl-pill--bl" src="../resources/images/pill-green.png" alt="" aria-hidden="true">

      <h1 id="pl-title" class="sr-only"><%= jdom.APP_NAME %></h1>
      <p id="pl-desc" class="sr-only">Connexion à votre espace</p>

      <!-- Message d’accueil alimenté par l’API -->
      <h2 class="pl-welcome">Bienvenue à la *<span id="pl-pharma-name"></span>*</h2>

      <div class="pl-grid">
        <!-- Colonne contenu (formulaire) -->
        <div class="pl-col pl-col--form">
          <!-- Barre “search” (grand champ haut) = identifiant -->
          <label class="sr-only" for="str_login">Identifiant</label>
          <div class="pl-input pl-input--search">
            <span class="pl-input__icon" aria-hidden="true">🔍</span>
            <input id="str_login" name="str_login" type="text" autocomplete="username" autofocus
                   placeholder="Identifiant"/>
          </div>

          <!-- 1re “pill” blanche = mot de passe -->
          <label class="sr-only" for="str_password">Mot de passe</label>
          <div class="pl-input">
            <input id="str_password" name="str_password" type="password" autocomplete="current-password"
                   placeholder="Mot de passe"/>
          </div>

          <!-- Bouton vert “pill” -->
          <button type="button" id="login" name="login" class="pl-btn">Connexion</button>
          <span class="pl-loader" id="loader" role="status" aria-live="polite" aria-label="Connexion en cours" style="display:none;">
            <span class="pl-loader__orbit" aria-hidden="true">
              <span class="pl-loader__dot"></span>
            </span>
            <span class="pl-loader__text">Connexion sécurisée…</span>

        </div>

        <!-- Colonne avatar -->
        <aside class="pl-col pl-col--avatar" aria-hidden="true">
          <div class="pl-avatar">
            <div class="pl-avatar__icon">👤</div>
          </div>
        </aside>
      </div>

      <footer class="pl-footer"><%= jdom.APP_NAME %> · v<%= jdom.APP_VERSION %></footer>
    </section>
  </main>

  <!-- Récupération du nom de la pharmacie -->
  <script>

(function () {
  var el = document.getElementById('pl-pharma-name');
  if (!el) return;

  var ctx = '<%= request.getContextPath() %>';   // ex: /prestige
  var URL = ctx + '/api/v1/officine';

  function setNameFromData(data) {
    try {
      var first = Array.isArray(data) && data.length ? data[0] : null;
      var name = first && (first.nomComplet || first.fullName || first.name);
      if (name) {
        el.textContent = name;
      } else {
        console.warn('API officine: pas de nomComplet dans la réponse', data);
        el.textContent = ''; 
      }
    } catch (e) {
      console.error('Parsing officine KO:', e);
    }
  }

  // 1) Tentative via fetch (cache désactivé)
  fetch(URL, { cache: 'no-store', credentials: 'same-origin' })
    .then(function (r) {
      if (!r.ok) throw new Error('HTTP ' + r.status);
      return r.json();
    })
    .then(setNameFromData)
    .catch(function (err) {
      console.warn('fetch officine KO, on tente jQuery:', err);
      if (window.jQuery && jQuery.getJSON) {
        jQuery.getJSON(URL, function (data) {
          setNameFromData(data);
        }).fail(function (jq, textStatus, errorThrown) {
          console.error('jQuery officine KO:', textStatus || errorThrown);
        });
      }
    });
})();
</script>

</body>
</html>
