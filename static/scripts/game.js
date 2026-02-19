function selectPiece(id){
    document.getElementById(id).classList.add('selected')
    previewJogadas(id);

}

function unselectPiece(id){
    document.getElementById(id).classList.remove('selected')
    
    while(document.getElementsByClassName('possible').length != 0){
    
        document.getElementsByClassName('possible')[0].remove();
    }
    
    while(document.getElementsByClassName('possible_comer').length != 0){
    
    	document.getElementsByClassName('possible_comer')[0].classList.remove("possible_comer");
    }

}

function adicionarPossibles(quadrados){
    const arrayQuadrados = quadrados.split(" ");
    
    idsUsados = [];
    
    for(i = 0; i < arrayQuadrados.length - 1; i++){
    
    	if (document.getElementById(arrayQuadrados[i]).innerText == ""){
	    	newDiv = document.createElement("div");
	    	
	    	newDiv.setAttribute("class", "possible")
	        
	        document.getElementById(arrayQuadrados[i]).appendChild(newDiv);
      	} else {
      		document.getElementById(arrayQuadrados[i]).classList.add("possible_comer")
      	}
    }
}

function checkClickPiece(click){
    node = click.srcElement
    while(!node.classList.contains('light') && !node.classList.contains('dark')){
    	node = node.parentNode
    }
    
    id = node.id

    if (selected == null){
    	if (document.getElementById(id).innerText != ""){
    		if (corPlayer == 'branco'){
    			if (document.getElementById(id).innerText != '♟' && document.getElementById(id).innerText != '♜' && document.getElementById(id).innerText != '♞' && document.getElementById(id).innerText != '♝' && document.getElementById(id).innerText != '♛' && document.getElementById(id).innerText != '♚'){
    				selected = String(id)
        			selectPiece(id)
    			}
    		} else if (corPlayer == 'preto'){
    			if (document.getElementById(id).innerText != '♙' && document.getElementById(id).innerText != '♖' && document.getElementById(id).innerText != '♗' && document.getElementById(id).innerText != '♕' && document.getElementById(id).innerText != '♘' && document.getElementById(id).innerText != '♔'){
    				selected = String(id)
        			selectPiece(id)
    			}
    		}
    	}
        
    } else if (selected == String(id)){
        unselectPiece(id)
        selected = null
    } else {
    
    	jogada = selected + " " + String(id)
        
	     if (document.getElementById(selected).innerText == "♙"){
	       	if (id.charAt(1) == '8' && selected.charAt(1) == '7'){
	         	abrirPromocao()
	         	return false
	        }
	     } else if (document.getElementById(selected).innerText == "♟"){
	     	if (id.charAt(1) == '1' && selected.charAt(1) == '2'){
	        	abrirPromocao()
	        	return false
	        }
	     }

        console.log("Tentar Jogada: " + jogada)

        document.getElementById('move').value= jogada

        document.getElementById('tempo').value= tempo

        document.getElementById('formsMove').submit()
    }
}

function abrirPromocao(){
	document.getElementsByClassName('zona_pop_up_promocao')[0].style.display = 'initial'
}

function enviarJogadaComPromocao(click){
	  node = click.srcElement
	  
	  while (!node.classList.contains('peca_promocao')){
	  	node = click.srcElement.parentNode
	  }
	
	  pieceToPromote = node.id
	
	  jogada += " " + pieceToPromote
	
	  console.log("Tentar Jogada: " + jogada)
	
	  document.getElementById('move').value= jogada
	
	  document.getElementById('tempo').value= tempo
	
	  console.log("submit:" + jogada + ", " + tempo)
	  document.getElementById('formsMove').submit()
}


function interatividadeTabuleiro(){
    for (i = 1; i < 9; i++){
        document.getElementById('a'+String(i)).addEventListener('click',checkClickPiece)
        document.getElementById('b'+String(i)).addEventListener('click',checkClickPiece)
        document.getElementById('c'+String(i)).addEventListener('click',checkClickPiece)
        document.getElementById('d'+String(i)).addEventListener('click',checkClickPiece)
        document.getElementById('e'+String(i)).addEventListener('click',checkClickPiece)
        document.getElementById('f'+String(i)).addEventListener('click',checkClickPiece)
        document.getElementById('g'+String(i)).addEventListener('click',checkClickPiece)
        document.getElementById('h'+String(i)).addEventListener('click',checkClickPiece)
    }
    
    for (i = 0; i < 4; i++){     
	  	document.getElementsByClassName('peca_promocao')[i].addEventListener('click',enviarJogadaComPromocao)
	}
}



