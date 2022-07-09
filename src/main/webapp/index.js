let table, xhr = new XMLHttpRequest(), type = "REST";
let levels = [{ name: "Beginner", rows: 9, cols: 9, bombs: 10 }, { name: "Intermediate", rows: 16, cols: 16, bombs: 40 }, { name: "Advanced", rows: 16, cols: 40, bombs: 99 }];
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
    sendData(row, col, "SHOW");
}
function markBomb(event) {
    let cell = event.target;
    let col = cell.cellIndex;
    let row = cell.parentNode.rowIndex;
    sendData(row, col, "FLAG");
    event.preventDefault();
}
function sendData(row, col, state) {
    let formData = new FormData();
    formData.append("row", row);
    formData.append("col", col);
    formData.append("state", state);
    xhr.onload = function () {
        let obj = JSON.parse(xhr.responseText);
        printMatrixTable(obj.board);
        setNumOfBombs(obj.bombs);
        endOfGame(obj.winner);
        setEvents();
    };
    if (type == "Servlet") {
        xhr.open("put", "MinesweeperServlet");
        xhr.send(formData);
    } else {
        xhr.open("put", "webresources/minesweeper");
        xhr.setRequestHeader("Content-Type", "application/json");
        let object = formToObj(formData);
        xhr.send(JSON.stringify(object));
    }
}
function endOfGame(winner) {
    if (winner === "WIN") {
        showMessage("You win! &#9786;");
    } else if (winner === "LOSE") {
        showMessage("You lose! &#9785;");
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
    let value = diff.value ? parseInt(diff.value) : 0;
    let { rows, cols, bombs } = levels[value];
    let formData = new FormData();
    formData.append("rows", rows);
    formData.append("cols", cols);
    formData.append("bombs", bombs);
    xhr.onload = function () {
        let obj = JSON.parse(xhr.responseText);
        printMatrixTable(obj.board);
        setNumOfBombs(obj.bombs);
        setEvents();
        showMessage("");
    };
    if (type == "Servlet") {
        xhr.open("post", "MinesweeperServlet");
        xhr.send(formData);
    } else {
        xhr.open("post", "webresources/minesweeper");
        xhr.setRequestHeader("Content-Type", "application/json");
        let object = formToObj(formData);
        xhr.send(JSON.stringify(object));
    }
}
function formToObj(formData) {
    let object = {};
    formData.forEach((value, key) => object[key] = value);
    return object;
}
function init() {
    let button = document.querySelector("input[type='button']");
    button.onclick = newGame;
    let diff = document.querySelector("#difficulty");
    let str = "";
    levels.forEach((l, i) => {
        str += `<option value="${i}">${l.name} (${l.rows} x ${l.cols}, ${l.bombs} bombs)</option>`;
    });
    diff.innerHTML = str;
    newGame();
}
onload = init;
