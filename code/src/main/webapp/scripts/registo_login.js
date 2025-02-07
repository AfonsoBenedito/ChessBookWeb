function slide_to_one(){
    document.getElementById("slide_total").style.left = "0%";
    document.getElementById("info_1").style.backgroundColor = "rgba(255, 255, 255)";
    document.getElementById("info_2").style.backgroundColor = "rgba(255, 255, 255, 0)";
    document.getElementById("info_3").style.backgroundColor = "rgba(255, 255, 255, 0)";
}

function slide_to_two(){
    document.getElementById("slide_total").style.left = "-100%";
    document.getElementById("info_1").style.backgroundColor = "rgba(255, 255, 255, 0)";
    document.getElementById("info_2").style.backgroundColor = "rgba(255, 255, 255)";
    document.getElementById("info_3").style.backgroundColor = "rgba(255, 255, 255, 0)";
}

function slide_to_three(){
    document.getElementById("slide_total").style.left = "-200%";
    document.getElementById("info_1").style.backgroundColor = "rgba(255, 255, 255, 0)";
    document.getElementById("info_2").style.backgroundColor = "rgba(255, 255, 255, 0)";
    document.getElementById("info_3").style.backgroundColor = "rgba(255, 255, 255)";
}

var verify = 1;

function mudarSlideTempo(){
    if (verify == 1){
        slide_to_one()
        verify = 2;
    }else if (verify == 2){
        slide_to_two()
        verify = 3;
    }else if (verify == 3){
        slide_to_three();
        verify = 1;
    }
}

function change_to_login(){
    document.getElementById("pag_registo").style.display= "none";
    document.getElementById("pag_login").style.display= "initial";
    document.getElementById("pag_login_btn").style.borderBottom="2px solid #00665d";
    document.getElementById("pag_registo_btn").style.borderBottom="2px solid rgba(82, 82, 82, 0.844)";

    document.getElementById("pag_login_btn").style.color="#00665d";
    document.getElementById("pag_registo_btn").style.color="rgb(111, 111, 111)";
    
    document.title = "ChessBookWEB - Login";
}

function change_to_register(){
    document.getElementById("pag_registo").style.display= "initial";
    document.getElementById("pag_login").style.display= "none";
    document.getElementById("pag_login_btn").style.borderBottom="2px solid rgba(82, 82, 82, 0.844)";
    document.getElementById("pag_registo_btn").style.borderBottom="2px solid #00665d";

    document.getElementById("pag_login_btn").style.color="rgb(111, 111, 111)";
    document.getElementById("pag_registo_btn").style.color="#00665d";
    
    document.title = "ChessBookWEB - Registo";
}


//------------------------

function abrir_erroEmailregisto(){
    document.getElementById("erro_email_registo").style.display= "block";
}

function fechar_erroEmailregisto(){
    document.getElementById("erro_email_registo").style.display= "none";
}


function abrir_contaRegistada(){
    document.getElementById("conta_registada").style.display= "initial";
}

function fechar_contaRegistada(){
    document.getElementById("conta_registada").style.display= "none";
}


function abrir_erroEmailLogin(){
    document.getElementById("erro_email_login").style.display= "block";
}

function fechar_erroEmailLogin(){
    document.getElementById("erro_email_login").style.display= "none";
}


function abrir_erroNaoCorrespondem(){
    document.getElementById("erro_nao_correspondem").style.display= "block";
}

function fechar_erroNaoCorrespondem(){
    document.getElementById("erro_nao_correspondem").style.display= "none";
}


//------------------------


function main (){

    document.getElementById("info_1").addEventListener("click", slide_to_one);
    document.getElementById("info_2").addEventListener("click", slide_to_two);
    document.getElementById("info_3").addEventListener("click", slide_to_three);

    document.getElementById("pag_login_btn").addEventListener("click", change_to_login)
    document.getElementById("pag_registo_btn").addEventListener("click", change_to_register)

    setInterval(mudarSlideTempo, 1000*7);
}

window.addEventListener("load", main);