// -------- POP-UPs FIM DE JOGO ---------

var c = document.getElementById("Canvas");
var ctx = c.getContext("2d");

var cwidth, cheight;
var shells = [];
var pass= [];

let counter;

var colors = ['#FF5252', '#FF4081', '#E040FB', '#7C4DFF', '#536DFE', '#448AFF', '#40C4FF', '#18FFFF', '#64FFDA', '#69F0AE', '#B2FF59', '#EEFF41', '#FFFF00', '#FFD740', '#FFAB40', '#FF6E40'];

window.onresize = function() { reset(); }
reset();
function reset() {
  cwidth = window.innerWidth;
	cheight = window.innerHeight;
	c.width = cwidth;
	c.height = cheight;
}

function newShell() {
  var left = (Math.random() > 0.5);
  var shell = {};
  shell.x = (1*left);
  shell.y = 1;
  shell.xoff = (0.01 + Math.random() * 0.007) * (left ? 1 : -1);
  shell.yoff = 0.01 + Math.random() * 0.007;
  shell.size = Math.random() * 6 + 3;
  shell.color = colors[Math.floor(Math.random() * colors.length)];

  shells.push(shell);
}

function newPass(shell) {
  var pasCount = Math.ceil(Math.pow(shell.size, 2) * Math.PI);

  for (i = 0; i < pasCount; i++) {

    var pas = {};
    pas.x = shell.x * cwidth;
    pas.y = shell.y * cheight;

    var a = Math.random() * 4;
    var s = Math.random() * 10;

		pas.xoff = s *  Math.sin((5 - a) * (Math.PI / 2));
  	pas.yoff = s *  Math.sin(a * (Math.PI / 2));

    pas.color = shell.color;
    pas.size = Math.sqrt(shell.size);

    if (pass.length < 1000) { pass.push(pas); }
  }
}

var lastRun = 0;

function Run() {

  var dt = 1;
  if (lastRun != 0) { dt = Math.min(50, (performance.now() - lastRun)); }
	lastRun = performance.now();

	ctx.fillStyle = "rgba(0,0,0,1)";
	ctx.fillRect(0, 0, cwidth, cheight);

  if ((shells.length < 10) && (Math.random() > 0.96)) { newShell(); }

  for (let ix in shells) {

    var shell = shells[ix];

    ctx.beginPath();
    ctx.arc(shell.x * cwidth, shell.y * cheight, shell.size, 0, 2 * Math.PI);
    ctx.fillStyle = shell.color;
    ctx.fill();

    shell.x -= shell.xoff;
    shell.y -= shell.yoff;
    shell.xoff -= (shell.xoff * dt * 0.001);
    shell.yoff -= ((shell.yoff + 0.2) * dt * 0.00005);

    if (shell.yoff < -0.005) {
      newPass(shell);
      shells.splice(ix, 1);
    }
  }

  for (let ix in pass) {
    var pas = pass[ix];

    ctx.beginPath();
    ctx.arc(pas.x, pas.y, pas.size, 0, 2 * Math.PI);
    ctx.fillStyle = pas.color;
    ctx.fill();

    pas.x -= pas.xoff;
    pas.y -= pas.yoff;
    pas.xoff -= (pas.xoff * dt * 0.001);
    pas.yoff -= ((pas.yoff + 5) * dt * 0.0005);
    pas.size -= (dt * 0.002 * Math.random())

    if ((pas.y > cheight)  || (pas.y < -50) || (pas.size <= 0)) {
        pass.splice(ix, 1);
    }
  }
  if (counter == 0){
    requestAnimationFrame(Run);
  } else{
    return undefined;
  }
}

// VITORIA
function ativar_vitoria(){
  counter = 0;
  document.getElementById("Canvas").style.display = "initial";
  document.getElementById("painel_vitoria").style.display = "initial";
  Run();

  document.getElementById("fechar_pop_up_background").addEventListener('click', desativar_vitoria);
   document.getElementById("btn_fechar_pop_up").addEventListener('click', desativar_vitoria);
   document.getElementById("btn_voltar_jogo").addEventListener('click', desativar_vitoria);
 }

 function desativar_vitoria(){
  counter = 1;
  document.getElementById("painel_vitoria").style.display = "none";
  document.getElementById("Canvas").style.display = "none";

  document.getElementById("fechar_pop_up_background").removeEventListener('click', desativar_vitoria);
   document.getElementById("btn_fechar_pop_up").removeEventListener('click', desativar_vitoria);
   document.getElementById("btn_voltar_jogo").removeEventListener('click', desativar_vitoria);
 }

