let matrix, table, rows, cols, bombs;
function createMatrix(numrows, numcols, initial) {
    let arr = [];
    for (let i = 0; i < numrows; ++i) {
        arr.push(new Array(numcols).fill(initial));
    }
    return arr;
}
function printMatrixTable() {
    table = document.querySelector("table");
    let inner = "";
    for (let i = 0; i < matrix.length; i++) {
        inner += "<tr>";
        for (let j = 0; j < matrix[i].length; j++) {
            inner += "<td class='blocked'></td>";
        }
        inner += "</tr>";
    }
    table.innerHTML = inner;
}
function fillBombs() {
    matrix = createMatrix(rows, cols, 0);
    for (let i = 0; i < bombs; ) {
        let row = Math.floor((Math.random() * rows));
        let col = Math.floor((Math.random() * cols));
        if (matrix[row][col] === 0) {
            matrix[row][col] = -1;
            i++;
        }
    }
}
function countPosition(row, col) {
    let count = 0;
    for (let i = row - 1; i <= row + 1; i++) {
        for (let j = col - 1; j <= col + 1; j++) {
            if (i >= 0 && i < rows && j >= 0 && j < cols && matrix[i][j] === -1) {
                count++;
            }
        }
    }
    return count;
}
function setNumbers() {
    matrix.forEach(function (vector, i) {
        vector.forEach(function (cell, j) {
            if (cell === 0) {
                matrix[i][j] = countPosition(i, j);
            }
        });
    });
}
function showCell(row, col) {
    let cell = table.rows[row].cells[col];
    cell.className = "show";
    var str = matrix[row][col];
    switch (matrix[row][col]) {
        case - 1:
            str = "&#128163;";
            cell.className = "flag";
            break;
        case 0:
            str = "";
            break;
        default:
            cell.className = "b" + matrix[row][col];
    }
    cell.innerHTML = str;
}
function openCell(row, col) {
    let cell = table.rows[row].cells[col];
    if (matrix[row][col] !== -1) {
        if (matrix[row][col] === 0 && cell.className === 'blocked') {
            showCell(row, col);
            openCells(row, col);
        } else {
            showCell(row, col);
        }
    }
}
function openCells(row, col) {
    let rowMax = matrix.length, colMax = matrix[0].length;
    for (let i = row - 1; i <= row + 1; i++) {
        for (let j = col - 1; j <= col + 1; j++) {
            if (i >= 0 && i < rowMax && j >= 0 && j < colMax) {
                openCell(i, j);
            }
        }
    }
}
function showBombs() {
    for (let i = 0; i < matrix.length; i++) {
        for (let j = 0; j < matrix[i].length; j++) {
            if (matrix[i][j] === -1) {
                showCell(i, j);
            }
        }
    }
}
function endOfGame() {
    let blocked = document.querySelectorAll(".blocked").length;
    if (blocked === bombs) {
        showMessage("Você ganhou! &#9786;");
        showBombs();
        unsetEvents();
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
    let value = matrix[row][col];
    if (cell.className !== 'flag') {
        if (value === -1) {
            showMessage("Você perdeu! &#9785;");
            showBombs();
            unsetEvents();
        } else if (value === 0) {
            openCell(row, col);
            endOfGame();
        } else {
            showCell(row, col);
            endOfGame();
        }
    }
}
function markBomb(event) {
    let cell = event.target;
    if (cell.className === "flag") {
        cell.innerHTML = "";
        cell.className = "blocked";
        setNumOfBombs(++bombs);
    } else if (cell.className === "blocked") {
        cell.innerHTML = "&#9873;";//&#128681;
        cell.className = "flag";
        setNumOfBombs(--bombs);
    }
    endOfGame();
    return false;
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
    let difficulties = [{rows: 9, cols: 9, bombs: 10}, {rows: 16, cols: 16, bombs: 40}, {rows: 16, cols: 40, bombs: 99}];
    let diff = document.querySelector("#difficulty");
    let value = (diff.value) ? parseInt(diff.value) : 0;
    let obj = difficulties[value];
    rows = obj.rows;
    cols = obj.cols;
    bombs = obj.bombs;
    setNumOfBombs(bombs);
    fillBombs();
    setNumbers();
    printMatrixTable();
    setEvents();
    showMessage("");
}
function init() {
    let button = document.querySelector("input[type='button']");
    button.onclick = newGame;
    newGame();
}
onload = init;
