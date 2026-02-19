function dropJogadas(num_game){
    document.getElementsByClassName("display_jogadas")[num_game].style.display = "block";

    document.getElementsByClassName('arg_btn_expandir')[num_game].style.transform = "rotate(90deg)";

    document.getElementsByClassName('arg_btn_expandir')[num_game].addEventListener("mouseover", function (event){

        this.style.transform = "rotate(90deg) scale(0.9)";

    })

    document.getElementsByClassName('arg_btn_expandir')[num_game].addEventListener("mouseleave", function (event){

        this.style.transform = "rotate(90deg)";
        
    })
}

function closeJogadas(num_game){
    document.getElementsByClassName("display_jogadas")[num_game].style.display = "none";
    
    document.getElementsByClassName('arg_btn_expandir')[num_game].style.transform = "rotate(0deg)";

    document.getElementsByClassName('arg_btn_expandir')[num_game].addEventListener("mouseover", function (event){

        this.style.transform = "rotate(0deg) scale(0.9)";

    })

    document.getElementsByClassName('arg_btn_expandir')[num_game].addEventListener("mouseleave", function (event){

        this.style.transform = "rotate(0deg)";
        
    })

}

function btn_suporte(x){
    document.getElementsByClassName('arg_btn_expandir')[x].addEventListener("click" , function abrir(event){

        dropJogadas(x);

        this.removeEventListener("click", abrir);

        this.addEventListener("click", function fechar(event){

            closeJogadas(x);

            this.removeEventListener("click", fechar);

            this.addEventListener("click", abrir)

        })
    })
}


function mudarParaJogadores(){
    document.getElementById("content_total_partidas").style.display = "none";
    document.getElementById("content_total_jogadores").style.display = "initial";

    document.getElementById("btn_partidas").style.color = "rgb(91, 91, 91)";
    document.getElementById("btn_partidas").style.borderBottomColor = "rgb(91, 91, 91)";
    document.getElementById("btn_partidas").style.borderBottomWidth = "2px";

    document.getElementById("btn_jogadores").style.color = "#22b08a";
    document.getElementById("btn_jogadores").style.borderBottomColor = "#22b08a";
    document.getElementById("btn_jogadores").style.borderBottomWidth = "3px";
}

function mudarParaPartidas(){
    document.getElementById("content_total_partidas").style.display = "initial";
    document.getElementById("content_total_jogadores").style.display = "none";

    document.getElementById("btn_partidas").style.color = "#22b08a";
    document.getElementById("btn_partidas").style.borderBottomColor = "#22b08a";
    document.getElementById("btn_partidas").style.borderBottomWidth = "3px";

    document.getElementById("btn_jogadores").style.color = "rgb(91, 91, 91)";
    document.getElementById("btn_jogadores").style.borderBottomColor = "rgb(91, 91, 91)";
    document.getElementById("btn_jogadores").style.borderBottomWidth = "2px";
}




function main (){
    document.getElementById("btn_partidas").addEventListener("click", mudarParaPartidas);
    
    document.getElementById("btn_jogadores").addEventListener("click", mudarParaJogadores);

    btns_drop = document.getElementsByClassName('arg_btn_expandir')

    for (let i = 0; i < btns_drop.length; i++){
        btn_suporte(i);
    }
}

window.addEventListener("load", main);