// DERROTA
function ativar_derrota(){
  document.getElementById("painel_derrota").style.display = "initial";

  document.getElementById("fechar_pop_up_background_derrota").addEventListener('click', desativar_derrota);
   document.getElementById("btn_fechar_pop_up_derrota").addEventListener('click', desativar_derrota);
   document.getElementById("btn_voltar_jogo_derrota").addEventListener('click', desativar_derrota);
 }

 function desativar_derrota(){
  document.getElementById("painel_derrota").style.display = "none";

  document.getElementById("fechar_pop_up_background_derrota").removeEventListener('click', desativar_derrota);
   document.getElementById("btn_fechar_pop_up_derrota").removeEventListener('click', desativar_derrota);
   document.getElementById("btn_voltar_jogo_derrota").removeEventListener('click', desativar_derrota);
 }

 // EMPATE
function ativar_empate(){
  document.getElementById("painel_empate").style.display = "initial";

  document.getElementById("fechar_pop_up_background_empate").addEventListener('click', desativar_empate);
   document.getElementById("btn_fechar_pop_up_empate").addEventListener('click', desativar_empate);
   document.getElementById("btn_voltar_jogo_empate").addEventListener('click', desativar_empate);
 }

 function desativar_empate(){
  document.getElementById("painel_empate").style.display = "none";

  document.getElementById("fechar_pop_up_background_empate").removeEventListener('click', desativar_empate);
   document.getElementById("btn_fechar_pop_up_empate").removeEventListener('click', desativar_empate);
   document.getElementById("btn_voltar_jogo_empate").removeEventListener('click', desativar_empate);
 }

