<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import = "persist.ChessPlayerDM, domain.ChessPlayer, java.util.Optional" %>

<%
    String nomeRegisto = request.getParameter("nomeRegisto");
    String emailRegisto = request.getParameter("emailRegisto");

    String nomeLogin = request.getParameter("nomeLogin");
    String emailLogin = request.getParameter("emailLogin");


    String output;
    int newId = -1;

    if (nomeRegisto != null){

        ChessPlayerDM cpDM = ChessPlayerDM.getInstance();
        Optional<ChessPlayer> cpTest = null;

        try{
            cpTest = cpDM.findByEmail(emailRegisto);
        } catch (Exception e){
            response.sendRedirect("Erro");
        }

        if (!cpTest.isPresent()){
            newId = cpDM.insert(new ChessPlayer(nomeRegisto, emailRegisto));
            output = "Registo sucesso";

            nomeLogin = "registo";
        } else {
            output = "Registo erro";
        }
    } else if (nomeLogin != null){

        ChessPlayerDM cpDM = ChessPlayerDM.getInstance();

        Optional<ChessPlayer> cpTest = null;

        try{
            cpTest = cpDM.findByEmail(emailLogin);
        } catch (Exception e){
            response.sendRedirect("Erro");
        }

        if (cpTest.isPresent()){

            if (cpTest.get().getName().equals(nomeLogin)){

                request.getSession().setAttribute("logged","true");
                request.getSession().setAttribute("name",nomeLogin);
                request.getSession().setAttribute("email",emailLogin);
                request.getSession().setAttribute("interatividade",false);

                output = "Login sucesso";

                response.sendRedirect("GameList");
            } else {
                output = "Login erro nome";
            }

        } else {
            output = "Login erro email";
        }
    } else {
        output = "none";
    }
%>

<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" type="image/x-icon" href="images/favicon.ico">
    <meta charset="UTF-8">
    <link rel="stylesheet" href="styles/geral.css">
    <link rel="stylesheet" href="styles/registo_login.css">
    <title>ChessBookWEB - Registo</title>
</head>

<body>
<header>
    <h1>ChessBookWEB</h1>
    <div class="header_options">
        <a href="ManageDB">
            <li class="btn_jogos">
                <p>ManageDB</p>
            </li>
        </a>
    </div>
</header>
<div class="background"></div>
<div class="bloco_registo_login">
    <div class="sub_bloco_info">
        <div class="slide_total" id="slide_total">
            <div class="info_parcela">
                <div class="info_imagem">
                    <img src="images/info_1.png" alt="">
                </div>
                <div class="info_texto">
                    <h3>Sabias que o xadrez...</h3>
                    <p>Existem 170 000 000 000 000 000 000 000 000 hipoteses de movimentos
                        nas primeiras 10 jogadas</p>
                    <p>Na Romênia, é uma disciplina obrigatória</p>
                    <p>Surgiu por volta do século V na Pérsia</p>
                </div>
            </div>
            <div class="info_parcela">
                <div class="info_imagem">
                    <img src="images/info_2.png" alt="">
                </div>
                <div class="info_texto">
                    <h3>Desenvolvedores</h3>
                    <p>Grupo de alunos de LTI, na cadeira de Construção
                        de Sistemas de software</p>
                    <p>Afonso Benedito 54937</p>
                    <p>Afonso Telles 54945</p>
                    <p>Tomás Ndlate 54970</p>
                </div>
            </div>
            <div class="info_parcela">
                <div class="info_imagem">
                    <img src="images/info_3.png" alt="">
                </div>
                <div class="info_texto">
                    <h3>Porque jogar xadrez?</h3>
                    <p>Pode aumentar o QI</p>
                    <p>Exercita ambos o lados do cérebro</p>
                    <p>Aumenta a criatividade</p>
                    <p>Melhora a memória</p>
                </div>
            </div>
        </div>
        <div class="btns_slide">
            <li id="info_1"></li>
            <li id="info_2"></li>
            <li id="info_3"></li>
        </div>
    </div>
    <div class="sub_bloco_registo_login">
        <div class="bloco_btns_registo_login">
            <span class="pag_registo_btn" id="pag_registo_btn">REGISTAR</span>
            <span class="pag_login_btn" id="pag_login_btn">LOGIN</span>
        </div>
        <div class="zona_inputs" id="pag_registo">

            <form action="Registo" method="POST">
                <div class="input-container">
                    <input type="text" name="nomeRegisto" required>
                    <label>Nome</label>
                </div>

                <div class="input-container">
                    <input type="email" name="emailRegisto" required>
                    <label>Email</label>
                    <p id="erro_email_registo">Este email não é válido!</p>
                </div>

                <p class="condicao_registo">Este registo serve apenas para poder acessar ao jogo, não há qualquer palavra-passe
                    e qualquer pessoa consegue aceder à sua conta.</p>

                <button type="submit" class="btn">Registar</button>
            </form>
        </div>
        <div class="zona_inputs" id="pag_login">
            <form action="Registo" method="POST">
                <div class="input-container">
                    <input type="text" name="nomeLogin" required>
                    <label>Nome</label>
                </div>
                <div class="input-container">
                    <input type="email" name="emailLogin" required>
                    <label>Email</label>
                    <p id="erro_email_login">Este email não existe!</p>
                </div>
                <p class="erro_geral" id="erro_nao_correspondem">Nome e email não correspondem!</p>
                <button type="submit" class="btn">Login</button>
            </form>

        </div>
    </div>
</div>

<div class="conta_registada" id="conta_registada">
    <p>Conta registada com sucesso. Faça o login.</p>
</div>

<script src="scripts/registo_login.js"></script>
<%
    if (nomeLogin != null){
%>
<script>
    change_to_login();
</script>
<%
    }
    if (output.equals("Registo sucesso")){
%>
<script>
    abrir_contaRegistada();
</script>
<%
} else if (output.equals("Registo erro")){
%>
<script>
    abrir_erroEmailregisto();
</script>
<%
} else if (output.equals("Login erro nome")){
%>
<script>
    abrir_erroNaoCorrespondem();
</script>
<%
} else if (output.equals("Login erro email")){
%>
<script>
    abrir_erroEmailLogin();
</script>
<%
    }
%>
</body>
</html>
