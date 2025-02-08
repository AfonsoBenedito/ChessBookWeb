<!DOCTYPE html>
<html lang="en">
<head>
	<link rel="shortcut icon" type="image/x-icon" href="images/favicon.ico">
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="styles/error.css">
    <title>ChessBookWEB - Error</title>
</head>
<body>

    <header>
        <h1>ChessBookWEB</h1>
        <div class="header_options">
        	<a href="Logout">
            <li class="btn_sair" >
                <p>Sair</p>
            </li>
            </a>
        </div>
        <div class="zona_nome">
            <h2>Ocorreu um <span>Erro</span></h2>
        </div>
    </header>

    <div class="background">
            <div class="cube"></div>
            <div class="cube"></div>
            <div class="cube"></div>
            <div class="cube"></div>
            <div class="cube"></div>
            <div class="cube"></div>
    </div>
    
    <div id="parallax"></div>

    <div class="zona_geral">
        <div class="zona_img"></div>
        <div class="zona_erro">
            <div class="titulo_erro">OOPS... OCORREU UM ERRO.</div>
            <div class="nome_erro">� apenas um erro de liga��o � Base de Dados!</div>
            <div class="descricao_erro">Voltar � pagina inicial e diverte-te a jogar com os outros jogadores.</div>
            <a href="Logout" class="btn_voltar_login"><span>Voltar ao Login</span></a>
        </div>
    </div>

    <script src="scripts/error.js"></script>
</body>
</html>