//-------------------- 
// Rodar Tabuleiro
function blackView(){
    document.getElementById('a1').style.transform = 'translate(700%, -700%)';
    document.getElementById('b1').style.transform = 'translate(500%, -700%)';
    document.getElementById('c1').style.transform = 'translate(300%, -700%)';
    document.getElementById('d1').style.transform = 'translate(100%, -700%)';
    document.getElementById('e1').style.transform = 'translate(-100%, -700%)';
    document.getElementById('f1').style.transform = 'translate(-300%, -700%)';
    document.getElementById('g1').style.transform = 'translate(-500%, -700%)';
    document.getElementById('h1').style.transform = 'translate(-700%, -700%)';
    document.getElementsByClassName('numero_linha')[8].style.transform = 'translate(0%, -700%)';

    document.getElementById('a2').style.transform = 'translate(700%, -500%)';
    document.getElementById('b2').style.transform = 'translate(500%, -500%)';
    document.getElementById('c2').style.transform = 'translate(300%, -500%)';
    document.getElementById('d2').style.transform = 'translate(100%, -500%)';
    document.getElementById('e2').style.transform = 'translate(-100%, -500%)';
    document.getElementById('f2').style.transform = 'translate(-300%, -500%)';
    document.getElementById('g2').style.transform = 'translate(-500%, -500%)';
    document.getElementById('h2').style.transform = 'translate(-700%, -500%)';
    document.getElementsByClassName('numero_linha')[7].style.transform = 'translate(0%, -500%)';

    document.getElementById('a3').style.transform = 'translate(700%, -300%)';
    document.getElementById('b3').style.transform = 'translate(500%, -300%)';
    document.getElementById('c3').style.transform = 'translate(300%, -300%)';
    document.getElementById('d3').style.transform = 'translate(100%, -300%)';
    document.getElementById('e3').style.transform = 'translate(-100%, -300%)';
    document.getElementById('f3').style.transform = 'translate(-300%, -300%)';
    document.getElementById('g3').style.transform = 'translate(-500%, -300%)';
    document.getElementById('h3').style.transform = 'translate(-700%, -300%)';
    document.getElementsByClassName('numero_linha')[6].style.transform = 'translate(0%, -300%)';

    document.getElementById('a4').style.transform = 'translate(700%, -100%)';
    document.getElementById('b4').style.transform = 'translate(500%, -100%)';
    document.getElementById('c4').style.transform = 'translate(300%, -100%)';
    document.getElementById('d4').style.transform = 'translate(100%, -100%)';
    document.getElementById('e4').style.transform = 'translate(-100%, -100%)';
    document.getElementById('f4').style.transform = 'translate(-300%, -100%)';
    document.getElementById('g4').style.transform = 'translate(-500%, -100%)';
    document.getElementById('h4').style.transform = 'translate(-700%, -100%)';
    document.getElementsByClassName('numero_linha')[5].style.transform = 'translate(0%, -100%)';

    document.getElementById('a5').style.transform = 'translate(700%, 100%)';
    document.getElementById('b5').style.transform = 'translate(500%, 100%)';
    document.getElementById('c5').style.transform = 'translate(300%, 100%)';
    document.getElementById('d5').style.transform = 'translate(100%, 100%)';
    document.getElementById('e5').style.transform = 'translate(-100%, 100%)';
    document.getElementById('f5').style.transform = 'translate(-300%, 100%)';
    document.getElementById('g5').style.transform = 'translate(-500%, 100%)';
    document.getElementById('h5').style.transform = 'translate(-700%, 100%)';
    document.getElementsByClassName('numero_linha')[4].style.transform = 'translate(0%, 100%)';

    document.getElementById('a6').style.transform = 'translate(700%, 300%)';
    document.getElementById('b6').style.transform = 'translate(500%, 300%)';
    document.getElementById('c6').style.transform = 'translate(300%, 300%)';
    document.getElementById('d6').style.transform = 'translate(100%, 300%)';
    document.getElementById('e6').style.transform = 'translate(-100%, 300%)';
    document.getElementById('f6').style.transform = 'translate(-300%, 300%)';
    document.getElementById('g6').style.transform = 'translate(-500%, 300%)';
    document.getElementById('h6').style.transform = 'translate(-700%, 300%)';
    document.getElementsByClassName('numero_linha')[3].style.transform = 'translate(0%, 300%)';

    document.getElementById('a7').style.transform = 'translate(700%, 500%)';
    document.getElementById('b7').style.transform = 'translate(500%, 500%)';
    document.getElementById('c7').style.transform = 'translate(300%, 500%)';
    document.getElementById('d7').style.transform = 'translate(100%, 500%)';
    document.getElementById('e7').style.transform = 'translate(-100%, 500%)';
    document.getElementById('f7').style.transform = 'translate(-300%, 500%)';
    document.getElementById('g7').style.transform = 'translate(-500%, 500%)';
    document.getElementById('h7').style.transform = 'translate(-700%, 500%)';
    document.getElementsByClassName('numero_linha')[2].style.transform = 'translate(0%, 500%)';

    document.getElementById('a8').style.transform = 'translate(700%, 700%)';
    document.getElementById('b8').style.transform = 'translate(500%, 700%)';
    document.getElementById('c8').style.transform = 'translate(300%, 700%)';
    document.getElementById('d8').style.transform = 'translate(100%, 700%)';
    document.getElementById('e8').style.transform = 'translate(-100%, 700%)';
    document.getElementById('f8').style.transform = 'translate(-300%, 700%)';
    document.getElementById('g8').style.transform = 'translate(-500%, 700%)';
    document.getElementById('h8').style.transform = 'translate(-700%, 700%)';
    document.getElementsByClassName('numero_linha')[1].style.transform = 'translate(0%, 700%)';

    document.getElementById('a1').classList.replace('borda_baixo_esq','borda_cima_dir');
    document.getElementById('h1').classList.replace('borda_baixo_dir','borda_cima_esq');
    document.getElementById('a8').classList.replace('borda_cima_esq','borda_baixo_dir');
    document.getElementById('h8').classList.replace('borda_cima_dir','borda_baixo_esq');

    document.getElementsByTagName('th')[1].style.transform = 'translate(700%,0%)';
    document.getElementsByTagName('th')[2].style.transform = 'translate(500%,0%)';
    document.getElementsByTagName('th')[3].style.transform = 'translate(300%,0%)';
    document.getElementsByTagName('th')[4].style.transform = 'translate(100%,0%)';
    document.getElementsByTagName('th')[5].style.transform = 'translate(-100%,0%)';
    document.getElementsByTagName('th')[6].style.transform = 'translate(-300%,0%)';
    document.getElementsByTagName('th')[7].style.transform = 'translate(-500%,0%)';
    document.getElementsByTagName('th')[8].style.transform = 'translate(-700%,0%)';
    
}

