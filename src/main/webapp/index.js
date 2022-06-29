let table, xhr = new XMLHttpRequest();
function printMatrixTable(doc) {
    table = document.querySelector("table");
    table.innerHTML = "";
    for (let i = 0; i < doc.length; i++) {
        let tr = document.createElement("tr");
        table.appendChild(tr);
        for (let j = 0; j < doc[i].length; j++) {
            let td = document.createElement("td");
            tr.appendChild(td);
            let cell = doc[i][j];
            if (cell.state === "FLAG") {
                td.className = "flag";
                td.innerHTML = "&#9873;";
            } else if (cell.state === "SHOW") {
                if (cell.value === "BOMB") {
                    td.className = "flag";
                    td.innerHTML = "&#128163;";
                } else if (cell.value === "NONE") {
                    td.className = "show";
                    td.textContent = "";
                } else {
                    td.className = "b" + cell.value.substring(3);
                    td.textContent = cell.value.substring(3);
                }
            } else {
                td.className = "blocked";
            }
        }
    }
}
function showMessage(msg) {
    let message = document.querySelector("#message");
    message.innerHTML = msg;
}
function check(event) {
    let cell = event.target;
    let col = cell.cellIndex;
    let row = cell.parentNode.rowIndex;
    let formData = new FormData();
    formData.append("row", row);
    formData.append("col", col);
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            let obj = JSON.parse(xhr.responseText);
            printMatrixTable(obj.board);
            setNumOfBombs(obj.bombs);
            endOfGame(obj.winner);
            setEvents();
        }
    };
    xhr.open("post", "CheckMinesweeperServlet", true);
    xhr.send(formData);
}
function markBomb(event) {
    let cell = event.target;
    let col = cell.cellIndex;
    let row = cell.parentNode.rowIndex;
    let formData = new FormData();
    formData.append("row", row);
    formData.append("col", col);
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            console.log(xhr.responseText);
            let obj = JSON.parse(xhr.responseText);
            printMatrixTable(obj.board);
            setNumOfBombs(obj.bombs);
            endOfGame(obj.winner);
            setEvents();
        }
    };
    xhr.open("post", "PutFlagMinesweeperServlet", true);
    xhr.send(formData);
    return false;
}
function endOfGame(winner) {
    if (winner) {
        if (winner === "WIN") {
            showMessage("You win! &#9786;");
        } else if (winner === "LOSE") {
            showMessage("You lose! &#9785;");
        }
    }
}
function setEvents() {
    table.onclick = check;
    table.oncontextmenu = markBomb;
}
function unsetEvents() {
    table.onclick = undefined;
    table.oncontextmenu = undefined;
}
function setNumOfBombs(n) {
    let p = document.querySelector("#numBombs");
    p.textContent = n;
}
function newGame() {
    let diff = document.querySelector("#difficulty");
    let value = (diff.value) ? parseInt(diff.value) : 0;
    let levels = [{rows: 9, cols: 9, bombs: 10}, {rows: 16, cols: 16, bombs: 40}, {rows: 16, cols: 40, bombs: 99}];
    let {rows, cols, bombs} = levels[value];
    let formData = new FormData();
    formData.append("rows", rows);
    formData.append("cols", cols);
    formData.append("bombs", bombs);
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            let obj = JSON.parse(xhr.responseText);
            printMatrixTable(obj.board);
            setNumOfBombs(obj.bombs);
            setEvents();
            showMessage("");
        }
    };
    xhr.open("post", "CreateMinesweeperServlet");
    xhr.send(formData);
}
function init() {
    let button = document.querySelector("input[type='button']");
    button.onclick = newGame;
    newGame();
}
onload = init;
