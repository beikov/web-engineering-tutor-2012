<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<jsp:useBean id="gameBean" class="webapp.model.Formel0SinglePlayerGame" scope="session" />
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="de">
    <head>
        <title xml:lang="de">Formel 0 - Spielen</title>
        <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
        <link rel="stylesheet" type="text/css" href="styles/screen.css" />
        <script src="js/jquery.js" type="text/javascript"></script>
    </head>
    <body>
        <div id="container">
            <div id="bordercontainer">
                <div id="header">
                    <div id="header_left"><h1 class="accessibility">Formel 0</h1></div>
                    <div id="header_right"></div>
                </div>
                <div id="navigation">
                    <h2 class="accessibility">Navigation</h2>
                    <ul title="Navigation">
                        <li><a id="startNewGame" href="Servlet?action=restart" tabindex="1">Neues Spiel</a></li>
                        <li><a id="logout" href="#" tabindex="2">Ausloggen</a></li>
                    </ul>
                </div>
                <div id="main-area">
                    <div class="info">
                        <h2>Spielinformationen</h2>
                        <table summary="Diese Tabelle zeigt Informationen zum aktuellen Spiel">
                            <tr>
                                <th id="leaderLabel" class="label">F&uuml;hrender</th>
                                <td id="leader" class="data">
                                    <% if (gameBean.getLeader() == null) {%>
                                    mehrere
                                    <% } else {%>
                                    <%= gameBean.getLeader().getName()%>
                                    <% }%>
                                </td>
                            </tr>
                            <tr>
                                <th id="roundLabel" class="label">Runde</th>
                                <td id="round" class="data"><%= gameBean.getRound()%></td>
                            </tr>
                            <tr>
                                <th id="timeLabel" class="label">Zeit</th>
                                <td id="time" class="data"><%= gameBean.getTime()%></td>
                            </tr>
                            <tr>
                                <th id="computerScoreLabel" class="label">W&uuml;rfelergebnis <em><%= gameBean.getPlayer2().getName()%></em></th>
                                <td id="computerScore" class="data"><%= gameBean.getComputerScore()%></td>
                            </tr>  	      	      	    
                        </table>  
                        <h2>Spieler</h2>
                        <table summary="Diese Tabelle listet die Namen der Spieler auf">
                            <tr>
                                <th id="player1NameLabel" class="label">Spieler 1</th>
                                <td id="player1Name" class="data"><%= gameBean.getPlayer1().getName()%></td>
                            </tr>
                            <tr>
                                <th id="player2NameLabel" class="label">Spieler 2</th>
                                <td id="player2Name" class="data"><%= gameBean.getPlayer2().getName()%></td>
                            </tr>
                        </table>    	  
                    </div>
                    <div id="field" class="field">
                        <h2 class="accessibility">Spielbereich</h2>
                        <ol id="road">
                            <li id="start_road">
                                <span class="accessibility">Startfeld</span>
                                
                                <% if(gameBean.getPlayer1().getPositionMinusT(0) == 0) { %>
                                <span id="player1"><span class="accessibility"><em>Spieler 1</em></span></span>
                                <% } %>
                                
                                <% if(gameBean.getPlayer2().getPositionMinusT(0) == 0) { %>
                                <span id="player2"><span class="accessibility"><em>Spieler 2</em></span></span>
                                <% } %>
                            </li>
                            <li class="empty_road" id="road_1">
                                <span class="accessibility">Feld 2</span>
                                
                                <% if(gameBean.getPlayer1().getPositionMinusT(0) == 1) { %>
                                <span id="player1"><span class="accessibility"><em>Spieler 1</em></span></span>
                                <% } %>
                                
                                <% if(gameBean.getPlayer2().getPositionMinusT(0) == 1) { %>
                                <span id="player2"><span class="accessibility"><em>Spieler 2</em></span></span>
                                <% } %>
                            </li>
                            <li class="oil_road" id="road_2">
                                <span class="accessibility">Feld 3</span>
                                
                                <% if(gameBean.getPlayer1().getPositionMinusT(0) == 2) { %>
                                <span id="player1"><span class="accessibility"><em>Spieler 1</em></span></span>
                                <% } %>
                                
                                <% if(gameBean.getPlayer2().getPositionMinusT(0) == 2) { %>
                                <span id="player2"><span class="accessibility"><em>Spieler 2</em></span></span>
                                <% } %>
                            </li>
                            <li class="empty_road" id="road_3">
                                <span class="accessibility">Feld 4</span>
                                
                                <% if(gameBean.getPlayer1().getPositionMinusT(0) == 3) { %>
                                <span id="player1"><span class="accessibility"><em>Spieler 1</em></span></span>
                                <% } %>
                                
                                <% if(gameBean.getPlayer2().getPositionMinusT(0) == 3) { %>
                                <span id="player2"><span class="accessibility"><em>Spieler 2</em></span></span>
                                <% } %>
                            </li>
                            <li class="empty_road"  id="road_4">
                                <span class="accessibility">Feld 5</span>
                                
                                <% if(gameBean.getPlayer1().getPositionMinusT(0) == 4) { %>
                                <span id="player1"><span class="accessibility"><em>Spieler 1</em></span></span>
                                <% } %>
                                
                                <% if(gameBean.getPlayer2().getPositionMinusT(0) == 4) { %>
                                <span id="player2"><span class="accessibility"><em>Spieler 2</em></span></span>
                                <% } %>
                            </li>
                            <li class="oil_road" id="road_5">
                                <span class="accessibility">Feld 6</span>
                                
                                <% if(gameBean.getPlayer1().getPositionMinusT(0) == 5) { %>
                                <span id="player1"><span class="accessibility"><em>Spieler 1</em></span></span>
                                <% } %>
                                
                                <% if(gameBean.getPlayer2().getPositionMinusT(0) == 5) { %>
                                <span id="player2"><span class="accessibility"><em>Spieler 2</em></span></span>
                                <% } %>
                            </li>
                            <li id="finish_road">
                                <span class="accessibility">Zielfeld</span>
                                
                                <% if(gameBean.getPlayer1().getPositionMinusT(0) == 6) { %>
                                <span id="player1"><span class="accessibility"><em>Spieler 1</em></span></span>
                                <% } %>
                                
                                <% if(gameBean.getPlayer2().getPositionMinusT(0) == 6) { %>
                                <span id="player2"><span class="accessibility"><em>Spieler 2</em></span></span>
                                <% } %>
                            </li>
                        </ol>
                    </div>
                    <div id="player" class="player">
                        <h2 class="accessibility">W&uuml;rfelbereich</h2>
                        <% if (gameBean.getRound() != 1) {%>
                        <span class="accessibility"><%= gameBean.isGameOver() ? "Das Spiel ist beendet. " : ""%>Das letzte Wurfergebnis des Spielers </span><div id="currentPlayerName"><%= gameBean.getPlayer1().getName()%></div><span class="accessibility"> war:</span>
                        <% } else { %>
                        <span class="accessibility">An der Reihe ist </span><div id="currentPlayerName"><%= gameBean.getPlayer1().getName()%></div>
                        <% } %>
                        <form name="diceform" action="Servlet" method="post">
                            <div>
                                <input type="hidden" name="action" value="dice"/>
                                <input id="dice" type="image" tabindex="4" <%= gameBean.isGameOver() ? "disabled=\"disabled\"" : ""%> src="img/wuerfel<%=gameBean.getPlayerScore()%>.png" title="W&uuml;rfel mit einer <%=gameBean.getPlayerScore()%>" alt="W&uuml;rfel mit einer <%=gameBean.getPlayerScore()%>"/>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            <div id="footer">
                &copy; 2013 Formel 0
            </div>
        </div>

        <script type="text/javascript">
            //<![CDATA[
            doAnimations();

            function doAnimations() {
                var timeoutPlayer2 = 0;

            <% if (gameBean.getRound() != 1) {%>
                setPlayersToLastPosition();
                timeoutPlayer2 = animatePlayer1();
                animatePlayer2(timeoutPlayer2);
            <% }%>
            }

            function setPlayersToLastPosition() {
                if (player1ReachedOil()) {
                    $("#player1").appendTo(getFieldId(<%= gameBean.getPlayer1().getPositionMinusT(2)%>));
                } else if (player1Moved()) {
                    $("#player1").appendTo(getFieldId(<%= gameBean.getPlayer1().getPositionMinusT(1)%>));
                }
            <% if (gameBean.getComputerScore() != 0) {%>
                if (player2ReachedOil()) {
                    $("#player2").appendTo(getFieldId(<%= gameBean.getPlayer2().getPositionMinusT(2)%>));
                } else if (player2Moved()) {
                    $("#player2").appendTo(getFieldId(<%= gameBean.getPlayer2().getPositionMinusT(1)%>));
                }
            <% }%>
            }

            function animatePlayer1() {
                var animationTime = 0;
                if (player1ReachedOil()) {
                    doDoubleMove("#player1", getFieldId(<%= gameBean.getPlayer1().getPositionMinusT(1)%>), getFieldId(<%= gameBean.getPlayer1().getPositionMinusT(0)%>));
                    animationTime = 1000;
                } else if (player1Moved()) {
                    doSingleMove("#player1", getFieldId(<%= gameBean.getPlayer1().getPositionMinusT(0)%>));
                    animationTime = 400;
                }
                return animationTime;
            }

            function animatePlayer2(timeoutPlayer2) {
            <% if (gameBean.getComputerScore() == 0) {%>
                $("#player2").appendTo(getFieldId(<%= gameBean.getPlayer2().getPositionMinusT(0)%>));
            <% } else {%>
                if (player2ReachedOil()) {
                    setTimeout(doDoubleMove, timeoutPlayer2, "#player2", getFieldId(<%= gameBean.getPlayer2().getPositionMinusT(1)%>), getFieldId(<%= gameBean.getPlayer2().getPositionMinusT(0)%>));
                } else if (player2Moved()) {
                    setTimeout(doSingleMove, timeoutPlayer2, "#player2", getFieldId(<%= gameBean.getPlayer2().getPositionMinusT(0)%>));
                }
            <% }%>
            }

            function player1ReachedOil() {
            <% if (gameBean.getPlayer1().getPositionMinusT(1) > gameBean.getPlayer1().getPositionMinusT(0)) {%>
                return true;
            <% } else {%>
                return false;
            <% }%>
            }

            function player1Moved() {
            <% if (gameBean.getPlayer1().getPositionMinusT(1) < gameBean.getPlayer1().getPositionMinusT(0)) {%>
                return true;
            <% } else {%>
                return false;
            <% }%>
            }

            function player2ReachedOil() {
            <% if (gameBean.getPlayer2().getPositionMinusT(1) > gameBean.getPlayer2().getPositionMinusT(0)) {%>
                return true;
            <% } else {%>
                return false;
            <% }%>
            }

            function player2Moved() {
            <% if (gameBean.getPlayer2().getPositionMinusT(1) < gameBean.getPlayer2().getPositionMinusT(0)) {%>
                return true;
            <% } else {%>
                return false;
            <% }%>
            }

            function getFieldId(fieldindex) {
                if (fieldindex == 6) {
                    return "#finish_road";
                } else if (fieldindex > 0 && fieldindex < 6) {
                    return "#road_" + fieldindex;
                }
                return "#start_road";
            }

            function doSingleMove(playerId, fieldId) {
                $(playerId).fadeOut(200, function() {
                    $(playerId).appendTo(fieldId);

                    $(playerId).fadeIn(200);
                });
            }

            function doDoubleMove(playerId, fieldId1, fieldId2) {
                $(playerId).fadeOut(200, function() {
                    $(playerId).appendTo(fieldId1);
                    $(playerId).fadeIn(200, function() {
                        $(playerId).fadeOut(200, function() {
                            $(playerId).appendTo(fieldId2);

                            $(playerId).fadeIn(200);
                        });
                    });
                });
            }

            //]]>
        </script>
    </body>
</html>