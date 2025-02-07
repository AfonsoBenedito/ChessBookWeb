tempo = 0;

function msToTime(mili) {
    let seconds = parseInt((mili / 1000) % 60);
    seconds = (seconds < 10) ? ("0" + seconds) : seconds;
    let minutes = parseInt((mili / (1000 * 60)) % 60);
    minutes = (minutes < 10) ? ("0" + minutes) : minutes;
    let hours = parseInt((mili / (1000 * 60 * 60)) % 24);
    hours = (hours < 10) ? ("0" + hours) : hours;
    let days = parseInt((mili / (1000 * 60 * 60 * 24)) % 30);
    days = (days < 10) ? ("0" + days) : days;

    let result = days + hours + minutes + seconds;

    return result;
}

function calc(data){
    var now = new Date();
    var diff = now.getTime() - data.getTime();
    return(diff);
}

function counterDisplay(inp){
    var data = new Date(inp);
    setInterval(() => {
        tempo = calc(data);
        output = msToTime(tempo);
        document.getElementById('dia0').innerHTML = output.charAt(0);
        document.getElementById('dia1').innerHTML = output.charAt(1);
        document.getElementById('hora0').innerHTML = output.charAt(2);
        document.getElementById('hora1').innerHTML = output.charAt(3);
        document.getElementById('minuto0').innerHTML = output.charAt(4);
        document.getElementById('minuto1').innerHTML = output.charAt(5);
        document.getElementById('segundo0').innerHTML = output.charAt(6);
        document.getElementById('segundo1').innerHTML = output.charAt(7);
    }, 1000);
}

function counterBlackDisplay(inp){
    const data = new Date(inp);
    let output = msToTime(data);
    document.getElementById('blackDia0').innerHTML = output.charAt(0);
    document.getElementById('blackDia1').innerHTML = output.charAt(1);
    document.getElementById('blackHora0').innerHTML = output.charAt(2);
    document.getElementById('blackHora1').innerHTML = output.charAt(3);
    document.getElementById('blackMinuto0').innerHTML = output.charAt(4);
    document.getElementById('blackMinuto1').innerHTML = output.charAt(5);
    document.getElementById('blackSegundo0').innerHTML = output.charAt(6);
    document.getElementById('blackSegundo1').innerHTML = output.charAt(7);
}

function counterWhiteDisplay(inp){
    const data = new Date(inp);
    let output = msToTime(data);
    document.getElementById('whiteDia0').innerHTML = output.charAt(0);
    document.getElementById('whiteDia1').innerHTML = output.charAt(1);
    document.getElementById('whiteHora0').innerHTML = output.charAt(2);
    document.getElementById('whiteHora1').innerHTML = output.charAt(3);
    document.getElementById('whiteMinuto0').innerHTML = output.charAt(4);
    document.getElementById('whiteMinuto1').innerHTML = output.charAt(5);
    document.getElementById('whiteSegundo0').innerHTML = output.charAt(6);
    document.getElementById('whiteSegundo1').innerHTML = output.charAt(7);
}

function counterTotalDisplay(whiteInp, blackInp){
    const whiteData = new Date(whiteInp);
    const blackData = new Date(blackInp);
    let data = whiteData.getTime() + blackData.getTime();
    let output = msToTime(data);
    document.getElementById('totalDia0').innerHTML = output.charAt(0);
    document.getElementById('totalDia1').innerHTML = output.charAt(1);
    document.getElementById('totalHora0').innerHTML = output.charAt(2);
    document.getElementById('totalHora1').innerHTML = output.charAt(3);
    document.getElementById('totalMinuto0').innerHTML = output.charAt(4);
    document.getElementById('totalMinuto1').innerHTML = output.charAt(5);
    document.getElementById('totalSegundo0').innerHTML = output.charAt(6);
    document.getElementById('totalSegundo1').innerHTML = output.charAt(7);
}