function whiteView(){
    document.getElementById('a1').style.transform = ''
    document.getElementById('b1').style.transform = ''
    document.getElementById('c1').style.transform = ''
    document.getElementById('d1').style.transform = ''
    document.getElementById('e1').style.transform = ''
    document.getElementById('f1').style.transform = ''
    document.getElementById('g1').style.transform = ''
    document.getElementById('h1').style.transform = ''
    document.getElementsByClassName('numero_linha')[8].style.transform = ''

    document.getElementById('a2').style.transform = ''
    document.getElementById('b2').style.transform = ''
    document.getElementById('c2').style.transform = ''
    document.getElementById('d2').style.transform = ''
    document.getElementById('e2').style.transform = ''
    document.getElementById('f2').style.transform = ''
    document.getElementById('g2').style.transform = ''
    document.getElementById('h2').style.transform = ''
    document.getElementsByClassName('numero_linha')[7].style.transform = ''

    document.getElementById('a3').style.transform = ''
    document.getElementById('b3').style.transform = ''
    document.getElementById('c3').style.transform = ''
    document.getElementById('d3').style.transform = ''
    document.getElementById('e3').style.transform = ''
    document.getElementById('f3').style.transform = ''
    document.getElementById('g3').style.transform = ''
    document.getElementById('h3').style.transform = ''
    document.getElementsByClassName('numero_linha')[6].style.transform = ''

    document.getElementById('a4').style.transform = ''
    document.getElementById('b4').style.transform = ''
    document.getElementById('c4').style.transform = ''
    document.getElementById('d4').style.transform = ''
    document.getElementById('e4').style.transform = ''
    document.getElementById('f4').style.transform = ''
    document.getElementById('g4').style.transform = ''
    document.getElementById('h4').style.transform = ''
    document.getElementsByClassName('numero_linha')[5].style.transform = ''

    document.getElementById('a5').style.transform = ''
    document.getElementById('b5').style.transform = ''
    document.getElementById('c5').style.transform = ''
    document.getElementById('d5').style.transform = ''
    document.getElementById('e5').style.transform = ''
    document.getElementById('f5').style.transform = ''
    document.getElementById('g5').style.transform = ''
    document.getElementById('h5').style.transform = ''
    document.getElementsByClassName('numero_linha')[4].style.transform = ''

    document.getElementById('a6').style.transform = ''
    document.getElementById('b6').style.transform = ''
    document.getElementById('c6').style.transform = ''
    document.getElementById('d6').style.transform = ''
    document.getElementById('e6').style.transform = ''
    document.getElementById('f6').style.transform = ''
    document.getElementById('g6').style.transform = ''
    document.getElementById('h6').style.transform = ''
    document.getElementsByClassName('numero_linha')[3].style.transform = ''

    document.getElementById('a7').style.transform = ''
    document.getElementById('b7').style.transform = ''
    document.getElementById('c7').style.transform = ''
    document.getElementById('d7').style.transform = ''
    document.getElementById('e7').style.transform = ''
    document.getElementById('f7').style.transform = ''
    document.getElementById('g7').style.transform = ''
    document.getElementById('h7').style.transform = ''
    document.getElementsByClassName('numero_linha')[2].style.transform = ''

    document.getElementById('a8').style.transform = ''
    document.getElementById('b8').style.transform = ''
    document.getElementById('c8').style.transform = ''
    document.getElementById('d8').style.transform = ''
    document.getElementById('e8').style.transform = ''
    document.getElementById('f8').style.transform = ''
    document.getElementById('g8').style.transform = ''
    document.getElementById('h8').style.transform = ''
    document.getElementsByClassName('numero_linha')[1].style.transform = ''

    document.getElementById('a1').classList.replace('borda_cima_dir','borda_baixo_esq');
    document.getElementById('h1').classList.replace('borda_cima_esq','borda_baixo_dir');
    document.getElementById('a8').classList.replace('borda_baixo_dir','borda_cima_esq');
    document.getElementById('h8').classList.replace('borda_baixo_esq','borda_cima_dir');

    document.getElementsByTagName('th')[1].style.transform = ''
    document.getElementsByTagName('th')[2].style.transform = ''
    document.getElementsByTagName('th')[3].style.transform = ''
    document.getElementsByTagName('th')[4].style.transform = ''
    document.getElementsByTagName('th')[5].style.transform = ''
    document.getElementsByTagName('th')[6].style.transform = ''
    document.getElementsByTagName('th')[7].style.transform = ''
    document.getElementsByTagName('th')[8].style.transform = ''
}

function clickFlipBoard(){
    if (corBoard == 'white'){
        blackView()
        corBoard = 'black'
    } else {
        whiteView()
        corBoard = 'white'
    }
}

corBoard = 'white'

//--------------------

function onload(){
	document.getElementById('flipBoard').addEventListener('click', clickFlipBoard)
}

selected = null

jogada = null;

window.addEventListener('load',onload)