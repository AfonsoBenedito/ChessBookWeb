function select(click){
    if (click.srcElement.classList.contains('liJogador')){
        idPlayer = click.srcElement.id
    } else {
        idPlayer = click.srcElement.parentNode.id
    }

    document.getElementsByClassName('nome_jogador_escolhido')[0].innerHTML = "<span>Oponente: </span>" + document.getElementById("h3" + idPlayer).innerText
    
    selectedId = idPlayer
    
    document.getElementsByClassName('inputStartGame')[0].value = selectedId
}

function pesquisa(){
    input = document.getElementsByClassName('input_pesquisa')[0].value

    listaOutput = []

    for (i = 0; i < listaJogadores.length && listaOutput.length < 10; i++){
        
        if (listaJogadores[i][0].toLowerCase().includes(input.toLowerCase())){

            listaOutput.push(listaJogadores[i])
        }
    
    }

    alterarResultados(listaOutput)
}

function alterarResultados(lista){
    document.getElementsByClassName('resultado_busca')[0].innerHTML = ""

    if (lista.length != 0){
        for (i = 0; i < lista.length; i++){
            li = document.createElement('li')
            h3 = document.createElement('h3')

            li.setAttribute('class','liJogador')
            li.setAttribute('id',lista[i][1])

            h3.setAttribute('id','h3' + lista[i][1])
            h3.innerText = lista[i][0]

            li.appendChild(h3)

            document.getElementsByClassName('resultado_busca')[0].appendChild(li)
        }
        clicksLi()
    }
}

function clicksLi(){
    listaSugestoes = document.getElementsByClassName('liJogador')

    for (i = 0; i < listaSugestoes.length; i++){
        document.getElementsByClassName('liJogador')[i].addEventListener('click',select)
    }
}


function onInterativo(){
    document.getElementById("modo_interativo_btn").removeEventListener("change", onInterativo)
    document.getElementById("modo_interativo_btn").addEventListener("change", offInterativo)

    document.getElementById("nome_mogo_interativo").style.color = "#00886f";
    
    document.getElementById("formsInteratividade").submit();
}

function offInterativo(){
    document.getElementById("modo_interativo_btn").removeEventListener("change", offInterativo)
    document.getElementById("modo_interativo_btn").addEventListener("change", onInterativo)

    document.getElementById("nome_mogo_interativo").style.color = "rgb(171, 171, 171)";
    
    document.getElementById("formsInteratividade").submit();
}


function onload(){
    clicksLi()

    document.getElementsByClassName('input_pesquisa')[0].addEventListener('change',pesquisa)
    
    document.getElementById("modo_interativo_btn").addEventListener("change", onInterativo)
}

selectedId = null

window.addEventListener('load',onload)